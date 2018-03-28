<%@page import="java.security.SecureRandom"%>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="./css/bootstrap.min.css">
		<link rel="stylesheet" href="./css/problem.css">
		<link rel="stylesheet" href="./css/trumbowyg.css">
		<script type="text/javascript">
			var xmlHttp;
		</script> 
		<script src="./js/jquery-2.1.1.min.js"></script>
		<script src="./js/addProblem.js"></script>
		<script src="./js/initAJAX.js"></script>
		<script src="./js/trumbowyg.js"></script>
		<script src="./js/trumbowyg.base64.js"></script>
		<title>SZUCPC Online Judge</title>
		<%
			final String CHECK_SESSION = "checkSession";
			
			//产生随机数，用以验证是否为合法提交
			SecureRandom random;
			String randomString = "";
			try {
				random = SecureRandom.getInstance("SHA1PRNG");

				long seq = random.nextLong();
				randomString = seq+"";
				session.setAttribute(CHECK_SESSION,randomString+"LL");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			String account = (String)session.getAttribute("account");
			String isAdmin = (String)session.getAttribute("isAdmin");
			if( isAdmin==null || !isAdmin.equals("true") ){
				response.sendRedirect(request.getContextPath() + "/contest.jsp");
				return;
			}
		%>
	</head>
	<body>
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			<form action="<%=request.getContextPath()%>/addProblem" method="POST" name="addProblemForm">
			<div class="outer">
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Title:</label>
					<div class="col-md-10">
						<input id="inputTitle" type="text" class="size custom-select" name="title" onclick="setEmpty();">
					</div>
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Time limit(ms):</label>
					<div class="col-md-10">
						<input id="inputTime" type="text" class="size custom-select" name="time" value="1000" onclick="setEmpty();">
					</div> 
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row" onclick="setEmpty();"><font>*</font>Mem limit(MB):</label>
					<div class="col-md-10">
						<input id="inputMem" type="text" class="size custom-select" name="mem" value="128" onclick="setEmpty();">
					</div> 
				</div>
				<div class="row row2">
					<label class="col-md-2 col-form-label row">Origin:</label>
					<div class="col-md-10">
						<input id="inputOrigin" type="text" class="size custom-select" name="origin" value="<%=account%>" onclick="setEmpty();">
					</div> 
				</div>
				
				
				<div class="row row2">
					<p class="title3"><br><font>*</font>Description:</p>
					<div id="odiv1" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 400px;text-align: left;" id="description" class="editor"></div>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Input:</p>
					<div id="odiv2" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 400px;text-align: left;" id="inputFormat" class="editor"></div>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Output:</p>
					<div id="odiv3" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 400px;text-align: left;" id="outputFormat" class="editor"></div>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Sample Input:</p>
					<textarea class="form-control input" name="sampleInput" id="sampleInput" rows="8" placeholder="Enter your sample input." onclick="setEmpty();"></textarea>
				</div>
				<div class="row row2">
					<p class="title3"><br><font>*</font>Sample Output:</p>
					<textarea class="form-control input" name="sampleOutput" id="sampleOutput" rows="8" placeholder="Enter your sample output." onclick="setEmpty();"></textarea>
				</div>
				<div class="row row2">
					<p class="title3"><br>Hint:</p>
					<div id="odiv4" style="display:none;position: absolute;z-index: 100;"></div>
					<div onmousedown="show_element(event)" style="clear:both; height: 400px;text-align: left;" id="hint" class="editor"></div>
				</div>
			</div>
			<label style="color: red;" id="addProblemStatus"><br></label><br>
			<input id="addProblem" type="button" class="button" value="Submit" onclick="addPro();">
			<input type="hidden" id="checkCode" name="checkCode" value="<%=randomString%>">
			</form>
		</div>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		<input type="hidden" id="type" value="addPro">
	</body>
</html>