package EntityInfo.Infobox;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by yan on 15/11/29.
 * 这个类把2014年和2015年的事件结果中抽取得到的实体写入entity2014&2015.txt
 */
public class EntityInput {
    public static final String event2015 = "/Volumes/Elements/知识图谱/教育知识图谱系统/百度百科infobox抽取/data/event_result2015.txt";
    public static final String event2014 = "/Volumes/Elements/知识图谱/教育知识图谱系统/百度百科infobox抽取/data/result2014.txt";
    public static final String entityOutput = "/Volumes/Elements/知识图谱/教育知识图谱系统/百度百科infobox抽取/data/entity2014&2015.txt";
    public static final HashSet<String> entities = new HashSet<String>();

    public static void getEntity2014(String eventFileName) throws Exception{
        String inputStr ;
        FileReader fr = new FileReader(eventFileName);
        BufferedReader br = new BufferedReader(fr);
        int count=0;


        while((inputStr = br.readLine()) != null) {
            //跳过N.话题以及空白行
            if (inputStr.split("\t").length <= 1)
                continue;

            String[] data = inputStr.split("\t");
            for (int i = 5; i <= 8; i++) {
                //跳过null
                if (data[i].equals("null"))
                    continue;
                String[] entity = data[i].split(",");
                for (int j = 0; j < entity.length; j++) {
                    entities.add(entity[j]);
                    count++;
                }
            }
        }
        br.close();
        System.out.println(count);
        System.out.println("Using "+ eventFileName +"... Now we have " + entities.size() + " entities");
    }

    public static void getEntity2015(String eventFileName) throws Exception{
        String inputStr;
        FileReader fr = new FileReader(eventFileName);
        BufferedReader br = new BufferedReader(fr);
        int count=0;


        while((inputStr = br.readLine()) != null) {
            //跳过N.话题以及空白行
            if (inputStr.split("\t").length <= 2)
                continue;

            String[] data = inputStr.split("\t");
            for (int i = 5; i <= 7; i++) {
                //跳过null
                if (data[i].equals("null"))
                    continue;
                String[] entity = data[i].split(",");
                for (int j = 0; j < entity.length; j++) {
                    entities.add(entity[j]);
                    count++;
                }
            }
        }
        br.close();
        System.out.println(count);
        System.out.println("Using "+ eventFileName +"... Now we have " + entities.size() + " entities");
    }

    public static void writeEntity(String entityOutput) throws Exception{
        FileOutputStream fos = new FileOutputStream(entityOutput);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        String s;

        Iterator<String> iEntity = entities.iterator();
        while(iEntity.hasNext()){
            bw.write(iEntity.next());
            bw.newLine();
        }
        bw.close();
        System.out.println("Done with the entities!");


    }

    public static void main(String[] args) throws Exception{
        getEntity2014(event2014);
        getEntity2015(event2015);
        writeEntity(entityOutput);

    }


}
