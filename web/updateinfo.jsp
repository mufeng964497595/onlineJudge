<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
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
		<script type="text/javascript">
			var xmlHttp = false;
		</script> 
		<script src="js/control.js"></script>
		<script src="js/updateInfo.js"></script>
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/verify.min.js"></script>
		<title>SZUCPC Online Judge</title>
		
		<%
			String username = (String)session.getAttribute("account");
			if( username==null ){
				response.sendRedirect("./loginpage.jsp");
				return;
			}
			
			String nickname = GetInfoFromDatabaseDao.getNickname(username);
		%>
		
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
				<form action="<%=request.getContextPath()%>/updateInfo" method="POST" name="updateInfoForm">
				<div class="outer3">
					<div class="inner">
						<font class="register">*</font>
						<label class="name">Username: </label>
						<input name="username" class="text3 custom-select" type="text"  value="<%=username%>" onclick="setEmpty()" readonly>
					</div>
					<div class="inner">
						<font class="register">*</font>
						<label class="name">Old Password: </label>
						<input name="oldPassword" class="text3 custom-select" type="password" onclick="setEmpty()">
					</div>
					<div class="inner">
						<label class="name">New Password: </label>
						<input name="newPassword" class="text3 custom-select" type="password" placeholder="6~16 characters" onclick="setEmpty()">
					</div>
					<div class="inner">
						<label class="name">Repeat Password: </label>
						<input name="confirmPassword" class="text3 custom-select" type="password" placeholder="Repeat your new password" onclick="setEmpty()">
					</div>
					<div class="inner">
						<font class="register">*</font>
						<label class="name">Nickname: </label>
						<input name="nickname" class="text3 custom-select" type="text" value="<%=nickname%>" onclick="setEmpty()">
					</div>
					<div class="inner">
						<font class="register">*</font>
						<label class="name">Email: </label>
						<input name="email" class="text3 custom-select" type="text" onclick="setEmpty()">
					</div>
					
					<div id="mpanel" style="margin-left: 20%;"></div>
					<input type="hidden" id="verifyStatus" value="0">
					<label style="margin-top: 10px;" class="error" id="updateStatus"><br></label>
					
					<input type="hidden" id="checkCode" name="checkCode" value="<%=randomString%>">
					
					<div style="width: 80%;margin: 0 auto;">
						<input id="resetButton" type="button" class="button" style="float: left;" value="Reset" 
							onclick="reset();setEmpty();">
						<input id="updateInfo" type="button" class="button" style="float: right;" value="Update"
							onclick="updateInfo();">
					</div>
				</div>
			</form>
			</div>
		</div>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
	</body>
</html>