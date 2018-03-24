function setEmpty(){
	var loginStatus = document.getElementById("loginStatus");
	loginStatus.innerHTML = "<br>";
}
function forgot(){
	window.alert("to be continue.");
}
function login(){
	var username = document.getElementById("usernameId").value;
	var password = document.getElementById("passwordId").value;
	var verifyStatus = document.getElementById("verifyStatus").value;
	
	var loginStatus = document.getElementById("loginStatus");

	setEmpty();
	
	if( username=="" ){
		loginStatus.innerHTML = "Please enter your username";	refleshVerify();
	}else if( password=="" ){
		loginStatus.innerHTML = "Please enter your password";	refleshVerify();
	}else if( verifyStatus!="1" ){
		loginStatus.innerHTML = "Please complete the verify";	refleshVerify();
	}else{
		var checkCode = document.getElementById("checkCode").value;
		var url = loginForm.action+"?username="+username+"&password="+password+"&checkCode="+checkCode;
		if( loginForm.saveUsername.checked ){
			url += "&saveUsername=true";
		}
		if( loginForm.autoLogin.checked ){
			url += "&autoLogin=true";
		}
		
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				if( xmlHttp.responseText=="fail" ){
					loginStatus.innerHTML = "Username or password error";	refleshVerify();
				}else if( xmlHttp.responseText=="success" ){
					//window.alert("success");
					window.location.href = "./contest.jsp";
				}else if( xmlHttp.responseText=="error" ){
					loginStatus.innerHTML = "Login error,please try again";	refleshVerify();
				}else{
					loginStatus.innerHTML = xmlHttp.responseText; 	refleshVerify();
				}
			}else{
				loginStatus.innerHTML = "Login...";
			}
		}
		xmlHttp.send();
	}	
}

function initAJAX(){
	if( window.XMLHttpRequest ){
		xmlHttp = new XMLHttpRequest();
// 					window.alert("support AJAX");
	}else if( window.ActiveXObject ){
		try{
			xmlHttp = new ActiveXObject("Msxm12.XMLHTTP");
// 						window.alert("support AJAX");
		}catch( e ){
			window.alert("not support AJAX");
		}
	}
}

function prepareLogin(){
	var checkCode = loginForm.checkCode.value;
	var url = loginForm.action+"?checkCode="+checkCode;
	xmlHttp.open("GET",url,true);
	xmlHttp.onreadystatechange = function(){
		if( xmlHttp.readyState==4 ){
			if( xmlHttp.responseText=="autoLogin" ){
				//window.alert("autoLogin");
				window.location.href = "./contest.jsp";
			}else{
				loginForm.username.value = xmlHttp.responseText;
			}
		}
	}
	xmlHttp.send();
}

function refleshVerify(){
	$("#mpanel").children().remove();
	createVerify();
}

function createVerify(){
	$('#mpanel').slideVerify({
		type : 1,		//类型
		vOffset : 5,	//误差量，根据需求自行调整
        barSize : {
        	width : '70%',
        	height : '30px',
        },
        ready : function() {
        	$("#verifyStatus").val("0");
    	},
        success : function() {
        	$("#verifyStatus").val("1");
        },
        error : function() {
			$("#verifyStatus").val("0");
        }
        
    });
}

window.onload = function(){
	createVerify();initAJAX();prepareLogin(); 
}