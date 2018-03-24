package com.hwf.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hwf.bean.ContestContentBean;
import com.hwf.bean.ContestListBean;
import com.hwf.bean.InputOutputDataBean;
import com.hwf.bean.ProblemBean;
import com.hwf.bean.StatusBean;
import com.hwf.bean.TotalRankBean;
import com.hwf.bean.UserContestProblemInfoBean;
import com.hwf.bean.UserContestRankBean;

/**
 * 工具类，用以获取数据库的信息
 */
public class GetInfoFromDatabaseDao {
	private static int pageRow = 50;/*每页最多50条数据*/

	public static ArrayList<ContestListBean> getContestList(long pageNow) {
		ArrayList<ContestListBean> contestList = new ArrayList<>();

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		long top = (pageNow - 1) * pageNow;
		if (top < 0)
			top = 0;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call showContestList(?)}";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, top);
			rs = ps.executeQuery();

			while (rs.next()) {
				ContestListBean bean = new ContestListBean();
				bean.setCid(rs.getInt(1));
				bean.setTitle(rs.getString(2));
				bean.setStartDate(rs.getString(3));
				bean.setEndDate(rs.getString(4));
				bean.setPriv(rs.getInt(5) == 1);
				bean.setCreator(rs.getString(6));

				contestList.add(bean);
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return contestList;
	}

	public static long getContestListTotalPage() {
		long num = 1;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getContestListNum()}";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {
				num = rs.getLong(1);

