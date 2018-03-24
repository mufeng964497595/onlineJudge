package com.hwf.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import com.hwf.dao.ConnectionDao;
import org.xml.sax.SAXException;

/**
 * 添加问题的servlet
 */

// TODO 记录添加问题操作

public class AddProblemServlet extends HttpServlet {
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

		String title = request.getParameter("title");
		String time = request.getParameter("time");
		String mem = request.getParameter("mem");
		String origin = request.getParameter("origin");
		String description = request.getParameter("description");
		String inputFormat = request.getParameter("inputFormat");
		String outputFormat = request.getParameter("outputFormat");
		String sampleInput = request.getParameter("sampleInput");
		String sampleOutput = request.getParameter("sampleOutput");
		String hint = request.getParameter("hint");
		// String testInput = request.getParameter("testInput");
		// String testOutput = request.getParameter("testOutput");

		// 若为null则转为空串
		if (origin == null) {
			origin = "";
		}
		if (hint == null) {
			hint = "";
		}

		// 解码
		title = URLDecoder.decode(title, "UTF-8");
		time = URLDecoder.decode(time, "UTF-8");
		mem = URLDecoder.decode(mem, "UTF-8");
		origin = URLDecoder.decode(origin, "UTF-8");
		description = URLDecoder.decode(description, "UTF-8");
		inputFormat = URLDecoder.decode(inputFormat, "UTF-8");
		outputFormat = URLDecoder.decode(outputFormat, "UTF-8");
		sampleInput = URLDecoder.decode(sampleInput, "UTF-8");
		sampleOutput = URLDecoder.decode(sampleOutput, "UTF-8");
		hint = URLDecoder.decode(hint, "UTF-8");

		System.out.println(time);

		String result = "";
		ConnectionDao conn = new ConnectionDao();
		Statement st = null;
		try {
			conn.connection();
			st = conn.createStatement();

			// 题目信息写入数据库
			String sql = "select addProblemContent(?,?,?,?,?,?,?,?,?,?);";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, title);
			ps.setString(2, time);
			ps.setString(3, mem);
			ps.setString(4, origin);
			ps.setString(5, description);
			ps.setString(6, inputFormat);
			ps.setString(7, outputFormat);
			ps.setString(8, sampleInput);
			ps.setString(9, sampleOutput);
			ps.setString(10, hint);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				// 将sample数据写入数据库
				int pid = rs.getInt(1);
				sql = "{call addProblemTestData(?,?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, pid);
				ps.setString(2, sampleInput);
				ps.setString(3, sampleOutput);
				ps.executeQuery();
				ps.close();
				// 将test数据写入数据库
				// sql = "{call addProblemTestData(?,?,?)}";
				// ps = conn.prepareStatement(sql);
				// ps.setInt(1, pid);
				// ps.setString(2, testInput);
				// ps.setString(3, testOutput);
				// ps.executeQuery();
				// ps.close();

				result = "success";
			}
			rs.close();
			ps.close();
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
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
