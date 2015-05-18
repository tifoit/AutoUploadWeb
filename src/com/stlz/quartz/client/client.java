package com.stlz.quartz.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @Title: client.java
 * @Package:
 * @Desc: ʹ��HttpURLConnection����POST����(��ʱ�������ڴ����ļ�����)
 * @Copyright: ���ļ�Ŀǰ�����⣬��������
 * @author: liuzhuang
 * @date: 2015/05/15
 * @Email: liuzhuang@umpay.com
 */

public class client {
	private static final String ENCODING_UTF_8 = "UTF-8";

	private void httpUrlConnection() {
		try {
			String pathUrl = "http://localhost:8086/autoload/upload";
			// ��������
			URL url = new URL(pathUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();

			// //������������
			httpConn.setDoOutput(true);// ʹ�� URL ���ӽ������
			httpConn.setDoInput(true);// ʹ�� URL ���ӽ�������
			httpConn.setUseCaches(false);// ���Ի���
			httpConn.setRequestMethod("POST");// ����URL���󷽷�
			String requestString = "�ͷ���Ҫ��������ʽ���͵�����˵�����...";

			// ������������
			// ��������ֽ����ݣ������������ı��룬���������������˴����������ı���һ��
			byte[] requestStringBytes = requestString.getBytes(ENCODING_UTF_8);
			httpConn.setRequestProperty("Content-length", ""
					+ requestStringBytes.length);
			httpConn.setRequestProperty("Content-Type",
					"application/octet-stream");
			// httpConn.setRequestProperty("Content-Type","MULTIPART/FORM-DATA");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
			httpConn.setRequestProperty("Charset", "UTF-8");
			//
			String name = URLEncoder.encode("TestUser", "utf-8");
			httpConn.setRequestProperty("NAME", name);

			// �������������д������
			OutputStream outputStream = httpConn.getOutputStream();
			outputStream.write(requestStringBytes);
			outputStream.close();
			// �����Ӧ״̬
			int responseCode = httpConn.getResponseCode();

			if (HttpURLConnection.HTTP_OK == responseCode) {// ���ӳɹ�
				// ����ȷ��Ӧʱ��������
				StringBuffer sb = new StringBuffer();
				String readLine;
				BufferedReader responseReader;
				// ������Ӧ�����������������Ӧ������ı���һ��
				responseReader = new BufferedReader(new InputStreamReader(
						httpConn.getInputStream(), ENCODING_UTF_8));
				while ((readLine = responseReader.readLine()) != null) {
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				System.out.println("==========>\n" + sb.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new client().httpUrlConnection();
	}

}
