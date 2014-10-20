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
        <script src="${pageContext.request.contextPath}/Scripts/javaScript.js" type="text/javascript"></script>
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
                    <li><a href="/Instagrim">Home</a></li>
                    <li><a href="/Instagrim/Register">Register</a></li>
                    <li><a href="/Instagrim/Login">Login</a></li>  
                </ul>
                <%
                } else {
                    UserName = lg.getUsername();%>
                <ul>
                    <li><a href="/Instagrim">Home</a></li>
                    <li><a href="/Instagrim/upload.jsp">Upload</a></li>
                    <li><a href="/Instagrim/Friends">Friends</a></li>    
                    <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">My Images</a></li>
                    <li><a href="/Instagrim/DeleteList/<%=lg.getUsername()%>">Delete Image</a></li>
                    <li><a href="/Instagrim/Logout/" id="lo" onclick="alertUser('Are you sure you want to logout?', 'lo', '/Instagrim/Logout')">Logout</a></li>
                </ul>
            </div>
        </nav>
        <h1>Accounts page</h1>
        <div class="details">
            <form method="POST" name="AccountUpdate" action="Account">
                <ul>
                    <li>User: <%=UserName%> </li>
                    <li>First Name: <%=lg.getFirstName()%>  (<a title="edit_first_name" href="#" onclick="editAccount('firstName');
                            return false">Edit</a>)</li>
                    <input id="firstName" name="firstName" value="<%=lg.getFirstName()%>" type="text" style="display: none"><input id="firstNameButton" type="submit" value="Update" style="display: none">
                    <li>Last Name : <%=lg.getLastName()%>   (<a title="edit_last_name" href="#" onclick="editAccount('lastName');
                            return false">Edit</a>)</li>
                    <input id="lastName" name="lastName" value="<%=lg.getLastName()%>" type="text" style="display: none"><input id="lastNameButton" type="submit" value="Update" style="display: none">
                    </br>
                    <li><strong>Address</strong>    (<a title="edit_account" href="#" onclick="editAccount('address');
                            return false">Edit</a>)</li>
                    <li><%=lg.getAddress()%></li>
                </ul>
                <div id="address" style="display: none">
                    <ul>
                        <li>Street</li>
                        <input name="Street" type="text" value="<%=lg.getStreet()%>">
                        <li>City</li>
                        <input name="City" type="text" value="<%=lg.getCity()%>">
                        <li>Post Code</li>
                        <input name="PostCode" type="text" value="<%=lg.getPostCode()%>"><input id="addressButton" type="submit" value="Update">
                    </ul>
                </div>
            </form>
        </div>
        <%
            }
        %>
        <footer>
                <p>&COPY; Andy C</p>
        </footer>
    </body>
</html>
