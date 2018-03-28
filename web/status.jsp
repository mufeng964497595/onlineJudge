<%@page import="java.net.URLDecoder"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.hwf.bean.ContestListBean"%>
<%@page import="java.security.SecureRandom"%>
<%@page import="com.hwf.util.SelectOptionValueUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="com.hwf.dao.CheckParamDao"%>
<%@page import="com.hwf.bean.StatusBean"%>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="javax.xml.parsers.ParserConfigurationException" %>
<%@ page contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/content.css">
		<link rel="stylesheet" href="css/resultStyle.css">
		<link rel="stylesheet" href="css/component.css">
		<script type="text/javascript">
			var xmlHttp;
			
			<%
				String queryString = request.getQueryString();
				if( queryString==null ){
					queryString = "";
				}else if(queryString.contains("pageNow") ){
					queryString = queryString.substring(0,queryString.indexOf("pageNow"));
				}
				String url = request.getRequestURI()+"?"+queryString;
			%>
			
			function lastPage(){
				<%-- 上一页 --%>
				var _pageNow = document.getElementById("_pageNow");
				var string = _pageNow.value;
				var pageNow = Number(string);
				
				if( pageNow===1 ){
					var pageError = document.getElementById("pageError");
					
					pageError.innerText = "当前已是第一页";
				}else{
					pageNow--;
					window.location.href = "<%=url%>&pageNow="+pageNow;
				}
			}
			
			function nextPage(){
				<%-- 下一页 --%>
				var _pageNow = document.getElementById("_pageNow");
				var _totalPage = document.getElementById("_totalPage");
				var string1 = _pageNow.value;
				var pageNow = Number(string1);
				var string2  = _totalPage.value;
				var totalPage = Number(string2);
				
				if( pageNow>=totalPage ){
					var pageError = document.getElementById("pageError");
					pageError.innerText = "当前已是最后一页";
				}else{
					pageNow++;
					window.location.href = "<%=url%>&pageNow="+pageNow;
				}
			}
		</script>
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/control.js"></script>
		<script src="js/status.js"></script>
		<script src="js/initAJAX.js"></script>
		<%
			String cid = request.getParameter("cid");
			String pno = request.getParameter("pno");
			String username = request.getParameter("username");
			String result = request.getParameter("result");
			String language = request.getParameter("language");
			String _pageNow = request.getParameter("pageNow");

			if( username!=null ){
				username = URLDecoder.decode(username,"UTF-8");
			}else{
				username = "";
			}
			
			//处理异常
			try{
				int tmp = Integer.parseInt(result);
				if( tmp<0 || tmp>=SelectOptionValueUtil.getResults().length ){
					result  = "0";
				}
			}catch (Exception e){
				result = "0";
			}
			
			try{
				int tmp = Integer.parseInt(language);
				if( tmp<0 || tmp>=SelectOptionValueUtil.getLanguages().length ){
					language  = "0";
				}
			}catch (Exception e){
				language = "0";
			}
			
			int pageNow;
			try {
				pageNow = Integer.parseInt(_pageNow);
				if (pageNow < 1) {
					pageNow = 1;
				}
			} catch (NumberFormatException e) {
				pageNow = 1;
			}
			
			String showType = request.getParameter("showType");
			if( showType==null || !showType.equals("1") )
				showType = "0";
			boolean isShowContest = showType.equals("1");

			boolean isCidCorrect = false;
			try {
				isCidCorrect = CheckParamDao.checkCid(cid);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			int totalPage = GetInfoFromDatabaseDao.getStatusTotalPage(cid, pno, username, result, language, isCidCorrect,isShowContest);
			if (pageNow > totalPage) pageNow = totalPage;
			ArrayList<StatusBean> statusList = GetInfoFromDatabaseDao.getStatus(cid, pno, username, result, language, isCidCorrect,isShowContest,pageNow);
			String[] results = SelectOptionValueUtil.getResults();
			String[] languages = SelectOptionValueUtil.getLanguages();
			
			if( cid==null ){
				cid = "";
			}
			if( username==null ){
				username = "";
			}
			if( result==null || result.equals("")){
				result = "0";
			}
			if( language==null || language.equals("")){
				language = "0";
			}
			
			boolean isAdmin = false;
			String adminString = (String)session.getAttribute("isAdmin");
			if( adminString!=null && adminString.equals("true") ){
				isAdmin = true;
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		%>
		
		<%
			final String CHECK_SESSION = "checkSession";
			
			//产生随机数，用以验证是否为合法提交
			SecureRandom random;
			long seq = 0;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
				seq = random.nextLong();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			String randomString = seq+"";
			session.setAttribute(CHECK_SESSION,randomString+"LL");
		%>
		<title>SZUCPC Online Judge</title>
	</head>
	<body>
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			<div class="outer">
				<input type="hidden" id="cidHidden" value="<%=cid%>">
				<%
					String style;
					String uname = (String)session.getAttribute("account");
					if( isCidCorrect ){
						if( uname==null ){
							response.sendRedirect("./loginpage,jsp");
							return;
						}
						boolean isNeedPasswd = GetInfoFromDatabaseDao.isNeedPasswd(uname, cid);
						boolean isStarted = GetInfoFromDatabaseDao.isStarted(cid);
						if( isNeedPasswd || !isStarted ){
							response.sendRedirect("./contest.jsp?cid="+cid+"&showType="+showType);
							return;
						}
						style="margin-top: -20px;";%>
				<jsp:include page="includeJSP/problemsetTitle.jsp"></jsp:include>
				<input type="hidden" id="type" value="contest">
				<%}else{ style="margin-top: 10px;"; %>
				<input type="hidden" id="type" value="status">
				<%}%>
				<table class="table table-striped" style="margin: 0 auto; width: 95%; text-align: center;background: rgb(238, 238, 238);border: 1px solid #DDDDDD;<%=style%>"> 
					<thead>
						<tr class="toprow" align="center">
							<th class="center" width="10%">Run ID<br><br></th>
							<th width=20%>
								Username<br>
								<input class="search custom-select" type="text" id="searchUsername" value="<%=username%>" title="">
							</th>
							<th width=10%>Cid<br><br></th>
							<th width=5%>Pno<br><br></th>
							<th width=10%>Result<br>
								<select id="select-result" class="custom-select">
									<% for( int i=0;i<results.length;++i ){%>
									<option value="<%=i%>"><%=results[i]%></option>
									<%}%>
		                    	</select>
							</th>
							<th width="5%">Time<br>(ms)<br></th>
							<th width="5%">Mem<br>(MB)<br></th>
							<th width="5%">Length<br>(B)<br></th>
							<th width="5%">Lang<br>
								<select id="select-language" class="custom-select">
									<% for( int i=0;i<languages.length;++i ){ %>
									<option value="<%=i%>"><%=languages[i]%></option>
									<%}%>
		                    	</select>
		                    </th>
		                    <th width="25%">Submit Time<br><br></th>
						</tr>
					</thead>
					<tbody>
						<%
							int length = statusList.size();
							for( int i=0;i<length;++i ){
								StatusBean bean = statusList.get(i);
								String className = "oddrow";
								if( (i&1)==0 ){
									className = "evenrow";
								}
						%>
						<tr class="<%=className%>">
							<%
								boolean isEnd = false;
								ContestListBean contest = GetInfoFromDatabaseDao.getContestDate(bean.getCid()+"");
								if( contest!=null ){
									Date now = new Date();
									Date end = null;
									try {
										end = sdf.parse(contest.getEndDate());
									} catch (ParseException e) {
										e.printStackTrace();
									}
									if( now.compareTo(end)>=0 ){
										isEnd = true;
									}
								}
							%>
							<td><%=bean.getRunId()%></td>
							<td><%=bean.getUsername()%></td>
							<td><a href="contest.jsp?cid=<%=bean.getCid()%>&showType=<%=showType%>"><%=bean.getCid()%></a></td>
							<td><a href="problem.jsp?cid=<%=bean.getCid()%>&pno=<%=bean.getPno()%>&showType=<%=showType%>"><%=bean.getPno()%></a></td>
							<td><%switch(bean.getResult()){
								case "1":
									out.print("<span class='accept'>"+results[Integer.parseInt(bean.getResult())]+"</span>");break;
								case "7": case "8": case "9": case "10": case "11":
									out.print("<span class='grey'>"+results[Integer.parseInt(bean.getResult())]+"</span>");break;
								default:
									out.print("<span class='attempt'>"+results[Integer.parseInt(bean.getResult())]+"</span>");
								break;}
							%>
							<td><%=bean.getTime()%></td>
							<td><%=bean.getMem()%></td>
							<td><%=bean.getLength()%></td>
							<td><%if( uname!=null && (isAdmin || bean.getUsername().equals(uname)
									|| (!uname.equals("") && bean.getShared().equals("1") && isEnd)) ){%>
								<a href="javascript:showCode('<%=bean.getRunId()%>');" class="md-trigger" data-modal="modal-1"><%=languages[Integer.parseInt(bean.getLanguage())]%></a>
								<%}else{
									out.print(languages[Integer.parseInt(bean.getLanguage())]);
								}%></td>
							<td><%=bean.getSubmitDate()%></td>
						</tr>
						<%}%>
					</tbody>
				</table>
				
				<label class="error" style="text-align: center" id="pageError"><br></label><br>
				<input type="button" value="上一页" class="button" style="width: 10%; margin-right: 20px;" onclick="lastPage()">
				<label>第 <%=pageNow%> 页/共 <%=totalPage%> 页</label>
				<input type="button" value="下一页" class='button' style="width: 10%" onclick="nextPage()">
				<input type="hidden" id="_pageNow" name="_pageNow" value="<%=pageNow%>">
				<input type="hidden" id="_totalPage" value="<%=totalPage%>">
				
				<input type="hidden" id="showType" value="<%=showType%>">
			</div>
		</div>
		
		<div class="md-modal md-effect-1" id="modal-1">
			<div class="md-content">
				<h3>Code</h3>
				<div>
					<textarea class="form-control" id="codeText" style="margin: 0 auto;width: 70%;height: 500px;resize: none" readonly></textarea><br>
					<button class="md-close btn-sm btn-primary">Close</button>
				</div>
			</div>
		</div>
		
		<div class="md-overlay"></div>
		
		<input type="hidden" id="checkCode" value="<%=randomString%>">
		<input type="hidden" id="url" value="<%=request.getContextPath()%>/showCode">
		
		<script src="js/classie.js"></script>
		<script src="js/modalEffects.js"></script>
		
		<script type="text/javascript">
			$("#select-result").val("<%=result%>");
			$("#select-language").val("<%=language%>");
			<% if( isCidCorrect ){%>
				document.getElementById("con_status").style.backgroundColor = "#EAEAEA";
			<%}%>
		</script>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		
	</body>
</html>