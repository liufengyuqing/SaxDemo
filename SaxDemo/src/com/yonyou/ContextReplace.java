package com.yonyou;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
					|| (pathname.isFile() && pathname.getName().endsWith("pom.xml"))) {
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
				String message = " 找到的源文件：\t" + subFile.getAbsolutePath();
				System.out.println(message);
				System.out.println("---------------------------");
				
				fileMsg = fileMsg+message+"\n";
				fileMsg = fileMsg + "---------------------------\n";
				
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
						String search =groupIdStr + "----" + artifactIdStr + "------当前版本：" + versionStr; 
						System.out.println(search);
						fileMsg+=search+"\n";
						setDversion(versionStr);
					} else if(match(preStr,artifactIdStr)) {
						version.setText(postStr);
						setDversion(postStr);
						//String result = " 修改后： " + version.getName() + " ：" + version.getText();
						String result = groupIdStr + "----" + artifactIdStr + "------修改后：" + version.getText();
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

	/*public static void main(String[] args) {
		File srcFile = new File(srcDir);
		ContextReplace contextReplace = new ContextReplace();
		contextReplace.readDir(srcFile, "", "liuzhiwei");
		System.out.println(contextReplace.getFileMsg());
	}*/

}