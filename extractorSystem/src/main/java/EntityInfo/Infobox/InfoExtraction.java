package EntityInfo.Infobox;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yan on 15/11/29.
 */
public class InfoExtraction{

    public static final HashMap<String, String> hs  = new HashMap<String, String>();

    public static List<String> readList(String listInPath) throws Exception{
        List<String> fileInPath = new ArrayList<String>();
        String inputStr;
        FileReader fr = new FileReader(listInPath);
        BufferedReader br = new BufferedReader(fr);
        while((inputStr = br.readLine()) != null) {
            fileInPath.add("/Volumes/Elements/baikedump/"+inputStr);
        }
        br.close();
        return fileInPath;
    }

    public static Document readXML(File xmlInPath) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlInPath);
        return document;
    }


    private static void iterDocument(String listInPath, String infoboxOutPath) throws Exception{
        int count = 0;
        FileOutputStream fos = new FileOutputStream(infoboxOutPath);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        List<String> fileInPath = readList(listInPath);

        //对每个xml文件都进行文档解析
        for(String xmlInPath : fileInPath){
            File fileXML = new File(xmlInPath);
            Document document = readXML(fileXML);
            System.out.println(xmlInPath);

            //获取XML文档的根元素，<data></data>
            Element rootElement = document.getRootElement();

            //获取根元素下的所有子元素，<page></page>
            List<Element> elements = rootElement.elements();

            //遍历所有子元素集合
            for (Element element : elements) {

                //获取title子元素的文本内容
                String title = element.elementText("title");
                System.out.println(title);

                //获得该page里的所有infobox
                Element infobox = element.element("infobox");

                List<Element> attribute = infobox.elements("attr");

                //没有infobox的实体就扔掉
                if(attribute.size() == 0)
                    continue;

                bw.write(title);
                for(Element attr : attribute){
                    bw.write("\t"+attr.elementText("key")+"="+attr.elementText("value"));
                }
                bw.newLine();
                count++;
            }
        }
        bw.close();
        System.out.println(count + " has done!");
    }

    public static void main(String[] args) throws Exception{
        iterDocument("/Volumes/Elements/baikedump/list.txt", "/Volumes/Elements/baikedump/result.txt");
    }
}
