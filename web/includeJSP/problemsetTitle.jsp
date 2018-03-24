<%@page import="com.hwf.dao.GetInfoFromDatabaseDao"%>
<%@page import="com.hwf.bean.ContestListBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String cid = request.getParameter("cid");
	String showType = request.getParameter("showType");
	String contestClass = (showType==null || showType.equals("1"))? "active" : "";
	String practiceClass = contestClass.equals("") ? "active" : "";
	
	String _queryString = request.getQueryString();
	if( _queryString.indexOf("&showType")!=-1 ){
		_queryString = _queryString.substring(0,_queryString.indexOf("&showType"));
	}
	String _url = request.getRequestURI()+"?"+_queryString;
%>
<nav class="navbar navbar-default" role="navigation" style="background: rgb(251, 251, 251);" >
	<div class="container-fluid">
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li id="con_overview"><a href="./contest.jsp?cid=<%=cid%>&showType=<%=showType%>">Overview</a></li>
				<li id="con_problem"><a href="./problem.jsp?cid=<%=cid%>&pno=A&showType=<%=showType%>">Problem</a></li>
				<li id="con_status"><a href="./status.jsp?cid=<%=cid%>&showType=<%=showType%>&pageNow=1">Status</a></li>
				<li id="con_rank"><a href="./ranklist.jsp?cid=<%=cid%>&showType=<%=showType%>">Rank</a></li>
			</ul>
			
			<ul class="nav navbar-nav navbar-right">
				<li id="con_set"><a href="javascript:;"class="md-trigger" data-modal="modal-10">Setting</a></li>				
			</ul>
		</div>
	</div>
</nav>

<div class="md-modal md-effect-1" id="modal-10">
	<div class="md-content">
		<h3>Setting</h3>
		
		<div class="row row2">
			<label class="col-md-2 col-form-label">Show:</label>
			<div class="col-md-10">
			    <div class="btn-group" data-toggle="buttons" >
			        <label class="btn btn-secondary <%=contestClass%>" onclick="changeChoose(this);">
			            <input value="1" autocomplete="off" type="hidden">Contest
			        </label>
			        <label class="btn btn-secondary <%=practiceClass%>" onclick="changeChoose(this);">
			            <input value="0" autocomplete="off" type="hidden">Practice
			        </label>
			        <input type="hidden" id="showType" value="<%=contestClass.equals("") ? 0 : 1%>">
			    </div>
			</div>
		</div>
		<input type="hidden" class="md-close btn-sm btn-primary">
	</div>
</div>

<div class="md-overlay"></div>
<script src="./js/classie.js"></script>
<script src="./js/modalEffects.js"></script>
<script type="text/javascript">
	function changeChoose(obj){
		//更新显示
		$(obj).addClass("active").siblings().removeClass("active");
		if( $("#showType")[0].value!=($(obj).children())[0].value ){
			window.location.href = "<%=_url%>"+"&showType="+$(obj).children()[0].value;
		}
	}
</script>
