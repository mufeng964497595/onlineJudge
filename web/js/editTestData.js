window.onload = function(){
	setAlive(); initAJAX();
}

function showData(id,type){
	var dataText = $("#dataText");
	var checkCode = $("#checkCode")[0].value;
	var url = "./showData";
	
	try{
		xmlHttp.open("POST",url,true);
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState==4 ){
				dataText.css("color","black");
				dataText.val(xmlHttp.responseText);
			}else{
				dataText.css("color","green");
				dataText.val("Loading,Please wait just a moment...");
			}
		}
		xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
		xmlHttp.send("id="+id+"&type="+type+"&checkCode="+checkCode);
	}catch( e ){
		dataText.css("color","red");
		dataText.val("System error.");
	}
}

function updateTitle(id,fileType,title1,title2){
	updateDataForm.action = updateDataForm.action+"&id="+id+"&fileType="+fileType;
	$("#h3").text(title1);
	$("#h5").text(title2);
}

function addData(){
	var addLabel = $("#addLabel");
	if( $("#inputName")[0].value==noFileSelected ){
		addLabel.text("Please select input file!");
	}else if( $("#outputName")[0].value==noFileSelected ){
		addLabel.text("Please select output file!");
	}else{
		addDataForm.submit();
	}
}

function updateData(){
	var updateLabel = $("#updateLabel");
	if( $("#fileName")[0].value==noFileSelected ){
		updateLabel.text("Please select data file!");
	}else{
		updateDataForm.submit();
	}
}

function delTitle(id){
	deleteDataForm.action = deleteDataForm.action+"&id="+id;
}