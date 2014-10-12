package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.exceptions.fileSizeException;
import uk.ac.dundee.computing.aec.instagrim.exceptions.badTypeException;
import uk.ac.dundee.computing.aec.instagrim.stores.Message;
import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
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
   "/DeleteList/*",
   "/Delete",
   "/Delete/*",
   "/Comment"
})
@MultipartConfig

public class Image extends HttpServlet {

   private static final long serialVersionUID = 1L;
   private Cluster cluster;
   private HashMap CommandsMap = new HashMap();

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
      CommandsMap.put("Delete", 5);
      CommandsMap.put("Comment", 6);
   }

   @Override
   public void init(ServletConfig config) throws ServletException {
      // TODO Auto-generated method stub
      cluster = CassandraHosts.getCluster();
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
         case 2://Display all images
            DisplayImageList(args[2], request, response, false);
            break;
         case 3:
            DisplayImage(Convertors.DISPLAY_THUMB, args[2], request, response);
            break;
         case 4:
            DisplayImageList(args[2], request, response, true);
            break;
         case 5:
            DeleteImage(request, response);
            break;
         case 6:
            //Do a thing
         default:
            error("Option not found", "This option has not been recognized", "Home", "/Instagrim", response, request);
      }
   }

   private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response, Boolean del) throws ServletException, IOException {
      PicModel tm = new PicModel();
      tm.setCluster(cluster);
      java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
      RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
      request.setAttribute("Pics", lsPics);
      request.setAttribute("DeleteList", del);
      request.setAttribute("User", User);
      rd.forward(request, response);
   }

   private void DisplayImage(int type, String image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      PicModel tm = new PicModel();
      tm.setCluster(cluster);

      Pic p = tm.getPic(type, java.util.UUID.fromString(image));

      try (OutputStream out = response.getOutputStream()) {
         response.setContentType(p.getType());
         response.setContentLength(p.getLength());
         //out.write(Image);
         InputStream is = new ByteArrayInputStream(p.getBytes());
         BufferedInputStream input = new BufferedInputStream(is);
         byte[] buffer = new byte[8192];
         for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
         }
      } catch (IOException e) {
         String t = "ERROR TYPE";//e.getErrorType();
         String m = "ERROR MESSAGE";//e.getErrorMessage();
         error(t, m, "", "", response, request);
      }
   }

   private void DeleteImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      HttpSession session = request.getSession();
      PicModel tm = new PicModel();
      Pic delP = (Pic) session.getAttribute("Pic");
      tm.setCluster(cluster);
      tm.deletePic(java.util.UUID.fromString(delP.getSUUID()));
      LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
      DisplayImageList(lg.getUsername(), request, response, true);
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, badTypeException, fileSizeException {
      try {
         for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());
            String type = part.getContentType();
            if (!type.startsWith("image")) {
               throw new badTypeException();
            }
            if (part.getSize() > 1500000) {//FOR TEST
               throw new fileSizeException();
            }
            String filename = part.getSubmittedFileName();
            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            String username = "majed";
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
            RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
            rd.forward(request, response);
         }
      } catch (badTypeException e) {
         String t = "Bad Type Exception";//e.getErrorType();
         String m = "Error: You may only upload image files!";//e.getErrorMessage();
         String rn = "Return";
         String r = "/Instagrim/upload.jsp";
         error(t, m, rn, r, response, request);
      } catch (fileSizeException fs) {
         String t = "File Size Exception";//e.getErrorType();
         String m = "Error: You may only upload files of upto 1500KB!";//e.getErrorMessage();
         String rn = "Return";
         String r = "/Instagrim/upload.jsp";
         error(t, m, rn, r, response, request);
      } finally {
         String t = "Unknown Exception";
         String m = "Error: An unknown problem has occured - Sorry";
         String rn = "Home";
         String r = "/Instagrim";
         error(t, m, rn, r, response, request);
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
