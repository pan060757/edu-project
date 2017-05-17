package EntityInfo.Infobox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yan on 15/12/1.
 */
public class Test {


    public static void main(String[] args){
        String sb = "悉尼_(卑诗省)\t悉尼2_(不列颠哥伦比亚省)";
        String regEx = "(_\\(.*?\\))";
        String sbNew = sb.replaceAll("\\(.*?\\)", "");
        System.out.println(sbNew);
        /*
        for (int i = 0; m.find(); i++) {

            System.out.println(m.group(i));
        }*/
    }

}
