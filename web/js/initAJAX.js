function initAJAX(){
	if( window.XMLHttpRequest ){
		xmlHttp = new XMLHttpRequest();
 		//window.alert("support AJAX");
	}else if( window.ActiveXObject ){
		try{
			xmlHttp = new ActiveXObject("Msxm12.XMLHTTP");
 			//window.alert("support AJAX");
		}catch( e ){
			window.alert("not support AJAX");
		}
	}
}