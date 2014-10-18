<%-- 
    Document   : UsersPics
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
        <title> Instagrim: <%=user%>'s Pics</title>
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
            <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li >
            <li class="nav"><a href="/Instagrim/Images/majed">Sample Images</a></li ><!--NEEDS DELT WITH-->
            <li><a href="/Instagrim"> Home </a></li>
        </ul>
    </nav>

    <h1> <%=user%>'s Pics </h1>
    <%
        //Check if the page is to display images for display or delete
        Boolean del = (Boolean) request.getAttribute("DeleteList");
        java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
        if (lsPics == null) {
            //If no pictures are found%>
    <p> No Pictures found </p>
    <%
    } else {
        if (del) {//If delete bool is set to true
            out.println("<h2>Please select a photo to delete:</h2>");
        }
        //Display all pics
        Iterator<Pic> iterator;
        iterator = lsPics.iterator();
        int id = 0;//Counter to provide unique photo id
        while (iterator.hasNext()) {
            id++;
            Pic p = (Pic) iterator.next();
            session.setAttribute("Pic", p);
            if (!del) {
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
    <%} else {%>
    <!--DISPLAY IMAGES FOR DELETE -->
    <div class="picture">
        <a id="<%=p.getSUUID()%>" method="DELETE" onclick="alertUser('This will delete this image, are you sure?', '<%=p.getSUUID()%>', '/Instagrim/Delete/<%=user%>/<%=p.getSUUID()%>');"><img alt="Instagrim User Image" src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><br/>
    </div>
    <!--End of Picture to delete-->
    <%}
            }//End of pictures
        }
    %>
    <footer>
        <ul>
            <li class="footer"> <a href="/Instagrim"> Home </a></li >
        </ul>
    </footer>
</body>
</html>
