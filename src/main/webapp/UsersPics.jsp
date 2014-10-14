<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>
<%@page import="java.util.*"%>
<%@page import="javax.servlet.jsp.tagext.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <script src="${pageContext.request.contextPath}/Scripts/alertBox.js" type="text/javascript"></script>
    </head>

    <header>
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
    </header>

    <nav>
        <ul>
            <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li >
            <li class="nav"><a href="/Instagrim/Images/majed">Sample Images</a></li ><!--NEEDS DELT WITH-->
        </ul>
    </nav>

    <% String user = (String) request.getAttribute("User");%>

    <h1> <%=user%>'s Pics </h1>
    <%
       Boolean del = (Boolean) request.getAttribute("DeleteList");
       java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
       if (lsPics == null) {
    %>
    <p> No Pictures found </p>
    <%
    } else {
       if (del) {//If delete bool is set to true
          out.println("<h2>Please select a photo to delete:</h2>");
       }
       Iterator<Pic> iterator;
       iterator = lsPics.iterator();
       int id = 0;
       while (iterator.hasNext()) {
          id++;
          Pic p = (Pic) iterator.next();
          session.setAttribute("Pic", p);
    %>      
    <%session.setAttribute("User", user);
       if (!del) {
    %>
    <!-- DISPLAY IMAGES FOR VIEWING -->
    <a href="/Instagrim/Image/<%=p.getSUUID()%>"><img alt="User instagrim picture" src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><br/>

    <form action="/Instagrim/Comment/" id="commentsForm" name ="comments" method="POST">
        Comments:<br />
        <textarea form="commentsForm" placeholder="Enter comments here..." onfocus="visable(<%=id%>);" name="commentsbox" id="comments" style="width:250px;height:50px;"></textarea>
        <br />
        <input id="<%=id%>" style="display: none " type="submit" value="Comment" />
    </form>
    <%} else {%>
    <!--DISPLAY IMAGES FOR DELETE -->
    <a id="<%=p.getSUUID()%>" onclick="alertUser('This will delete this image, are you sure?', '<%=p.getSUUID()%>', '/Instagrim/Delete/<%=p.getSUUID()%>');"><img alt="Instagrim User Image" src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><br/>
        <%}
              }
           }
        %>

    <footer>
        <ul>
            <li class="footer"> <a href="/Instagrim"> Home </a></li >
        </ul>
    </footer>

</html>
