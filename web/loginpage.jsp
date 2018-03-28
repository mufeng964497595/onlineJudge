<%@page import="java.security.SecureRandom"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/content.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/loginAndRegister.css">
		<link rel="stylesheet" href="css/verify.css">
		<script src="js/control.js"></script>
		<script src="js/login.js"></script>
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/verify.min.js"></script>
		<script type="text/javascript">
			var xmlHttp = false;
		</script> 
		<title>SZUCPC Online Judge</title>
		
		<%
			final String CHECK_SESSION = "checkSession";
			
			//产生随机数，用以验证是否为合法提交
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			long seq = random.nextLong();
			String randomString = seq+"";
			session.setAttribute(CHECK_SESSION,randomString+"LL");
		%>
	</head>
	<body>
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			
			<div class="outer"> 
			<form action="<%=request.getContextPath()%>/login" method="POST" name="loginForm">
				<div class="outer2">
					<div class="inner">
						<img class="icon" src="picture/loginpage/icon_user.png">
						<input class="text1 custom-select" name="username" id="usernameId" type="text" placeholder="Please enter your username" onclick="setEmpty();" 
							value="" >
					</div><br>
					<div class="inner">
						<img class="icon" src="picture/loginpage/icon_passwd.png">
						<input class="text1 custom-select" name="password" id="passwordId" type="password" placeholder="Please enter your password" onclick="setEmpty();"
							 value="" >
					</div><br>
					<br>
					<div id="mpanel" style="margin-left: 20%;"></div>
					<input type="hidden" id="verifyStatus" value="0"><br>
					
					<div class="inner">
						<input type="checkbox" name="saveUsername" value="saveUsername">Remember username
						<input type="checkbox" name="autoLogin" value="autoLogin" style="margin-left: 20px;">Auto login(a week)
					</div>
					<label class="error" id="loginStatus"><br></label>
					<input type="hidden" id="checkCode" name="checkCode" value="<%=randomString%>">
					
					<div style="width: 85%;margin: 0 auto;">
						<input class="button" type="button" onclick="forgot()" value="Forgot password" style="float: left;">
						<input class="button" type="button" onclick="login()" value="Login" style="float: right;">
					</div>
					<a class="link" href="registerpage.jsp">Click to register</a>
				</div>
			</form>
			</div>
		</div>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		
	</body>
</html>