<%@page import="java.security.SecureRandom"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/createContest.css">
		<link rel="stylesheet" href="css/bootstrap-datetimepicker.css">
		<link rel="stylesheet" href="css/trumbowyg.css">
		<script type="text/javascript">
			var now = 'A';
		</script>
		<script src="js/control.js"></script>
		<script src="js/initAJAX.js"></script>
		<script src="js/createContest.js"></script>
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script src="js/moment-with-locales.js"></script>
		<script src="js/bootstrap-datetimepicker.js"></script>
		<script src="js/trumbowyg.js"></script>
		<script src="js/trumbowyg.base64.js"></script>
		<title>SZUCPC Online Judge</title>
		
		<%
			String account = (String)session.getAttribute("account");
			String isAdmin = (String)session.getAttribute("isAdmin");
			
			if( isAdmin==null || !isAdmin.equals("true") ){
				response.sendRedirect(request.getContextPath() + "/contest.jsp");
				return;
			}
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
			
			<form action="./createContest" method="POST" name="createForm">
			<div class="outer" style="text-align: left;padding: 10px;">
				<div class="row row2">
					<label class="col-md-2 col-form-label"><font>*</font>Title:</label>
					<div class="col-md-10">
						<input name="title" type="text" class="full custom-select" onclick="setEmpty();">
					</div> 
				</div>
				
				<div class="row row2">
					<label class="col-md-2 col-form-label"><font>*</font>Start Time:</label>
					<div class='col-md-10'>
				        <div class="form-group">
				            <div class='input-group date' id='datetimepicker1'>
				                <input type='text' name="startTime" class="form-control"/>
				                <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
				                </span>
				            </div>
				        </div>
				    </div>
				</div>
				
				<div class="row row2">
					<label class="col-md-2 col-form-label"><font>*</font>End Time:</label>
					<div class='col-md-10'>
				        <div class="form-group">
				            <div class='input-group date' id='datetimepicker2'>
				                <input type='text' class="form-control" name="endTime" />
				                <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
				                </span>
				            </div>
				        </div>
				    </div>
				</div>
				
				<div class="row row2">
					<label for="submit-share" class="col-md-2 col-form-label"><font>*</font>Private:</label>
					<div class="col-md-10">
					    <div class="btn-group" data-toggle="buttons" id="submit-share">
					        <label class="btn btn-secondary active selected" onclick="change(this);setEmpty();">
					            <input value="1" autocomplete="off" type="hidden">Yes
					        </label>
					        <label class="btn btn-secondary" onclick="change(this);setEmpty();">
					            <input value="0" autocomplete="off" type="hidden">No
					        </label>
					        <input type="hidden" name="priv" value="1">
					    </div>
					</div>
				</div>
				
				<div id="passwdDiv" class="row row2" style="display: block;">
					<label class="col-md-2 col-form-label"><font>*</font>Password:</label>
					<div class="col-md-10">
						<input name="password" type="password" class="full custom-select" onclick="setEmpty();">
					</div> 
				</div>
				
				<div class="row row2">
					<label class="col-md-2 col-form-label">Hint:</label>
					<div class="col-md-10">
                        <div id="odiv1" style="display:none;position: absolute;z-index: 100;"></div>
						<div onmousedown="show_element(event)" style="clear:both; height: 100px;text-align: left;" id="hint" class="editor"></div>
                    </div>
                </div>
				
				<div class="row row2">
                    <%--@declare id="submit-solution"--%><label for="submit-solution" class="col-md-2 col-form-label"><font>*</font>Problem Set:</label>
                    <div class="col-md-10">
                    	<div id="proSet" style="border: 1px solid;border-color: rgb(217,217,217);border-radius: 5px;padding-bottom: 5px;">
	                    	<div style="padding-left: 20px;">
	                    		<a href="javascript:addPro();"><img height="18" src="picture/icon_add.png" border="0"></a>
	                    		<label style="margin-left: 20px; width: 100px;">Problem ID</label>
	                    		<label style="margin-left: 30px;">Title</label>
	                    	</div>
                    	</div>
                    	<label id="errorProSet" class="error"><br></label>
                    </div>
                </div>
                
				<div class="modal-footer" style="margin: 0 auto;margin-top: 20px; width: 95%;">
	              	<label id="hintLabel" class="hint"></label>
	                <input type="button" class="btn btn-primary" id="btn-submit" value="Submit" onclick="creaCont();">
	            </div>
			</div>
			<input type="hidden" name="creator" value="<%=account%>">
			<input type="hidden" name="checkCode" value="<%=randomString%>">
			</form>
		</div>
	
		<input type="hidden" id="type" value="creaCont">
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
	</body>
	
	<script type="text/javascript">
	    $(function () {
	        $('#datetimepicker1').datetimepicker({locale: 'en-GB',format: 'YYYY-MM-DD HH:mm:00',minDate: new Date()});
	        $('#datetimepicker2').datetimepicker({locale: 'en-GB',format: 'YYYY-MM-DD HH:mm:00'});
	        $("#datetimepicker1").on("dp.change",function (e) {
	            $('#datetimepicker2').data("DateTimePicker").minDate(e.date);
	        });
	        $("#datetimepicker2").on("dp.change",function (e) {
	            $('#datetimepicker1').data("DateTimePicker").maxDate(e.date);
	        });
	    });
	</script>
</html>