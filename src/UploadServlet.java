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

/**
 * @Title: UploadServlet.java
 * @Package: 
 * @Desc: �ó���ΪServer�˴��룬
 *        �������н��ա�����Client�˳����͹������ļ��ϴ�POST����,�����ϴ����ļ����浽filePath·����; 
 *        �ù������������ͻ����ϴ��ķ���/����:
 *           ����1��ʹ���Զ��Ŀͻ��˳���FileUploadClient.java;
 *           ����2��ʹ��upload.html�����˹�ѡ����ϴ����ļ�;
 * @Copyright:
 * @author: liuzhuang
 * @date: 2015/05/15
 * @Email: liuzhuang@umpay.com
 */

// Servlet �ļ��ϴ�  
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String filePath; // �ļ����Ŀ¼
	private String tempPath; // ��ʱ�ļ�Ŀ¼

	// ��ʼ��
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// �������ļ��л�ó�ʼ������
		filePath = config.getInitParameter("filepath");
		tempPath = config.getInitParameter("temppath");

		ServletContext context = getServletContext();

		filePath = context.getRealPath(filePath);
		tempPath = context.getRealPath(tempPath);
		System.out.println("�ļ����Ŀ¼����ʱ�ļ�Ŀ¼׼����� ...");
	}

	// doPost
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		res.setContentType("text/plain;charset=gbk");
		PrintWriter pw = res.getWriter();
		try {
			DiskFileItemFactory diskFactory = new DiskFileItemFactory();
			// threshold ���ޡ��ٽ�ֵ����Ӳ�̻��� 1M
			diskFactory.setSizeThreshold(4 * 1024);
			// repository �����ң�����ʱ�ļ�Ŀ¼
			diskFactory.setRepository(new File(tempPath));

			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			// ���������ϴ�������ļ���С 4M
			upload.setSizeMax(4 * 1024 * 1024);
			// ����HTTP������Ϣͷ
			List fileItems = upload.parseRequest(req);
			Iterator<FileItem> iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					System.out.println("��������� ...");
					processFormField(item, pw);
				} else {
					System.out.println("�����ϴ����ļ� ...");
					processUploadFile(item, pw);
				}
			}// end while()

			pw.close();
			System.out.println("�ļ��ϴ����.");
			
		} catch (Exception e) {
			System.out.println("ʹ�� fileupload ��ʱ�����쳣 ...");
			e.printStackTrace();
		}// end try ... catch ...
		
	}// end doPost()

	// ���������
	private void processFormField(FileItem item, PrintWriter pw)
			throws Exception {
		String name = item.getFieldName();
		String value = item.getString();
		pw.println(name + " : " + value + "\r\n");
	}

	// �����ϴ����ļ�
	private void processUploadFile(FileItem item, PrintWriter pw)
			throws Exception {
		// ��ʱ���ļ���������������·������ע��ӹ�һ��
		String filename = item.getName();
		System.out.println("�������ļ�����" + filename);
		int index = filename.lastIndexOf("\\");
		filename = filename.substring(index + 1, filename.length());

		long fileSize = item.getSize();

		if ("".equals(filename) && fileSize == 0) {
			System.out.println("�ļ���Ϊ�� ...");
			return;
		}

		File uploadFile = new File(filePath + "/" + filename);
		item.write(uploadFile);
		pw.println(filename + " �ļ�������� ...");
		pw.println("�ļ���СΪ ��" + fileSize + "\r\n");
	}

	// doGet
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		doPost(req, res);
	}
}