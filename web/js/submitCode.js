function submitCode(){
	var button = document.getElementById("btn-submit");
	button.disabled = true;
	
	var cid = $("#cidHidden")[0].value;
	var pno = $("#pnoHidden")[0].value;
	var username = $("#username")[0].value;
	var language = $("#submit-language")[0].value;
	var share = $("#share")[0].value;
	var code = $("#code")[0].value;
	var showType = $("#showType")[0].value;
	var hintLabel = $("#hint");
	var verifyStatus = $("#verifyStatus")[0].value;
	
	if( code=="" || code.length<50 ){
		hintLabel.html("Please enter your code at least 50 characters."); refleshVerify(); button.disabled = false; return;
	}
	if( verifyStatus!="1" ){
		hintLabel.html("Please complite the verify."); refleshVerify(); button.disabled = false; return;
	}
	
	//编码
	code = encodeURIComponent(encodeURIComponent(code));
	
	var checkCode = submitCodeForm.checkCode.value;
	var url = submitCodeForm.action;
	
	try{
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				if( xmlHttp.responseText=="success" ){
					window.location.href = "./status.jsp?cid="+cid+"&username="+username+"&showType="+showType;
				}else if( xmlHttp.responseText=="limit" ){
					hintLabel.text("You can submit only once in 30 seconds");	
				}else{
					//hintLabel.html(xmlHttp.responseText);
					hintLabel.text("Submit error,please try again.");
				}
			}else{
				hintLabel.text("Submiting...");
			}
		}
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		xmlHttp.send("cid="+cid+"&pno="+pno+"&username="+username+"&language="+language+"&share="+share+"&code="+code+"&checkCode="+checkCode);
	}catch( e ){
		hintLabel.HTML(e);
		button.disabled = false;
	}
	
	button.disabled = false;
}

function change(obj){
	//更新显示
	$(obj).addClass("active").siblings().removeClass("active");
	//$("label.active").removeClass("active");
	//obj.className = "btn btn-secondary active";
	
	submitCodeForm.share.value = $(obj).children()[0].value;
}

function setEmpty(){
	document.getElementById("hint").innerText = "";
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
        	width : '100%',
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
	createVerify(); setAlive(); initAJAX();
}