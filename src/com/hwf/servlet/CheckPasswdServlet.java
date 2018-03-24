package com.hwf.servlet;

import com.hwf.dao.ConnectionDao;
import org.xml.sax.SAXException;

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
 * 核对密码的servlet
 */
public class CheckPasswdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String cid = request.getParameter("cid");
		String passwd = request.getParameter("passwd");
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("account");

		if (cid == null || passwd == null || username == null)
			return;

		String result = "";

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call isContestPasswdCorrect(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, passwd);
			rs = ps.executeQuery();

			if (rs.next() && rs.getInt(1) == 1) {
				result = "success";

				/*更新数据库*/
				rs.close();
				ps.close();

				sql = "{call addUserLoginContest(?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, cid);
				ps.executeQuery();
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException e) {
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
				e.printStackTrace();
			}
		}

		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
	}

}
