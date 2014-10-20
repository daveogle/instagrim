<%-- 
    Document   : usersPics
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
                <li><a href="/Instagrim"> Home </a></li>
                <li><a href ="/Instagrim/Comments/<%=user%>">Show Comments</a></li>
                <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                <li><a href="/Instagrim/Friends">Friends</a></li>    
                <li><a href="/Instagrim/DeleteList/<%=user%>">Delete Image</a></li>
                <li><a href="/Instagrim/account.jsp">My Account</a></li>
                <li><a href="/Instagrim/Logout/" id="lo" onclick="alertUser('Are you sure you want to logout?', 'lo', '/Instagrim/Logout')">Logout</a></li>
            </ul>
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
        <p>&COPY; Andy C</p>
    </footer>
</body>
</html>
