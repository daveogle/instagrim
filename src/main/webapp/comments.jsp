<%-- 
    Document   : comments
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>
<%@page import="java.util.*"%>
<%@page import="javax.servlet.jsp.tagext.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<!DOCTYPE html>
<html>
    <% String user = (String) request.getAttribute("User");//Set the user that owns the pictures displayed%>
    <head>
        <title> Instagrim: <%=user%>'s Comments</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <script src="${pageContext.request.contextPath}/Scripts/javaScript.js" type="text/javascript"></script>
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>

        <nav>
            <ul>
                <li><a href="/Instagrim"> Home </a></li>
                <li class="nav"><a href="/Instagrim/Images/<%=user%>">Just Images</a></li>
                <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                <li><a href="/Instagrim/Friends">Friends</a></li>    
                <li><a href="/Instagrim/Images/<%=user%>">My Images</a></li>
                <li><a href="/Instagrim/DeleteList/<%=user%>">Delete Image</a></li>
                <li><a href="/Instagrim/Account">My Account</a></li>
                <li><a href="/Instagrim/Logout/" id="lo" onclick="alertUser('Are you sure you want to logout?', 'lo', '/Instagrim/Logout')">Logout</a></li>
            </ul>
    </nav>

    <h1> <%=user%>'s Pics & Comments </h1>
    <%
        //Check if the page is to display images for display or delete
        java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
        if (lsPics == null) {
            //If no pictures are found%>
    <p> No Pictures found </p>
    <%
    } else {
        //Display all pics
        Iterator<Pic> iterator;
        iterator = lsPics.iterator();
        int id = 0;//Counter to provide unique photo id
        while (iterator.hasNext()) {
            id++;
            Pic p = (Pic) iterator.next();
            session.setAttribute("Pic", p);
    %>
    <div class="picture">
        <!-- DISPLAY IMAGES FOR VIEWING -->
        <!--IMAGE-->
        <a href="/Instagrim/Image/<%=p.getSUUID()%>"><img alt="User instagrim picture" src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><br/>
        <!--Comments-->
        <form action="/Instagrim/Comment" id="commentsForm<%=id%>" onsubmit="return validateComment('Text<%=id%>');" name ="comments" method="POST">
            <h3>Comments:</h3><input type="button" id="button<%=id%>" value="+" onclick="onClick('picture<%=id%>');">
            <div class="commentsAll" id="picture<%=id%>">
                <%  Iterator<CommentBean> i;
                    if (p.getComments() != null) {
                        i = p.getComments().iterator();
                        while (i.hasNext()) {
                            CommentBean comment = new CommentBean();
                            comment = (CommentBean) i.next();
                %>
                <div class="commentBox">
                    <h3>Author: <%=comment.getCommentor()%></h3>
                    <p>Said: "<%=comment.getComment()%>"</p>
                    <p id="date">On :<%=comment.getCommentDate()%></p>
                    <a href="#" id="deleteComment<%=comment.getCommentID()%>" onclick="alertUser('This will delete this comment <%=comment.getCommentID()%>, are you sure?', 'deleteComment<%=comment.getCommentID()%>', '/Instagrim/DeleteComment/<%=user%>/<%=p.getSUUID()%>/<%=comment.getCommentID()%>');"><small>Delete</small></a>
                </div>
                <%
                        }
                    }%>
                <textarea form="commentsForm<%=id%>" placeholder="Enter comments here..." onfocus="visable(<%=id%>);" name="commentsBox<%=p.getSUUID()%>" id="Text<%=id%>" style="width:250px;height:50px;"></textarea>
                <br />
                <input type="hidden" name="picId" value="<%=p.getSUUID()%>">
                <input id="<%=id%>" style="display: none " type="submit" value="Comment" />
            </div>
        </form>
        <!--End of Comments-->
    </div>
    <%
            }//End of pictures
        }
    %>
        <footer>
                <p>&COPY; Andy C</p>
        </footer>
</body>
</html>
