<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/content.css">
		<title>SZUCPC Online Judge</title>
	</head>
	<body>
		<div style="width: 50%;margin: 0 auto;margin-top: 30px;">
			<img src="picture/500.png" style="width: 50%;">
			<p style="font-size: 20px;"><span>立刻<a id="href" href="contest.jsp">返回主页</a></span>  <span>等待<b id="wait">4</b>秒</span></p>
				<script type="text/javascript">                            (function() {
						var wait = document.getElementById('wait'), href = document.getElementById('href').href;
						var interval = setInterval(function() {
							var time = --wait.innerHTML;
							if (time <= 0) {
								
								clearInterval(interval);
								window.location.href = href;
							}
							;
						}, 1000);
					})();
				</script>
		</div>	
	</body>
</html>