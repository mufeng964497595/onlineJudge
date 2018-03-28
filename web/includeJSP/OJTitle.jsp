<%@page import="java.security.SecureRandom"%>
<%@page import="com.hwf.dao.ConnectionDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String account = (String)session.getAttribute("account");
	String isAdmin = (String)session.getAttribute("isAdmin");
%>
<nav class="navbar navbar-default" role="navigation" style="background: rgb(251, 251, 251);" >
	<div class="container-fluid">
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li id="contest"><a href="./contest.jsp?pageNow=1">Contest</a></li>
				<li id="status"><a href="./status.jsp?pageNow=1">Status</a></li>
				<li id="rankList"><a href="./ranklist.jsp?pageNow=1">Rank</a></li>
				<li id="faqs"><a href="./faqs.jsp">FAQS</a></li>
			</ul>
			
			<ul class="nav navbar-nav navbar-right">
				<li id="a" class="dropdown" onclick="showList(this)">
					<%if( account==null ){%>
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Login<span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu" style="width: 50px;text-align: left;">
						<li><a href="./loginpage.jsp">Login</a></li>
						<li><a href="./registerpage.jsp">Register</a></li>
					</ul>
					<%}else{%>
					<a href="#" class="dropdown-toggle" data-toggle="dropdown"><%=account%><span class="caret"></span></a>
					<ul class="dropdown-menu" role="menu" style="width: 50px;text-align: left;">
						<li><a href="./updateinfo.jsp">Update</a></li>
<!-- 						<li><a href="./applyaddtest.jsp">Apply Add Test</a></li> -->
						<li><a href="./logout">Logout</a></li>
					</ul>
					<%}%>
				</li>
			</ul>
			
			<% if( isAdmin!=null && isAdmin.equals("true") ){ %>
			<ul class="nav navbar-nav navbar-right">
				<li id="manaPro"><a href="./manageproblem.jsp?pageNow=1">Manage Problem</a></li>
			</ul>
			<%}%>
		</div>
	</div>
</nav>

<marquee id="broadcast" class="toprow" style="margin-top:10px;background: rgb(238, 238, 238);" scrollamount="1" scrolldelay="50" 
				onmouseover="this.stop()" onmouseout="this.start()">Welcome to SZUCPC Online Judge</marquee>
