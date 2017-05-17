package edu.ecnu.edukg.singalnew;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import util.Constant;
import util.utils;

import java.util.*;

/**
 * Created by sunkai on 2015/5/18.
 */
public class getKeySentenceByTfIdf {

    //分句
    public ArrayList<String> fenju(String content){
        ArrayList<String> sentences = new ArrayList<String>();

        List<Term> results = NlpAnalysis.parse(content);
        StringBuilder sb = new StringBuilder();
        for(Term result : results){

            if (!result.getNatureStr().equals("w")) {  // W要改，改成名称的标识符
                sb.append(result.getName());
            }else{
                if(result.getName().equals("。")){
                    sb.append(result.getName());
                    sentences.add(sb.toString());
                    sb.delete(0, sb.length());
                }else {
                    sb.append(result.getName());
                }
            }

        }
        return sentences;
    }

    /**
     * 方案一，整个句子的tfidf值
     *
     */
    //每个句子得分
    private double getSentenceScoreTfidf(String title,String sentence){


        KeyWordComputer kwc = new KeyWordComputer(Constant.SetingKeyWords);
        Collection<Keyword> result = kwc.computeArticleTfidf(title,sentence);
        double scores = 0;
        Iterator<Keyword> iterator = result.iterator();
        while (iterator.hasNext()){
            Keyword word = iterator.next();
            String name = word.getName();
            double score = word.getScore();
           scores=scores+score;
        }
        double f = scores/result.size();
        return f;
    }


    /**
     * 方案二，只计算名词的tfidf值
     *
     */

    private double getSentenceScoreTfidf_n(String title,String sentence){
        ArrayList<String> nuns = new ArrayList<String>();
        List<Term> results = NlpAnalysis.parse(sentence);
        for(Term result : results){
            if(result.getNatureStr().equals("n")||result.getNatureStr().equals("nt")||result.getNatureStr().equals("nw")){
                nuns.add(result.getName());
            }
        }

        KeyWordComputer kwc = new KeyWordComputer(Constant.SetingKeyWords);
        Collection<Keyword> result = kwc.computeArticleTfidf(title,sentence);
        double scores = 0;
        double count = 0;
        Iterator<Keyword> iterator = result.iterator();
        while (iterator.hasNext()){
            Keyword word = iterator.next();
            String name = word.getName();
            double score = word.getScore();
            if(nuns.contains(name)){
                scores=scores+score;
                count++;
            }

        }
        double f = scores/count;
        return f;
    }

    /**
     * 方案三，只计算名词的tfidf值
     *
     */
    private double getSentenceScoreTfidf_nv(String title,String sentence){


        ArrayList<String> nuns = new ArrayList<String>();
        List<Term> results = NlpAnalysis.parse(sentence);
        for(Term result : results){
            if(result.getNatureStr().equals("n")||result.getNatureStr().equals("nt")||result.getNatureStr().equals("nw")||result.getNatureStr().equals("v")){
                nuns.add(result.getName());
            }
        }

        KeyWordComputer kwc = new KeyWordComputer(Constant.SetingKeyWords);
        Collection<Keyword> result = kwc.computeArticleTfidf(title,sentence);
        double scores = 0;
        double count = 0;
        Iterator<Keyword> iterator = result.iterator();
        while (iterator.hasNext()){
            Keyword word = iterator.next();
            String name = word.getName();
            double score = word.getScore();
            if(nuns.contains(name)){
                scores=scores+score;
                count++;
            }

        }
        double f = scores/count;
        return f;
    }

    /**
     * 方案四，计算0.2*所有词的tfidf值+0.8*（题目与句子的相似性）
     *
     */

