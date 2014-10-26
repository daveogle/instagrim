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
import uk.ac.dundee.computing.aec.instagrim.models.*;
import uk.ac.dundee.computing.aec.instagrim.stores.*;
import uk.ac.dundee.computing.aec.instagrim.Exceptions.*;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(name = "Account", urlPatterns = {"/Account", "/Avatar"})
@MultipartConfig
public class Account extends HttpServlet {

    /**
     * Constructor
     */
    public Account() {

    }

    private Cluster cluster = null;

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
     * Method to get the account details for display
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg != null && lg.getlogedin()) {//If the user is logged in
            if (args[1].equals("Avatar")) {
                displayAvatar(lg, request, response);
                return;
            }
            try {
                User myUser = new User();
                myUser.setCluster(cluster);
                AccountBean ac = new AccountBean();
                ac = myUser.getAccountInfo(ac, lg.getUsername());//call the model
                request.setAttribute("AccountInfo", ac);
                RequestDispatcher rd = request.getRequestDispatcher("account.jsp");
                rd.forward(request, response);
            } catch (ServletException | IOException | AccountException e) {
                Message m = new Message();
                m.setMessageTitle("Loggin Error");
                m.setMessage("There was an error accessing your account" + e.getMessage());
                m.setPageRedirectName("Home");
                m.setPageRedirect("index.jsp");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            }
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

    /**
     * Method to display the avatar of the user
     *
     * @param lg - loggedIn bean
     * @param request -
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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
        } catch (Exception e) {
            Message m = new Message();
            m.setMessageTitle("Error :");
            m.setMessage("Error displaying image" + e.getMessage());
            m.setPageRedirectName("Home");
            m.setPageRedirect("/Instagrim");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Method for update of Avatar info
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Message m = new Message();
        try {
            for (Part part : request.getParts()) {
                String type = part.getContentType();
                if (!type.startsWith("image")) {//Check image type
                    m.setMessageTitle("Bad Type Error");
                    m.setMessage("Error you can only upload image files");
                } else if (part.getSize() > 1500000) {//Check image size
                    m.setMessageTitle("File to large Error");
                    m.setMessage("Error you can only upload images of upto 1500kb in size");
                } else {
                    String filename = part.getSubmittedFileName();
                    InputStream is = request.getPart(part.getName()).getInputStream();
                    int i = is.available();
                    HttpSession session = request.getSession();
                    LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                    String username = null;
                    if (lg.getlogedin()) { //Check logged In
                        username = lg.getUsername();
                        if (i > 0) {
                            byte[] b = new byte[i + 1];
                            is.read(b);
                            System.out.println("Length : " + b.length);
                            PicModel tm = new PicModel();
                            tm.setCluster(cluster);
                            tm.updateAvatar(b, type, filename, username);
                            Pic pic = tm.getAvatar(username);
                            lg.setAvatar(pic);//Set the new avatar in the login bean
                            is.close();
                            m.setMessageTitle("Avatar Updated :");
                            m.setMessage("Your avatar has been successfully updated");
                        }
                    } else {
                        m.setMessageTitle("Account Update Error: ");
                        m.setMessage("You must be logged in to update your account");
                    }
                    m.setPageRedirectName("Accout");
                    m.setPageRedirect("/Instagrim/Account");
                    request.setAttribute("message", m);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                    dispatcher.forward(request, response);
                }
            }
        } catch (IOException | AccountException e) {
            System.out.println(e);
            m.setMessageTitle("Account Error :");
            m.setMessage(e.getMessage());
            m.setPageRedirectName("Accout");
            m.setPageRedirect("account.jsp");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
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
            doPut(request, response);//Redirect avatar updates to doPUT method
            return;
        }
        try {
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
                ac.setFirstName(firstName);//Set the infomation in the bean
                ac.setLastName(lastName);
                ac.setEmail(email);
                ac.setAddress(street, city, postCode);
                User myUser = new User();
                myUser.setCluster(cluster);
                myUser.setAccountInfo(ac, lg.getUsername());//Call the model
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
                m.setMessageTitle("Loggin Error");
                m.setMessage("You must be logged in to update your account");
                m.setPageRedirectName("Accout");
                m.setPageRedirect("account.jsp");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            }
        } catch (AccountException | ServletException | IOException e) {
            Message m = new Message();
            m.setMessageTitle("Account Error :");
            m.setMessage(e.getMessage());
            m.setPageRedirectName("Accout");
            m.setPageRedirect("account.jsp");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
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
