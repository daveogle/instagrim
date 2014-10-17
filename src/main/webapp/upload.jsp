<%-- 
    Document   : upload
    Created on : Sep 22, 2014, 6:31:50 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>InstaGrim :Upload</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
    </head>
    <nav>
        <%
           LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
           if (lg == null || !lg.getlogedin()) {%>
        <div class="homeMenu">
            <h3>Please register as a new user or login </h3>
            <ul> 
                <li><a href="register.jsp">Register</a></li>
                <li><a href="login.jsp">Login</a></li>  
            </ul>
            <%
            } else {
               String UserName = lg.getUsername();%>
            <ul>
                <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">My Images</a></li>
                <li><a href="/Instagrim/DeleteList/<%=lg.getUsername()%>">Delete Image</a></li>
                <li><a href="/Instagrim/account.jsp">My Account</a></li>
                <li><a href="/Instagrim/logout/" id="lo" onclick="alertUser('Are you sure you want to logout?', 'lo', '/Instagrim/logout')">Logout</a></li>
            </ul>
        </div>
        <%
           }
        %>
    </nav>
    <article>
        <% boolean uploaded = false;
           if (request.getAttribute("added") != null) {
              uploaded = (boolean) request.getAttribute("added");
           }
           if (uploaded) {%>
        <h3>Congratulations, Your file was added!</h3>
        <%} else {%>
        <h3>Upload a File</h3>
        <%}%>
        <form method="POST" enctype="multipart/form-data" action="Image">
            <%if (uploaded) {%>
            <p>Select another file to upload</p>
            <%} else {%>
            <p>Select a file to upload:</p>
            <%}%>
            <input type="file" name="upfile">
            <br/>
            <br/>
            <input type="submit" value="Press"> <p>to upload the file!</p>
        </form>
    </article>

    <footer>
        <ul>
            <li class="footer"><a href="/Instagrim">Home</a></li>
        </ul>
    </footer>
</body>
</html>
