package com.stlz.autoupload.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 * @Title: UploadServlet.java
 * @Package: com.stlz.quartz.server
 * @Desc: 该程序为Server端代码，
 *        用来进行接收、处理Client端程序发送过来的文件上传POST请求,并将上传的文件保存到filePath路径下;
 * @Copyright:
 * @author: liuzhuang
 * @date: 2015/05/15
 * @Email: liuzhuang@umpay.com
 */

// Servlet 文件上传
public class UploadServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(UploadServlet.class);

	private static final long serialVersionUID = 1L;
	private String filePath; // 文件存放目录
	private String tempPath; // 临时文件目录

	// 初始化
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// 从配置文件中获得初始化参数
		filePath = config.getInitParameter("filepath");
		tempPath = config.getInitParameter("temppath");

		ServletContext context = getServletContext();

		filePath = context.getRealPath(filePath);
		tempPath = context.getRealPath(tempPath);

		createFolder(filePath);
		createFolder(tempPath);

		logger.info("临时文件目录:" + tempPath);
		logger.info("文件存放目录:" + filePath);

		logger.info("文件存放目录、临时文件目录准备完毕 ...");

	}

	private void createFolder(String filePath) {
		File f = new File(filePath);

		// 创建文件夹
		if (!f.exists()) {
			f.mkdirs();
		}
		f = null;
	}

	// doPost
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		res.setContentType("text/plain;charset=gbk");
		PrintWriter pw = res.getWriter();
		try {
			DiskFileItemFactory diskFactory = new DiskFileItemFactory();
			// threshold 极限、临界值，即硬盘缓存 1M
			diskFactory.setSizeThreshold(4 * 1024);
			// repository 贮藏室，即临时文件目录
			diskFactory.setRepository(new File(tempPath));

			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			// 设置允许上传的最大文件大小 4M
			upload.setSizeMax(4 * 1024 * 1024);
			// 解析HTTP请求消息头
			List fileItems = upload.parseRequest(req);
			Iterator<FileItem> iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					logger.info("处理表单内容 ...");
					processFormField(item, pw);
				} else {
					logger.info("===>>>文件上传开始 ...");
					processUploadFile(item, pw);
				}
			}// end while()

			pw.close();
			logger.info("===>>>文件上传完毕.\n");

		} catch (Exception e) {
			System.out.println("使用 fileupload 包时发生异常 ...");
			logger.error("使用 fileupload 包时发生异常 ...", e);
			e.printStackTrace();
		}// end try ... catch ...

	}// end doPost()

	// 处理表单内容
	private void processFormField(FileItem item, PrintWriter pw)
			throws Exception {
		String name = item.getFieldName();
		String value = item.getString();
		pw.println(name + " : " + value + "\r\n");
	}

	// 处理上传的文件/文件上传开始
	private void processUploadFile(FileItem item, PrintWriter pw)
			throws Exception {

		// 此时的文件名包含了完整的路径，得注意加工一下
		String filename = item.getName();
		logger.info("完整的文件名：" + filename);

		int index = filename.lastIndexOf("\\");
		filename = filename.substring(index + 1, filename.length());

		long fileSize = item.getSize();

		if ("".equals(filename) && fileSize == 0) {
			logger.info("文件名为空 ...");
			return;
		}

		File uploadFile = new File(filePath + "/" + filename);
		item.write(uploadFile);
		pw.println(filename + " 文件保存完毕 ...");
		pw.println("文件大小为 ：" + fileSize + "\r\n");

		logger.info(filename + " 文件保存完毕 ...");
		logger.info("文件大小为 ：" + fileSize);
	}

	// doGet
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		doPost(req, res);
	}
}