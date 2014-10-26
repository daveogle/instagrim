package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.stores.Message;
import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.*;
import uk.ac.dundee.computing.aec.instagrim.Exceptions.*;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/Image",
    "/Image/*",
    "/Thumb/*",
    "/Images",
    "/Images/*",
    "/DeleteList",
    "/DeleteList/*"
})
@MultipartConfig

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private final HashMap CommandsMap = new HashMap();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1);
        CommandsMap.put("Images", 2);
        CommandsMap.put("Thumb", 3);
        CommandsMap.put("DeleteList", 4);
    }

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        cluster = CassandraHosts.getCluster();
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        try {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            if (args[2].equals(lg.getUsername())) {
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.deletePic(java.util.UUID.fromString(args[3]), args[2]);//Call deletePic with the picId
                tm.deleteComments(java.util.UUID.fromString(args[3]));//Delete Comments associtated with the picture
            } else {
                String t = "Restriction Error";
                String m = "Error you must be logged in to delete images";
                Error(t, m, "Home", "/Instagrim", response, request);
            }
        } catch (ImageException | ServletException | IOException e) {
            Error("Error :", e.getMessage(), "Home", "/", response, request);
        }
    }

    /**
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     * @param request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            Error("Option not found", "This option has not been recognized", "Home", "/Instagrim", response, request);
            return;
        }
        switch (command) {
            case 1:
                DisplayImage(Convertors.DISPLAY_PROCESSED, args[2], request, response);
                break;
            case 2:
                DisplayImageList(args[2], request, response, false);
                break;
            case 3:
                DisplayImage(Convertors.DISPLAY_THUMB, args[2], request, response);
                break;
            case 4:
                DisplayImageList(args[2], request, response, true);
                break;
            default:
                Error("Option not found", "This option has not been recognized", "Home", "/Instagrim", response, request);
        }
    }

    /**
     * Method to display the list of user images
     *
     * @param User
     * @param request
     * @param response
     * @param del - if the list is to be shown for deletion or for viewing
     * @throws ServletException
     * @throws IOException
     */
    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response, Boolean del) throws ServletException, IOException {
        PicModel tm = new PicModel();
        try {
            if (User.equals("Sample")) {//If the user is not logged in
                if (del) {//if trying to delete without logging in
                    del = false;//Set delete to false! Easy
                }
                tm.setCluster(cluster);
                if (tm.hasPictures(User) == false) {//If the sample pictures have not been uploaded
                    UploadSampleImages();//Upload the default sample images to the database
                }
            }
            tm.setCluster(cluster);
            java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User, false);//Get images without comments
            RequestDispatcher rd = request.getRequestDispatcher("/usersPics.jsp");
            request.setAttribute("Pics", lsPics);
            request.setAttribute("DeleteList", del);//Set if the display is for delete or not
            request.setAttribute("User", User);//set the user that the pics belong to
            rd.forward(request, response);
        } catch (IOException | ImageException | ServletException e) {
            Error("Picture List Error", e.getMessage(), "Home", "/", response, request);
        }
    }

    /**
     * A method for displaying a users image
     *
     * @param type
     * @param image
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void DisplayImage(int type, String image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        try {
            Pic p = tm.getPic(type, java.util.UUID.fromString(image));
            OutputStream out = response.getOutputStream();
            response.setContentType(p.getType());
            response.setContentLength(p.getLength());
            InputStream is = new ByteArrayInputStream(p.getBytes());
            BufferedInputStream input = new BufferedInputStream(is);
            byte[] buffer = new byte[8192];
            for (int length; (length = input.read(buffer)) > 0;) {
                out.write(buffer, 0, length);
            }
        } catch (ImageException | IOException e) {
            String t = "Error";
            String m = "Error displaying image" + e.getMessage();
            Error(t, m, "Home", "/Instagrim", response, request);
        }
    }

    /**
     * Method to handle doPost HTTP requests
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String t = "";
            String m = "";
            for (Part part : request.getParts()) {
                System.out.println("Part Name " + part.getName());
                String type = part.getContentType();
                if (!type.startsWith("image")) {//Check type of image
                    t = "Bad Type Error";
                    m = "Error you can only upload image files";
                } else if (part.getSize() > 1500000) {//Check file size
                    t = "File to large Error";
                    m = "Error you can only upload images of upto 1500kb in size";
                } else {
                    String filename = part.getSubmittedFileName();
                    InputStream is = request.getPart(part.getName()).getInputStream();
                    int i = is.available();
                    HttpSession session = request.getSession();
                    LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                    String username = "Sample";
                    if (lg.getlogedin()) {
                        username = lg.getUsername();
                    }
                    if (i > 0) {
                        byte[] b = new byte[i + 1];
                        is.read(b);
                        System.out.println("Length : " + b.length);
                        PicModel tm = new PicModel();
                        tm.setCluster(cluster);
                        tm.insertPic(b, type, filename, username);
                        is.close();
                    }
                    RequestDispatcher rd;
                    request.setAttribute("added", true);
                    rd = request.getRequestDispatcher("/upload.jsp");
                    rd.forward(request, response);
                    return;
                }
                Error(t, m, "Return", "/Instagrim/upload.jsp", response, request);
            }
        } catch (IOException | ServletException | ImageException e) {
            Error("Error :", e.getMessage(), "Home", "/Instagrim", response, request);
        }
    }

    /**
     * A method to upload the sample images to the database
     *
     * @throws IOException
     */
    private void UploadSampleImages() throws ImageException {
        try {
            String[] samplePics = new String[]{"boat1.jpg", "boat2.jpg", "boat3.jpg"};
            for (int i = 0; i < 3; i++) {
                Path path = Paths.get("/SamplePics/" + samplePics[i]);
                String type = Files.probeContentType(path);
                InputStream bais = getClass().getResourceAsStream("/SamplePics/" + samplePics[i]);
                BufferedImage bufferedImage = ImageIO.read(bais);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                byte[] imageInByte = baos.toByteArray();
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertPic(imageInByte, type, samplePics[i], "Sample");
                bais.close();
            }
        } catch (IOException | ImageException e) {
            throw new ImageException(e.toString());
        }
    }

    /**
     * Function to call the message page with an error message
     *
     * @param err -the error type
     * @param mess - the error message
     * @param RName - the redirect link name
     * @param redirect - the redirect URL
     * @param response
     * @param request
     * @throws ServletException
     * @throws IOException
     */
    private void Error(String err, String mess, String RName, String redirect, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        Message m = new Message();
        m.setMessageTitle(err);
        m.setMessage(mess);
        m.setPageRedirectName(RName);
        m.setPageRedirect(redirect);
        request.setAttribute("message", m);
        RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Called by the servlet container to indicate to a servlet that the servlet
     * is being taken out of service. See {@link Servlet#destroy}.
     *
     *
     */
    @Override
    public void destroy() {
        cluster.close();
    }
}
