package entityrank.nlp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import common.ANSJ;

public class SSWeight {

    public static double ssWeight(String sen1, String sen2) {
        Set<String> wordSet1 = new HashSet<String>();
        Set<String> wordSet2 = new HashSet<String>();

        String []terms1 = ANSJ.segContent(sen1).split(" ");
        String []terms2 = ANSJ.segContent(sen2).split(" ");
        for (String term : terms1) {
            wordSet1.add(term);
        }
        for (String term : terms2) {
            wordSet2.add(term);
        }

        int count = 0;
        Iterator<String> iter = wordSet1.iterator();
        while (iter.hasNext()) {
            String word = iter.next();
            if (wordSet2.contains(word))
                count++;
        }
        return (double) count / (0.000001 + Math.log(wordSet1.size()) + Math.log(wordSet2.size()));
    }

}
