function addPro(){
	if( now<='Z' ){
		//var div = document.getElementById("proSet");
		var add = "<div id=\"div"+now+"\" style=\"margin-top: 5px;padding-left: 20px;\"> <a id=\"a"+now+"\" href=\"javascript:subPro('"+now+"');\"><img height=\"18\" src=\"./picture/icon_sub.png\" border=\"0\"></a><input name=\"pid\" type=\"text\" style=\"margin-left: 20px;width: 100px;\" onkeyup=\"getTitle(this);\" class=\"size custom-select\" onclick=\"setEmpty();\"><label id=\"label"+now+"\" style=\"width: 10px;margin-left: 10px;\">"+now+"</label><label id=\"title\" style=\"padding-left: 20px;width: 50%\"></label><input type=\"hidden\" name=\"titleStatus\" value=\"1\"></div>"
		//div.innerHTML += add;
		$('div#proSet').append(add);
		now = String.fromCharCode(now.charCodeAt(0)+1);
	}else{
		$("label#errorProSet").html("The number of problems is at most 26.");
	}
}

function subPro(val){
	if( now=='B' ){
		$("label#errorProSet").html("The number of problems is at least one.");
	}else{
		var id = '#div'+val;
		$(id).remove();
		reName(val);
	}
}

function reName(val){
	var end = now.charCodeAt(0);
	for( var tmp=val.charCodeAt(0)+1;tmp<end;++tmp ){
		var sl = String.fromCharCode(tmp-1);
		var sn = String.fromCharCode(tmp);
		
		//window.alert(sl+"---"+sn);
		//document.getElementById('div'+sn).id = 'div'+sl;
		//document.getElementById('a'+sn).href = "javascript:subPro('"+sl+"');";
		//document.getElementById('a'+sn).id = 'a'+sl;
		//document.getElementById('label'+sn).innerHTML = sl;
		//document.getElementById('label'+sn).id = 'label'+sl;
		$("#div"+sn).attr("id","div"+sl);
		$("#a"+sn).attr("href","javascript:subPro('"+sl+"');");
		$("#a"+sn).attr("id","a"+sl);
		$("#label"+sn).html(sl);
		$("#label"+sn).attr("id","label"+sl);
	}
	now = String.fromCharCode(now.charCodeAt(0)-1);
}

function getTitle(obj){
	var pid = obj.value;
	if( pid=="" ) return;
	
	var titleText =$($(obj).next()).next();
	var titleStatus = $(titleText).next();
	var checkCode = createForm.checkCode.value;
	
	try{
		var url = "./getTitle?pid="+pid+"&checkCode="+checkCode;
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				if( xmlHttp.responseText=="" ){
					titleText.css("color","red");
					titleText.text("No such PID!");
					titleStatus.val("0");
				}else{
					titleText.css("color","black");
					titleText.text(xmlHttp.responseText);
					titleStatus.val("1");
				}
			//}else{
			//	titleText.css("color","green");
			//	titleText.text("Waiting...");
			}
		}
		xmlHttp.send();
	}catch( e ){
		titleText.css("color","red");
		titleText.text("Error.");
	}
}

function creaCont(){
	var title = createForm.title.value;
	var start = createForm.startTime.value;
	var end = createForm.endTime.value;
	var priv = createForm.priv.value;
	var passwd = createForm.password.value;
	var hint = $("#hint").html();
	var creator = createForm.creator.value;
	var pid = document.getElementsByName("pid");
	var pidTitle = document.getElementsByName("titleStatus");
	var pids = "";
	
	var hintLabel = document.getElementById("hintLabel");
	if( title=="" ){
		hintLabel.innerText = "Please enter the title."; return;
	}else if( start=="" ){
		hintLabel.innerText = "Please enter the start time."; return;
	}else if( end=="" ){
		hintLabel.innerText = "Please enter the end time."; return;
	}else if( priv=="1" && passwd=="" ){
		hintLabel.innerText = "Please enter the password."; return;
	}else{
		for( var i=0;i<pid.length;++i ){
			if( pid[i].value=="" ){
				hintLabel.innerText = "Please enter the PID."; return;
			}
			if( i!=0 ) 
				pids += ",";
			pids += pid[i].value;
			
			if( pidTitle[i].value=="0" ){
				hintLabel.innerText = "No such PID."; return;
			}
		}
	}
	
	
	
	//编码
	title = encodeURIComponent(encodeURIComponent(title));
	start = encodeURIComponent(encodeURIComponent(start));
	end = encodeURIComponent(encodeURIComponent(end));
	passwd = encodeURIComponent(encodeURIComponent(passwd));
	hint = encodeURIComponent(encodeURIComponent(hint));
	creator = encodeURIComponent(encodeURIComponent(creator));
	
	var url = createForm.action;
	var checkCode = createForm.checkCode.value;
	try{
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				if( xmlHttp.responseText=="success" ){
					window.location.href = "./contest.jsp";
				}else{
					hintLabel.innerText = xmlHttp.rsponseText;
				}
			}else{
				hintLabel.innerText = "Submiting...";
			}
		}
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		xmlHttp.send("title="+title+"&start="+start+"&end="+end+"&private="+priv+"&passwd="+passwd+"&hint="+hint+"&creator="+creator+"&pids="+pids+"&checkCode="+checkCode);
	}catch( e ){
		hintLabel.innerText = "error";
	}
}

function change(obj){
	//更新显示
	$(obj).addClass("active selected").siblings().removeClass("active selected");
	
	createForm.priv.value = $(obj).children()[0].value;
	//显示/隐藏密码栏
	if( $(obj).children()[0].value==1 ){
		$("#passwdDiv").show();
	}else{
		$("#passwdDiv").hide();
	}
}

function setEmpty(){
	$("label#hint").html("");
}

window.onclick = function(e){
	$("label#errorProSet").html("<br>");
	if( e.target.classList!="dropdown-toggle" && e.target.classList!="caret" ){
		hideList("");
	}
}

window.onload = function(){
	initAJAX();addPro();
}