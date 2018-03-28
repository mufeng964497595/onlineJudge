<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    %>
    

<div class="center" style="margin-top: 20px;"><label>SZUCPC Online Judge @ 2017 by hwf</label><br>
	<label>Server Time:</label>
	<span id="nowdate"></span><br>
</div>
<script src="./js/showTime.js"></script>
<script>
	var diff=new Date("<%=sdf.format(new Date())%>").getTime()-new Date().getTime();
	clock();
</script>