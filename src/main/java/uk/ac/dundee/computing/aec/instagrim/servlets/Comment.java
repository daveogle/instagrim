/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Dave Ogle
 */
@WebServlet(urlPatterns = {
    "/Comment",
    "/Comment/*",
    "/DeleteComment",
    "/DeleteComment/*"
})
public class Comment extends HttpServlet {

    private Cluster cluster;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
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
        String args[] = Convertors.SplitRequestPath(request);
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        if (lg != null && lg.getlogedin()) {
            String username = lg.getUsername();
            String owner = args[2];
            if (username.equals(owner)) {
                PicModel pm = new PicModel();
                pm.setCluster(cluster);
                boolean deleted = pm.deleteComment(java.util.UUID.fromString(args[3]), java.util.UUID.fromString(args[4]));
                if (deleted) {
                    RequestDispatcher rd = request.getRequestDispatcher("Images/" + username);
                    rd.forward(request, response);
                    //response.sendRedirect("/Instagrim/Images/" + username);
                } else {

                }
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
        String picId = request.getParameter("picId");
        String comment = request.getParameter("commentsBox" + picId);//This is an issue
        HttpSession session = request.getSession();
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        String username = lg.getUsername();
        if (lg.getlogedin()) {
            PicModel tm = new PicModel();
            tm.setCluster(cluster);
            tm.insertComment(username, java.util.UUID.fromString(picId), comment);
        }
        response.sendRedirect("Images/" + username);
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
