package com.yonyou;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ContextReplace {
	// 起始目录
	// private static String srcDir =
	// "D:\\yonyou\\gitRepository\\icop-technology";

	//private static String srcDir = "f:\\test";
	
	//修改后信息
	private String resultMsg = "";
	//文件路径信息+查找信息
	private String fileMsg = "";
	//当前版本号
	private String dversion = "";

	List<String> groupIdList = Arrays.asList("com.yyjz");
	List<String> artifactIdList = Arrays.asList("icop-security", "icop-security-web", "icop-security-api",
			"icop-security-impl", "icop-schedule-api", "icop-technology-api", 
			"icop-quality-api","icop-technology","icop-technology-impl","icop-technology-web",
			"icop-schedule","icop-schedule-impl","icop-schedule-web",
			"icop-quality","icop-quality-impl","icop-quality-web",
			"icop-repository","icop-repository-api","icop-repository-impl","icop-repository-web",
			"icop-task","icop-task-api","icop-task-impl","icop-task-web",
			"icop-environment","icop-environment-api","icop-environment-impl","icop-environment-web");

	List<String> artifactIdList1 = Arrays.asList("icop-security", "icop-schedule", "icop-technology", 
			"icop-quality","icop-environment","icop-task");
	
	//存储每次匹配到的内容
    HashSet<String> matched = new HashSet<String>();
	
	public HashSet<String> getMatched() {
		return matched;
	}

	public void setMatched(HashSet<String> matched) {
		this.matched = matched;
	}

	public String getDversion() {
		return dversion;
	}

	public void setDversion(String dversion) {
		this.dversion = dversion;
	}
	
	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getFileMsg() {
		return fileMsg;
	}

	public void setFileMsg(String fileMsg) {
		this.fileMsg = fileMsg;
	}
	

	// 过滤器
	private FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {
			// 只处理：目录并且非target下目录 或是 pom.xml文件
			if ((pathname.isDirectory() && !pathname.getName().endsWith("target"))
					//|| (pathname.isDirectory() && !pathname.getName().endsWith("node_modules"))
					|| (pathname.isFile() && pathname.getName().endsWith("pom.xml")))
					//|| (pathname.isFile() && pathname.getName().endsWith("package.json"))) 
					{
				return true;
			} else {
				return false;
			}
		}
	};

	// 递归调用
	public void readDir(File file, List<String> preStr, String postStr) {
		// 以过滤器作为参数
		File[] files = file.listFiles(filter);
		for (File subFile : files) {
			// 处理目录
			if (subFile.isDirectory()) {
				readDir(subFile, preStr, postStr);
			}
			// 处理文件
			else {
				
				
				String message = "\n------------------------------------------------------------------------\n"
						+ " 找到的源文件：\t" + subFile.getAbsolutePath()+"\n";
				System.out.println(message);
				System.out.println("---------------------------");
				
				fileMsg = fileMsg+message;
			//	fileMsg = fileMsg + "---------------------------\n";
				
				resultMsg = resultMsg+message+"\n";
				resultMsg = resultMsg + "---------------------------\n";
				
				// 主要处理逻辑
				replace(subFile.getAbsolutePath(), preStr, postStr);
			}
		}
	}

	// 主要处理 传入文件路径
	public void replace(String infilename, List<String> preStr, String postStr) {
		// dom4j解析器
		SAXReader reader = new SAXReader();
		try {
			// 获取文档对象
			Document document = reader.read(infilename);

			// 获取root节点
			Element classElement = document.getRootElement();
			// System.out.println("Root element replace : " +
			// classElement.getName());

			// 递归遍历
			listNodes2(classElement, preStr, postStr);

			// 写回文件
			writerDocumentToNewFile(document, infilename);

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
/*
	@SuppressWarnings("unchecked")
	private void listNodes(Element node, String preStr, String postStr) {
		// System.out.println("当前节点的名称：" + node.getName());
		// 如果当前节点内容不为空，则输出
		if (!(node.getTextTrim().equals(""))) {
			// System.out.println( node.getName() + "：" + node.getText());

			if (node.getName().equals("version") && node.getText().equals(preStr)) {
				node.setText(postStr);
				String result = "修改后： " + node.getName() + " ：" + node.getText() + "\n";
				resultMsg += result;
				System.out.println(result);
			}
		}
		// 同时迭代当前节点下面的所有子节点
		// 使用递归
		Iterator<Element> iterator = node.elementIterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			listNodes(e, preStr, postStr);
		}
	}
*/
	
	//匹配字段
	public boolean match(List<String>list,String str){
		for(int i = 0;i<list.size();i++){
			if(Pattern.matches(list.get(i)+".*", str)){
				matched.add(list.get(i));
				return true;
			}
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private void listNodes2(Element node, List<String> preStr, String postStr) {

		// System.out.println("当前节点的名称：" + node.getName());
		// 如果当前节点内容不为空，则输出
		// if (!(node.getTextTrim().equals(""))) {
		if (node.getName().equals("project") || node.getName().equals("parent")
				|| node.getName().equals("dependency")) {
			
			Element groupId = node.element("groupId");
			Element artifactId = node.element("artifactId");
			Element version = node.element("version");

			if (groupId != null && artifactId != null && version != null) {

				String groupIdStr = groupId.getText();
				String artifactIdStr = artifactId.getText();
				String versionStr = version.getText();
				

				if (groupIdList.contains(groupIdStr) && match(artifactIdList1,artifactIdStr)) {
					if (postStr.equals("1")) {
						String search ="groupId: "+ groupIdStr + "----" +"artifactId: " +artifactIdStr + "------当前版本：" + versionStr; 
						System.out.println(search);
						fileMsg+=search+"\n";
						setDversion(versionStr);
					} else if(match(preStr,artifactIdStr)) {
						version.setText(postStr);
						setDversion(postStr);
						//String result = " 修改后： " + version.getName() + " ：" + version.getText();
						String result = "groupId: "+groupIdStr + "----" + "artifactId: "+artifactIdStr + "------修改后：" + version.getText();
						resultMsg+=result+"\n";
						System.out.println(result);

					}
				}

			}
		}

		// }

		// 同时迭代当前节点下面的所有子节点
		// 使用递归
		Iterator<Element> iterator = node.elementIterator();

		while (iterator.hasNext()) {
			Element e = iterator.next();
			listNodes2(e, preStr, postStr);
		}
	}

	// document写入文件
	public void writerDocumentToNewFile(Document document, String infilename) throws Exception {
		File infile = new File(infilename);

		// 输出格式 缩进格式
		OutputFormat format = OutputFormat.createPrettyPrint();

		// 设置编码
		format.setEncoding("UTF-8");

		// 临时文件
		File outfile = new File(infile + ".tmp");

		// XMLWriter 指定输出文件以及格式
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8"), format);

		// 写入新文件
		writer.write(document);
		writer.flush();
		writer.close();
		infile.delete();
		outfile.renameTo(infile);
	}
	
	/**
	 *  处理前端——icop-security-frontend中的package.json文件中version版本
	 *  
	 * @param infilename
	 * @return
	 */

	public String fingVersion(String infilename) {

		File infile = new File(infilename);
		// read file content from file
		//线程安全的
		//StringBuilder线程不安全的
		StringBuffer sb = new StringBuffer("");

		try {
			FileReader reader = new FileReader(infile);
			BufferedReader br = new BufferedReader(reader);
			String str = null;
			//按行读入每一行
			while ((str = br.readLine()) != null) {
				sb.append(str + "\r\n");
				//System.out.println("每一行的字符：" + str);
			}
			br.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//正则表达式的高级用法

		String pattern = "(?<=(\"version\"):.?\")[\\d.]*(?=\")";

		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(sb);
		String matchedStr = "";
		while (m.find()) {
			matchedStr = m.group();
			if (matchedStr != null && matchedStr.length() > 0)
				System.out.println("找到当前版本:" + matchedStr);
		}
		return matchedStr;
	}

	public void replaceFrontend(String infilename,String postVersion) {
		File infile = new File(infilename);

		try {
			// read file content from file
			StringBuffer sb = new StringBuffer("");

			FileReader reader = new FileReader(infile);
			BufferedReader br = new BufferedReader(reader);

			String str = null;

			while ((str = br.readLine()) != null) {
				sb.append(str + "\r\n");
				//System.out.println("每一行的字符："+str);
			}
			br.close();
			reader.close();

			String pattern = "(?<=(\"version\"):.?\")[\\d.]*(?=\")";

			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(sb);
			while (m.find()) {
				String matchedStr = m.group();
				if (matchedStr != null && matchedStr.length() > 0)
					System.out.println("当前的版本:" + matchedStr);
			}

			// write string to file
			FileWriter writer = new FileWriter(infile);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(m.replaceAll(postVersion));
			// bw.write(sb.toString());
			
			bw.close();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * // 输出格式 缩进格式 OutputFormat format = OutputFormat.createPrettyPrint();
		 * 
		 * // 输入流 BufferedReader in = new BufferedReader(new
		 * InputStreamReader(new FileInputStream(infile), "utf-8")); // 临时文件
		 * File outfile = new File(infile + ".tmp"); // 输出流 PrintWriter out =
		 * new PrintWriter( new BufferedWriter(new OutputStreamWriter(new
		 * FileOutputStream(outfile), "utf-8")));
		 * 
		 * String reading; StringBuilder stringBuilder = new StringBuilder();
		 * while ((reading = in.readLine()) != null) {
		 * System.out.println(reading); //
		 * stringBuilder.append(reading).append("\r\n"); } String str =
		 * stringBuilder.toString(); // System.out.println(str);
		 * System.out.println("----");
		 * 
		 * String pattern = "(?<=(\"version\"):.?\")[\\d.]*(?=\")";
		 * 
		 * Pattern r = Pattern.compile(pattern); Matcher m = r.matcher(str);
		 * while (m.find()) { String matchedStr = m.group(); if (matchedStr !=
		 * null && matchedStr.length() > 0) System.out.println("come here:" +
		 * matchedStr); } System.out.println(m.replaceAll("1.5.1"));
		 * out.println(m.replaceAll("1.5.1")); // out.format(format, null);
		 * out.close(); in.close(); infile.delete(); outfile.renameTo(infile);
		 */
	}
	
	

/*	public static void main(String[] args) {
		
		String srcDir = "E:\\Program Files\\git\\icop-lzw-frontend\\package.json";
		File srcFile = new File(srcDir);
		
		ContextReplace contextReplace = new ContextReplace();
		
		contextReplace.replaceFrontend(srcDir,"1.5.02");
	}
*/
}