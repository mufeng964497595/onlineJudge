<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="./css/bootstrap.min.css">
    <link rel="stylesheet" href="./css/content.css">
    <script src="./js/control.js"></script>
    <title>SZU Online Judge</title>
</head>
<body onload="setAlive();">
<div class="container">
    <jsp:include page="./includeJSP/OJTitle.jsp"></jsp:include>
    <div class="outer">
        <div class="jumbotron" style="background-color: white; text-align: left;">
            <center>
                <font size="+3">SZU Online Judge FAQS</font>
            </center>
            <hr>
            <p><font color="green">Q</font>:这个在线裁判系统使用什么样的编译器和编译选项?<br>
                <font color="red">A</font>:系统运行于Ubuntu系统，使用GNU GCC/G++作为C/C++编译器，用java-8-oracle编译 JAVA。对应的编译选项如下:<br>
            </p>
            <p>
            <table data-anchor-id="4e1e" class="table table-striped-white table-bordered">
                <thead>
                <tr>
                    <th>编程语言</th>
                    <th>编译指令</th>
                </tr>
                </thead>
                <tbody style="text-align: center;"><tr>
                    <td>C</td>
                    <td>gcc -Wall -lm Main.c -o Main</td>
                </tr>
                <tr>
                    <td>C++</td>
                    <td>g++ -Wall -fno-asm -lm -std=c++11 Main.cpp -o Main</td>
                </tr>
                <tr>
                    <td>JAVA</td>
                    <td>javac -J-Xms32m -J-Xmx64m -encoding UTF-8 Main.java</td>
                </tr>
                </tbody></table>
            </p>

            <p>  编译器版本为（系统可能升级编译器版本，这里仅供参考）:<br>
                <font color="blue">gcc (Ubuntu 5.4.0-6ubuntu1~16.04.4) 5.4.0</font><br>
                <font color="blue">g++ (Ubuntu 5.4.0-6ubuntu1~16.04.9) 5.4.0</font><br>
                <font color="blue">java version "1.8.0_151"<br>
                </font></p>
            <hr>
            <p><font color="green">Q</font>:程序怎样取得输入、进行输出?<br>
                <font color="red">A</font>:你的程序应该从标准输入 stdin('Standard
                Input')获取输入，并将结果输出到标准输出 stdout('Standard Output').例如,在C语言可以使用 'scanf'
                ，在C++可以使用'cin' 进行输入；在C使用 'printf' ，在C++使用'cout'进行输出.</p>
            <br> <br>
            <p>下面是编号1000作业第A题的参考答案</p>
            <p>C<br></p>
            <pre><font color="blue">#include &lt;stdio.h&gt;
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
                <li><p><span style="color: blue; ">main</span> 函数必须返回<font color="blue">int</font>, <font color="blue">void main</font> 的函数声明会报编译错误。<br><p>
        </p><li><p><font color="green">i</font> 在循环外失去定义 "<font color="blue">for</font>(<font color="blue">int</font> <font color="green">i</font>=0...){...}"<br><p>
        </li><li><p><font color="green">itoa</font> 不是ANSI标准函数.<br><p>
        </li><li><p><font color="green">__int64</font> 不是ANSI标准定义，只能在VC使用, 但是可以使用<font color="blue">long long</font>声明64位整数。<br>如果用了__int64,试试提交前加一句#define __int64 long long<p>
        </li></ul>
            <hr>
            <p><font color="green">Q</font>:系统返回信息都是什么意思?<br><p>
            <p><font color="red">A</font>:详见下述:<br><p>

            <ul data-anchor-id="jzsc">
                <li><p><font color="blue">Accepted</font>: 程序通过。</p></li>
                <li><p><font color="blue">Wrong Answer</font>: 答案不对，仅仅通过样例数据的测试并不一定是正确答案，一定还有你没想到的地方。</p></li>
                <li><p><font color="blue">Time Limit Exceeded</font>: 运行超出时间限制，检查下是否有死循环，或者应该有更快的算法。</p></li>
                <li><p><font color="blue">Memory Limit Exceeded</font>: 超出内存限制，数据可能需要压缩，检查内存是否有泄露。</p></li>
                <li><p><font color="blue">Runtime Error</font>: 运行时错误，非法的内存访问，数组越界，指针漂移，调用禁用的系统函数等。</p></li>
                <li><p><font color="blue">Output Limit Exceeded</font>: 输出超限，意味着程序输出的数据大小超出答案输出的一倍。</p></li>
                <li><p><font color="blue">Compile Error</font>: 编译错误，不计入罚时。</p></li>
                <li><p><font color="blue">System Error</font>: 系统错误，可能是系统后台出问题了，不计入罚时。</p></li>
                <li><p><font color="blue">Queueing</font>: 排队等待评测中。</p></li>
                <li><p><font color="blue">Compiling</font>: 正在编译程序。</p></li>
                <li><p><font color="blue">Running</font>: 正在运行程序。</p></li>
            </ul><hr>
            <p>
                <font color="green">Q</font>:如何参加在线比赛?<br>
                <font color="red">A</font>:<a href="./registerpage.php">注册</a> 一个帐号，然后就可以练习，点击比赛列表Contests可以看到正在进行的比赛并参加。<br>
            </p>
            <br>
            <hr>
            <div style="float: right;">
                <a href="./contest.jsp"><font color="red">SZUCPC Online Judge</font></a>
                <font color="red">2017.12.07</font>
            </div>
        </div>
    </div>
</div>
<jsp:include page="./includeJSP/OJTail.jsp"></jsp:include>
<input type="hidden" id="type" value="faqs">
</body>
</html>