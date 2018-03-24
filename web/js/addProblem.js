function addPro(){
	var button = $("#addProblem");
	button.disabled = true;
	
	var title = addProblemForm.title.value;
	var time = addProblemForm.time.value;
	var mem = addProblemForm.mem.value;
	var origin = addProblemForm.origin.value;
	var description = $("#description").html();
	var inputFormat = $("#inputFormat").html();
	var outputFormat = $("#outputFormat").html();
	var hint = $("#hint").html();
	var sampleInput = addProblemForm.sampleInput.value;
	var sampleOutput = addProblemForm.sampleOutput.value;
//	var testInput = addProblemForm.testInput.value;
//	var testOutput = addProblemForm.testOutput.value;
/*
	title = title.replace(/\+/g,'%2B');
	time = time.replace(/\+/g,'%2B');
	mem = mem.replace(/\+/g,'%2B');
	origin = origin.replace(/\+/g,'%2B');
	description = description.replace(/\+/g,'%2B');
	inputFormat = inputFormat.replace(/\+/g,'%2B');
	outputFormat = outputFormat.replace(/\+/g,'%2B');
	sampleInput = sampleInput.replace(/\+/g,'%2B');
	sampleOutput = sampleOutput.replace(/\+/g,'%2B');
	hint = hint.replace(/\+/g,'%2B');
	testInput = testInput.replace(/\+/g,'%2B');
	testOutput = testOutput.replace(/\+/g,'%2B');
	
	title = encodeURI(encodeURI(title));
	time = encodeURI(encodeURI(time));
	mem = encodeURI(encodeURI(mem));
	origin = encodeURI(encodeURI(origin));
	description = encodeURI(encodeURI(description));
	inputFormat = encodeURI(encodeURI(inputFormat));
	outputFormat = encodeURI(encodeURI(outputFormat));
	sampleInput = encodeURI(encodeURI(sampleInput));
	sampleOutput = encodeURI(encodeURI(sampleOutput));
	hint = encodeURI(encodeURI(hint));
	testInput = encodeURI(encodeURI(testInput));
	testOutput = encodeURI(encodeURI(testOutput));
	*/
	//编码，以避免中文乱码，同时，将特殊字符也进行编码，在后台解码
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
//	testInput = encodeURIComponent(encodeURIComponent(testInput));
//	testOutput = encodeURIComponent(encodeURIComponent(testOutput));
	
	var addProblemStatus = document.getElementById("addProblemStatus");

	if( title=="" ){
		addProblemStatus.innerHTML = "Please enter the title";
	}else if( time=="" ){
		addProblemStatus.innerHTML = "Please enter the time limit";
	}else if( mem=="" ){
		addProblemStatus.innerHTML = "Please enter the memory limit";
	}else if( description=="" ){
		addProblemStatus.innerHTML = "Please enter the description";
	}else if( inputFormat=="" ){
		addProblemStatus.innerHTML = "Please enter the input format";
	}else if( outputFormat=="" ){
		addProblemStatus.innerHTML = "Please enter the output format";
	}else if( sampleInput=="" ){
		addProblemStatus.innerHTML = "Please enter the sample input";
	}else if( sampleOutput=="" ){
		addProblemStatus.innerHTML = "Please enter the sample output";
//	}else if( testInput=="" ){
//		addProblemStatus.innerHTML = "Please enter the test input";
//	}else if( testOutput=="" ){
//		addProblemStatus.innerHTML = "Please enter the test output";
	}else{
		//addProblemForm.submit();
		var checkCode = addProblemForm.checkCode.value;
		
		/*var url = addProblemForm.action+"?title="+title+"&time="+time+"&mem="+mem+"&origin="+origin+"&description="+description+
			"&inputFormat="+inputFormat+"&outputFormat="+outputFormat+"&sampleInput="+sampleInput+"&sampleOutput="+sampleOutput+
			"&hint="+hint+"&testInput="+testInput+"&testOutput="+testOutput+"&checkCode="+checkCode;
			* */
		var url = addProblemForm.action;
		try{
			xmlHttp.open("POST",url,true);
			xmlHttp.onreadystatechange = function(){
				if( xmlHttp.readyState==4 ){
					if( xmlHttp.responseText=="success" ){
						window.location.href = "./manageproblem.jsp";
					}else{
						addProblemStatus.innerHTML = "Submit error,please try again";
					}
				}else{
					addProblemStatus.innerHTML = "Submiting...";
				}
			}
			xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			xmlHttp.send("title="+title+"&time="+time+"&mem="+mem+"&origin="+origin+"&description="+description+
			"&inputFormat="+inputFormat+"&outputFormat="+outputFormat+"&sampleInput="+sampleInput+"&sampleOutput="+sampleOutput+
			"&hint="+hint+
			//"&testInput="+testInput+"&testOutput="+testOutput+
			"&checkCode="+checkCode);
		}catch( e ){
			addProblemStatus.innerHTML = "Submit error,please try again";
		}
	}
	
	button.disabled = false;
}
function setEmpty(){
	document.getElementById("addProblemStatus").innerHTML = "<br>";
}

window.onload = function(){
	initAJAX();
}