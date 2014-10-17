<%-- 
    Document   : account
    Created on : 12-Oct-2014, 16:57:43
    Author     : Dave Ogle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>InstaGrim: Account Page</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <script src="${pageContext.request.contextPath}/Scripts/alertBox.js" type="text/javascript"></script>
    </head>
    <body>
        <header>
            <h1>Welcome to InstaGrim!</h1>
            <h2>Your World In Black And White</h2>
        </header>
        <nav>              
            <%
               String UserName = "";
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
                   UserName = lg.getUsername();%>
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
        <h1>Accounts page</h1>
        <form method="POST" name="Account" onsubmit="">
            <ul>
                <li>User: <%=UserName%> </li>
                <li>First Name: <%=lg.getFirstName()%><a title="edit_first_name" href="#" onclick="editAccount('firstName');return false">Edit</a></li>
                <input id="firstName" type="text" style="display: none"><input id="firstNameButton" type="button" value="Update" style="display: none"><!--Do this for all then add to JS-->
                <li>Last Name : <%=lg.getFirstName()%><a href="#">edit</a></li>
                <input id="LastName" type="text" style="display: none">
                <li><strong>Address</strong><a href="#">edit</a> </li>
                <li>Street</li>
                <input id="Street" type="text" style="display: none">
                <li>City</li>
                <input id="City" type="text" style="display: none">
                <li>Post Code</li>
                <input id="PostCode" type="text" style="display: none">
            </ul>
        </form>
        </br>
        <a href="#"><img alt="User avatar picture" src=""></a>
        <footer>
            <a href="/Instagrim/">Home</a> 

        </footer>

    </body>
</html>
