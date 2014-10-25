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
//import javax.servlet.http.HttpServletRequest;
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
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;

/**
 *
 * @author Administrator
 */
@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    Cluster cluster = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
        rd.forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
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
        us.setCluster(cluster);
        boolean exists = us.IsValidUser(username, password);
        if (exists == true) { //if the user alread exists in the database
            RequestDispatcher rd = request.getRequestDispatcher("/register.jsp");
            request.setAttribute("exists", exists);
            rd.forward(request, response);
        } else {
            boolean registered = us.RegisterUser(username, password);
            if (registered) {//If successfully registered (CHECK THIS IS DONE PROPERLY AT THE MODEL END
                uploadDefaultAvatar(username);
                RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
                request.setAttribute("registered", true);
                rd.forward(request, response);
            } else {
                //Forward to message page
            }
        }
    }

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
            PicModel tm = new PicModel();
            tm.setCluster(cluster);
            tm.setAvatar(imageInByte, type, "Avatar", userName);
            bais.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
