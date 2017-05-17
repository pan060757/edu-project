package common;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Howie on 2015/4/23.
 */
public class getDataPathList {

    private static ArrayList<String> AL = new ArrayList<String>();

    public static void clear(){
        AL.clear();
    }

    public static ArrayList<String> getDPL(String inputFolder) {

        File path = new File(inputFolder);
        if (path.isDirectory()) {
            File[] fileList = path.listFiles();
            for (File file : fileList) {
                getDPL(file.getPath());
            }
        }
        else if (path.toString().endsWith(".xml") && !path.getName().startsWith(".")) {
            AL.add(path.toString());
        }
        return AL;
    }

    public static ArrayList<String> getDPLTXT(String inputFolder) {

        File path = new File(inputFolder);
        if (path.isDirectory()) {
            File[] fileList = path.listFiles();
            for (File file : fileList) {
                getDPLTXT(file.getPath());
            }
        }
        else if (path.toString().endsWith(".txt") && !path.getName().startsWith(".")) {
            AL.add(path.toString());
        }
        return AL;
    }


}
