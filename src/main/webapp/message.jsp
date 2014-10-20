<%-- 
    Document   : message
    Created on : 10-Oct-2014, 14:57:21
    Author     : Dave Ogle
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.Message"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.servlets.Logout"%>
<!DOCTYPE html>

    <head>
        <title>Instagrim: Message</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <title>Message</title>
    </head>
    
        <header>
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>
        </header>
        <%Message message = (Message) request.getAttribute("message");%>
        <h1><%=message.getMessageTitle()%></h1><br>
        <p><%=message.getMessage()%></p> 
        <form method="GET" action="<%=message.getPageRedirect()%>">
            <input type="submit" value="<%=message.getPageRedirectName()%>">
        </form>
        <footer>
            <p>&COPY; Andy C</p>
        </footer>
    

