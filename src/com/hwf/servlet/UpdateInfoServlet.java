package com.hwf.servlet;

import com.hwf.dao.CheckParamDao;
import com.hwf.dao.ConnectionDao;
import com.hwf.dao.SetInfoIntoDatabaseDao;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 更新个人信息的servlet
 */
public class UpdateInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String CHECK_SESSION = null;

	private final String PATH = "/contest.jsp";

	@Override
	public void init() {
		ServletContext application = this.getServletContext();
		CHECK_SESSION = application.getInitParameter("checkSessionName");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath() + PATH);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charSet=utf-8");
		HttpSession session = request.getSession();

		response.setCharacterEncoding("UTF-8");
		String checkCode = request.getParameter("checkCode");
		String checkSession = (String) session.getAttribute(CHECK_SESSION);

		if (checkCode == null || checkSession == null || !(checkCode + "LL").equals(checkSession)) {
			// 非法提交
			response.sendRedirect(request.getContextPath() + PATH);
			return;
		}

		String username = request.getParameter("username");
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email");

		String result = "fail";

		boolean isCorrect = CheckParamDao.isUserInfoCorrect(username, oldPassword, email);
		if (isCorrect) {
			ConnectionDao conn = null;
			PreparedStatement ps = null;

			try {
				if (nickname != null) {
					SetInfoIntoDatabaseDao.updateUserNickname(username, nickname);
				}
				if (newPassword != null && !newPassword.equals("")) {
					SetInfoIntoDatabaseDao.updateUserPassword(username, newPassword);

					String SID = (String) session.getAttribute("sessionID");

					session.removeAttribute("account");
					session.removeAttribute("isAdmin");
					session.removeAttribute("sessionID");

					/*更新数据库*/
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					conn = new ConnectionDao();
					conn.connection();
					String sql = "{call updateAutoLoginDatelimit(?,?,?,0)}";

					String date = sdf.format(new Date());
					ps = conn.prepareStatement(sql);
					ps.setString(1, username);
					ps.setString(2, SID);
					ps.setString(3, date);

					ps.executeQuery();
				}
				result = "success";
			} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
				result = "error";
				e.printStackTrace();
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
