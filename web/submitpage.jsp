<%@page import="com.hwf.util.SelectOptionValueUtil"%>
<%@page import="java.security.SecureRandom"%>
<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/verify.css">
		<script type="text/javascript">
			var xmlHttp;
		</script>
		
		<script src="js/control.js"></script>
		<script src="js/initAJAX.js"></script>
		<script src="js/jquery-3.2.1.min.js"></script>
		<script src="js/verify.min.js"></script>
		<script src="js/submitCode.js"></script>
		<title>SZUCPC Online Judge</title>
		
		<%
			String username = (String)session.getAttribute("account");
			if( username==null || username.equals("") ){
				response.sendRedirect("./loginpage.jsp");
				return;
			}
			
			String cid = request.getParameter("cid");
			String pno = request.getParameter("pno");
			
			boolean isNeedPasswd = GetInfoFromDatabaseDao.isNeedPasswd(username, cid);
			boolean isStarted = GetInfoFromDatabaseDao.isStarted(cid);
			if( isNeedPasswd || !isStarted ){
				response.sendRedirect("./contest.jsp?cid="+cid+"&showType=1");
				return;
			}
			
			String title = GetInfoFromDatabaseDao.getProblemTitle(cid,pno);
			
			if( title==null ){
				response.sendRedirect("./contest.jsp");
				return;
			}
			
			String showType = request.getParameter("showType");
			if( showType==null || !showType.equals("0") )
				showType = "1";
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
			<form action="./submitCode" method="POST" name="submitCodeForm">
			<div class="outer" style="text-align: left;padding: 10px;">
				<div class="row row2">
<!-- 					<label class="col-md-2 col-form-label">Problem:</label> -->
<!-- 					<div class="col-md-10"> -->
<!-- 						<select id="submit-language" class="size custom-select" name="pno"> -->
<!-- 							<option value="0">A - Title</option> -->
<!-- 						</select> -->
<!-- 					</div>  -->
					<label class="col-md-2 col-form-label">Problem:</label>
					<div class="col-md-10">
						<input class="size custom-select" name="pno" readonly value="<%=pno%> - <%=title%>">
					</div> 
				</div>
				
				<div class="row row2">
					<label class="col-md-2 col-form-label">Language:</label>
					<div class="col-md-10">
						<select id="submit-language" class="size custom-select" name="language">
							<% 	String languages[] = SelectOptionValueUtil.getLanguages();
								for( int i=1;i<languages.length;++i ){
							%>
							<option value="<%=i%>"><%=languages[i]%></option>
							<%}%>
						</select>
					</div>
				</div>
				<div class="row row2">
					<label for="submit-share" class="col-md-2 col-form-label">Share:</label>
					<div class="col-md-10">
					    <div class="btn-group" data-toggle="buttons" id="submit-share">
					        <label class="btn btn-secondary active" onclick="change(this);">
					            <input value="1" autocomplete="off" type="hidden">Yes
					        </label>
					        <label class="btn btn-secondary" onclick="change(this);">
					            <input value="0" autocomplete="off" type="hidden">No
					        </label>
					        <input type="hidden" id="share" value="1">
					    </div>
					</div>
				</div>
				<div class="row row2">
                    <label for="submit-solution" class="col-md-2 col-form-label">Solution:</label>
                    <div class="col-md-10">
                        <textarea class="form-control input" id="code" id="submit-solution" rows="15" placeholder="At least 50 characters" onclick="setEmpty();"></textarea>
                    </div>
                </div>
				<div class="modal-footer" style="margin: 0 auto;margin-top: 20px; width: 95%;">
					<div style="margin: 0 auto;width: 20%;">
					<div id="mpanel"></div>
					<input type="hidden" id="verifyStatus" value="0"><br>
					</div>
	              	<label id="hint" class="hint"></label>
	                <input type="button" class="btn btn-primary" id="btn-submit" value="Submit"  style="float: right;" onclick="submitCode();">
	            </div>
			</div>
			
			<input type="hidden" id="cidHidden" value="<%=cid%>">
			<input type="hidden" id="pnoHidden" value="<%=pno%>">
			<input type="hidden" id="username" value="<%=username%>">
			<input type="hidden" id="showType" value="<%=showType%>">
			<input type="hidden" name="checkCode" value="<%=randomString%>">
			</form>
		</div>
		
		<script type="text/javascript">
			$("#submit-language").val("2");
		</script>
		
		<input type="hidden" id="type" value="contest">	
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
	</body>
	
</html>