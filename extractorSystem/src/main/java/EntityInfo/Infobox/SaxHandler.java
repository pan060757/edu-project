package EntityInfo.Infobox;

import java.util.ArrayList;
import java.util.List;

import EntityInfo.Infobox.EntityPair;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by yan on 15/12/1.
 * 这是一个工具类，是底层的读取xml文件的工具
 */
public class SaxHandler extends DefaultHandler{
    List<EntityPair> entityPairList = null;
    EntityPair entityPair = null;
    StringBuffer properties = null;
    private String currentTag = null;
    private String nodeName = null;

    public List<EntityPair> getList(){
        return entityPairList;
    }

    public SaxHandler(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public void startDocument() throws SAXException {
        // TODO 当读到一个开始标签的时候，会触发这个方法
        entityPairList = new ArrayList<EntityPair>();
    }

    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        // TODO 当遇到文档的开头的时候，调用这个方法
        if(name.equals(nodeName)){
            entityPair = new EntityPair();
            properties = new StringBuffer();
        }
        currentTag = name;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO 这个方法用来处理在XML文件中读到的内容
        String content = new String(ch,start,length);
        if(currentTag != null && content != null && !content.trim().equals("")){
            if("title".equals(currentTag)){
                entityPair.setTitle(content);
            }else if("key".equals(currentTag)){
                properties.append(content+"=");
            }else if("value".equals(currentTag)){
                properties.append(content+"#");
            }
        }
        currentTag=null;
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // TODO 在遇到结束标签的时候，调用这个方法
        if(name.equals("infobox")){
            entityPair.setProperties(properties);
            //System.out.println(entityPair.toString());
            //如果infobox为空，不放进entityList里面
            if(!properties.toString().trim().equals("") && properties != null)
                entityPairList.add(entityPair);
            entityPair = null;
            properties = null;
        }
        super.endElement(uri, localName, name);
    }

    //结束解析文档，即解析根元素结束标签时调用该方法
    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
        super.endDocument();
    }


}
