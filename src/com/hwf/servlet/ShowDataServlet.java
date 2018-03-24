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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 展示题目测试数据的servlet
 */
public class ShowDataServlet extends HttpServlet {
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
		String id = request.getParameter("id");
		String type = request.getParameter("type");

		if (checkCode == null || checkSession == null || !(checkCode + "LL").equals(checkSession) || id == null || type == null) {
			// 非法提交
			response.sendRedirect(request.getContextPath() + PATH);
			return;
		}

		boolean isInput;
		switch (type) {
			case "in": isInput = true; break;
			case "out": isInput = false; break;
			default: response.sendRedirect(request.getContextPath() + PATH); return;
		}

		String result = "";
		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql;
			if (isInput)
				sql = "{call getInputData(?)}";
			else
				sql = "{call getOutputData(?)}";

			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getString(1);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
