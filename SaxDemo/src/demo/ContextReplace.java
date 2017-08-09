package demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ContextReplace {
	
	//过滤条件
	private static String srcStr = "<groupId>com.yyjz</groupId>\r\n"
			+ "<artifactId>icop-security</artifactId>\r\n"
			+ "<version>1.5.0-SNAPSHOT</version>\r\n";
	//过滤结果
	private static String desStr = "<groupId>com.yyjz</groupId>\r\n"
			+ "<artifactId>icop-security</artifactId>\r\n"
			+ "<version>1.5.0</version>\r\n";
	//起始目录
	private static String srcDir = "f:\\test";
	//过滤器
	private static FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {
			// 只处理：目录 或是 .xml文件
			if (pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(".xml"))) {
				return true;
			} else {
				return false;
			}
		}
	};

	public static void readDir(File file) {
		// 以过滤器作为参数
		File[] files = file.listFiles(filter);
		for (File subFile : files) {
			// 处理目录
			if (subFile.isDirectory()) {
				readDir(subFile);
			}
			// 处理文件
			else {
				System.err.println(" 源文件：\t" + subFile.getAbsolutePath());
				System.err.println("---------------------------");
				try {
					replace(subFile.getAbsolutePath(), srcStr, desStr);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	public static void replace(String infilename, String from, String to)
			throws IOException, UnsupportedEncodingException {
		File infile = new File(infilename);
		//输入流
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile), "utf-8"));
		//临时文件
		File outfile = new File(infile + ".tmp");
		//输出流
		PrintWriter out = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outfile), "utf-8")));
		
		String reading;
		
		while ((reading = in.readLine()) != null) {
			System.out.println(reading);
			out.println(reading.replaceAll(from, to));
		}
		out.close();
		in.close();
		infile.delete();
		outfile.renameTo(infile);
	}

	public static void main(String[] args) {
		File srcFile = new File(srcDir);
		
		readDir(srcFile);
		
		srcFile = null;
	}
}