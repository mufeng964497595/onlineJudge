function clock(){
	var x,h,m,s,n,xingqi,y,mon,d;
	var x = new Date(new Date().getTime()+diff);
	y = x.getYear()+1900;
	if (y>3000) y-=1900;
	mon = x.getMonth()+1;
	d = x.getDate();
	xingqi = x.getDay();
	h=x.getHours();
	m=x.getMinutes();
	s=x.getSeconds();
	n=y+"-"+changeNum(mon)+"-"+changeNum(d)+" "+changeNum(h)+":"+changeNum(m)+":"+changeNum(s);
	//alert(n);
	document.getElementById("nowdate").innerText=n;
	setTimeout("clock()",1000);
}
//如果是一位就补0
function changeNum(num){
    if(num<10){
        return '0'+num;
    }
    return num;
}