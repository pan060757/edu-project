package entityrank.nlp;

import java.util.List;

public class SEWeight {


    public static double seWeight(String entity, String sentence, List<String> sentences) {
        return (1 + Math.log(tf(entity, sentence))) * Math.log(sentences.size() / sf(entity, sentences)) + 0.001;
    }

    public static double sf(String entity, List<String> sentences) {
        int count = 0;
        for (String s : sentences) {
            if (s.indexOf(entity) >= 0)
                count++;
        }
        return count;
    }

    public static double tf(String entity, String content) {
        int count = 0;
        while (content.indexOf(entity) >= 0) {
            count++;
            content = content.substring(content.indexOf(entity) + entity.length());
        }
        return count;
    }

}