				if (num % pageRow == 0) {
					num /= pageRow;
					if (num == 0)
						num = 1;
				} else {
					num = num / pageRow + 1;
				}
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return num;
	}

	public static ArrayList<ContestListBean> searchContestListWithTitle(String search, long pageNow) {
		ArrayList<ContestListBean> contestList = new ArrayList<>();

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		if (search == null)
			search = "";
		search = "%" + search + "%";

		long top = (pageNow - 1) * pageRow;
		if (top < 0)
			top = 0;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call searchContestListWithTitle(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, search);
			ps.setLong(2, top);
			rs = ps.executeQuery();

			while (rs.next()) {
				ContestListBean bean = new ContestListBean();
				bean.setCid(rs.getInt(1));
				bean.setTitle(rs.getString(2));
				bean.setStartDate(rs.getString(3));
				bean.setEndDate(rs.getString(4));
				bean.setPriv(rs.getInt(5) == 1);
				bean.setCreator(rs.getString(6));

				contestList.add(bean);
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return contestList;
	}

	public static long getSearchContestListTotalPage(String search) {
		long num = 1;

		if (search == null)
			search = "";
		search = "%" + search + "%";

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getSearchContestListNum(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, search);
			rs = ps.executeQuery();

			if (rs.next()) {
				num = rs.getLong(1);

				if (num % pageRow == 0) {
					num /= pageRow;
					if (num == 0)
						num = 1;
				} else {
					num = num / pageRow + 1;
				}
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return num;
	}

	public static ArrayList<ContestContentBean> getContestContent(String cid, String username, boolean isShowContest) {
		ArrayList<ContestContentBean> contestList = new ArrayList<>();

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call showContestContent(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			rs = ps.executeQuery();

			while (rs.next()) {
				ContestContentBean bean = new ContestContentBean();

				bean.setPno(rs.getString(1));
				bean.setOrigin(rs.getString(2));
				bean.setTitle(rs.getString(3));

				contestList.add(bean);
			}

			rs.close();
			ps.close();

			for (ContestContentBean bean : contestList) {
				if (isShowContest)
					sql = "{call showMyStatusOntime(?,?,?)}";
				else
					sql = "{call showMyStatus(?,?,?)}";

				ps = conn.prepareStatement(sql);
				ps.setString(1, cid);
				ps.setString(2, bean.getPno());
				ps.setString(3, username);

				rs = ps.executeQuery();
				if (rs.next()) {
					bean.setMyStatus(rs.getInt(1));
				}

				rs.close();
				ps.close();

				if (isShowContest)
					sql = "{call showContentSubmitInfoOntime(?,?)}";
				else
					sql = "{call showContentSubmitInfo(?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, cid);
				ps.setString(2, bean.getPno());

				rs = ps.executeQuery();
				if (rs.next()) {
					bean.setAccept(rs.getInt(1));
					bean.setSubmit(rs.getInt(2) + bean.getAccept());
				} else {
					bean.setAccept(0);
					bean.setSubmit(0);
				}

				rs.close();
				ps.close();
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return contestList;
	}

	public static String getContestHint(String cid) {
		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String hint = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getContestHint(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			rs = ps.executeQuery();

			if (rs.next()) {
				hint = rs.getString(1);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return hint;
	}

	public static ProblemBean getProblem(String cid, String pno) {
		ProblemBean problemBean = null;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call showProblem(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, pno);

			rs = ps.executeQuery();

			if (rs.next()) {
				problemBean = new ProblemBean();

				// Contest InputDesc OutputDesc HintΪ���ı��༭�����ɵģ�����Ҫת��ʽ
				problemBean.setPid(rs.getInt(1));
				problemBean.setTitle(replaceString(rs.getString(2)));
				problemBean.setTimeLimit(rs.getDouble(3));
				problemBean.setMemLimit(rs.getDouble(4));
				problemBean.setOrigin(replaceString(rs.getString(5)));
				problemBean.setContent(rs.getString(6));
				problemBean.setInputDesc(rs.getString(7));
				problemBean.setOutputDesc(rs.getString(8));
				problemBean.setSampleInput(replaceString(rs.getString(9)));
				problemBean.setSampleOutput(replaceString(rs.getString(10)));
				problemBean.setHint(rs.getString(11));
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return problemBean;
	}

	public static String getProblemTitle(String cid, String pno) {
		if (cid == null || pno == null)
			return null;

		String result = null;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getProblemTitleByPno(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, pno);

			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return result;
	}

	public static ArrayList<StatusBean> getStatus(String cid, String pno, String username, String result, String language, boolean isCidCorrect, boolean isShowContest,
			long pageNow) {
		ArrayList<StatusBean> statusList = new ArrayList<>();

		if (pno == null || pno.equals(""))
			pno = "%";
		if (username == null || username.equals(""))
			username = "%";
		else
			username = "%" + username + "%";
		if (result == null || result.equals("") || result.equals("0"))
			result = "%";
		if (language == null || language.equals("") || language.equals("0"))
			language = "%";

		long top = (pageNow - 1) * pageRow;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();

			if (!isCidCorrect) {
				String sql = "{call getAllContestSubmitStatus(?,?,?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, result);
				ps.setString(3, language);
				ps.setLong(4, top);
				rs = ps.executeQuery();
			} else {
				String sql;
				if (isShowContest)
					sql = "{call getSubmitStatusOntime(?,?,?,?,?,?)}";
				else
					sql = "{call getSubmitStatus(?,?,?,?,?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, cid);
				ps.setString(2, pno);
				ps.setString(3, username);
				ps.setString(4, result);
				ps.setString(5, language);
				ps.setLong(6, top);
				rs = ps.executeQuery();
			}

			while (rs.next()) {
				StatusBean bean = new StatusBean();
				bean.setRunId(rs.getLong(1));
				bean.setUsername(rs.getString(2));
				bean.setCid(rs.getInt(3));
				bean.setPno(rs.getString(4));
				bean.setResult(rs.getString(5));
				bean.setTime(rs.getDouble(6));
				bean.setMem(rs.getDouble(7));
				bean.setLength(rs.getInt(8));
				bean.setLanguage(rs.getString(9));
				bean.setShared(rs.getString(10));
				bean.setCode(rs.getString(11));
				bean.setSubmitDate(rs.getString(12));

				statusList.add(bean);
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return statusList;
	}

	public static long getStatusTotalPage(String cid, String pno, String username, String result, String language, boolean isCidCorrect, boolean isShowContest) {
		if (pno == null || pno.equals(""))
			pno = "%";
		if (username == null || username.equals(""))
			username = "%";
		else
			username = "%" + username + "%";
		if (result == null || result.equals("") || result.equals("0"))
			result = "%";
		if (language == null || language.equals("") || language.equals("0"))
			language = "%";

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		long num = 1;

		try {
			conn = new ConnectionDao();
			conn.connection();

			if (!isCidCorrect) {
				String sql = "{call getAllContestSubmitStatusNum(?,?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, result);
				ps.setString(3, language);
				rs = ps.executeQuery();
			} else {
				String sql;

				if (isShowContest)
					sql = "{call getSubmitStatusNumOntime(?,?,?,?,?)}";
				else
					sql = "{call getSubmitStatusNum(?,?,?,?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, cid);
				ps.setString(2, pno);
				ps.setString(3, username);
				ps.setString(4, result);
				ps.setString(5, language);
				rs = ps.executeQuery();
			}

			if (rs.next()) {
				num = rs.getLong(1);

				if (num % pageRow == 0) {
					num /= pageRow;
					if (num == 0)
						num = 1;
				} else {
					num = num / pageRow + 1;
				}
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return num;
	}

	public static ArrayList<TotalRankBean> getTotalRankList(long pageNow) {
		ArrayList<TotalRankBean> totalRankList = new ArrayList<>();

		long top = (pageNow - 1) * pageRow;
		if (top < 0)
			top = 0;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getTotalRankList(?)}";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, top);
			rs = ps.executeQuery();

			for (int i = 1; rs.next(); ++i) {
				TotalRankBean bean = new TotalRankBean();
				bean.setRank(i);
				bean.setUsername(rs.getString(1));
				bean.setNickname(rs.getString(2));
				bean.setAccept(rs.getInt(3));
				bean.setSubmit(rs.getInt(4));
				bean.setAcRate(rs.getDouble(5));

				totalRankList.add(bean);
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return totalRankList;
	}

	public static long getRankListTotalPage() {
		long num = 1;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getTotalRankListNum()}";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {
				num = rs.getLong(1);

				if (num % pageRow == 0) {
					num /= pageRow;
					if (num == 0)
						num = 1;
				} else {
					num = num / pageRow + 1;
				}
			}

		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return num;
	}

	public static ArrayList<UserContestRankBean> getUserContestRank(String cid, boolean isShowContest) {
		ArrayList<UserContestRankBean> userContestRank = new ArrayList<>();

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql;
			if (isShowContest)
				sql = "{call getContestRankListOntime(?)}";
			else
				sql = "{call getContestRankList(?)}";

			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			rs = ps.executeQuery();

			for (int i = 1; rs.next(); ++i) {
				UserContestRankBean bean = new UserContestRankBean();
				bean.setRank(i);
				bean.setUsername(rs.getString(1));
				bean.setNickname(rs.getString(2));
				bean.setSolve(rs.getInt(3));
				bean.setPenalty(rs.getInt(4));

				userContestRank.add(bean);
			}

			for (UserContestRankBean userContestBean : userContestRank) {
				ArrayList<UserContestProblemInfoBean> problemInfo = new ArrayList<>();

				rs.close();
				ps.close();

				if (isShowContest)
					sql = "{call getContestUserInfoOntime(?,?)}";
				else
					sql = "{call getContestUserInfo(?,?)}";

				ps = conn.prepareStatement(sql);
				ps.setString(1, cid);
				ps.setString(2, userContestBean.getUsername());
				rs = ps.executeQuery();

				while (rs.next()) {
					UserContestProblemInfoBean bean = new UserContestProblemInfoBean();
					bean.setPno(rs.getString(1));
					bean.setWrong(rs.getInt(2));
					bean.setAc(rs.getInt(3));
					bean.setAcMinute(rs.getString(4));
					bean.setFirstBlood(rs.getInt(5));

					problemInfo.add(bean);
				}

				userContestBean.setProblemInfo(problemInfo);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return userContestRank;
	}

	public static ArrayList<InputOutputDataBean> getInputOutputData(String cid, String pno) {
		ArrayList<InputOutputDataBean> inputOutputData = new ArrayList<>();

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getInputOutputData(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			ps.setString(2, pno);
			rs = ps.executeQuery();

			while (rs.next()) {
				InputOutputDataBean bean = new InputOutputDataBean();
				bean.setInputData(rs.getString(1));
				bean.setOutputData(rs.getString(2));

				inputOutputData.add(bean);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return inputOutputData;
	}

	public static String getSubmitCodeByRunId(String runId) {
		String code = "";

		if (runId != null) {
			ConnectionDao conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				conn = new ConnectionDao();
				conn.connection();
				String sql = "{call getSubmitCode(?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, runId);
				rs = ps.executeQuery();

				if (rs.next()) {
					code = rs.getString(1);
				}
			} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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
		}

		return code;
	}

	public static boolean isNeedPasswd(String username, String cid) {
		boolean ans = true;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call isContestPrivate(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			rs = ps.executeQuery();

			if (!rs.next() || rs.getString(1).equals("0")) {
				ans = false;
			} else {
				rs.close();
				ps.close();

				sql = "{call isUserLoginContest(?,?)}";
				ps = conn.prepareStatement(sql);
				ps.setString(1, username);
				ps.setString(2, cid);
				rs = ps.executeQuery();

				if (rs.next() && rs.getInt(1) == 1) {
					ans = false;
				}
			}

		} catch (ClassNotFoundException |

				SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return ans;
	}

	public static ContestListBean getContestDate(String cid) {
		ContestListBean contest = null;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getContestDate(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cid);
			rs = ps.executeQuery();

			if (rs.next()) {
				contest = new ContestListBean();
				contest.setStartDate(rs.getString(1));
				contest.setEndDate(rs.getString(2));
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return contest;
	}

	public static ArrayList<ProblemBean> getPidAndTitle(String search, long pageNow) {
		ArrayList<ProblemBean> list = new ArrayList<>();

		if (search == null || search.equals(""))
			search = "%";
		else
			search = "%" + search + "%";

		long top = (pageNow - 1) * pageRow;
		if (top < 0)
			top = 0;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getPidAndTitle(?,?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, search);
			ps.setLong(2, top);
			rs = ps.executeQuery();
			while (rs.next()) {
				ProblemBean bean = new ProblemBean();
				bean.setPid(rs.getInt(1));
				bean.setTitle(rs.getString(2));

				list.add(bean);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return list;
	}

	public static long getProblemTotalPage(String search) {
		long num = 1;

		if (search == null || search.equals(""))
			search = "%";
		else
			search = "%" + search + "%";

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getProblemListNum(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, search);
			rs = ps.executeQuery();

			if (rs.next()) {
				num = rs.getLong(1);

				if (num % pageRow == 0) {
					num /= pageRow;
					if (num == 0)
						num = 1;
				} else {
					num = num / pageRow + 1;
				}
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return num;
	}

	public static ProblemBean getProblem(String pid) {
		ProblemBean problemBean = null;

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getProblemByPid(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			rs = ps.executeQuery();

			if (rs.next()) {
				problemBean = new ProblemBean();

				problemBean.setPid(rs.getInt(1));
				problemBean.setTitle(rs.getString(2));
				problemBean.setTimeLimit(rs.getDouble(3));
				problemBean.setMemLimit(rs.getDouble(4));
				problemBean.setOrigin(rs.getString(5));
				problemBean.setContent(rs.getString(6));
				problemBean.setInputDesc(rs.getString(7));
				problemBean.setOutputDesc(rs.getString(8));
				problemBean.setSampleInput(rs.getString(9));
				problemBean.setSampleOutput(rs.getString(10));
				problemBean.setHint(rs.getString(11));
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return problemBean;
	}

	public static ArrayList<Integer> getInputOutputDataId(String pid) {
		ArrayList<Integer> list = new ArrayList<>();

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();

			String sql = "{call getInputOutputDataId(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt(1));
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return list;
	}

	public static String getNickname(String username) {
		String nickName = "";

		ConnectionDao conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new ConnectionDao();
			conn.connection();
			String sql = "{call getNickname(?)}";
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			rs = ps.executeQuery();

			if (rs.next()) {
				nickName = rs.getString(1);
			}
		} catch (ClassNotFoundException | SQLException | ParserConfigurationException | SAXException | IOException e) {
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

		return nickName;
	}

	public static boolean isStarted(String cid) {
		boolean result = false;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ContestListBean bean = getContestDate(cid);
		Date now = new Date();
		Date start;
		try {
			start = sdf.parse(bean.getStartDate());

			if (now.compareTo(start) >= 0) {
				result = true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			result = false;
			e.printStackTrace();
		}

		return result;
	}

	private static String replaceString(String str) {
		return str.replace("<", "&lt;").replace(">", "&gt").replace("\n", "<br>");
	}
}
