package EntityInfo.Infobox;

import EntityInfo.db.EntityDAO;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by yan on 15/12/6.
 */
public class EntityMissing {

    /**
     * 事件所有实体，减去已经和数据库有匹配的，得到剩下的没匹配的实体
     * @param entityOverlap
     * @param entityMissing
     * @param entityOutput
     * @throws Exception
     */
    public static void findMissing(String entityOverlap, String entityMissing, String entityOutput) throws Exception{
        HashSet<String> exist = new HashSet<String>();
        String inputStr ;

        FileReader fr2 = new FileReader(entityOverlap);
        BufferedReader br2 = new BufferedReader(fr2);

        while((inputStr = br2.readLine()) != null) {
            exist.add(inputStr);
        }
        br2.close();

        int count = 0;
        FileOutputStream fos = new FileOutputStream(entityMissing);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        FileReader fr = new FileReader(entityOutput);
        BufferedReader br = new BufferedReader(fr);

        while((inputStr = br.readLine()) != null) {
            if(!exist.contains(inputStr)) {
                bw.write(inputStr);
                bw.newLine();
                count++;
            }
        }
        br.close();
        bw.close();
        System.out.println(count + " done!");
    }

    /**
     * 对重定向的文件再做一下处理，输出entityInfo2和仍然剩下的entityMissing2
     * @param entityInfo2
     * @param entityMissing2
     * @param entityMissing
     * @throws Exception
     */
    public static void findMissing2(String entityInfo2, String entityMissing2, String entityMissing) throws Exception{
        HashSet<String> redirect = new HashSet<String>();
        String inputStr;
        FileReader fr = new FileReader(entityInfo2);
        BufferedReader br = new BufferedReader(fr);
        while((inputStr = br.readLine()) != null){
            String[] data = inputStr.split("\t");
            redirect.add(data[0]);
        }
        br.close();

        FileOutputStream fos = new FileOutputStream(entityMissing2);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        FileReader fr2 = new FileReader(entityMissing);
        BufferedReader br2 = new BufferedReader(fr2);

        while((inputStr = br2.readLine()) != null) {
            if(!redirect.contains(inputStr)) {
                bw.write(inputStr);
                bw.newLine();
            }
        }
        br2.close();
        bw.close();
    }

    /**
     * 处理重定向文件中的_()和_等
     */
    public static void preprocess(String redirect, String redirectProcessed) throws Exception{
        String inputStr;
        FileReader fr = new FileReader(redirect);
        BufferedReader br = new BufferedReader(fr);

        FileOutputStream fos = new FileOutputStream(redirectProcessed);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        while((inputStr = br.readLine()) != null) {
            String input_ = inputStr.replaceAll(("\\(.*?\\)"), "");
            String input_2 = input_.replaceAll("_", "");
            bw.write(input_2);
            bw.newLine();
        }
        bw.close();
        br.close();
    }

    /**
     * 繁简体转换
     */
    public static void convertToSimplified(){
        //使用工具即可
    }

    /**
     * 繁简体转换后的去重
     * @throws Exception
     */
    public static void deDuplicate(String redirectProcessed, String redirectDuplicated) throws Exception{
        HashSet<String> words = new HashSet<String>();
        String inputStr;
        FileReader fr = new FileReader(redirectProcessed);
        BufferedReader br = new BufferedReader(fr);

        FileOutputStream fos = new FileOutputStream(redirectDuplicated);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        while((inputStr = br.readLine()) != null) {
            String[] data = inputStr.split("\t");
            for(String word:data){
                words.add(word);
            }
            Iterator<String> it = words.iterator();
            for(int i = 0; it.hasNext(); i++){
                String value = it.next();
                if(i == words.size()-1)
                    bw.write(value);
                else
                    bw.write(value+"\t");
            }
            bw.newLine();
            words.clear();
        }
        bw.close();
        br.close();


    }

    /**
     * 通过处理过的重定向文件，找到某个entity的重定向的词的组合
     * @param entity 找不到infobox的entity本体
     * @return
     * @throws Exception
     */
    public static HashSet<String> redirect(String entity, String redirectDuplicated) throws Exception{
        HashSet<String> entities = new HashSet<String>();
        String inputStr;
        FileReader fr = new FileReader(redirectDuplicated);
        BufferedReader br = new BufferedReader(fr);
        while((inputStr = br.readLine()) != null){
            String[] inputs = inputStr.split("\t");
            for(int i = 0; i < inputs.length; i++)
                if(entity.equals(inputs[i]))
                    for(int j = 0; j < inputs.length; j++)
                        entities.add(inputs[j]);

        } //把相同意思的词放到一个Set中，所有的Set再放到一个列表里

        br.close();
        return entities;
    }

    /**
     * 针对某个实体，进行重定向以后输出的HashMap<id, infobox>的集合
     * @param entity
     * @param redirectDuplicated
     * @return
     * @throws Exception
     */
    public static HashMap<Integer, String> runRedirectForOne(String entity, String redirectDuplicated) throws Exception {
        HashSet<String> group = redirect(entity, redirectDuplicated);
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        //重定向也找不到infobox，只能返回-1了
        if (group.size() < 1 || group == null) {
            return result;
        }
        //现在是重定向以后至少有一个entity的，把所有结果都放到result里
        else {
            Iterator<String> it = group.iterator();
            while (it.hasNext()) {
                String data = it.next();
                HashMap<Integer, String> infoSet = EntityDAO.queryInfo(data);
                if(infoSet!=null)
                    result.putAll(infoSet);
            }
        }
        return result;
    }

    /**
     * 对entityMissing里面所有的entity，重定向以后再找一遍，输出entityInfo2.txt
     * @throws Exception
     */
    public static void runRedirect(String entityInfo2, String entityMissing, String redirectDuplicated) throws Exception{
        int countAll = 0;
        int matchCount;
        FileOutputStream fos = new FileOutputStream(entityInfo2);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        HashSet<String> group;
        String inputStr;
        FileReader fr = new FileReader(entityMissing);
        BufferedReader br = new BufferedReader(fr);
        while((inputStr = br.readLine()) != null){
            group = redirect(inputStr, redirectDuplicated); //查不到的词finding its partner

            Iterator<String> it = group.iterator();
            while(it.hasNext()){
                String data = it.next();
                HashMap<Integer, String> infoSet = EntityDAO.queryInfo(data);
                matchCount = infoSet.size();
                //如果返回结果不为空
                if(matchCount != 0){
                    countAll++;
                    bw.write(inputStr+"\t"+matchCount);
                    Iterator<Integer> iterator = infoSet.keySet().iterator();
                    while(iterator.hasNext()){
                        int id = iterator.next();
                        bw.write("\t"+infoSet.get(id));
                    }
                    bw.newLine();
                }
                if((countAll%100)==0)
                    System.out.println(countAll);
            }
        }
        br.close();
        bw.close();
        System.out.println("done!");
    }

    public static void main(String[] args) throws Exception{

    }
}
