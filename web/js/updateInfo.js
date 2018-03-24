function setEmpty(){
	var updateStatus = document.getElementById("updateStatus");
	
	updateStatus.innerHTML = "<br>";
}

function reset(){
	var usernameText = updateInfoForm.username;
	var passwordText = updateInfoForm.password;
	var confirmPasswordText = updateInfoForm.confirmPassword;
	var nicknameText = updateInfoForm.nickname;
	var emailText = updateInfoForm.nickname;
    var updateStatus = $("#updateStatus");
	
	usernameText.value = "";
	passwordText.value = "";
	confirmPasswordText.value = "";
	nicknameText.value = "";
	emailText.value = "";
	updateStatus.val("0");
	
	refleshVerify();
}

function updateInfo(){
	var username = updateInfoForm.username.value;
	var oldPassword = updateInfoForm.oldPassword.value;
	var newPassword = updateInfoForm.newPassword.value;
	var confirmPassword = updateInfoForm.confirmPassword.value;
	var nickname = updateInfoForm.nickname.value;
	var email = updateInfoForm.email.value;
	var verifyStatus = document.getElementById("verifyStatus").value;
	
	var updateStatus = document.getElementById("updateStatus");
	
	if( oldPassword==="" ){
		updateStatus.innerHTML = "Please enter your old password"; refleshVerify();return;
	}
	
	if( newPassword!=="" ){
		if( /.*[\u4e00-\u9fa5]+.*$/.test(newPassword) ){
			updateStatus.innerHTML = "Password cannot contain Chinese"; refleshVerify();return;
		}else if( newPassword.length<6 ){
			updateStatus.innerHTML = "Password is too short"; refleshVerify();return;
		}else if( newPassword.length>16 ){
			updateStatus.innerHTML = "Password is too long";refleshVerify(); return;
		}
	}
	
	if( nickname==="" ){
		updateStatus.innerHTML = "Please enter your nickname"; refleshVerify();return;
	}else if( nickname.indexOf("%")>0 ){
		updateStatus.innerHTML = "Nickname shouldn't contain %";refleshVerify();return;
	}
	if( email==="" ){
		updateStatus.innerHTML = "Please enter your email";refleshVerify(); return;
	}

	if( newPassword!=="" && confirmPassword==="" ){
		updateStatus.innerHTML = "Please repeat your password";refleshVerify();return;
	}else if( confirmPassword!==newPassword ){
		updateStatus.innerHTML = "Two passwords are not the same";refleshVerify();return;
	}
	
	if( verifyStatus!=="1" ){
		updateStatus.innerHTML = "Please complete the verify";refleshVerify();
	}else{
		var checkCode = document.getElementById("checkCode").value;
		var url = updateInfoForm.action+"?username="+username+"&oldPassword="+oldPassword+"&newPassword="+newPassword+"&nickname="+nickname+"&email="+email+"&checkCode="+checkCode;
		
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState===4 ){
				//window.alert(xmlHttp.responseText);
				if( xmlHttp.responseText==="fail" ){
					updateStatus.innerHTML = "Incorrect password or email";refleshVerify();
				}else if( xmlHttp.responseText==="success" ){
					//window.alert("success");
					window.location.href = "./updateinfo.jsp";
				}else{
					updateStatus.innerHTML = "Update error，please try again";refleshVerify();
				}
			}else{
				updateStatus.innerHTML = "Updating...";
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
        	height : '30px'
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