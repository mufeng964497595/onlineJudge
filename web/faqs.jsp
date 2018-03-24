<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<link rel="stylesheet" href="css/content.css">
		<script src="js/control.js"></script>
		<title>SZU Online Judge</title>
	</head>
	<body onload="setAlive();">
		<div class="container">
			<jsp:include page="includeJSP/OJTitle.jsp"></jsp:include>
			<div class="outer">
			<div class="jumbotron" style="background-color: white; text-align: left;">
<center>
  <font size="+3">SZU Online Judge FAQS</font>
</center>
<hr>
<p><font color="green">Q</font>:这个在线裁判系统使用什么样的编译器和编译选项?<br>
  <font color="red">A</font>:系统运行于Ubuntu系统，使用GNU GCC/G++作为C/C++编译器，用java-8-oracle编译 JAVA。对应的编译选项如下:<br>
</p>
<p>  编译器版本为（系统可能升级编译器版本，这里仅供参考）:<br>
  <font color="blue">gcc (Ubuntu 5.4.0-6ubuntu1~16.04.4) 5.4.0</font><br>
  <font color="blue">java version "1.8.0_151"<br>
</font></p>
<hr>
<p><font color="green">Q</font>:程序怎样取得输入、进行输出?<br>
  <font color="red">A</font>:你的程序应该从标准输入 stdin('Standard 
Input')获取输入，并将结果输出到标准输出 stdout('Standard Output').例如,在C语言可以使用 'scanf' 
，在C++可以使用'cin' 进行输入；在C使用 'printf' ，在C++使用'cout'进行输出.</p>
<p>用户程序不允许直接读写文件, 如果这样做可能会判为运行时错误 "<font color="green">Runtime Error</font>"。<br>
  <br>
下面是编号1000作业第A题的参考答案</p>
<p>C<br></p>
<pre><font color="blue">
#include &lt;stdio.h&gt;
int main(){
    int a,b;
    while(scanf("%d %d",&amp;a, &amp;b) != EOF)
        printf("%d\n",a+b);
    return 0;
}
</font></pre>
<p> C++<br></p>
<pre><font color="blue">#include &lt;iostream&gt;
using namespace std;
int main(){
    int a,b;
    while(cin &gt;&gt; a &gt;&gt; b)
        cout &lt;&lt; a+b &lt;&lt; endl;
    return 0;
}
</font></pre>
<p>JAVA<br></p>
<pre><font color="blue">import java.util.*;
public class Main{
    public static void main(String args[]){
        Scanner cin = new Scanner(System.in);
        int a, b;
        while (cin.hasNext()){
            a = cin.nextInt(); b = cin.nextInt();
            System.out.println(a + b);
        }
        cin.close();
    }
}</font></pre>

<hr>
<p><font color="green">Q</font>:为什么我的程序在自己的电脑上正常编译，而系统告诉我编译错误!<br></p>
<p><font color="red">A</font>:GCC的编译标准与VC6有些不同，更加符合c/c++标准:<br><p>
<ul>
  <li><p><font style="color: blue; ">main</font> 函数必须返回<font color="blue">int</font>, <font color="blue">void main</font> 的函数声明会报编译错误。<br><p>
  </p><li><p><font style="color: green; ">i</font> 在循环外失去定义 "<font style="color: blue; ">for</font>(<font
                    style="color: blue; ">int</font> <font style="color: green; ">i</font>=0...){...}"<br><p>
  </li><li><p><font style="color: green; ">itoa</font> 不是ANSI标准函数.<br><p>
  </li><li><p><font style="color: green; ">__int64</font> 不是ANSI标准定义，只能在VC使用, 但是可以使用<font style="color: blue; ">long long</font>声明64位整数。<br>如果用了__int64,试试提交前加一句#define __int64 long long<p>
</li>
                <hr>
<p><font style="color: green; ">Q</font>:系统返回信息都是什么意思?<br><p>
<p><font style="color: red; ">A</font>:详见下述:<br><p>
<p><font style="color: blue; ">Queuing &amp;&amp; Judging</font> : 正在排队评判 </p><br>
<p><font style="color: blue; ">Accepted</font> : 程序通过!<br>
  <br>
  <font style="color: blue; ">Wrong Answer</font> : 答案不对，仅仅通过样例数据的测试并不一定是正确答案，一定还有你没想到的地方.<br>
  <br>
  <font color="blue">Time Limit Exceeded</font> : 运行超出时间限制，检查下是否有死循环，或者应该有更快的计算方法。<br>
  <br>
  <font color="blue">Memory Limit Exceeded</font> : 超出内存限制，数据可能需要压缩，检查内存是否有泄露。<br>
  <br>
  <font color="blue">Runtime Error</font> : 运行时错误，非法的内存访问，数组越界，指针漂移，调用禁用的系统函数。<br>
  <br>  <font color="blue">Compile Error</font> : 编译错误<br>
  <br>  <font color="blue">System Error</font> : 系统错误，可能是系统后台出问题了，不计入罚时<br>
</p>
<hr>
<p>
<font color="green">Q</font>:如何参加在线比赛?<br>
<font color="red">A</font>:<a href="registerpage.jsp">注册</a> 一个帐号，然后就可以练习，点击比赛列表Contests可以看到正在进行的比赛并参加。<br>
</p>
<br>
<hr>
<div style="float: right;">
      <a href="contest.jsp"><font color="red">SZU Online Judge</font></a>
      <font color="red">2017.12.07</font>
</div>
      </div>
		</div>
		</div>
		<jsp:include page="includeJSP/OJTail.jsp"></jsp:include>
		<input type="hidden" id="type" value="faqs">
	</body>
</html>