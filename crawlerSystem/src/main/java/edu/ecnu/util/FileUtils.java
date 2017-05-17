package edu.ecnu.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author wenliang
 * @version: 0.0.2
 * @modify:wenliang
 * @Copyright: 华东师范大学数据科学与工程院版权所有
 */
public class FileUtils {
	
	public static String read(String filepath) {
		return read(filepath, "UTF-8");
	}

	public static String read(File docDir) {
		return read(docDir, "UTF-8");
	}

	public static String read(String filepath, String encode) {
		File docDir = new File(filepath);
		return read(docDir, encode);
	}

	public static List<String> readAsList(String path) {
		return readAsList(path, "UTF-8");
	}

	public static List<String> readAsList(String path,int column) {
		return readAsList(path, column, "UTF-8");
	}
	
	public static List<String> readAsList(String path, String encode) {
		List<String> result = new ArrayList<String>();
		BufferedReader br;// 读字符串
		String line = null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					path), encode);
			br = new BufferedReader(read);
			while ((line = br.readLine()) != null) {
				if (line.equals("") || line.equals(null) || line.equals("\n")) {
					continue;
				}
				result.add(line.trim().replace("\"", ""));
			}
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	
	public static List<String> readAsList(String path,int column,String encode) {
		List<String> result = new ArrayList<String>();
		BufferedReader br;// 读字符串
		String line = null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					path), encode);
			br = new BufferedReader(read);
			while ((line = br.readLine()) != null) {
				if (line.equals("") || line.equals(null) || line.equals("\n")) {
					continue;
				}
				// if(!result.contains(line)){
				result.add(line.split("\t")[column].trim().replace("\"", ""));
				// }
			}
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/***
	 *
	 * @param path
	 * @return
	 */
	public static List<String> readGBKAsList(String path) {
		List<String> result = new ArrayList<String>();
		BufferedReader br;// 读字符串
		String line = null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					path), "gbk");
			br = new BufferedReader(read);
			while ((line = br.readLine()) != null) {
				if (line.equals("") || line.equals(null)) {
					continue;
				}
				if (!result.contains(line)) {
					result.add(line);
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	public static List<String> readFromFile(String path, String charset) {
		List<String> result = new ArrayList<String>();
		FileInputStream fis;
		InputStreamReader isr;// 读流
		BufferedReader br;// 读字符串
		String line = null;
		try {
			fis = new FileInputStream(path);
			isr = new InputStreamReader(fis, charset);
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line == "") {
					continue;
				}
				if (!result.contains(line)) {
					result.add(line.trim());
				}
			}
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/***
	 *
	 * @param docDir
	 * @param encode
	 * @return
	 */
	public static String read(File docDir, String encode) {
		FileInputStream fis;
		InputStreamReader isr;// 读流
		BufferedReader br;// 读字符串
		String line = null;
		String temp = null;
		StringBuffer buff = new StringBuffer();// 字符串缓存，存读入进来的字符串的
		try {
			fis = new FileInputStream(docDir);
			isr = new InputStreamReader(fis, encode);
			br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// line = line.substring(line.indexOf("\t") + 1);
				// System.out.println(line);
				buff.append(line);
				buff.append("\r\n");
			}
			br.close();
			isr.close();
			fis.close();

			temp = buff.toString();
			buff.delete(0, buff.length());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return temp;
	}

	/***
	 * 将字符串写入到filepath的路径下
	 * @param file
	 * @param str
	 * @param isAppend
	 * @param encode
	 * @return
	 */
	public synchronized static boolean write(File file, String str, boolean isAppend,
			String encode) {
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(file, isAppend);
			osw = new OutputStreamWriter(fileos, encode);
			bw = new BufferedWriter(osw);
			if (!str.equals("")) {
				bw.append(str);
			}
			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 将字符串写入到filepath的路径下
	 * @param filepath
	 * @param str
	 * @param isAppend
	 * @param encode
	 * @return
	 */
	public synchronized static boolean write(String filepath, String str, boolean isAppend,
			String encode) {
		File file = new File(filepath);
		return write(file, str, isAppend, encode);
	}

	/***
	 * 默认编码方式为UTF-8
	 * @param filepath
	 * @param str
	 * @param isAppend
	 * @return
	 */
	public synchronized static boolean write(String filepath, String str, boolean isAppend) {
		String encode = "UTF-8";
		return write(filepath, str, isAppend, encode);
	}

	/***
	 * 默认为追加模式
	 * @param filepath
	 * @param str
	 * @param encode
	 * @return
	 */
	public static boolean write(String filepath, String str, String encode) {
		return write(filepath, str, true, encode);
	}

	/***
	 * 将字符串写入到filepath的路径下,默认为追加，默认为“UTF-8”模式
	 * @param filepath
	 * @param str
	 * @return
	 */
	public synchronized  static boolean write(String filepath, String str) {
		return write(filepath, str, true);
	}

	/***
	 * 将string的一个数组list书序写入到filepath的文件中，换行
	 * @param filepath
	 * @param list
	 * @param isAppend
	 * @param encode
	 * @return
	 */
	public static boolean write(String filepath, List<String> list,
			boolean isAppend, String encode) {
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(filepath, isAppend);
			osw = new OutputStreamWriter(fileos, encode);
			bw = new BufferedWriter(osw);
			for (String s : list) {
				bw.append(s);
			}
			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/***
	 * 将string的一个数组list书序写入到filepath的文件中，换行
	 * @param filepath
	 * @param list
	 * @param isAppend
	 * @return
	 */
	public static boolean write(String filepath, List<String> list,
			boolean isAppend) {
		String encode = "UTF-8";
		return write(filepath, list, isAppend, encode);
	}

	/***
	 * 将string的一个数组list书序写入到filepath的文件中，换行
	 * @param filepath
	 * @param list
	 * @return
	 */
	public static boolean write(String filepath, List<String> list) {
		return write(filepath, list, true);
	}

	public static <T> boolean write(String filepath, Map<T, T> resultMap) {
		Iterator<Entry<T, T>> iterator = resultMap.entrySet().iterator();
		List<String> resultList = new ArrayList<String>();
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			Entry<String, Long> entry = (Entry<String, Long>) iterator
					.next();
			resultList.add(entry.getKey() + "\t" + entry.getValue());
		}
		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(filepath, true);
			osw = new OutputStreamWriter(fileos, "UTF-8");
			bw = new BufferedWriter(osw);
			for (String s : resultList) {
				if (!s.equals("")) {
					bw.append(s);
					bw.newLine();
				}
			}
			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteFile(File file) {
		try {
			if (file.exists()) {
				file.delete();
				System.out.println("delete：" + file.getName());
				return true;
			}
		} catch (Exception e) {
			System.out.println("delete failed：" + file.getName());
			e.printStackTrace();
		}
		return false;
	}

	/***
	 * 序列化一个对象，对象需要实现Serializable接口
	 * @param ob
	 * @param path
	 * @return
	 */
	public static boolean writeSeriObject(Object ob, String path) {
		try {
			FileOutputStream fo = new FileOutputStream(path);
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(ob);
			so.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("resource")
	public static Object readSeriObject(String path) {
		FileInputStream fi = null;
		try {
			fi = new FileInputStream(path);
			ObjectInputStream si = new ObjectInputStream(fi);
			return si.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
