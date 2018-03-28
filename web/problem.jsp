<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="com.hwf.bean.ProblemBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/component.css">
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/control.js"></script>
		<script type="text/javascript">
			function submitCode(){
				var cid = $("#cidHidden")[0].value;
				var pno = $("#pnoHidden")[0].value;
				<%
					String showType = request.getParameter("showType");
					if( showType==null || !showType.equals("0"))
						showType = "1";
				%>
				
				window.location.href = "./submitpage.jsp?cid="+cid+"&pno="+pno+"&showType=<%=showType%>";
			}
		</script>
		<title>SZUCPC Online Judge</title>
		
		<%
			String username = (String)session.getAttribute("account");
			String cid = request.getParameter("cid");
			String pno = request.getParameter("pno");
			
			if( username==null ){
				response.sendRedirect("./loginpage.jsp");
				return;
			}
			
			ProblemBean problem = GetInfoFromDatabaseDao.getProblem(cid, pno);
			boolean isSelectProblem = problem!=null;
			if( !isSelectProblem ){
				response.sendRedirect("./contest.jsp");
				return;
			}
			
			boolean isNeedPasswd = GetInfoFromDatabaseDao.isNeedPasswd(username, cid);
			boolean isStarted = GetInfoFromDatabaseDao.isStarted(cid);
			if( isNeedPasswd || !isStarted ){
				response.sendRedirect("./contest.jsp?cid="+cid+"&showType="+ showType);
				return;
			}
		%>
	</head>
	<body onload="setAlive();">
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			
			<div class="outer">
				<jsp:include page="includeJSP/problemsetTitle.jsp"></jsp:include>
				<center>
					<p class="title"><%=pno%> - <%=problem.getTitle()%></p>
					<p class="tip">Time limit: <%=problem.getTimeLimit()%> MS &nbsp; Memory limit: <%=problem.getMemLimit()%> MB</p>
				</center>
				
				<div class="content">
					<p><%=problem.getContent()%></p>
				</div>
				<p class="title2"><br>Input</p>
				<div class="content">
					<p><%=problem.getInputDesc()%></p>
				</div>
				<p class="title2"><br>Output</p>
				<div class="content">
					<p><%=problem.getOutputDesc()%></p>
				</div>
				<p class="title2"><br>Sample Input</p>
				<div class="content">
					<p><%=problem.getSampleInput()%></p>
				</div>
				<p class="title2"><br>Sample Output</p>
				<div class="content">
					<p><%=problem.getSampleOutput()%></p>
				</div>
				<p class="title2"><br>Hint</p>
				<div class="content">
					<p><%=problem.getHint()%></p>
				</div>
				<p class="title2"><br>Origin</p>
				<div class="content">
					<p><%=problem.getOrigin()%></p>
				</div>
				<br>

				<input type="hidden" id="cidHidden" value="<%=cid%>">				
				<input type="hidden" id="pnoHidden" value="<%=pno%>">
				<input id="submitCode" type="button" class="button" value="Submit" onclick="submitCode();">
				
			</div>
			
		</div>

		<script type="text/javascript">
			document.getElementById("con_problem").style.backgroundColor = "#EAEAEA";
		</script>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		<input type="hidden" id="type" value="contest">	
	</body>
</html>