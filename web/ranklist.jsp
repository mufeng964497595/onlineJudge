<%@page import="com.hwf.bean.UserContestProblemInfoBean"%>
<%@page import="com.hwf.bean.ContestContentBean"%>
<%@page import="com.hwf.bean.UserContestRankBean"%>
<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="com.hwf.bean.TotalRankBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hwf.dao.CheckParamDao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/content.css">
		<link rel="stylesheet" href="css/problem.css">
		<link rel="stylesheet" href="css/resultStyle.css">
		<link rel="stylesheet" href="css/component.css">
		
		<script src="js/jquery-2.1.1.min.js"></script>
		<script src="js/control.js"></script>
		<script type="text/javascript">
			var xmlHttp;
			
			<%
				String queryString = request.getQueryString();
				if( queryString==null ){
					queryString = "";
				}else if( queryString.indexOf("pageNow")!=-1 ){
					queryString = queryString.substring(0,queryString.indexOf("pageNow"));
				}
				String url = request.getRequestURI()+"?"+queryString;
				
			%>
			
			function lastPage(){
				<%-- 上一页 --%>
				var _pageNow = document.getElementById("_pageNow");
				var string = _pageNow.value;
				var pageNow = new Number(string);
				
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
				var pageNow = new Number(string1);
				var string2  = _totalPage.value;
				var totalPage = new Number(string2);
				
				if( pageNow>=totalPage ){
					var pageError = document.getElementById("pageError");
					pageError.innerText = "当前已是最后一页";
				}else{
					pageNow++;
					window.location.href = "<%=url%>&pageNow="+pageNow;
				}
			}
		</script>
		<title>SZUCPC Online Judge</title>
		
		<%
			String username = (String)session.getAttribute("account");
			String cid = request.getParameter("cid");
			boolean isCidCorrect = CheckParamDao.checkCid(cid);
			
			String showType = request.getParameter("showType");
			boolean isShowContest = showType==null || showType.equals("1");
		%>
	</head>
	<body onload="setAlive();">
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			
			<div class="outer">
				<% if( !isCidCorrect ){
					String _pageNow = request.getParameter("pageNow");
					int pageNow = 1;
					try {
						pageNow = Integer.parseInt(_pageNow);
						if (pageNow < 1) {
							pageNow = 1;
						}
					} catch (NumberFormatException e) {
						pageNow = 1;
					}

					int totalPage = GetInfoFromDatabaseDao.getRankListTotalPage();
					if (pageNow > totalPage) pageNow = totalPage;
					ArrayList<TotalRankBean> totalRankList = GetInfoFromDatabaseDao.getTotalRankList(pageNow);
				%>
				<table class="table table-striped" border="" bordercolor="#DDDDDD" style="margin: 0 auto; width: 95%; text-align: center;margin-top: 10px;background: rgb(238, 238, 238);"> 
					<thead>
						<tr class="toprow" align="center">
							<th class="center" width="10%">Rank</th>
							<th width=35%>Username</th>
							<th width=35%>Nickname</th>
							<th width=5%>Accept</th>
							<th width=5%>Submit</th>
							<th width=10%>AC Rate</th>
						</tr>
					</thead>
					<tbody>
						<%
							int length = totalRankList.size();
							for( int i=0;i<length;++i ){
								TotalRankBean bean = totalRankList.get(i);
								String className = "oddrow";
								if( (i&1)==0 ){
									className = "evenrow";
								}
						%>
						<tr class="<%=className%>">
							<td><%=bean.getRank()%></td>
							<td><a href="status.jsp?username=<%=bean.getUsername()%>"><%=bean.getUsername()%></a></td>
							<td><a href="status.jsp?username=<%=bean.getUsername()%>"><%=bean.getNickname()%></a></td>
							<td><a href="status.jsp?username=<%=bean.getUsername()%>&result=1"><%=bean.getAccept()%></a></td>
							<td><a href="status.jspsername=<%=bean.getUsername()%>&result=0"><%=bean.getSubmit()%></a></td>
							<td><%=bean.getAcRate()%></td>
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
				
				<input type="hidden" id="type" value="rankList">
				<%}else{
					boolean isNeedPasswd = GetInfoFromDatabaseDao.isNeedPasswd(username, cid);
					boolean isStarted = GetInfoFromDatabaseDao.isStarted(cid);
					if( isNeedPasswd || !isStarted ){
						response.sendRedirect("./contest.jsp?cid="+cid+"&showType="+(showType==null ? "1" : showType));
						return;
					}
				
					ArrayList<UserContestRankBean> userRank = GetInfoFromDatabaseDao.getUserContestRank(cid,isShowContest);
					ArrayList<ContestContentBean> contestList = GetInfoFromDatabaseDao.getContestContent(cid, username,isShowContest);
					int pnum = contestList.size();
				%>
				<jsp:include page="includeJSP/problemsetTitle.jsp"></jsp:include>
				<table class="table table-striped" border="" bordercolor="#DDDDDD" style="align-content: left; width: auto; text-align: center;margin-top: -20px;background: rgb(238, 238, 238);">
					<thead>
						<tr class="toprow" align="center">
							<th style="width: 80px;">Rank<br><br></th>
							<th style="width: 200px;">User<br><br></th>
							<th style="width: 10px;">Solve<br><br></th>
							<th style="width: 100px;">Penalty<br><br></th>
							<% for( ContestContentBean bean : contestList ){
									String className = "";
									switch (bean.getMyStatus()){
										case -1: className="class='attempt'"; break;
										case 1: className="class='accept'"; break;
									}
							 %>
								<th style="width: 80px;" <%=className%>><a href="problem.jsp?cid=<%=cid%>&pno=<%=bean.getPno()%>&showType=<%=showType%>" <%=className%>><%=bean.getPno()%></a><br><%=bean.getAccept()%> / <%=bean.getSubmit()%></th>
							<%}%>
						</tr>
					</thead>
					<tbody>
						<%
							int length = userRank.size();
							for( int i=0;i<length;++i ){
								UserContestRankBean bean = userRank.get(i);
								String className = "oddrow";
								if( (i&1)==0 ){
									className = "evenrow";
								}
								
								String tdClass = "";
								if( bean.getUsername().equals(username) ){
									tdClass = "class='me'";
								}
						%>
						<tr class="<%=className%>">
							<td <%=tdClass%>><%=bean.getRank()%><br><br></td>
							<td <%=tdClass%>><%=bean.getUsername()%><br><font style="color: gray;">(<%=bean.getNickname()%>)</font></td>
							<td <%=tdClass%>><%=bean.getSolve()%></td>
							<td <%=tdClass%>><%=bean.getPenalty()%></td>
							<% 	ArrayList<UserContestProblemInfoBean> problemInfo = bean.getProblemInfo();
								int num = 0;
								if( problemInfo!=null ) num = problemInfo.size();
								for( int j=0,k=0;j<pnum;++j ){
									if( k>=num ){
										out.print("<td></td>");
										continue;
									}
									
									UserContestProblemInfoBean info = problemInfo.get(k);
									if( info.getPno().equals((char)(j+'A')+"") ){
										String style = "class='attempt'";
										if( info.getAc()>0 ){
											style = "class='accept'";
											String acMinute = info.getAcMinute();
											if( acMinute==null ){
												style = "class='timeOver'";
												acMinute = "";
											}else if ( info.getFirstBlood()>0 ){
												style = "class='firstBlood'";
											}
											
											out.print("<td "+style+">"+acMinute);
										}else{
											out.print("<td "+style+">");
										}
										if( info.getWrong()>0 ){
											out.print("<br><font>( -"+info.getWrong()+" )</font>");
										}
										out.print("</td>");
										
										++k;
									}else{
										out.print("<td></td>");
									}
								%>
							<%}%>
						</tr>
						<%}%>
					</tbody>
				</table>
				
				<script type="text/javascript">
					document.getElementById("con_rank").style.backgroundColor = "#EAEAEA";
				</script>
				<input type="hidden" id="type" value="contest">
				<%}%>
			</div>
		</div>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		
	</body>
</html>