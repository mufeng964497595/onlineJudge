package com.hwf.servlet;

import com.hwf.dao.ConnectionDao;
import com.hwf.deal.JudgeSubmitCode;
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
import java.util.Date;

/**
 * 提交代码的servlet
 */
public class SubmitCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String CHECK_SESSION = null;

	private final String PATH = "/contest.jsp";

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

		String cid = request.getParameter("cid");
		String pno = request.getParameter("pno");
		String username = request.getParameter("username");
		final String language = request.getParameter("language");
		String share = request.getParameter("share");
		String code = request.getParameter("code");

		code = URLDecoder.decode(code, "UTF-8");

		String result = "";
		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs;

		/*比较两次提交的时间间隔*/
		Date now = new Date();
		Date last = (Date) session.getAttribute("submitDate");

		if (last != null) {
			long a = now.getTime();
			long b = last.getTime();
			long submit_limit = 30;// 每30秒只能提交一次
			if ((a - b) / 1000 < submit_limit) {
				result = "limit";
			}
		}

		if (result.equals("")) {// 满足提交条件
			try {
				conn = new ConnectionDao();
				conn.connection();

				String sql = "{call addSubmitStatus(?,?,?,?,?,?,?,?,?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, cid);
				ps.setString(3, pno);
				ps.setString(4, "7");
				ps.setDouble(5, 0.0);
				ps.setDouble(6, 0.0);
				ps.setInt(7, code.length());
				ps.setString(8, language);
				ps.setString(9, code);
				ps.setString(10, share);

				rs = ps.executeQuery();
				long runId = 0;
				if (rs.next())
					runId = rs.getLong(1);

				session.setAttribute("submitDate", now);

				result = "success";

				//TODO 要改，按队列来
				final JudgeSubmitCode testSubmitCode = new JudgeSubmitCode(cid, pno, code, runId);
				new Thread(new Runnable() {
					@Override
					public void run() {
						switch (language) {
							case "1": testSubmitCode.testCCode();  break;
							case "2": testSubmitCode.testCppCode(); break;
							default: testSubmitCode.testJavaCode(); break;
						}
					}
				}).start();

			} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (ps != null)
						ps.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
