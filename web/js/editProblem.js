function editPro(){
	var pid = editProblemForm.pid.value;
	var title = editProblemForm.title.value;
	var time = editProblemForm.time.value;
	var mem = editProblemForm.mem.value;
	var origin = editProblemForm.origin.value;
	var description = editProblemForm.description.value;
	var inputFormat = editProblemForm.inputFormat.value;
	var outputFormat = editProblemForm.outputFormat.value;
	var sampleInput = editProblemForm.sampleInput.value;
	var sampleOutput = editProblemForm.sampleOutput.value;
	var hint = editProblemForm.hint.value;
	//编码，以避免中文乱码，同时，将特殊字符也进行编码，在后台解码
	pid = encodeURIComponent(encodeURIComponent(pid));
	title = encodeURIComponent(encodeURIComponent(title));
	time = encodeURIComponent(encodeURIComponent(time));
	mem = encodeURIComponent(encodeURIComponent(mem));
	origin = encodeURIComponent(encodeURIComponent(origin));
	description = encodeURIComponent(encodeURIComponent(description));
	inputFormat = encodeURIComponent(encodeURIComponent(inputFormat));
	outputFormat = encodeURIComponent(encodeURIComponent(outputFormat));
	sampleInput = encodeURIComponent(encodeURIComponent(sampleInput));
	sampleOutput = encodeURIComponent(encodeURIComponent(sampleOutput));
	hint = encodeURIComponent(encodeURIComponent(hint));
	
	var editProblemStatus = document.getElementById("editProblemStatus");

	if( title=="" ){
		editProblemStatus.innerHTML = "Please enter the title";
	}else if( time=="" ){
		editProblemStatus.innerHTML = "Please enter the time limit";
	}else if( mem=="" ){
		editProblemStatus.innerHTML = "Please enter the memory limit";
	}else if( description=="" ){
		editProblemStatus.innerHTML = "Please enter the description";
	}else if( inputFormat=="" ){
		editProblemStatus.innerHTML = "Please enter the input format";
	}else if( outputFormat=="" ){
		editProblemStatus.innerHTML = "Please enter the output format";
	}else if( sampleInput=="" ){
		editProblemStatus.innerHTML = "Please enter the sample input";
	}else if( sampleOutput=="" ){
		editProblemStatus.innerHTML = "Please enter the sample output";
	}else{
		var checkCode = editProblemForm.checkCode.value;
		
		var url = editProblemForm.action;
		try{
			xmlHttp.open("POST",url,true);
			xmlHttp.onreadystatechange = function(){
				if( xmlHttp.readyState==4 ){
					if( xmlHttp.responseText=="success" ){
						window.location.href = "./manageproblem.jsp";
					}else{
						//editProblemStatus.innerHTML = xmlHttp.responseText;
						editProblemStatus.innerHTML = "Submit error,please try again";
					}
				}else{
					editProblemStatus.innerHTML = "Submiting...";
				}
			}
			xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			xmlHttp.send("pid="+pid+"&title="+title+"&time="+time+"&mem="+mem+"&origin="+origin+"&description="+description+
			"&inputFormat="+inputFormat+"&outputFormat="+outputFormat+"&sampleInput="+sampleInput+"&sampleOutput="+sampleOutput+
			"&hint="+hint+"&checkCode="+checkCode);
		}catch( e ){
			//editProblemStatus.innerHTML = e;
			editProblemStatus.innerHTML = "Submit error,please try again";
		}
	}
}
function setEmpty(){
	document.getElementById("editProblemStatus").innerHTML = "<br>";
}

window.onload = function(){
	setAlive(); initAJAX();
}