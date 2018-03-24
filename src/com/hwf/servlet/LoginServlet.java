package com.hwf.servlet;

import com.hwf.dao.ConnectionDao;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 登录页面的servlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String AUTO_LOGIN_NAME = null;
	private String AUTO_LOGIN_SID = null;
	private String SAVE_NAME = null;
	private String CHECK_SESSION = null;
	private String SUCCESS = null;
	private String FAIL = null;
	private String ERROR = null;

	private String accountTitle = null;
	private String sessionIDTitle = null;
	private String isAdmin = null;

	private final String PATH = "/loginpage.jsp";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void init() {
		AUTO_LOGIN_NAME = this.getInitParameter("autoLoginName");
		AUTO_LOGIN_SID = this.getInitParameter("autoLoginSId");
		SAVE_NAME = this.getInitParameter("saveName");

		SUCCESS = this.getInitParameter("success");
		FAIL = this.getInitParameter("fail");
		ERROR = this.getInitParameter("error");

		ServletContext application = this.getServletContext();
		accountTitle = application.getInitParameter("account");
		sessionIDTitle = application.getInitParameter("sessionID");
		isAdmin = application.getInitParameter("isAdmin");
		CHECK_SESSION = application.getInitParameter("checkSessionName");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

		// 判断是否执行自动登录
		Cookie[] cookies = request.getCookies();

		String autoLoginName = "", autoLoginSId = "", saveName = "";
		int autoLoginFlag = 0;
		if (cookies != null) { // 查看是否有登录信息的cookie
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(AUTO_LOGIN_NAME)) {
					autoLoginName = cookie.getValue();
					++autoLoginFlag;
					if (autoLoginFlag == 2)
						break;
				} else if (cookie.getName().equals(AUTO_LOGIN_SID)) {
					autoLoginSId = cookie.getValue();
					++autoLoginFlag;
					if (autoLoginFlag == 2)
						break;
				} else if (cookie.getName().equals(SAVE_NAME)) {
					saveName = cookie.getValue();
				}
			}
		}

		String result;

		if (!autoLoginName.equals("") && !autoLoginSId.equals("")) {
			// 有自动登录的cookie，执行自动登录
			ConnectionDao conn = null;
			Statement stat = null;
			try {
				conn = new ConnectionDao();
				conn.connection();
				stat = conn.createStatement();

				/*检查“自动登录信息表”中是否有该cookie的信息存在且未过期*/
				String sql = "Select getDateLimit(?,?);";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, autoLoginName);
				ps.setString(2, autoLoginSId);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					/*信息存在，核对是否过期*/
					Date date = new Date();
					Date dateLimit = null;
					try {
						dateLimit = sdf.parse(rs.getString(1));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					assert dateLimit != null;
					if (date.compareTo(dateLimit) > 0) {
						/*过期，清除cookie*/
						result = saveName;

						Cookie ckName = new Cookie(AUTO_LOGIN_NAME, null);
						ckName.setMaxAge(0);
						Cookie ckSId = new Cookie(AUTO_LOGIN_SID, null);
						ckSId.setMaxAge(0);

						response.addCookie(ckName);
						response.addCookie(ckSId);
					} else {
						session.setAttribute(accountTitle, autoLoginName);
						session.setAttribute(sessionIDTitle, autoLoginSId);

						/*判断该账号是不是管理员*/
						sql = "Select isAdmin(?)";
						ps = conn.prepareStatement(sql);
						ps.setString(1, autoLoginName);
						rs = ps.executeQuery();
						if (rs.next()) {
							if (rs.getInt(1) == 1) {
								session.setAttribute(isAdmin, "true");
							}
						}

						// 将用户登录记录保存到登录记录表
						String ip = request.getRemoteAddr();
						sql = "{call addLoginHistory(?,?)}";
						ps = conn.prepareStatement(sql);
						ps.setString(1, autoLoginName);
						ps.setString(2, ip);

						ps.executeQuery();
						ps.close();

						result = "autoLogin";
					}
				} else {
					result = saveName;
				}
			} catch (SQLException | ClassNotFoundException | ParserConfigurationException | SAXException e) {
				e.printStackTrace();
				result = ERROR;
			} finally {
				try {
					if (stat != null) {
						stat.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					result = ERROR;
				}
			}
		} else {
			// 无自动登录信息
			result = saveName;
		}
		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charSet=utf-8");

		HttpSession session = request.getSession();
		/*获取参数*/
		request.setCharacterEncoding("UTF-8");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String checkCode = request.getParameter("checkCode");
		String checkSession = (String) session.getAttribute(CHECK_SESSION);

		if (checkCode == null || checkSession == null || !(checkCode + "LL").equals(checkSession)) {
			/*非法提交*/
			response.sendRedirect(request.getContextPath() + PATH);
			return;
		}

		String result;

		if (username != null && password != null) {
			try {
				ConnectionDao conn = new ConnectionDao();
				conn.connection();
				Statement stat = conn.createStatement();

				/*获取时间、IP、sessionID*/
				Date now = new Date();
				String date;
				String ip = request.getRemoteAddr();
				String sId = session.getId();

				String sql = "Select isPasswdAccept(?,?);";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, password);
				ResultSet rs = ps.executeQuery();

				if (!rs.next() || rs.getInt(1) == 0) {
					/*用户名或密码错误*/
					stat.close();
					conn.close();

					result = FAIL;
				} else {
					/*将用户登录记录保存到登录记录表*/
					sql = "{call addLoginHistory(?,?)}";
					ps = conn.prepareStatement(sql);
					ps.setString(1, username);
					ps.setString(2, ip);

					ps.executeQuery();
					ps.close();

					/*记住用户名/自动登录*/
					String saveUsername = request.getParameter("saveUsername");
					String autoLogin = request.getParameter("autoLogin");

					if (autoLogin != null) { // 勾选了自动登录
						final int DATE_LIMIT = 7;
						final int SAVE_TIME = 60 * 60 * 24 * DATE_LIMIT; // cookie过期时间为7天

						Cookie ckName = new Cookie(AUTO_LOGIN_NAME, username);
						ckName.setMaxAge(SAVE_TIME);
						Cookie ckSId = new Cookie(AUTO_LOGIN_SID, sId);
						ckSId.setMaxAge(SAVE_TIME);

						response.addCookie(ckName);
						response.addCookie(ckSId);

						// 将用户名、sessionId及过期时间存入到“自动登录信息表”中
						// 以便通过核对数据库及cookie的信息，确定是否要进行自动登录
						// 若数据库中已存在该用户名及sessionId，则更新过期时间；否则，插入新数据，这部分由存储过程实现

						Calendar calendar = new GregorianCalendar();
						calendar.setTime(now);
						calendar.add(Calendar.DATE, DATE_LIMIT);
						date = sdf.format(calendar.getTime());

						sql = "{call updateAutoLoginDatelimit(?,?,?,1)}";

						ps = conn.prepareStatement(sql);
						ps.setString(1, username);
						ps.setString(2, sId);
						ps.setString(3, date);
						ps.executeQuery();
					}
					if (saveUsername != null) { // 勾选了记住用户名
						Cookie ckName = new Cookie(SAVE_NAME, username);
						ckName.setMaxAge(60 * 60 * 24 * 365); // 默认是-1，改成一年
						response.addCookie(ckName);
					}

					/*登录成功，设置session*/
					session.setAttribute(accountTitle, username);
					session.setAttribute(sessionIDTitle, sId);
					/*判断当前账号是不是管理员*/
					sql = "Select isAdmin(?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, username);
					rs = ps.executeQuery();
					if (rs.next()) {
						if (rs.getInt(1) == 1) {
							session.setAttribute(isAdmin, "true");
						}
					}

					result = SUCCESS;

					stat.close();
					conn.close();
				}
			} catch (SQLException | ClassNotFoundException | ParserConfigurationException | SAXException e) {
				result = ERROR;
				e.printStackTrace();
			}
		} else {
			result = FAIL;
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}
}
