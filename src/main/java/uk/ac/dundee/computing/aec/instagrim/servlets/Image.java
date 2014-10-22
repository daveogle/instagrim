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
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

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

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (args[2].equals(lg.getUsername())) {
            PicModel tm = new PicModel();
            tm.setCluster(cluster);
            tm.deletePic(java.util.UUID.fromString(args[3]), args[2]);//Call deletePic with the picId
            tm.deleteComments(java.util.UUID.fromString(args[3]));
        } else {
            String t = "Restriction Error";//e.getErrorType();
            String m = "Error you must be logged in to delete images";//e.getErrorMessage();
            error(t, m, "Home", "/Instagrim", response, request);
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
        // TODO Auto-generated method stub
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Option not found", "This option has not been recognized", "Home", "/Instagrim", response, request);
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
                error("Option not found", "This option has not been recognized", "Home", "/Instagrim", response, request);
        }
    }

    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response, Boolean del) throws ServletException, IOException {
        PicModel tm = new PicModel();
        if (User.equals("Sample")) {//If the user is not logged in
            if (del) {//if trying to delete without logging in
                del = false;//Set delete to false! Easy
            }
            tm.setCluster(cluster);
            if (tm.hasPictures(User) != 1) {//If the sample pictures have not been uploaded
                UploadSampleImages();
            }
        }
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User, false);//Get images without comments
        RequestDispatcher rd = request.getRequestDispatcher("/usersPics.jsp");
        request.setAttribute("Pics", lsPics);
        request.setAttribute("DeleteList", del);//Set if the display is for delete or not
        request.setAttribute("User", User);//set the user that the pics belong to
        rd.forward(request, response);
    }

    private void DisplayImage(int type, String image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);

        Pic p = tm.getPic(type, java.util.UUID.fromString(image));

        try (OutputStream out = response.getOutputStream()) {
            response.setContentType(p.getType());
            response.setContentLength(p.getLength());
            InputStream is = new ByteArrayInputStream(p.getBytes());
            BufferedInputStream input = new BufferedInputStream(is);
            byte[] buffer = new byte[8192];
            for (int length; (length = input.read(buffer)) > 0;) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            String t = "I/O Error";//e.getErrorType();
            String m = "Error displaying image";//e.getErrorMessage();
            error(t, m, "Home", "/Instagrim", response, request);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean added = false;
        try {
            for (Part part : request.getParts()) {
                System.out.println("Part Name " + part.getName());
                String type = part.getContentType();
                if (!type.startsWith("image")) {
                    String t = "Bad Type Error";//e.getErrorType();
                    String m = "Error you can only upload image files";//e.getErrorMessage();
                    error(t, m, "Return", "/Instagrim/upload.jsp", response, request);
                }
                if (part.getSize() > 1500000) {//CHANGE FOR TEST
                    String t = "File to large Error";//e.getErrorType();
                    String m = "Error you can only upload images of upto 1500kb in size";//e.getErrorMessage();
                    error(t, m, "Return", "/Instagrim/upload.jsp", response, request);
                }
                String filename = part.getSubmittedFileName();
                InputStream is = request.getPart(part.getName()).getInputStream();
                int i = is.available();
                HttpSession session = request.getSession();
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                String username = "";//Do something with this
                if (lg.getlogedin()) {
                    username = lg.getUsername();
                }
                if (i > 0) {
                    byte[] b = new byte[i + 1];
                    is.read(b);
                    System.out.println("Length : " + b.length);
                    PicModel tm = new PicModel();
                    tm.setCluster(cluster);
                    added = tm.insertPic(b, type, filename, username);
                    is.close();
                }
                RequestDispatcher rd;
                if (added) {
                    request.setAttribute("added", true);
                    rd = request.getRequestDispatcher("/upload.jsp");
                    rd.forward(request, response);
                }
            }
        } catch (IOException | ServletException e) {
            String t = "Unknown Error";//e.getErrorType();
            String m = "An Error has occured";//e.getErrorMessage();
            error(t, m, "Home", "/Instagrim", response, request);

        }
    }

    private void UploadSampleImages() throws IOException {
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void error(String err, String mess, String RName, String redirect, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        Message m = new Message();
        m.setMessageTitle(err);
        m.setMessage(mess);
        m.setPageRedirectName(RName);
        m.setPageRedirect(redirect);
        request.setAttribute("message", m);
        RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
        dispatcher.forward(request, response);
    }
}
