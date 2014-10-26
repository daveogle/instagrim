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
        <title>InstaGrim: Home</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="${pageContext.request.contextPath}/Scripts/javaScript.js" type="text/javascript"></script>
    </head>
    <body>
        <header>
            <h1>Welcome to InstaGrim!</h1>
            <h2>Your World In Black And White</h2>
        </header>
        <nav>              
            <%
                //Check if the use is logged in
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                if (lg == null || !lg.getlogedin()) {%>
            <div class="homeMenu">
                <h3>Please register as a new user or login </h3>
                <ul> 
                    <li><a href="/Instagrim-dao/Register">Register</a></li>
                    <li><a href="/Instagrim-dao/Login">Login</a></li>  
                    <li><a href="/Instagrim-dao/Images/Sample">Sample Images</a></li>
                </ul>
                <%
                } else {%>
                <h3>Welcome back <%=lg.getUsername()%>!</h3>
                <a href="/Instagrim-dao/Account"><img id="avatar" alt="User avatar picture" src="/Instagrim-dao/Avatar"></a><br/>
                <ul>
                    <li><a href="/Instagrim-dao/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim-dao/Friends">Friends</a></li>    
                    <li><a href="/Instagrim-dao/Images/<%=lg.getUsername()%>">My Images</a></li>
                    <li><a href="/Instagrim-dao/DeleteList/<%=lg.getUsername()%>">Delete Image</a></li>
                    <li><a href="/Instagrim-dao/Account">My Account</a></li>
                    <li><a href="/Instagrim-dao/Logout/" id="lo" onclick="alertUser('Are you sure you want to logout?', 'lo', '/Instagrim-dao/Logout')">Logout</a></li>
                </ul>
            </div>
            <%
                }
            %>
        </nav>
        <footer>
            <p>&COPY; Andy C</p>
        </footer>
    </body>
</html>
