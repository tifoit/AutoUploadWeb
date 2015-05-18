package com.stlz.quartz.client;

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

/**
 * @Title: FileUploadClient.java
 * @Package: com.stlz.quartz.client
 * @Desc: �ó���ΪClient�˴��룬
 *        ʹ��HttpURLConnection���Զ�����POST����,���ϴ��ļ�(�ļ���������list������)��Post�����͵�Server��;
 * @Copyright:
 * @author: liuzhuang
 * @date: 2015/05/15
 * @Email: liuzhuang@umpay.com
 */

public class FileUploadClient {
	public static void main(String[] args) {
		try {

			FileUploadClient fupc = new FileUploadClient();
			fupc.upload();
			System.out.println("=============Client���ϴ��ļ�������ϣ�=============");
			//�����˳����رո�����
			System.exit(0);

		} catch (Exception e) {
			System.out.println("=============Client�˷����ļ������쳣��=============" + e);
			e.printStackTrace();
		}

	}

	public void upload() {
		// list�б������clientҪ����post�ϴ����ļ���,�磺d:\haha.doc..
		List<String> list = new ArrayList<String>();
		list.add("c:\\test.zip");
		try {
			// �������ݷָ���
			String BOUNDARY = "---------7d4a6d158c9";
			// ����post�ϴ���server�˴���URL
			URL url = new URL("http://localhost:8086/autoload/upload");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// ����POST�������������������
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
			// ����������ݷָ���
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
				// ����ļ�ʱ�������ļ�֮�����������
				out.write("\r\n".getBytes());
				in.close();
			}
			out.write(end_data);
			out.flush();
			out.close();

			// ����BufferedReader����������ȡURL����Ӧ
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			// ��ӡ���Server����Ӧ
			System.out.println("Server����Ӧ��Ϣ��");
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("Server����Ӧ����!");

		} catch (Exception e) {
			System.out.println("����POST��������쳣��" + e);
			e.printStackTrace();
		}
	}

}