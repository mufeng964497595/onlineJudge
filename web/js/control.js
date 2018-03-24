function showList(o){
	o.classList.toggle("open");
}

function hideList(option){
	var dropdowns = document.getElementsByClassName("dropdown open");
	
	for( var i=0;i<dropdowns.length;i++ ){
		var openDropdown = dropdowns[i];
		if( openDropdown.id != option && openDropdown.classList.contains("open") ){
			openDropdown.classList.remove("open");
		}
	}
}

window.onclick = function(e){
	if( e.target.classList!="dropdown-toggle" && e.target.classList!="caret" ){
		hideList("");
	}
}

function setAlive(){
	var value = document.getElementById("type").value;
	var dom = document.getElementById(value);
	dom.style.backgroundColor = "#EAEAEA";
}