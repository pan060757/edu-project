package entityrank.rank;

import common.Myutil;
import entityrank.entity.DocRecord;
import entityrank.entity.Pair;
import entityrank.graph.*;
import entityrank.multi.*;

import java.util.*;


public class MultiDocSum {

    public static List<String> entityRank(List<DocRecord> records) {
        List<String> outcome = new ArrayList<String>();
        List<List<Pair>> lists = new ArrayList<List<Pair>>();
        for (int i = 0; i < records.size(); i++) {
            DocRecord record = records.get(i);
            Graph graph = new RecordToGraph(record).toGraph();
            graph.iterativeWeightUpdate(100, 0.2);
            List<Pair> ranks = graph.normalizedEntityRank();
            lists.add(ranks);
        }
        List<Pair> sumList = MultiRankSum.rank(lists);
        Collections.sort(sumList);
        Collections.reverse(sumList);
        double rankSum = 0;
        int count = 0;
        for (Pair r : sumList) {
            rankSum += r.getRank();
            count++;
            if (rankSum > 0.999 || count > 50)
                break;
            else {
                outcome.add(r.getEntity());
            }
        }
        return outcome;
    }



    public static void main(String[] args) {

        //        List<Map.Entry<String, Integer>> LM = SortAsc(organization);
//        HashMap<Integer, Integer> temp = new HashMap<>();
//        for (int i = 0; i < LM.size() - 1; i++) {
//            for (int j = i + 1; j < LM.size(); j++) {
//                if (isSubString(LM.get(i).getKey(), LM.get(j).getKey())) {//A是否是B的字串
//                    temp.put(entityDictionary.get(LM.get(i).getKey()+"/机构名"), entityDictionary.get(LM.get(j).getKey()+"/机构名"));
//                    System.out.println(LM.get(i).getKey() + "\t" + LM.get(j).getKey());
//                    break;
//                }
//            }
//        }

        List<String> lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/page.txt");
        HashMap<String, String> pages = new HashMap<String, String>();
        for (String line : lines) {
            pages.put(line.split("\t")[0], line);
        }
        lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/entity.txt");
        HashMap<String, ArrayList<String>> entityofpage = new HashMap<String, ArrayList<String>>();
        for (String line : lines) {
            String[] TW = line.split("\t");
            if (!entityofpage.containsKey(TW[0]))
                entityofpage.put(TW[0], new ArrayList<String>());
            entityofpage.get(TW[0]).add(TW[1]);
        }
        lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/event_id2page_id.txt");
        HashMap<String, HashSet<String>> pageIdofevent = new HashMap<String, HashSet<String>>();
        for (String line : lines) {
            String[] TW = line.split("\t");
            if (!pageIdofevent.containsKey(TW[0]))
                pageIdofevent.put(TW[0], new HashSet<String>());
            pageIdofevent.get(TW[0]).add(TW[1]);
        }
        for (String eventId : pageIdofevent.keySet()) {
            System.out.println(eventId);
            List<DocRecord> records = new ArrayList<DocRecord>();
            ArrayList<String> original = new ArrayList<String>();
            for (String pageId : pageIdofevent.get(eventId)) {
                String[] TW = pages.get(pageId).split("\t");
                String title = TW[4];
                String content = TW[5];
                if(!entityofpage.containsKey(pageId))
                    continue;
                original.addAll(entityofpage.get(pageId));
                DocRecord docRecord = new DocRecord(pageId, title, entityofpage.get(pageId), content);
                records.add(docRecord);
            }
            
            List<String> result = entityRank(records);

            System.out.println("original");
            for (String entity : original) {
                System.out.println(entity);
            }
            System.out.println("result");
            for (String entity : result) {
                System.out.println(entity);
            }
        }
    }

}
