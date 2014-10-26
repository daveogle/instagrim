<%-- 
    Document   : login.jsp
    Created on : Sep 28, 2014, 12:04:14 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="${pageContext.request.contextPath}/Scripts/javaScript.js" type="text/javascript"></script>
        <title>InstaGrim: Login</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />

    </head>
    <body>
        <% boolean registered = false;
        //Check if the user has registered
            if (request.getAttribute("registered") != null) {
                registered = (boolean) request.getAttribute("registered");
            }%>
        <p></p>
        <header>
            <%if (registered) {%>
            <h1>Congratulations on registering to Instagrim!</h1>
            <h2>Please sign in below using your username and password!</h2>
            <%} else {%>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
            <%}%>
        </header>
        <nav>
            <ul>               
                <li><a href="/Instagrim-dao">Home</a></li>
                <li><a href="/Instagrim-dao/Register">Register</a></li>
                <li><a href="/Instagrim-dao/Images/Sample">Sample Images</a></li>
            </ul>
        </nav>

        <article>
            <h3>Login</h3>
            <form method="POST" name="login" onsubmit="return validateForm('login', 'username', 'password')" action="Login">
                <% boolean notAUser = false;
                //Check if the person logging in is a user already
                    if (request.getAttribute("notAUser") != null) {
                        notAUser = (boolean) request.getAttribute("notAUser");
                    }
                    if (notAUser) {%>
                    <p style="color:red"><b>That username does not exist, please try again or <a href="/Instagrim-dao/Register">register</a> a new user... </b></p> 
                <%}%>
                <ul>
                    <li>User Name <input type="text" name="username"></li>
                    <li>Password <input type="password" name="password"></li>
                </ul>
                <br/>
                <input type="submit" value="Login"> 
            </form>

        </article>
        <footer>
                <p>&COPY; Andy C</p>
        </footer>
    </body>
</html>