    private double getSentenceScoreTfidfandTitleSimilaration(String title,String sentence){


        ArrayList<String> nuns = new ArrayList<String>();
        List<Term> results = NlpAnalysis.parse(sentence);
        for(Term result : results){
            if(result.getNatureStr().equals("n")||result.getNatureStr().equals("nt")||result.getNatureStr().equals("nw")||result.getNatureStr().equals("v")){
                nuns.add(result.getName());
            }
        }

        KeyWordComputer kwc = new KeyWordComputer(Constant.SetingKeyWords);
        Collection<Keyword> result = kwc.computeArticleTfidf(title,sentence);
        double scores = 0;
        double count = 0;
        Iterator<Keyword> iterator = result.iterator();
        while (iterator.hasNext()){
            Keyword word = iterator.next();
            String name = word.getName();
            double score = word.getScore();
            if(nuns.contains(name)){
                scores=scores+score;
                count++;
            }
        }


//        KeyWordComputer kwc = new KeyWordComputer(Constant.SetingKeyWords);
//        Collection<Keyword> result = kwc.computeArticleTfidf(sentence);
//        double scores = 0;
//        Iterator<Keyword> iterator = result.iterator();
//        while (iterator.hasNext()){
//            Keyword word = iterator.next();
//            String name = word.getName();
//            double score = word.getScore();
//            scores=scores+score;
//        }
        double similar = SimByBOW(title, sentence);
        double f = 0.2*(scores/result.size())+0.8*similar;


        return f;
    }

    /**
     * 方案五，利用词包方法计算文本和题目的相似度
     * @param title
     * @param sentence
     * @return
     */
    private double SimByBOW(String title, String sentence) {
        ArrayList<Keyword> titleKeyWords = findKeyWords_ANSJ(title);
        ArrayList<Keyword> contentKeyWords = findKeyWords_ANSJ(sentence);
        HashMap<String,Double> allKeyWordsTitles = getAllKeyWordsTitles(titleKeyWords,contentKeyWords);
        double sim = utils.sim_BOW(titleKeyWords, contentKeyWords, allKeyWordsTitles);
        return sim;
    }


    //对得分进行排序
    public List<Map.Entry<String, Double>> sortContentTFidf(String title,String content,String tag){
        HashMap<String,Double> keyWords = new HashMap<String, Double>();
        ArrayList<String> sentences = fenju(content);
        double score = 0;
        for(String line : sentences){
            line = line.replace(" ","").replace("\t","").replace("\n","");
            if(tag=="N"){
                score = getSentenceScoreTfidf_n(title,line);
            }else if(tag=="NV"){
                score = getSentenceScoreTfidf_nv(title,line);
            }else if(tag=="ALL"){
                score = getSentenceScoreTfidf(title,line);
            }else if(tag =="TFBOW"){
                score = getSentenceScoreTfidfandTitleSimilaration(title,line);
            }else if(tag =="BOW"){
                score = SimByBOW(title,line);
            }
            keyWords.put(line,score);
        }
        List<Map.Entry<String, Double>> result = sort(keyWords);
        return result;
    }


    //对得分进行排序
    public List<Map.Entry<String, Double>> sortContentTFidf(String title,String content,ArrayList<String> summarizationWords,String tag){
        title = title.replace("\t","");
        content = content.replace("\t","");
        HashMap<String,Double> keyWords = new HashMap<String, Double>();
        ArrayList<String> sentences = fenju(content);
        ArrayList<String> summarizationSentences = new ArrayList<String>();
        double score = 0;
        for(String line : sentences){
            line = line.replace(" ","").replace("\t","").replace("\n","");
            for(String word : summarizationWords){

                if(line.startsWith(word)){

                    summarizationSentences.add(line);
                }
            }
            if(tag=="N"){
                score = getSentenceScoreTfidf_n(title,line);
            }else if(tag=="NV"){
                score = getSentenceScoreTfidf_nv(title,line);
            }else if(tag=="ALL"){
                score = getSentenceScoreTfidf(title,line);
            }else if(tag =="TFBOW"){
                score = getSentenceScoreTfidfandTitleSimilaration(title,line);
            }else if(tag =="BOW"){
                score = SimByBOW(title,line);
            }


            keyWords.put(line,score);
        }
        HashMap<String,Double> zongjie = new HashMap<String, Double>();
        HashMap<String,Double> qita = new HashMap<String, Double>();
        List<Map.Entry<String, Double>> result = sort(keyWords);
        if(!summarizationSentences.isEmpty()){
            for(Map.Entry<String,Double> line : result){
                String key = line.getKey();
                double value = line.getValue();
                if(summarizationSentences.contains(key)){
                    zongjie.put(key,value);
                }else {
                    qita.put(key,value);
                }
            }
            List<Map.Entry<String, Double>> result1 = sort(zongjie);
            List<Map.Entry<String, Double>> result2 = sort(qita);
            for(Map.Entry<String,Double> line : result2){
                result1.add(line);
            }
            return result1;
        }else {
            return result;
        }

    }

