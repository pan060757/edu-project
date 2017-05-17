package util;

//import edu.ecnu.preprocess.PageInfo;
//import edu.ecnu.preprocess.PageRetrieval;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by sunkai on 2015/5/18.
 */
public class utils {



    /**
     * 分句方法
     * @param content
     * @return
     */

    public static ArrayList<String> fenju(String content){
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

public static String ansjFenci(String content){
    String oneSentence = "";
    List<Term> results = NlpAnalysis.parse(content);
    StringBuilder sb = new StringBuilder();
    for(Term result : results){

        if (result.getNatureStr().equals("n")||result.getNatureStr().equals("nt")||result.getNatureStr().equals("nw")) {  // W要改，改成名称的标识符
            sb.append(result.getName()+" ");
        }
    }
    oneSentence = sb.toString();
    return oneSentence;
}


    /**
     * 从database中读取一篇文章的信息。
     *
     * @param retrieval
     * @param pageID
     * @return
     */
//    public static PageInfo getOnePageById(PageRetrieval retrieval, long pageID) {
//        PageInfo page = retrieval.retrievePage(pageID);
//        return page;
//    }

    /**
     * 按行读取文本
     * @param inputPath
     * @return
     */
    public static ArrayList<String> readByLine(String inputPath) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            // read file content from file

            FileReader reader = new FileReader(inputPath);
            BufferedReader br = new BufferedReader(reader);

            String str = null;

            while ((str = br.readLine()) != null) {
                result.add(str);
            }
            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static double sim_BOW(ArrayList<Keyword> textKeyWords, ArrayList<Keyword> contentKeyWords, HashMap<String, Double> allKeyWordsTitles) {
        HashMap<String, Double> result = new HashMap<String, Double>();

            ArrayList<TreeMap<String, Double>> twoVector = createVector(textKeyWords, contentKeyWords, allKeyWordsTitles); //value里面存的是向量的值
            TreeMap<String, Double> textMap = twoVector.get(0);
            TreeMap<String, Double> contentMap = twoVector.get(1);
            ArrayList<Double> textVector = new ArrayList<Double>(textMap.values());
            ArrayList<Double> contentVector = new ArrayList<Double>(contentMap.values());
//            System.out.println("pp"+textMap);
//            System.out.println("pp"+contentMap);
            double sim_value = sim(textVector, contentVector);

//        ArrayList<TreeMap<String,Double>> twoVector = createVector(textKeyWords,contentKeyWords); /alue里面存的是向量的值
        return sim_value;
    }

    // 求余弦相似度
    public static double sim(ArrayList<Double> textVector, ArrayList<Double> contentVector) {
        double result = 0;
        result = pointMulti(textVector, contentVector) / sqrtMulti(textVector, contentVector);

        return result;
    }

    private static double sqrtMulti(List<Double> vector1, List<Double> vector2) {
        double result = 0;
        result = squares(vector1) * squares(vector2);
        result = Math.sqrt(result);
        return result;
    }

    // 求平方和
    private static double squares(List<Double> vector) {
        double result = 0;
        for (Double dou : vector) {
            result += dou * dou;
        }
        return result;
    }

    // 点乘法
    private static double pointMulti(List<Double> vector1, List<Double> vector2) {
        double result = 0;
        for (int i = 0; i < vector1.size(); i++) {
            result += vector1.get(i) * vector2.get(i);
        }
        return result;
    }

    private static ArrayList<TreeMap<String, Double>> createVector(ArrayList<Keyword> textKeyWords, ArrayList<Keyword> contentKeyWords, HashMap<String, Double> allKeyWordsMap) {
//        TreeMap<String,Double> allKeyWordsMap = new TreeMap<String, Double>(); //里面存的是需要生成向量的所有无重复词

        TreeMap<String, Double> textMap = new TreeMap<String, Double>();
        TreeMap<String, Double> contentMap = new TreeMap<String, Double>();

        ArrayList<TreeMap<String, Double>> result = new ArrayList<TreeMap<String, Double>>();

        //切忌不能用映射
        Iterator<String> iterator = allKeyWordsMap.keySet().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            double value = allKeyWordsMap.get(name);
            textMap.put(name, value);
            contentMap.put(name, value);
        }


        for (Keyword textKeyWord : textKeyWords) {
            String name = textKeyWord.getName();
            double score = textKeyWord.getScore();
            score = Math.log(score);
            textMap.put(name, score);
        }
        for (Keyword contentKeyWord : contentKeyWords) {
            String name = contentKeyWord.getName();
            double score = contentKeyWord.getScore();
            score = Math.log(score);

            contentMap.put(name, score);
        }

        result.add(textMap);
        result.add(contentMap);

        return result;
    }

//
//    public static void main(String[] args) {
///*        //test1
//        ArrayList<String> testRead = readByLine("D:\\EduProject\\2015.01.22\\refdoc.txt");
//        for(String line : testRead){
//            System.out.println(line);
//        }*/
//
//        //test2
//        PageRetrieval retrieval = new PageRetrieval(Constant.DataBasePath);
//
//        PageInfo page = getOnePageById(retrieval,805844l);
//        String title = page.getTitle();
//        String content = page.getContent();
//        System.out.println(title);
//        System.out.println(content);
//
//    }
}
