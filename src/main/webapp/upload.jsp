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
        <script src="${pageContext.request.contextPath}/Scripts/javaScript.js" type="text/javascript"></script>
        <title>InstaGrim :Upload</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
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
                    <li><a href="/Instagrim-dao">Home</a></li>
                    <li><a href="Instagrim/Register">Register</a></li>
                    <li><a href="Instagrim/Login">Login</a></li>  
                </ul>
                <%
                } else {
                %>
                <h3>Logged In As: <%=lg.getUsername()%>!</h3>
                <a href="/Instagrim-dao/Account"><img id="avatar" alt="User avatar picture" src="/Instagrim-dao/Avatar"></a><br/>
                <ul>
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
                <input type="submit" value="Upload">
            </form>
        </article>
    </body>
    <footer>
        <p>&COPY; Andy C</p>
    </footer>
</body>
</html>
