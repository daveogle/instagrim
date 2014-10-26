<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="${pageContext.request.contextPath}/Scripts/javaScript.js" type="text/javascript"></script>
        <title>InstaGrim: Register</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
    </head>
    <body>
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <nav>
            <ul>                
                <li><a href="/Instagrim-dao">Home</a></li>
                <li><a href="/Instagrim-dao/Login">Login</a></li>
                <li><a href="/Instagrim-dao/Images/Sample">Sample Images</a></li>
            </ul>
        </nav>

        <article>
            <h3>Register a new user</h3>
            <form name="register" onsubmit="return validateForm('register', 'username', 'password')" method="POST"  action="Register">
                <% //Check if the user alread exists
                    boolean userExists = false;
                    if (request.getAttribute("exists") != null) {
                        userExists = (boolean) request.getAttribute("exists");
                    }
                    if (userExists) {%>
                <p style="color:red"><b>That name already taken, please choose another... </b></p> 
                <%}%>
                <ul>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                </ul>
                <br/>
                <input type="submit" value="Register"> 
            </form>
        </article>
        <footer>
            <p>&COPY; Andy C</p>
        </footer>
    </body>
</html>
