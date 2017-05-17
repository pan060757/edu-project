package EntityInfo.Infobox;


import EntityInfo.db.EntityDAO;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import java.io.*;
import java.util.*;

/**
 * Created by yan on 15/12/2.
 */
public class EntityOverlap {
    public static final String entityInfo = "/Volumes/Elements/知识图谱/教育知识图谱系统/百度百科infobox抽取/data/entityPair&info.txt";
    public static final String entityOverlap = "/Volumes/Elements/知识图谱/教育知识图谱系统/百度百科infobox抽取/data/entityOverlap.txt";

    /**
     * 输出2014、2015年事件的实体，同时在数据库能匹配上的，输出entityOverlap.txt
     * @throws Exception
     */
    public static void getOverlap(String resultTSV, String entityOverlap, String entityOutput) throws Exception{
        int count = 0;
        HashSet<String> entityBaike = new HashSet<String>();
        FileReader fr = new FileReader(resultTSV);
        BufferedReader br = new BufferedReader(fr);
        String inputStr;

        while((inputStr = br.readLine()) != null){
            String[] s = inputStr.split("\t");
            entityBaike.add(s[1]);
        }
        br.close();

        FileOutputStream fos = new FileOutputStream(entityOverlap);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        FileReader fr2 = new FileReader(entityOutput);
        BufferedReader br2 = new BufferedReader(fr2);
        while((inputStr = br2.readLine()) != null) {
            if(entityBaike.contains(inputStr)){
                count++;
                bw.write(inputStr);
                bw.newLine();
            }
        }
        br2.close();
        bw.close();
        System.out.println("一共有"+count+"个overlap");
    }

    /**
     * 对entityOverlap.txt中的实体，搜索数据库，将匹配到的entity，匹配个数和infobox输出成为entity&info.txt
     * @param outEntityInfo 输出文件entity&info.txt
     * @param entityOverlap 输入文件是事件与数据库匹配得到的实体列表
     * @throws Exception
     */
    public static void getInfoByEvent(String outEntityInfo, String entityOverlap) throws Exception{
        int countAll = 0;
        int matchCount;
        FileOutputStream fos = new FileOutputStream(outEntityInfo);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        String inputStr ;
        FileReader fr = new FileReader(entityOverlap);
        BufferedReader br = new BufferedReader(fr);

        while((inputStr = br.readLine())!=null){
            HashMap<Integer, String> infoSet = EntityDAO.queryInfo(inputStr);
            matchCount = infoSet.size();
            //如果返回结果不为空
            if(matchCount != 0){
                countAll++;
                bw.write(inputStr+"\t"+matchCount);
                Iterator<Integer> it = infoSet.keySet().iterator();
                while(it.hasNext()){
                    bw.write("\t"+infoSet.get(it.next()));
                }
                bw.newLine();
            }
            if((countAll%100)==0)
                System.out.println(countAll);

        }
        br.close();
        bw.close();
        System.out.println("done!");
    }

    /**
     * 遍历entity&info.txt，把查询得到多个infobox结果的entity进行输出。
     * @throws Exception
     */
    public static void getEntityWithMultipleInfo(String entityWithMultiInfo, String entityInfo) throws Exception{
        FileOutputStream fos = new FileOutputStream(entityWithMultiInfo);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        String inputStr ;
        FileReader fr = new FileReader(entityInfo);
        BufferedReader br = new BufferedReader(fr);

        while((inputStr = br.readLine())!=null){
            String[] data = inputStr.split("\t");
            if(Integer.parseInt(data[1]) >1 ){
                bw.write(data[0]);
                bw.newLine();
            }

        }
        br.close();
        bw.close();
        System.out.println("done!");

    }

    /**
     * 对于有多个Infobox匹配的entity，我们选出最有可能的一个entity所对应的infobox
     * @param entity 输入的实体
     * @param sentences 他所对应的句子
     * @return
     * @throws Exception
     */
    public static String getInfoBySentences(String entity, String sentences) throws Exception{
        HashMap<Integer, HashSet<String>> infoSegemented = new HashMap<Integer, HashSet<String>>(); //<id, <分词后的成语结合>>
        HashMap<Integer, String> info = EntityDAO.queryInfo(entity);

        //对句子进行分词
        HashSet<String> sentSegemented = segementation(sentences);

        //对不同的infobox结果进行分词
        Iterator<Integer> it = info.keySet().iterator();
        while(it.hasNext()){
            int id = it.next();
            String element = info.get(id);
            //如果只有一个info就返回当前内容
            if(info.size()<=1)
                return element;
            else{
                HashSet<String> wordsAll = new HashSet<String>();
                String[] pair = element.split("#"); // 中文名=叶青#国籍=中华人民共和国#民族=汉族#
                for(int i = 0; i < pair.length; i++){
                    String[] infomation = pair[i].split("="); //中文名=叶青
                    String property = infomation[1];
                    HashSet<String> hs = segementation(property);
                    wordsAll.addAll(hs); //对属性分词以后放进wordsAll中
                }
                infoSegemented.put(id, wordsAll); //得到多条infobox的分词结果
            }
        }

        //<id, 匹配的个数>
        HashMap<Integer, Integer> matchNum = new HashMap<Integer, Integer>();
        Iterator<Integer> infoIt = infoSegemented.keySet().iterator();
        while(infoIt.hasNext()){
            int id = infoIt.next();
            matchNum.put(id, compareSimilarity(infoSegemented.get(id), sentSegemented));
        }
        int maxId = findMax(matchNum);

        return info.get(maxId);

    }

