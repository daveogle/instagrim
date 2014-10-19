<%-- 
    Document   : friends
    Created on : 15-Oct-2014, 16:47:00
    Author     : Dave Ogle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<%@page import="java.util.*"%>
<%@page import="javax.servlet.jsp.tagext.*"%>
<!DOCTYPE html>
<html>
    <head>
        <title>InstaGrim: Friends</title>
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
                String userName = "majed";
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                java.util.LinkedList<String> users = (java.util.LinkedList<String>) request.getAttribute("users");
                if (lg == null || !lg.getlogedin()) {%>
            <div class="homeMenu">
                <h3>Please register as a new user or login </h3>
                <ul> 
                    <li><a href="register.jsp">Register</a></li>
                    <li><a href="login.jsp">Login</a></li>  
                </ul>
                <%
                } else {
                    userName = lg.getUsername();%>
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
        <ul>
            <h1>Friends</h1>
            <li>You have no friends :(</li>
            <h1>Potential Friends:</h1>
        </ul>
        <form action="Friends" method="POST" name="selectAFriend">
            <strong>Instagim user:</strong>
            <select name="userList" form="selectAFriend">
                <option value="">Select a friend from list</option>
                <%  Iterator<String> iterator;
                    iterator = users.iterator();
                    while (iterator.hasNext()) {
                        String aUser = iterator.next();
                        if (!aUser.equals(userName)) {
                %>
                <option value="<%=aUser%>"><%=aUser%></option>
                <%
                        }
                    }%>
            </select>
            <input type="submit" value="Add Friend">
        </form>
        <footer>
            <ul>
                <li><a href="/Instagrim">Home</a></li>
                <li>&COPY; Andy C</li>
            </ul>
        </footer>
    </body>
</html>