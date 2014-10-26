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
import uk.ac.dundee.computing.aec.instagrim.Exceptions.FriendException;
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
        if (lg != null && lg.getlogedin()) {//If logged in
            try {
                User us = new User();
                us.setCluster(cluster);
                java.util.LinkedList<String> users = us.getUsers(lg.getUsername());//Returns all the users as a linked list
                java.util.List<String> friends = us.getFriendList(lg.getUsername());//Returns all the friends as a linked list
                request.setAttribute("users", users);
                request.setAttribute("friends", friends);
                RequestDispatcher rd = request.getRequestDispatcher("friends.jsp");
                rd.forward(request, response);
            } catch (FriendException | ServletException | IOException e) {
                Message m = new Message();
                m.setMessageTitle("Error");
                m.setMessage("There was an error accessing your friends: \n" + e.getMessage());
                m.setPageRedirectName("Home");
                m.setPageRedirect("/Instagrim-dao");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
            }
        } else {
            Message m = new Message();
            m.setMessageTitle("Error");
            m.setMessage("You must be logged in to access your friends");
            m.setPageRedirectName("Home");
            m.setPageRedirect("/Instagrim-dao");
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Handles adding of friends Handles the HTTP <code>POST</code> method.
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
        try {
            HttpSession session = request.getSession();
            User us = new User();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            if (lg != null && lg.getlogedin()) {//Check if user is logged in
                us.setCluster(cluster);
                us.addFriend(lg.getUsername(), friend);//add the friend
                us.addFriend(friend, lg.getUsername());//add the user to the friend's friend list
                Message m = new Message();
                m.setMessageTitle("Friend Added");
                m.setMessage(friend + " was added as a friend");
                m.setPageRedirectName("Friends");
                m.setPageRedirect("/Instagrim-dao/Friends");
                request.setAttribute("message", m);
                RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
                dispatcher.forward(request, response);
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
        } catch (FriendException | ServletException | IOException e) {
            Message m = new Message();
            m.setMessageTitle("Friend Error");
            m.setMessage("An error occured adding your Friend: " + e.getMessage());
            m.setPageRedirectName("Home");
            m.setPageRedirect("index.jsp");
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
