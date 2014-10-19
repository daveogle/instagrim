/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.Message;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(name = "Account", urlPatterns = {"/Account"})
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
        //Get the user account Information
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
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg != null && lg.getlogedin()) {
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String street = request.getParameter("Street");
            String city = request.getParameter("City");
            String postCode = request.getParameter("PostCode");
            lg.setFirstName(firstName);
            lg.setLastName(lastName);
            lg.setAddress(street, city, postCode);
            User myUser = new User();
            myUser.setCluster(cluster);
            boolean added = myUser.setAccountInfo(lg);
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
