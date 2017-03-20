package org.xidea.el.impl.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CleanAndroid {
	static String cmd = "lint --fullpath --showall --check UnusedResources --simplehtml bin/report.html --xml bin/report.xml  ./";

	private static boolean deleteLine = false;


	static void test2() throws Exception {
		FileInputStream in1,in2;
		in1= new FileInputStream(
				"/Users/jinjinyun/Documents/workspace/files/source/amap_android-2.zip");
		 in2 = new FileInputStream(
				"/Users/jinjinyun/Documents/workspace/files/source/pkg.apk.zip");
		 ZipInputStream zin1 = new ZipInputStream(in1);
		 ZipInputStream zin2 = new ZipInputStream(in2);
		 ZipEntry e1 = null,e2=null;
		while(true){
			try{e1 = zin1.getNextEntry();
			}catch(Exception e){e.printStackTrace();}
			try{
				if(e1.toString().equals("assets/module/com.autonavi.minimap.weather.png")){
					System.out.println("!!!");
				}
				e2 = zin2.getNextEntry();
			}catch(Exception e){e.printStackTrace();}
			if(e1 == null && e2 == null){
				return;
			}
			String t1 = toString(e1);
			String t2 = toString(e2);
			if(!t1.equals(t2)){
				System.out.println(t1 +" != \n"+t2+"\n"+e1+e2);
				Thread.sleep(1000);
			}else{
				System.out.println("\t"+t1+'\n');
			}
		 }
	}
	static String toString(ZipEntry e){
		if(e == null){
			return "";
		}
		StringBuilder buf = new StringBuilder(e.getName()).append(';');
	buf.append(e.getCompressedSize()).append(',');
	buf.append(e.getCrc()).append(',');
	buf.append(e.getMethod());
		return buf.toString();
		
	}
	static void test3() throws Exception {
		FileInputStream in1,in2;
		in1= new FileInputStream(
				"/Users/jinjinyun/Documents/workspace/files/source/amap_android-2.zip");
		 in2 = new FileInputStream(
				"/Users/jinjinyun/Documents/workspace/files/source/pkg.apk.zip");

//		in1 = new FileInputStream(
//				"/Users/jinjinyun/Documents/workspace/files/amap_android-2/assets/module/com.autonavi.libs.png");
//		in2 = new FileInputStream(
//				"/Users/jinjinyun/Documents/workspace/files/pkg.apk/assets/module/com.autonavi.libs.png");
		int c = 6;//1024;// 16384;//8192;//4096;
		byte[] buf1 = new byte[c];
		byte[] buf2 = new byte[c];
		int inc = 0;
		boolean d = false;
		int begin=0;
		while (true) {
			int i = in1.read(buf1);
			int j = in2.read(buf2);
			if (i != j) {
				System.err.println("!!!! count " + i + "!=" + j);
				return;
			} else if(i<0){
				if (d) {
					int end = inc * c ;
					System.out.println("end:"+end+"; \t"+(end - begin));
				}
				break;}else{
				int p = findDef(buf1, buf2);
				if (p >= 0) {
					if (!d) {
						begin = inc * c + p;
						System.out.println("begin:"+begin+"\t" +i);
						d = true;
						printHex(buf1);
						printHex(buf2);
						

//						 System.out.println(new String(buf1,"ASCII"));
//						 System.out.println();
//						 System.out.println(new String(buf2,"ASCII"));
//						 System.out.println();
						Thread.sleep(1000);
					}

					// System.out.println(new String(buf1,"ASCII"));
					// System.out.println(new String(buf2,"ASCII"));
				} else {
					if (d) {
						int end = inc * c + p;
						System.out.println("end:"+end+"; \t"+(end - begin));
						d = false;
						printHex(buf1);
						Thread.sleep(1000);
					}
				}
			}

			inc++;
		}

	}

	private static int findDef(byte[] buf1, byte[] buf2) {
		for (int i = 0; i < buf2.length; i++) {
			if (buf1[i] != buf2[i]) {
				return i;
			}
		}
		return -1;
	}

	private static void printHex(byte[] buf1) {
		for (int i = 0; i < buf1.length; i++) {
			System.out.print(Integer.toHexString(0xff & buf1[i]));
		}
		System.out.println();
	}

	public static void main(String[] args) throws Exception {
		if (true) {
			test3();
			// test();
			return;
		}

		System.out.println("start...");
		File project = new File(
				"/Users/jinjinyun/Documents/workspace/amap_branch_dev");

		checkBackFiles(new File(project, "res.bak"), new File(project, "res"));
		// if(true)return;

		boolean needSearchAgain = false;
		int lintTime = 1;
		do {
			needSearchAgain = false;
			File report = new File(project + "/bin/report.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(report);
				NodeList issueList = document.getDocumentElement()
						.getElementsByTagName("issue");
				int i = 0;
				for (int j = issueList.getLength(); i < j; i++) {
					Node issue = issueList.item(i);
					if (issue.getNodeType() == 1) {
						if (issue.getAttributes().getNamedItem("id")
								.getNodeValue().equals("UnusedResources")) {
							String issueKey = null;
							if (issue.getAttributes()
									.getNamedItem("errorLine1") != null) {
								issueKey = issue.getAttributes()
										.getNamedItem("errorLine1")
										.getNodeValue().trim();
							}
							NodeList locationList = issue.getChildNodes();
							int k = 0;
							for (int l = locationList.getLength(); k < l; k++) {
								Node location = locationList.item(k);
								if (location.getNodeType() == 1) {
									File file = new File(location
											.getAttributes()
											.getNamedItem("file")
											.getNodeValue());
									if (location.getAttributes().getNamedItem(
											"line") != null) {
										deleteLine(issueKey, file);
									} else {
										moveFile(file);
										if (file.getAbsolutePath().endsWith(
												".xml")) {
											needSearchAgain = true;
										}
									}
								}
							}
						} else {
							System.err.println(String.format(
									"Warning Process issue id:%s message:%s",
									new Object[] {
											issue.getAttributes()
													.getNamedItem("id")
													.getNodeValue(),
											issue.getAttributes()
													.getNamedItem("message")
													.getNodeValue() }));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}

			lintTime++;
		} while (needSearchAgain && false);
	}

	private static void test() throws FileNotFoundException, IOException {
		String source = null;
		String outDir = null;
		FileInputStream fis = new FileInputStream(source);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;

		// getNextEntry()！=null时，ZipInputStream读取每个压缩文件，
		while ((entry = zis.getNextEntry()) != null) {
			int size;
			byte[] buffer = new byte[2048];

			FileOutputStream fos = new FileOutputStream(outDir + "/"
					+ entry.getName());
			BufferedOutputStream bos = new BufferedOutputStream(fos,
					buffer.length);

			while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
				bos.write(buffer, 0, size);
			}
			bos.flush();
			bos.close();
		}

		zis.close();
		fis.close();
	}

	private static void checkBackFiles(File file, final File dest) {
		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {

				File file2 = new File(dest, file.getName());
				if (file.isDirectory()) {
					checkBackFiles(file, file2);
				} else {
					if (file2.exists()) {
						// System.err.println("文件删除失败："+file2);
					} else {
						// System.err.println("备份文件成功："+file);
					}
				}
				return false;
			}
		});
	}

	private static void moveFile(File file) {
		String absolutePath = file.getAbsolutePath().replace('\\', '/');
		String dest = absolutePath.replace("/res/", "/res.bak/");
		if (dest.equals(absolutePath)) {
			System.err.println("文件位置异常：" + dest);
		} else {
			File toFile = new File(dest);
			toFile.getParentFile().mkdir();
			boolean result;
			if (toFile.exists() && toFile.length() == file.length()) {
				// result = file.delete();
			} else {

			}
			result = false;// file.renameTo(toFile);
			System.out.println("Move " + (result ? "success" : "failed")
					+ absolutePath + "=>\n\t" + dest);
		}
	}

	private static void deleteLine(String issueKey, File file) {
		if (deleteLine) {
			System.out.println("Delete " + issueKey + " in "
					+ file.getAbsolutePath());
			String content = readFile(file);
			int pos;
			if ((pos = content.indexOf(issueKey)) != -1) {
				String endTag = "</"
						+ issueKey.substring(1, issueKey.indexOf(" ")) + ">";

				if (issueKey.endsWith(endTag)) {
					content = content.substring(0, pos)
							+ content.substring(pos + issueKey.length());
				} else {
					content = content.substring(0, pos)
							+ content.substring(content.indexOf(endTag, pos)
									+ endTag.length());
				}
				writeFile(content, file);
			} else {
				System.err.println("Can't find " + issueKey + " in "
						+ file.getAbsolutePath());
			}
		} else {
			System.err.println("ignore:" + issueKey + "\t" + file);
		}
	}

	// private void runLint(int count) throws Exception {
	// String cmd = getProject().getProperty("android.tools.dir") +
	// "/lint --fullpath --showall --check UnusedResources --simplehtml " +
	// this.reportDir + "/report" + count + ".html --xml " + this.reportDir +
	// "/report" + count + ".xml ./";
	// System.out.println(cmd);
	// if (File.separator.equals("\\")) {
	// System.out.println("Windows OS");
	// ExecTask execTask = (ExecTask)getProject().createTask("exec");
	// execTask.setExecutable("cmd");
	// Commandline.Argument arg = execTask.createArg();
	// arg.setLine("/c");
	// arg = execTask.createArg();
	// arg.setLine(cmd);
	// execTask.execute();
	// } else {
	// System.out.println("None Windows OS");
	// ExecTask execTask = (ExecTask)getProject().createTask("exec");
	// execTask.setExecutable("/bin/sh");
	// Commandline commandline = new Commandline();
	// commandline.addArguments(new String[] { "-c", cmd });
	// execTask.setCommand(commandline);
	// execTask.execute();
	// }
	// }
	private static void writeFile(String s, File file) {
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file),
					Charset.forName("UTF-8"));
			writer.write(s);
		} catch (IOException e) {
			e.printStackTrace(System.err);

			if (writer != null)
				try {
					writer.close();
				} catch (IOException e2) {
					e2.printStackTrace(System.err);
				}
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
		}
	}

	private static String readFile(File file) {
		String content = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), Charset.forName("UTF-8")));
			char[] buf = new char[1024];
			StringBuilder builder = new StringBuilder();
			int read = -1;
			while ((read = reader.read(buf)) != -1) {
				builder.append(buf, 0, read);
			}
			content = builder.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.err);

			if (reader != null)
				try {
					reader.close();
				} catch (IOException e2) {
					e2.printStackTrace(System.err);
				}
		} catch (IOException e) {
			e.printStackTrace(System.err);

			if (reader != null)
				try {
					reader.close();
				} catch (IOException e2) {
					e2.printStackTrace(System.err);
				}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return content;
	}
}