package utils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by song on 2016/1/13.
 */
public class FileUtils {
    //写文件
    public static void writeToFile(String content) {
        try {
            FileOutputStream fis = new FileOutputStream("./node.txt", true);
            OutputStreamWriter fw = new OutputStreamWriter(fis, "utf-8");
            fw.write(content+"\n");
            fw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //读文件
    public static ArrayList<String> readByLine(String inputPath) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath), "UTF-8"));
            String str = null;

            while ((str = reader.readLine()) != null) {
                result.add(str);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
