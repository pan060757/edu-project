package cn.edu.hfut.dmic.webcollector.util;


import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by wlcheng on 12/7/15.
 */
public class Configuration {



    protected String defaultPath = "WebCollector/conf/webcollector-chinaedu.xml";
    HashMap<String,String> properties = new HashMap<String, String>();


    public Configuration(String defaultPath) {
        this.defaultPath = defaultPath;
        try {
            addResource(this.defaultPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Configuration() {
        try {
            addResource(defaultPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String regex){
        if(this.properties.get(regex)!=null){
            return this.properties.get(regex);
        }
        Set<Map.Entry<String, String>> entrySet = properties.entrySet();
        for (Map.Entry<String, String> entry:entrySet) {
            String key = entry.getKey();
            if(key.matches(regex)){
               return entry.getValue();
            }
        }
        return null;
    }


    public List<String> getValues(String regex){
        List<String> result = new ArrayList<String>();
        Set<Map.Entry<String, String>> entrySet = properties.entrySet();
        for (Map.Entry<String, String> entry:entrySet) {
            String key = entry.getKey();
            if(key.matches(regex)){
                result.add(entry.getValue());
            }
        }
        return result;
    }


    public void addResource(String path) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        File file = new File(path);
        try {
            parser = factory.newSAXParser();
            SaxReader dh = new SaxReader();
            parser.parse(file, dh);
            properties.putAll(dh.properties);
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
    }
}
