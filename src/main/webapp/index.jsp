<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>InstaGrim</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="${pageContext.request.contextPath}/Scripts/alertBox.js" type="text/javascript"></script>
    </head>
    <body>
        <header>
            <h1>Welcome to InstaGrim!</h1>
            <h2>Your World In Black And White</h2>
        </header>
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
                <h3>Welcome back <%=lg.getUsername()%>!</h3>
                <ul>
                    <li><a href="/Instagrim/upload.jsp">Upload</a></li>
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
        <footer>
            <ul>
                <li><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>
