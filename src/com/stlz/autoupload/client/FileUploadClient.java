package com.stlz.autoupload.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.stlz.util.config.BusiPropsUtil;

/**
 * @Title: FileUploadClient.java
 * @Package: com.stlz.autoupload.client
 * @Desc: 该程序为Client端代码，
 *        使用HttpURLConnection来自动发起POST请求,将上传文件(文件名保存在list变量中)的Post请求发送到Server端;
 * @Copyright:
 * @author: liuzhuang
 * @date: 2015/05/15
 * @Email: liuzhuang@umpay.com
 */

public class FileUploadClient {

	private static Logger logger = Logger.getLogger(FileUploadClient.class);

	public static void main(String[] args) {
		try {

			FileUploadClient fupc = new FileUploadClient();
			fupc.upload();
			logger.debug("=============Client端上传文件发送完毕！=============");
			// 必须退出、关闭该连接
			System.exit(0);

		} catch (Exception e) {
			logger.debug("=============Client端发送文件出现异常！=============" + e);
			logger.error("Client端发送文件出现异常！ ...", e);
			e.printStackTrace();
		}

	}

	public void upload() {
		// list中保存的是client要进行post上传的文件名,如：d:\haha.doc..
		List<String> list = new ArrayList<String>();
		String uploadFilename = BusiPropsUtil.getProps("xjfile.name");
		// list.add("c:\\test.zip");
		logger.info("Upload file:" + uploadFilename);
		list.add(uploadFilename);

		try {
			// 定义数据分隔线
			String BOUNDARY = "---------7d4a6d158c9";

			// 接收post上传的server端处理URL
			String postUrl = BusiPropsUtil.getProps("Server.url");
			URL url = new URL(postUrl);
			logger.info("Server Url:" + url);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// 定义最后数据分隔线
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			int leng = list.size();
			for (int i = 0; i < leng; i++) {
				String fname = list.get(i);
				File file = new File(fname);
				StringBuilder sb = new StringBuilder();
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data;name=\"file" + i
						+ "\";filename=\"" + file.getName() + "\"\r\n");
				sb.append("Content-Type:application/octet-stream\r\n\r\n");

				byte[] data = sb.toString().getBytes();
				out.write(data);
				DataInputStream in = new DataInputStream(new FileInputStream(
						file));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				// 多个文件时，二个文件之间必须加入这个
				out.write("\r\n".getBytes());
				in.close();
			}
			out.write(end_data);
			out.flush();
			out.close();

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			// 打印输出Server端响应
			logger.info("Server端响应信息：");
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			logger.info("Server端响应结束!");

		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			logger.error("发送POST请求出现异常！ ...", e);
			e.printStackTrace();
		}
	}
}