    public static List<Map.Entry<String, Double>> sort(HashMap<String, Double> result) {
        List<Map.Entry<String, Double>> infoIds =
                new ArrayList<Map.Entry<String, Double>>(result.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                if (o2.getValue() - o1.getValue() > 0) {
                    return 1;
                } else if (o2.getValue() - o1.getValue() == 0) {
                    return 0;
                } else {
                    return -1;
                }

            }
        });
        return infoIds;
    }

    private static HashMap<String, Double> getAllKeyWordsTitles(ArrayList<Keyword> pageKeyWords, ArrayList<Keyword> contentKeyWords) {
        HashMap<String, Double> allKeyWordsTitles = new HashMap<String, Double>();

            for (Keyword keyword : pageKeyWords) {
                String word = keyword.getName();
                if (!allKeyWordsTitles.containsKey(word)) {
                    allKeyWordsTitles.put(word, Constant.initVector);
                }
            }

        for (Keyword keyword : contentKeyWords) {
            String word = keyword.getName();
            if (!allKeyWordsTitles.containsKey(word)) {
                allKeyWordsTitles.put(word, Constant.initVector);
            }
        }
        return allKeyWordsTitles;
    }

//    private static ArrayList<PageKeyWord> getPageKeyWords(String title){
//        ArrayList<PageKeyWord> pageKeyWords = new ArrayList<PageKeyWord>();
//
//
//                ArrayList<Keyword> textKeyWords = findKeyWords_ANSJ(title);
//                PageKeyWord pageKeyWord = new PageKeyWord(title, textKeyWords);
//                pageKeyWords.add(pageKeyWord);
//
//
//        return pageKeyWords;
//    }

    private static ArrayList<Keyword> findKeyWords_ANSJ(String fileContent) {
        ArrayList<Keyword> result = new ArrayList<Keyword>();
        KeyWordComputer kwc = new KeyWordComputer(Constant.SetingKeyWords);
        Collection<Keyword> semi_result = kwc.computeArticleTfidf(fileContent);
        Iterator<Keyword> iterator = semi_result.iterator();
        while (iterator.hasNext()) {
            Keyword keyword = iterator.next();
            result.add(keyword);
        }

        return result;
    }

    private static ArrayList<String> sortHashMap(HashMap<String, Double> result) {

        ArrayList<String> jieguo = new ArrayList<String>();
        //对hashmap进行排序
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>();
        list.addAll(result.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> obj1, Map.Entry<String, Double> obj2) {//从高往低排序
                if (obj1.getValue() < obj2.getValue())
                    return 1;
                if (obj1.getValue() == obj2.getValue())
                    return 0;
                else
                    return -1;
            }
        });

        for (Iterator<Map.Entry<String, Double>> ite = list.iterator(); ite.hasNext(); ) {
            Map.Entry<String, Double> map = ite.next();
//            System.out.println("key-value: " + map.getKey() + "," + map.getValue());
            jieguo.add(map.getKey()+"\t"+map.getValue());
        }
        return jieguo;
    }


    public static void main(String[] args) {
        //test1
        String title = "北大2014年中学校长实名推荐制名单：吉林13人";
        String testText = "北京大学 2014年“中学校长实名推荐制”自主选拔录取候选人信息已公布，根据中学校长所推荐学生的具体情况，北京大学招生办公室使用“元培综合评价系统”对2014年“中学校长实名推荐制”推荐学生进行全方位、多角度、多层次的考核，包括初步审核评价、学科基础面试、综合面试、随机抽查笔试、体质测试五个环节。综合上述五个环节的考生表现和专家评审意见，经北大自主招生专家委员会匿名投票，确定北京大学2014年“中学校长实名推荐制”自主选拔录取候选人名单。现将候选人名单公示如下（公示期一周）。如公示对象存在相关问题，请通过书面方式向北京大学招生办公室反映。";
        getKeySentenceByTfIdf keyS = new getKeySentenceByTfIdf();
        List<Map.Entry<String, Double>> results = keyS.sortContentTFidf(title,testText,"TFBOW");
        for(Map.Entry<String, Double> result : results){
            System.out.println(result.getKey()+" "+result.getValue());
        }
    }
}
