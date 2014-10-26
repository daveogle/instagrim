/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.RequestDispatcher;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.Exceptions.*;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.Message;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    Cluster cluster = null;

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
     * Overridden doGET request forwards the user to register.jsp page.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
        rd.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method. Method called when user
     * registers a new user
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User us = new User();
        try {
            us.setCluster(cluster);
            boolean exists = us.IsValidUser(username, password);
            if (exists == true) { //if the user alread exists in the database
                RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
                request.setAttribute("exists", exists);
                rd.forward(request, response);
            } else {
                boolean registered = us.RegisterUser(username, password);
                if (registered) {//If successfully registered
                    uploadDefaultAvatar(username);
                    RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");//Redirect to Login page
                    request.setAttribute("registered", true);
                    rd.forward(request, response);
                }
            }
        } catch (RegisterException | ServletException | IOException e) {
            Message m = new Message();
            m.setMessageTitle("Error: ");
            m.setMessage(e.getMessage());
            m.setPageRedirectName("Home");
            m.setPageRedirect("/");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Method to upload the default avatar for a new user
     *
     * @param userName
     * @throws IOException
     */
    public void uploadDefaultAvatar(String userName) throws IOException {
        try {
            String avatarName = "defaultAvatar.png";
            Path path = Paths.get("/SamplePics/" + avatarName);
            String type = Files.probeContentType(path);
            InputStream bais = getClass().getResourceAsStream("/SamplePics/" + avatarName);
            BufferedImage bufferedImage = ImageIO.read(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageInByte = baos.toByteArray();
            PicModel pm = new PicModel();
            pm.setCluster(cluster);
            pm.setAvatar(imageInByte, type, "Avatar", userName);
            bais.close();
        } catch (Exception e) {
            System.out.println(e);
            throw new IOException();
        }
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
