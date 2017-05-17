package cn.edu.hfut.dmic.webcollector.util;

/**
 * Created by wlcheng on 12/7/15.
 */
import java.io.CharArrayWriter;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxReader extends DefaultHandler {

    private CharArrayWriter contents = new CharArrayWriter();
    private String key = "";

    public HashMap<String,String> properties = new HashMap<String, String>();
    @Override
    public void startDocument() throws SAXException {
        // System.out.println("SAX Event: START DOCUMENT");
    }
    @Override
    public void endDocument() throws SAXException {

    }
    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attr) throws SAXException {
        contents.reset();
    }
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("name")) {
            key = contents.toString().trim();
        }
        if (qName.equalsIgnoreCase("value")) {
            properties.put(key, contents.toString().trim());
        }
    }
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        contents.write( ch, start, length );
    }

}

