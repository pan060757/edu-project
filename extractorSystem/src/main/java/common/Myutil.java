package common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Myutil {

    /**
     *
     *
     * @param filePath
     * @return
     */
    public static ArrayList<String> readByLine(String filePath) {
        ArrayList<String> content = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                content.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return content;
    }
}
