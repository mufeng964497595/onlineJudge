package com.hwf.servlet;

import com.hwf.dao.ConnectionDao;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用以创建竞赛内容
 */
public class CreateContestServlet extends HttpServlet {
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

		String title = request.getParameter("title");
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		int isPrivate = Integer.parseInt(request.getParameter("private"));
		String passwd = request.getParameter("passwd");
		String hint = request.getParameter("hint");
		String creator = request.getParameter("creator");
		String pidStr = request.getParameter("pids");

		// 解码
		title = URLDecoder.decode(title, "UTF-8");
		start = URLDecoder.decode(start, "UTF-8");
		end = URLDecoder.decode(end, "UTF-8");
		passwd = URLDecoder.decode(passwd, "UTF-8");
		hint = URLDecoder.decode(hint, "UTF-8");
		creator = URLDecoder.decode(creator, "UTF-8");

		// TODO 判断时间是否正确

		// 获取PID
		String pidStrs[] = pidStr.split(",");
		int pids[] = new int[pidStrs.length];
		for (int i = 0; i < pids.length; ++i) {
			pids[i] = Integer.parseInt(pidStrs[i]);
		}

		String result = "emmmm....";

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			conn = new ConnectionDao();
			conn.connection();

			String sql = "select addContestList(?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, title);
			ps.setString(2, start);
			ps.setString(3, end);
			ps.setInt(4, isPrivate);
			ps.setString(5, passwd);
			ps.setString(6, creator);
			ps.setString(7, hint);
			rs = ps.executeQuery();

			if (rs.next()) {
				int cid = rs.getInt(1);
				rs.close();
				ps.close();

				sql = "{call addContestContent(?,?,?)}";
				for (int i = 0; i < pids.length; ++i) {
					ps = conn.prepareStatement(sql);
					ps.setInt(1, cid);
					ps.setString(2, (char) (i + 'A') + "");
					ps.setInt(3, pids[i]);

					ps.executeQuery();
					ps.close();
				}
			}

			result = "success";
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();

	}

}
