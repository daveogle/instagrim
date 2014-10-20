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
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Message;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(name = "Friends", urlPatterns = {"/Friends"})
public class Friends extends HttpServlet {

    Cluster cluster = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg != null && lg.getlogedin()) {
            try {
                User us = new User();
                us.setCluster(cluster);
                java.util.LinkedList<String> users = us.getUsers(lg.getUsername());//Returns all the users as a linked list
                java.util.List<String> friends = us.getFriendList(lg.getUsername());
                request.setAttribute("users", users);
                request.setAttribute("friends", friends);
                RequestDispatcher rd = request.getRequestDispatcher("friends.jsp");
                rd.forward(request, response);
            } catch (Exception e) {
//                Message m = new Message();
//                m.setMessageTitle("Error");
//                m.setMessage("You must be logged in to view your account");
//                m.setPageRedirectName("Login");
//                m.setPageRedirect("/Instagrim/Login");
//                request.setAttribute("message", m);
//                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
//                dispatcher.forward(request, response);
            }
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
        String friend = request.getParameter("userList");
        HttpSession session = request.getSession();
        User us = new User();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg != null && lg.getlogedin()) {
            try {
                us.setCluster(cluster);
                boolean added = us.addFriend(lg.getUsername(), friend);
                Message m = new Message();
                m.setMessageTitle("Friend Added");
                m.setMessage(friend + " was added as a friend");
                m.setPageRedirectName("Friends");
                m.setPageRedirect("/Instagrim/Friends");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            } catch (Exception e) {
                Message m = new Message();
                m.setMessageTitle("Friend Error");
                m.setMessage("An error occured adding your Friend");
                m.setPageRedirectName("Home");
                m.setPageRedirect("index.jsp");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            }
        } else {
            Message m = new Message();
            m.setMessageTitle("Friend Error");
            m.setMessage("You must be logged in to add Friends");
            m.setPageRedirectName("Home");
            m.setPageRedirect("index.jsp");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        }
    }

    public java.util.List<String> getFriends(String username) {
        java.util.List<String> friendList = new java.util.LinkedList<>();
        User us = new User();
        try {
            us.setCluster(cluster);
            friendList = us.getFriendList(username);
        } catch (Exception e) {

        }
        return friendList;
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
