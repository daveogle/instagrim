/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.stores.Message;
import com.datastax.driver.core.Cluster;
import javax.servlet.annotation.MultipartConfig;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.AccountBean;
import uk.ac.dundee.computing.aec.instagrim.stores.*;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(name = "Account", urlPatterns = {"/Account", "/Avatar"})
@MultipartConfig
public class Account extends HttpServlet {
    
    public Account() {
        
    }
    
    Cluster cluster = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg != null && lg.getlogedin()) {
            if (args[1].equals("Avatar")) {
                displayAvatar(lg, request, response);
                return;
            }
            User myUser = new User();
            myUser.setCluster(cluster);
            AccountBean ac = new AccountBean();
            ac = myUser.getAccountInfo(ac, lg.getUsername());
            request.setAttribute("AccountInfo", ac);
            RequestDispatcher rd = request.getRequestDispatcher("account.jsp");
            rd.forward(request, response);
        } else {
            Message m = new Message();
            m.setMessageTitle("Loggin Error");
            m.setMessage("You must be logged in to view your account");
            m.setPageRedirectName("Home");
            m.setPageRedirect("index.jsp");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    public void displayAvatar(LoggedIn lg, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Pic p = lg.getAvatar();
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
            Message m = new Message();
            m.setMessageTitle("I/O Error");
            m.setMessage("Error displaying image");
            m.setPageRedirectName("Home");
            m.setPageRedirect("/Instagrim");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AccountBean ac = new AccountBean();
        try {
            for (Part part : request.getParts()) {
                String type = part.getContentType();
                if (!type.startsWith("image")) {
                    String t = "Bad Type Error";//e.getErrorType();
                    String m = "Error you can only upload image files";//e.getErrorMessage();
                    //error(t, m, "Return", "/Instagrim/upload.jsp", response, request);
                }
                if (part.getSize() > 1500000) {//CHANGE FOR Avatar (SEE About putting inside @MultiPart Config
                    String t = "File to large Error";//e.getErrorType();
                    String m = "Error you can only upload images of upto 1500kb in size";//e.getErrorMessage();
                    //error(t, m, "Return", "/Instagrim/upload.jsp", response, request);
                }
                String filename = part.getSubmittedFileName();
                InputStream is = request.getPart(part.getName()).getInputStream();
                int i = is.available();
                HttpSession session = request.getSession();
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                String username = null;
                boolean add = false;
                if (lg.getlogedin()) {
                    username = lg.getUsername();
                    if (i > 0) {
                        byte[] b = new byte[i + 1];
                        is.read(b);
                        System.out.println("Length : " + b.length);
                        PicModel tm = new PicModel();
                        tm.setCluster(cluster);
                        add = tm.updateAvatar(b, type, filename, username);//tm.setAvatar(b, type, type, username)//UPDATE A FIELD NEEDED
                        Pic pic = tm.getAvatar(username);
                        lg.setAvatar(pic);
                        is.close();
                    }
                    Message m = new Message();
                    if (add) {
                        m.setMessageTitle("Account Updated");
                        m.setMessage("Your Avatar has been successfully updated");
                    } else {
                        m.setMessageTitle("Account Error");
                        m.setMessage("There was an error updating your Avatar");
                    }
                    m.setPageRedirectName("Accout");
                    m.setPageRedirect("/Instagrim/Account");
                    request.setAttribute("message", m);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                    dispatcher.forward(request, response);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
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
        String args[] = Convertors.SplitRequestPath(request);
        if (args[1].equals("Avatar")) {
            doPut(request, response);
        }
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        AccountBean ac = new AccountBean();
        if (lg != null && lg.getlogedin()) {
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String street = request.getParameter("Street");
            String city = request.getParameter("City");
            String postCode = request.getParameter("PostCode");
            ac.setFirstName(firstName);
            ac.setLastName(lastName);
            ac.setEmail(email);
            ac.setAddress(street, city, postCode);
            User myUser = new User();
            myUser.setCluster(cluster);
            boolean added = myUser.setAccountInfo(ac, lg.getUsername());
            if (added) {
                Message m = new Message();
                m.setMessageTitle("Account Updated");
                m.setMessage("Your account details have been successfully updated");
                m.setPageRedirectName("Accout");
                m.setPageRedirect("/Instagrim/Account");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            } else {
                Message m = new Message();
                m.setMessageTitle("Account Error");
                m.setMessage("There was an error in adding your account information");
                m.setPageRedirectName("Accout");
                m.setPageRedirect("account.jsp");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            }
        } else {
            Message m = new Message();
            m.setMessageTitle("Loggin Error");
            m.setMessage("You must be logged in to update your account");
            m.setPageRedirectName("Accout");
            m.setPageRedirect("account.jsp");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
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
