package edu.ecnu.util;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wlcheng on 12/23/15.
 */
public class StringUtils {

    public static String Jsonformat(Object object){
        ObjectMapper mapper = new ObjectMapper();
        String jsonstr = null;
        try {
            jsonstr = mapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonstr;
    }


}
