$(function(){
	$("#searchUsername").bind('keypress',function(event){
		//监听回车
		if(event.keyCode==13){
			reSearch();
		}
	});
	
	//监听选择事件
	$("#select-result").change(function(){
		reSearch();
	});
	
	$("#select-language").change(function(){
		reSearch();
	});
})

function reSearch(){
	var cid = $("#cidHidden")[0].value;
	var search = $("#searchUsername")[0].value;
	var result = $("#select-result")[0].value;
	var language = $("#select-language")[0].value;
	var showType = $("#showType")[0].value;
	search = encodeURIComponent(encodeURIComponent(search));
	window.location.href = "./status.jsp?cid="+cid+"&username="+search+"&result="+result+"&language="+language+"&showType="+showType;
}

window.onload = function(){
	setAlive(); initAJAX();
}

function showCode(runId){
	var codeText = $("#codeText");
	var checkCode = $("#checkCode")[0].value;
	var url = $("#url")[0].value;
	
	try{
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				codeText.css("color","black");
				codeText.val(xmlHttp.responseText);
			}else{
				codeText.css("color","green");
				codeText.val("Loading,Please wait just a moment...");
			}
		}
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		xmlHttp.send("runId="+runId+"&checkCode="+checkCode);
	}catch( e ){
		codeText.css("color","red");
		codeText.val("System error.");
	}
}