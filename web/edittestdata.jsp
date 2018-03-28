<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="java.security.SecureRandom"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/content.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/component.css">
		<link rel="stylesheet" href="css/fileInput.css">
		<script type="text/javascript">
			var xmlHttp;
		</script>
		<script src="js/initAJAX.js"></script>
		<script src="js/jquery-3.2.1.min.js"></script>
		<script src="js/control.js"></script>
		<script src="js/editTestData.js"></script>
		<title>SZUCPC Online Judge</title>
		<%
			String isAdmin = (String)session.getAttribute("isAdmin");
			String pid = request.getParameter("pid");
			if( isAdmin==null || !isAdmin.equals("true") || pid==null ){
				response.sendRedirect("./contest.jsp");
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
			String account = (String)session.getAttribute("account");
		%>
	</head>
	<body>
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			
			<div class="outer">
				<table class="table table-striped" style="margin: 0 auto; width: 95%; text-align: center;background: rgb(238, 238, 238);border: 1px solid #DDDDDD;margin-top: 10px;"> 
					<thead>
						<tr class="toprow" align="center">
							<th class="center" width="10%" style="text-align: center;">
								<a href="javascript:" class="md-trigger" data-modal="modal-2"><img height="18" src="picture/icon_add.png" border="0"></a>
							</th>
							<th style="text-align: center;">Input Data</th>
							<th style="text-align: center;">Output Data</th>
						</tr>
					</thead>
					<tbody>
						<%
							ArrayList<Integer> list = GetInfoFromDatabaseDao.getInputOutputDataId(pid);
							for( Integer id : list ){
						%>
						<tr>
							<th><a href="javascript:delTitle(<%=id%>)" class="md-trigger" data-modal="modal-4"><img height="18" src="picture/icon_sub.png" border="0"></a></th>
							<th>
								<a href="javascript:showData('<%=id%>','in')" class="md-trigger" data-modal="modal-1" style="padding-right: 10px;">View</a>
								<a href="javascript:updateTitle('<%=id%>','input','Update Input Data','Input Data File:')" class="md-trigger" data-modal="modal-3" style="padding-left: 10px;">Change</a>
							</th>
							<th>
								<a href="javascript:showData('<%=id%>','out')" class="md-trigger" data-modal="modal-1" style="padding-right: 10px;">View</a>
								<a href="javascript:updateTitle('<%=id%>','output','Update Output Data','Output Data File:')" class="md-trigger" data-modal="modal-3"  style="padding-left: 10px;">Change</a>
							</th>
						</tr>
						<%}%>
					</tbody>
				</table>
			</div>
		</div>
		
		<div class="md-modal md-effect-1" id="modal-1">
			<div class="md-content">
				<h3>Data</h3>
				<div>
					<textarea class="form-control" id="dataText" style="margin: 0 auto;width: 70%;height: 500px;resize: none" readonly></textarea><br>
					<button class="md-close btn-sm btn-primary">Close</button>
				</div>
			</div>
		</div>
		
		<div class="md-modal md-effect-2" id="modal-2">
			<div class="md-content">
				<h3>Upload New Data</h3>
				
				<form method="POST" action="./editTestData?checkCode=<%=randomString%>&fileType=add&pid=<%=pid%>"
					enctype="multipart/form-data" name="addDataForm">
					<div class='uploader white' style="margin: 0 auto;text-align: center;">
						<h5 style="float: left; margin-right: 10px;">Input Data File:</h5>
						<input type="button" name="file" class="file-button" value="Browse..."/>
						<input type="text" class="filename" id="inputName" readonly/>
						<input type="file" name="inputFile" style="width: 30%;float: right;"/>
					</div>
					<br>
					<div class='uploader white' style="margin: 0 auto;text-align: center;">
						<h5 style="float: left; margin-right: 10px;">Output Data File:</h5>
						<input type="button" name="file" class="file-button" value="Browse..."/>
						<input type="text" class="filename" id="outputName" readonly/>
						<input type="file" name="outputFile" style="width: 30%;float: right;"/>
					</div>
				</form>
				<br>
				<label class="error" id="addLabel" ><br></label><br>
				<button class="md-close btn-sm btn-primary" style="margin-bottom: 10px;margin-right: 30px;">Close</button>
				<button class="md-close btn-sm btn-primary" style="margin-bottom: 10px;margin-left: 30px;" onclick="addData();">Upload</button>
			</div>
			
		</div>
		
		<div class="md-modal md-effect-3" id="modal-3">
			<div class="md-content">
				<h3 id="h3"></h3>
				
				<form method="POST" action="./editTestData?checkCode=<%=randomString%>&pid=<%=pid%>"
					enctype="multipart/form-data" name="updateDataForm">
					<div class='uploader white' style="margin: 0 auto;text-align: center;">
						<h5 id="h5" style="float: left; margin-right: 10px;"></h5>
						<input type="button" name="file" class="file-button" value="Browse..."/>
						<input type="text" class="filename" id="fileName" readonly/>
						<input type="file" name="file" style="width: 30%;float: right;"/>
					</div>
				</form>
				<br>
				<label class="error" id="updateLabel" ><br></label><br>
				<button class="md-close btn-sm btn-primary" style="margin-bottom: 10px;margin-right: 30px;">Close</button>
				<button class="md-close btn-sm btn-primary" style="margin-bottom: 10px;margin-left: 30px;" onclick="updateData();">Upload</button>
			</div>
			
		</div>
		
		<div class="md-modal md-effect-3" id="modal-4">
			<div class="md-content">
				<h3>Delete?</h3>
				<p>Are you sure you want to delete?</p>
				
				<form action="./editTestData?op=delete&pid=<%=pid%>&checkCode=<%=randomString%>" method="POST" name="deleteDataForm">
					<button class="md-close btn-sm btn-primary" style="margin-bottom: 10px;margin-right: 30px;">Cancel</button>
					<button class="md-close btn-sm btn-primary" style="margin-bottom: 10px;margin-left: 30px;" onclick="submit">Delete</button>
				</form>
			</div>
		</div>
		
		<div class="md-overlay"></div>
		
		<script src="js/classie.js"></script>
		<script src="js/modalEffects.js"></script>
		
		<input type="hidden" id="checkCode" value="<%=randomString%>">
		
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		<input type="hidden" id="type" value="manaPro">
	
	</body>
	<script>
		var noFileSelected = "No file selected...";
		
		$(function(){
			$("input[type=file]").change(function(){$(this).parents(".uploader").find(".filename").val($(this).val());});
			$("input[type=file]").each(function(){
			if($(this).val()==""){$(this).parents(".uploader").find(".filename").val(noFileSelected);}
			});
		});
	</script>
</html>