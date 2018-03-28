<%@page import="com.hwf.bean.ContestContentBean"%>
<%@page import="com.hwf.bean.ContestListBean"%>
<%@page import="com.hwf.dao.CheckParamDao"%>
<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@ page import="java.text.ParseException" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/content.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/resultStyle.css">
		<link rel="stylesheet" href="css/component.css">
		<script type="text/javascript">
			<%
				String queryString = request.getQueryString();
				if( queryString==null ){
					queryString = "";
				}else if("pageNow".contains(queryString) ){
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
        <script type="text/javascript">
            var xmlHttp;
        </script>
		<script src="js/initAJAX.js"></script>
		<script src="js/control.js"></script>
		<script src="js/contest.js"></script>
		
		<title>SZUCPC Online Judge</title>
		
		<%
			String username = (String)session.getAttribute("account");
			String adminStr = (String)session.getAttribute("isAdmin");
			boolean isAdmin = (adminStr!=null && adminStr.equals("true"));
		%>
		<%
			String cid = request.getParameter("cid");
			String search = request.getParameter("search");
			if( search!=null ){
				search = URLDecoder.decode(search,"UTF-8");
			}else{
				search = "";
			}
			boolean isSelectContest = CheckParamDao.checkCid(cid);
			
			String showType = request.getParameter("showType");
			if( showType==null || !showType.equals("1") )
				showType = "0";
			boolean isShowContest = showType.equals("1");
		%>
		
		<%
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		%>
	</head>
	
	<body>
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			<div class="outer">
				<%
					if( !isSelectContest ){
						String _pageNow = request.getParameter("pageNow");
						int pageNow;
						try {
							pageNow = Integer.parseInt(_pageNow);
							if (pageNow < 1) {
								pageNow = 1;
							}
						} catch (NumberFormatException e) {
							pageNow = 1;
						}
					
						ArrayList<ContestListBean> contestList;
						int totalPage;
						if( search!=null && !search.equals("") ){
							totalPage = GetInfoFromDatabaseDao.getSearchContestListTotalPage(search);
							if (pageNow > totalPage) pageNow = totalPage;
							contestList = GetInfoFromDatabaseDao.searchContestListWithTitle(search,pageNow);
						}else{
							totalPage = GetInfoFromDatabaseDao.getContestListTotalPage();
							if (pageNow > totalPage) pageNow = totalPage;
							contestList = GetInfoFromDatabaseDao.getContestList(pageNow);
						}
				%>
			
				<table class="table table-striped" style="margin: 0 auto; width: 95%; text-align: center;margin-top: 10px;background: rgb(238, 238, 238);"> 
					<thead>
						<tr class="toprow" align="center">
							<th class="center" width="10%">
								ID<br>
							<%
								if( isAdmin ){
							%>
								<a href="createcontest.jsp"><img height="18" src="picture/icon_add.png" border="0"></a>
							<%
								}else{
									out.println("<br>");
								}
							%>
							</th>
							<th width=40%>
								Title<br>
								<input class="search custom-select" type="text" id="searchTitle" value="<%=search%>">
							</th>
							<th width=25%>Status<br><br></th>
							<th width=10%>Private<br><br></th>
							<th width=15%>Creator<br><br></th>
						</tr>
					</thead>
					<tbody>
					<%
						int length = contestList.size();
						for( int i=0;i<length;++i ){
							ContestListBean bean = contestList.get(i);
							String className = "oddrow";
							if( (i&1)==0 ){
								className = "evenrow";
							}
					%>
					
						<tr class="<%=className%>">
							<td><%=bean.getCid()%></td>
							<td>
								<a href="./contest.jsp?cid=<%=bean.getCid()%>&showType=0"><%=bean.getTitle()%></a>
							</td>
							<td>
								<%
									Date now = new Date();
									Date start;
									Date end;
									try {
										start = sdf.parse(bean.getStartDate());
										end = sdf.parse(bean.getEndDate());

										if( now.compareTo(start)<0 ){
											out.print("<font style='color: grey;'>Pending</font> | <font style='color: black;'>Start @ "+sdf.format(start)+"</font>");
										}else if( now.compareTo(end)>=0 ){
											out.print("<font style='color: blue;'>Ending</font>");
										}else{
											out.print("<font style='color: red;'>Running</font> | <font style='color: black;'>End @ "+sdf.format(end)+"</font>");
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								%>
							</td>
							<td>
								<%
									if( bean.isPriv() ){
										out.print("<font style='color: blue;'>Private</font>");
									}else{
										out.print("<font style='color: red;'>Public</font>");
									}
								%>
							</td>
							<td><%=bean.getCreator()%></td>
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
				
				<%}else{
					if( username==null ){
						response.sendRedirect("./loginpage.jsp");
						return;
					}
					boolean isNeedPasswd = GetInfoFromDatabaseDao.isNeedPasswd(username, cid);
					if( isNeedPasswd ){
				%>
				<div style="margin: 0 auto;margin-top: 20px;width: 50%;border: 1px solid;border-color: rgb(238, 238, 238);border-radius: 10px;">
					<div style="margin: 0 auto;width: 90%;margin-bottom: 10px;">
						<p style="margin-top: 10px;"><font style="color: black;margin-right: 10px;">Password:</font><input class="custom-select" type="password" id="passwd"></p>
						<label class="error" id="passwdLabel"></label><br>
						<hr>
						<input type="hidden" id="url" value="<%=request.getContextPath()%>/checkPasswd">
						<input type="button" class="btn btn-primary" value="Login" onclick="checkPassword(<%=cid%>);" id="loginButton">
					</div>
				</div>
				<%
					}else{
					boolean isStarted = true;
					ContestListBean contest = GetInfoFromDatabaseDao.getContestDate(cid);
					if( contest!=null ){
						Date now = new Date();
						Date start;
						Date end;
						try {
							start = sdf.parse(contest.getStartDate());
							end = sdf.parse(contest.getEndDate());

							out.print("<div style='margin: 0 auto;margin-top: 10px; margin-bottom: 10px;font-size: 20px;'>");
							if( now.compareTo(start)<0 ){
								isStarted = false;
								out.print("<font style='color: grey;;'>Pending</font> | <font style='color: black;'>Start @ "+sdf.format(start)+"</font>");
							}else if( now.compareTo(end)>=0 ){
								out.print("<font style='color: blue;'>Ending</font>");
							}else{
								out.print("<font style='color: red;'>Running</font> | <font style='color: black;'>End @ "+sdf.format(end)+"</font>");
							}
							out.print("</div>");
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					
					if( isStarted ){					
						ArrayList<ContestContentBean> contestContent = GetInfoFromDatabaseDao.getContestContent(cid, username,isShowContest);
				%>
				
					<jsp:include page="includeJSP/problemsetTitle.jsp"></jsp:include>
					<table class="table table-striped" style="margin: 0 auto; width: 95%; text-align: center;margin-top: -20px;background: rgb(238, 238, 238);border: 0px solid #DDDDDD;"> 
					<thead>
						<tr class="toprow" align="center">
							<th class="center" width="15%">My Status</th>
							<th width=15%>Status</th>
							<th width=5%>#</th>
							<th width=20%>Origin</th>
							<th width=45%>Title</th>
						</tr>
					</thead>
					<tbody>
						<%
							int length = contestContent.size();
							for( int i=0;i<length;++i ){
								ContestContentBean bean = contestContent.get(i);
								String className = "oddrow";
								if( (i&1)==0 ){
									className = "evenrow";
								}
						%>
						<tr class="<%=className%>">
							<td><%
								switch (bean.getMyStatus()){
									case -1 : out.print("<span class='attempt'>Attempted</span>"); break;
									case 1 : out.print("<span class='accept'>Accept</span>"); break;
								}
							%></td>
							<td><%
								if( bean.getSubmit()>0){
									out.print(bean.getAccept()+" / "+bean.getSubmit());
								}
							%></td>
							<td><a href="problem.jsp?cid=<%=cid%>&pno=<%=bean.getPno()%>&showType=<%=showType%>"><%=bean.getPno()%></a></td>
							<td><%=bean.getOrigin()%></td>	
							<td><a href="problem.jsp?cid=<%=cid%>&pno=<%=bean.getPno()%>&showType=<%=showType%>"><%=bean.getTitle()%></a></td>
						</tr>
						
						<%}%>
					</tbody>
					
					</table>
				
					<%
						String hint = GetInfoFromDatabaseDao.getContestHint(cid);
						if( hint!=null && !hint.equals("") ){
					%>
					<p class="title2" style="font-size: 15px;"><br>Hint</p>
					<div class="content">
						<p><%=hint%></p>
					</div>
					<%}%>
				
					<script type="text/javascript">
						document.getElementById("con_overview").style.backgroundColor = "#EAEAEA";
					</script>
					<%}}}%>
			</div>
			
		</div>
		
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		<input type="hidden" id="type" value="contest">
	</body>
</html>