    /**
     * 对于有多个Infobox匹配的entity，我们选出最有可能的一个entity所对应的infobox的id
     * @param sentences
     * @param result
     * @return
     * @throws Exception
     */
    public static int getIdBySentences(String sentences, HashMap<Integer, String> result) throws Exception{
        HashMap<Integer, HashSet<String>> infoSegemented = new HashMap<Integer, HashSet<String>>(); //<id, <分词后的成语结合>>
        HashMap<Integer, String> info = result;

        //对句子进行分词
        HashSet<String> sentSegemented = segementation(sentences);

        //对不同的infobox结果进行分词
        Iterator<Integer> it = info.keySet().iterator();
        while(it.hasNext()){
            int id = it.next();
            String element = info.get(id);
            if(info.size()==1)
                return id;
          HashSet<String> wordsAll = new HashSet<String>();
            String[] pair = element.split("#"); // 中文名=叶青#国籍=中华人民共和国#民族=汉族#
            for(int i = 0; i < pair.length; i++){
                String[] infomation = pair[i].split("="); //中文名=叶青
                String property = infomation[1];
                HashSet<String> hs = segementation(property);
                wordsAll.addAll(hs); //对属性分词以后放进wordsAll中
            }
            infoSegemented.put(id, wordsAll); //得到多条infobox的分词结果
        }

        //<id, 匹配的个数>
        HashMap<Integer, Integer> matchNum = new HashMap<Integer, Integer>();
        Iterator<Integer> infoIt = infoSegemented.keySet().iterator();
        while(infoIt.hasNext()){
            int id = infoIt.next();
            matchNum.put(id, compareSimilarity(infoSegemented.get(id), sentSegemented));
        }
        int maxId = findMax(matchNum);

        return maxId;

    }

    /**
     * 比较一个List中哪个数字最大并返回
     * @param list
     * @return
     */
    public static Integer findMax(HashMap<Integer, Integer> list) {
        int maxId = -1;
        int max = 0;
        Iterator<Integer> listIt = list.keySet().iterator();
        while(listIt.hasNext()){
            int id = listIt.next();
            int count = list.get(id);
            if(count>max) {
                max = count;
                maxId = id;
            }
        }
        return maxId;
    }

    /**
     * 计算infobox分词后得到的集合，与关键句分词得到的集合，两者匹配的个数
     * @param info
     * @param sentence
     * @return
     */
    public static Integer compareSimilarity(HashSet<String> info, HashSet<String> sentence){
        int count = 0;
        Iterator<String> infoIt = info.iterator();
        while(infoIt.hasNext()){
            String item = infoIt.next();
            if (sentence.contains(item))
                count++;
        }
        return count;
    }

    /**
     * 对句子进行分词，分词结果返回一个hashSet
     * @param sentence
     * @return
     */
    public static HashSet<String> segementation (String sentence){
        HashSet<String> words = new HashSet<String>();
        List parse = ToAnalysis.parse(sentence);
        Iterator var4 = parse.iterator();

        while(var4.hasNext()) {
            Term t = (Term)var4.next();
            String tStr = t.toString();
            if(tStr.indexOf("/") >= 0) {
                String name = tStr.substring(0, tStr.indexOf("/"));
                if(name.length() != 0) {
                    words.add(name);
                }
            }
        }
        return words;
    }

    /**
     * 给定一个输入文件sentences，根据关键句找出结果中最适合它的Infobox并输出
     * @param sentences
     * @param sentenceInfo
     * @throws Exception
     */
    public static void getMultipleInfo(String sentences, String sentenceInfo) throws Exception{
        int count = 0;
        FileOutputStream fos = new FileOutputStream(sentenceInfo);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        String inputStr ;
        FileReader fr = new FileReader(sentences);
        BufferedReader br = new BufferedReader(fr);
        while((inputStr = br.readLine()) != null){
            String[] data = inputStr.split("\t");
            //跳过没有关键句的
            if(data.length < 2)
                continue;
            count++;
            String infobox = getInfoBySentences(data[0], data[1]);
            bw.write(data[0] + "\t" + infobox);
            bw.newLine();
            if(count%100 == 0)
                System.out.println(count);
        }
        br.close();
        bw.close();
        System.out.println("done!");
    }

    public static void main(String[] args) throws Exception{
        //getInfoByEvent(entityInfo, entityOverlap);
        //getMutipleInfo();
        //System.out.println(segementation("评定为高级船长职称"));
        // System.out.println(getInfoBySentences("张志伟", "这个高级船长书画"));

    }
}
