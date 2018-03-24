$(function(){
	$("#searchTitle").bind('keypress',function(event){
		//监听回车
		if(event.keyCode==13){
			var search = $("#searchTitle")[0].value;
			search = encodeURIComponent(encodeURIComponent(search));
			window.location.href = "./contest.jsp?pageNow=1&search="+search;
		}
	});
})

function checkPassword(cid){
	var passwd = $("#passwd")[0].value;
	var passwdLabel = $("#passwdLabel");
	var button = $("#loginButton");
	
	button.disabled = false;
	passwdLabel.text("");
	
	if( passwd=="" ){
		passwdLabel.text("Please input the password of this contest.");
		button.disabled = true; return;
	}
	try{
		var url = $("#url")[0].value;
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				if( xmlHttp.responseText=="success" ){
					window.location.href = "./contest.jsp?cid="+cid;
				}else{
					passwdLabel.text("The password of this contest is wrong.")
				}
			}else{
				passwdLabel.text("Please waiting just a moment.");
			}
		}
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		xmlHttp.send("cid="+cid+"&passwd="+passwd);
	}catch( e ){
		passwdLabel.HTML(e);
	}
	
	button.disabled = true;
}

window.onload = function(){
	setAlive(); initAJAX();
}