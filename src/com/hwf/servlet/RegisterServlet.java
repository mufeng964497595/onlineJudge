package com.hwf.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import com.hwf.dao.ConnectionDao;
import org.xml.sax.SAXException;

/**
 * 注册页面的servlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String CHECK_SESSION = null;

	public void init() {
		ServletContext application = this.getServletContext();
		CHECK_SESSION = application.getInitParameter("checkSessionName");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		String PATH = "/registerpage.jsp";
		response.sendRedirect(request.getContextPath() + PATH);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charSet=utf-8");

		HttpSession session = request.getSession();

		// 获取参数
		request.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email");
		String checkCode = request.getParameter("checkCode");
		String checkSession = (String) session.getAttribute(CHECK_SESSION);

		String result = "";

		// 合法提交
// session.removeAttribute(CHECK_SESSION);
// 连接数据库
		if (checkCode != null && checkSession != null && (checkCode + "LL").equals(checkSession) && username != null && password != null) {
			try {
				ConnectionDao conn = new ConnectionDao();
				conn.connection();
				Statement stat = conn.createStatement();

				String ip = request.getRemoteAddr();

				String sql = "select isUsernameExist(?)";
				// String sql = "Select Username From " + USERS_DATABASE_NAME + " Where Username=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ResultSet rs = ps.executeQuery();

				if (rs.next() && rs.getInt(1) != 0) {
					// 用户名已存在
					result = "same";
				} else {
					// 检查用户名是否含有非法字符
					int i;
					String[] banString = {"管理员", "&", "=", "<", ">", "\"", "'", " ", "\\", "null", "%"};
					for (i = 0; i < banString.length; ++i) {
						if (username.contains(banString[i])) {
							result = "fail";
							break;
						}
					}

					if (i >= banString.length) {
						// 将注册信息写入数据库
						sql = "{call addUser(?,?,?,?,0,?)}";
						// sql = "Insert Into " + USERS_DATABASE_NAME + " (Username,Passwd,NickName,Email,Date,IP) Values(?,MD5(?),?,?,?,?)";
						ps = conn.prepareStatement(sql);
						ps.setString(1, username);
						ps.setString(2, password);
						ps.setString(3, nickname);
						ps.setString(4, email);
						ps.setString(5, ip);
						ps.executeUpdate();

						// 注册成功
						result = "success";
					}
				}
				// 关闭连接
				stat.close();
				conn.close();
			} catch (SQLException | ClassNotFoundException | ParserConfigurationException | SAXException e) {
				result = "error";
				e.printStackTrace();
			}
		} else {
			result = "error";
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
