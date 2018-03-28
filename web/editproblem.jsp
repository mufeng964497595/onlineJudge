<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="com.hwf.bean.ProblemBean"%>
<%@page import="java.security.SecureRandom"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/trumbowyg.css">
		<script type="text/javascript">
			var xmlHttp;
		</script> 
		<script src="js/control.js"></script>
		<script src="js/editProblem.js"></script>
		<script src="js/initAJAX.js"></script>
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/trumbowyg.js"></script>
		<script src="js/trumbowyg.base64.js"></script>
		<title>SZUCPC Online Judge</title>
		<%
			final String CHECK_SESSION = "checkSession";
			
			//产生随机数，用以验证是否为合法提交
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			long seq = random.nextLong();
			String randomString = seq+"";
			session.setAttribute(CHECK_SESSION,randomString+"LL");
			
			String account = (String)session.getAttribute("account");
			String isAdmin = (String)session.getAttribute("isAdmin");
			String pid = request.getParameter("pid");
			
			if( isAdmin==null || !isAdmin.equals("true") || pid==null ){
				response.sendRedirect(request.getContextPath() + "/contest.jsp");
				return;
			}
			
			ProblemBean problem = GetInfoFromDatabaseDao.getProblem(pid);
		%>
	</head>
	<body>
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			
			<form action="<%=request.getContextPath()%>/editProblem" method="POST" name="editProblemForm">
			<div class="outer">
				<%
					if( problem==null ){
						out.print("No such pid!");
					}else{
				%>
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Pid:</label>
					<div class="col-md-10">
						<input id="inputPid" type="text" class="size custom-select" name="pid" onclick="setEmpty();" value="<%=problem.getPid()%>" readonly style="background-color: #EAEAEA;">
					</div>
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Title:</label>
					<div class="col-md-10">
						<input id="inputTitle" type="text" class="size custom-select" name="title" onclick="setEmpty();" value="<%=problem.getTitle()%>">
					</div>
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Time limit(ms):</label>
					<div class="col-md-10">
						<input id="inputTime" type="text" class="size custom-select" name="time" value="<%=problem.getTimeLimit()%>" onclick="setEmpty();">
					</div> 
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Mem limit(MB):</label>
					<div class="col-md-10">
						<input id="inputMem" type="text" class="size custom-select" name="mem" value="<%=problem.getMemLimit()%>" onclick="setEmpty();">
					</div> 
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row">Origin:</label>
					<div class="col-md-10">
						<input id="inputOrigin" type="text" class="size custom-select" name="origin" value="<%=problem.getOrigin()%>" onclick="setEmpty();">
					</div> 
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Description:</p>
					<div id="odiv1" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 400px;text-align: left;" id="description" class="editor"><%=problem.getContent()%></div>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Input:</p>
					<div id="odiv2" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 100px;text-align: left;" id="inputFormat" class="editor"><%=problem.getInputDesc()%></div>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Output:</p>
					<div id="odiv3" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 100px;text-align: left;" id="outputFormat" class="editor"><%=problem.getOutputDesc()%></div>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Sample Input:</p>
					<textarea class="form-control input" name="sampleInput" id="sampleInput" rows="8" onclick="setEmpty();"><%=problem.getSampleInput()%></textarea>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Sample Output:</p>
					<textarea class="form-control input" name="sampleOutput" id="sampleOutput" rows="8" onclick="setEmpty();"><%=problem.getSampleOutput()%></textarea>
				</div>
				<div class="row row2">
					<p class="title3"><br>Hint:</p>
					<div id="odiv4" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 100px;text-align: left;" id="hint" class="editor"><%=problem.getHint()%></div>
				</div>
				<%}%>
			</div>
			<label style="color: red;" id="editProblemStatus"><br></label><br>
			<input id="editProblem" type="button" class="button" value="Submit" onclick="editPro();">
			<input type="hidden" id="checkCode" name="checkCode" value="<%=randomString%>">
			<input type="hidden" id="type" value="manaPro">
			</form>
		</div>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
	</body>
</html>