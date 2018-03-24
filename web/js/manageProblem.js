$(function(){
	$("#searchTitle").bind('keypress',function(event){
		//监听回车
		if(event.keyCode==13){
			var search = $("#searchTitle")[0].value;
			search = encodeURIComponent(encodeURIComponent(search));
			window.location.href = "./manageproblem.jsp?search="+search;
		}
	});
})