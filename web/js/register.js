function setEmpty(){
	var registerStatus = document.getElementById("registerStatus");
	
	registerStatus.innerHTML = "<br>";
}

function reset(){
	var usernameText = registerForm.username;
	var passwordText = registerForm.password;
	var confirmPasswordText = registerForm.confirmPassword;
	var nicknameText = registerForm.nickname;
	var emailText = registerForm.nickname;
	
	usernameText.value = "";
	passwordText.value = "";
	confirmPasswordText.value = "";
	nicknameText.value = "";
	emailText.value = "";
}

function register(){
	var username = registerForm.username.value;
	var password = registerForm.password.value;
	var confirmPassword = registerForm.confirmPassword.value;
	var nickname = registerForm.nickname.value;
	var email = registerForm.email.value;
	var verifyStatus = document.getElementById("verifyStatus").value;
	
	var registerStatus = document.getElementById("registerStatus");
	
	if( username=="" ){
		registerStatus.innerHTML = "Please enter your username"; refleshVerify();return;
	}else if( /.*[\u4e00-\u9fa5]+.*$/.test(username) ){
		registerStatus.innerHTML = "Username cannot contain Chinese"; refleshVerify();return;
	}else if( username.length<6 ){
		registerStatus.innerHTML = "Username is too short"; refleshVerify();return;
	}else if( username.length>16 ){
		registerStatus.innerHTML = "Username is too long"; refleshVerify();return;
	}
	
	if( password=="" ){
		registerStatus.innerHTML = "Please enter your password"; refleshVerify();return;
	}else if( /.*[\u4e00-\u9fa5]+.*$/.test(password) ){
		registerStatus.innerHTML = "Password cannot contain Chinese"; refleshVerify();return;
	}else if( password.length<6 ){
		registerStatus.innerHTML = "Password is too short"; refleshVerify();return;
	}else if( password.length>16 ){
		registerStatus.innerHTML = "Password is too long";refleshVerify(); return;
	}
	
	if( nickname=="" ){
		registerStatus.innerHTML = "Please enter your nickname"; refleshVerify();return;
	}else if( nickname.indexOf("%")>0 || nickname.indexOf("*")>0 || nickname.indexOf(" ")>0 ){
		updateStatus.innerHTML = "Nickname contains illegal characters";refleshVerify();return;
	}
	if( email=="" ){
		registerStatus.innerHTML = "Please enter your email";refleshVerify(); return;
	}

	if( confirmPassword=="" ){
		registerStatus.innerHTML = "Please repeat your password";refleshVerify();return;
	}else if( confirmPassword!=password ){
		registerStatus.innerHTML = "Two passwords are not the same";refleshVerify();return;
	}
	
	if( verifyStatus!="1" ){
		registerStatus.innerHTML = "Please complete the verify";refleshVerify();
	}else{
		var checkCode = document.getElementById("checkCode").value;
		var url = registerForm.action+"?username="+username+"&password="+password+"&nickname="+nickname+"&email="+email+"&checkCode="+checkCode;
		
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				//window.alert(xmlHttp.responseText);
				if( xmlHttp.responseText=="same" ){
					registerStatus.innerHTML = "Username has been registered";refleshVerify();
				}else if( xmlHttp.responseText=="success" ){
					//window.alert("success");
					window.location.href = "./loginpage.jsp";
				}else if( xmlHttp.responseText=="fail" ){
					registerStatus.innerHTML = "Username or password contains illegal characters";refleshVerify();
				}else{
					registerStatus.innerHTML = "Register error，please try again";refleshVerify();
				}
			}else{
				registerStatus.innerHTML = "Registering...";
			}
		};
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
	createVerify();initAJAX();
};