package EntityInfo.Infobox;

import java.io.*;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import EntityInfo.Infobox.EntityPair;
import EntityInfo.Infobox.SaxHandler;
import org.xml.sax.SAXException;


/**
 * Created by yan on 15/12/1.
 * 这个类调用了SaxHandler，将实体和infobox写入tsv文件
 */
public class ToolXmlBySAX {
    public static List<EntityPair> readXml(InputStream input, String nodeName){
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            SaxHandler handler = new SaxHandler(nodeName);
            parser.parse(input, handler);
            input.close();
            return handler.getList();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void printEntityInfo(String listPath, String resultTSV, String homePath) throws Exception{
        int count = 1;
        FileReader fr = new FileReader(listPath);
        BufferedReader br = new BufferedReader(fr);
        String inputStr;

        FileOutputStream fos = new FileOutputStream(resultTSV);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        while((inputStr = br.readLine()) != null){
            try {
                FileInputStream input = new FileInputStream(new File(homePath+inputStr));
                List<EntityPair> set = readXml(input, "title");
                for(EntityPair entityPair : set){
                    bw.write(count+"\t"+ entityPair.toString());
                    bw.newLine();
                    count++;
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        bw.close();
        br.close();
        System.out.println("一共" + count);
    }

    public static void main(String[] args) throws Exception{

    }
}
