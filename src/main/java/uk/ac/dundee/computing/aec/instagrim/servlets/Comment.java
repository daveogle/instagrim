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
import uk.ac.dundee.computing.aec.instagrim.Exceptions.ImageException;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Message;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(urlPatterns = {
    "/Comments",
    "/Comments/*"})
public class Comment extends HttpServlet {

    private Cluster cluster;

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
     * Processes requests for HTTP <code>GET</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs /
     *
     * //
     * <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = null;
        String args[] = Convertors.SplitRequestPath(request);
        if (args[2].equals("Sample")) {//If Sample user
            rd = request.getRequestDispatcher("/Images/Sample");//redirect back to images
        } else {
            try {
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(args[2], true);//Get images without comments
                rd = request.getRequestDispatcher("/comments.jsp");
                request.setAttribute("Pics", lsPics);
                request.setAttribute("User", args[2]);//set the user that the pics belong to
            } catch (Exception e) {
                Message m = new Message();
                m.setMessageTitle("Error");
                m.setMessage("Error getting comments" + e.getMessage());
                m.setPageRedirectName("Home");
                m.setPageRedirect("/");
                request.setAttribute("message", m);
                rd = request.getRequestDispatcher("message.jsp");
            }
        }
        rd.forward(request, response);
    }

    /**
     * handle doDelete HTTP request for a comment
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        try {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            if (lg != null && lg.getlogedin()) {//If the user is loggedIn
                String username = lg.getUsername();
                String owner = args[2];
                if (username.equals(owner)) {//if the user owns the picture
                    PicModel pm = new PicModel();
                    pm.setCluster(cluster);
                    pm.deleteComment(java.util.UUID.fromString(args[3]), java.util.UUID.fromString(args[4]));
                } else {
                    response.sendError(401, "You must be logged in to delete an image");
                }
            }
        } catch (ImageException | IOException e) {
            Message m = new Message();
            m.setMessageTitle("Delete Failed");
            m.setMessage("There was an error deleting your comment" + e.getMessage());
            m.setPageRedirectName("Home");
            m.setPageRedirect("/");
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
        String picId = request.getParameter("picId");
        String comment = request.getParameter("commentsBox" + picId);
        try {
            HttpSession session = request.getSession();
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            String username = lg.getUsername();
            if (lg.getlogedin()) {
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertComment(username, java.util.UUID.fromString(picId), comment);
            }
            Message m = new Message();
            m.setMessageTitle("Comment Added");
            m.setMessage("Your comment has been added");
            m.setPageRedirectName("Back");
            m.setPageRedirect("/Instagrim/Comments/" + username);
            request.setAttribute("message", m);
            RequestDispatcher dispatcher = request.getRequestDispatcher("message.jsp");
            dispatcher.forward(request, response);
        } catch (ImageException | ServletException | IOException e) {
            Message m = new Message();
            m.setMessageTitle("Comment Error");
            m.setMessage("An error occured adding your comment" + e.getMessage());
            m.setPageRedirectName("Home");
            m.setPageRedirect("/");
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
