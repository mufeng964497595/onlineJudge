package com.hwf.servlet;

import com.hwf.dao.SetInfoIntoDatabaseDao;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 编辑测试数据的servlet
 */
public class EditTestDataServlet extends HttpServlet {
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
		String fileType = request.getParameter("fileType");
		String pid = request.getParameter("pid");

		if (checkCode == null || checkSession == null || !(checkCode + "LL").equals(checkSession) || pid == null) {
			// 非法提交
			response.sendRedirect(request.getContextPath() + PATH);
			return;
		}

		String id = request.getParameter("id");
		String op = request.getParameter("op");

		if (op != null && op.equals("delete")) { // 删除操作
			SetInfoIntoDatabaseDao.deleteTestData(id);
		} else {
			/*上传文件操作*/

			int type;
			switch (fileType) {
				case "input": type = 1; break;
				case "output": type = 2; break;
				case "add": type = 3; break;
				default: response.sendRedirect(request.getContextPath() + PATH); return;
			}

			String savePath = "/onlineJudge/";
			File f1 = new File(savePath);
			if (!f1.exists()) {
				f1.mkdirs();
			}

			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			List<FileItem> fileList;
			try {
				fileList = upload.parseRequest(request);

				String data[] = new String[2];

				int size = fileList.size();
				for (int i = 0; i < size; ++i) {
					FileItem item = fileList.get(i);
					if (!item.isFormField()) {// 普通表单项
						String name = item.getName();

						if (name == null || name.trim().equals("")) {
							continue;
						}

						File file = new File(savePath + name);
						item.write(file);

						FileReader fr = new FileReader(file);
						BufferedReader br = new BufferedReader(fr);
						StringBuilder sb = new StringBuilder();
						String line;

						while ((line = br.readLine()) != null) {
							sb.append(line).append("\n");
						}

						// System.out.println(sb.toString());
						br.close();
						fr.close();
						file.delete();

						data[i] = sb.toString();
					}
				}

				if (type == 1) {
					SetInfoIntoDatabaseDao.updateProblemInputData(id, data[0]);
				} else if (type == 2) {
					SetInfoIntoDatabaseDao.updateProblemOutputData(id, data[0]);
				} else {
					SetInfoIntoDatabaseDao.addProblemTestData(pid, data[0], data[1]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		response.sendRedirect(request.getContextPath() + "/edittestdata.jsp?pid=" + pid);
	}

}
