package com.hwf.servlet;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import com.hwf.dao.ConnectionDao;
import org.xml.sax.SAXException;

/**
 * 注销登录的servlet
 */
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String AUTO_LOGIN_NAME = null;
	private String AUTO_LOGIN_SID = null;
	private String accountTitle = null;
	private String sessionIDTitle = null;
	private String isAdmin = null;

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void init() {
		AUTO_LOGIN_NAME = this.getInitParameter("autoLoginName");
		AUTO_LOGIN_SID = this.getInitParameter("autoLoginSId");

		ServletContext application = this.getServletContext();
		accountTitle = application.getInitParameter("account");
		sessionIDTitle = application.getInitParameter("sessionID");
		isAdmin = application.getInitParameter("isAdmin");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();

		String username = (String) session.getAttribute(accountTitle);
		String SID = (String) session.getAttribute(sessionIDTitle);
		/*清除session*/
		session.removeAttribute(accountTitle);
		session.removeAttribute(sessionIDTitle);
		session.removeAttribute(isAdmin);

		/*删除cookie*/
		Cookie ckName = new Cookie(AUTO_LOGIN_NAME, null);
		ckName.setMaxAge(0);
		Cookie ckSId = new Cookie(AUTO_LOGIN_SID, null);
		ckSId.setMaxAge(0);
		response.addCookie(ckName);
		response.addCookie(ckSId);

		/*更新数据库*/
		ConnectionDao conn = new ConnectionDao();
		Statement st = null;
		try {
			conn.connection();
			st = conn.createStatement();
			String sql = "{call updateAutoLoginDatelimit(?,?,?,0)}";

			String date = sdf.format(new Date());
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, SID);
			ps.setString(3, date);

			ps.executeQuery();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		String PATH = "/contest.jsp";
		response.sendRedirect(request.getContextPath() + PATH);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request, response);
	}

}
