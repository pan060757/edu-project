package timeline;

import common.ANSJ;
import common.Myutil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;


public class NewTimeLine {

    public static String formTitle(String title){

        title = title.replaceAll("\\(.*?\\)", "").replaceAll("\\（.*?\\）", "").replaceAll("\\[.*?\\]", "").replaceAll("\\【.*?\\】", "").trim();
        if (title.contains("--教育--人民网 "))
            title = title.substring(0, title.indexOf("--教育--人民网 "));
        if (title.contains("(图"))
            title = title.substring(0, title.indexOf("(图"));
        else if (title.contains("（图"))
            title = title.substring(0, title.indexOf("（图"));
        else if (title.contains(" 图"))
            title = title.substring(0, title.indexOf(" 图"));
        else if (title.contains(" 图"))
            title = title.substring(0, title.indexOf(" 图"));

        return title;
    }

    public static List<String> getTimeline(List<Record> records) {
        List<String> outcome = new ArrayList<String>();
        Collections.sort(records);
        List<Record> newRecords = new ArrayList<Record>();
        for (int i = 0; i < records.size(); i++) {
            boolean similarity = false;
            for (int j = 0; j < newRecords.size(); j++) {
                if (sim(formTitle(records.get(i).getTitle()), formTitle(newRecords.get(j).getTitle())) > 0.6) {
                    similarity = true;
                    break;
                }
                if (sim(records.get(i).getContent(), newRecords.get(j).getContent()) > 0.6) {
                    similarity = true;
                    break;
                }
            }
            if (!similarity)
                newRecords.add(records.get(i));
        }
        outcome.add(newRecords.get(0).getId());
        for (int i = 1; i < newRecords.size(); i++) {
            JSD kld = new JSD(newRecords.get(i - 1).getTopicDist(), newRecords
                    .get(i).getTopicDist());
            if (kld.divergence() > 0.2) {
                outcome.add(newRecords.get(i).getId());
                //System.out.println(newRecords.get(i).getId() + "\t"
                        //+ newRecords.get(i).getTitle() + "\t" + newRecords.get(i).getDate());
            }

        }
        //System.out.println();
        return outcome;
    }

    private static double sim(String aString, String bString) {
        aString = ANSJ.segContent(aString);
        bString = ANSJ.segContent(bString);
        String[] as = aString.split(" ");
        String[] bs = bString.split(" ");
        Set<String> aSet = new HashSet<String>();
        Set<String> bSet = new HashSet<String>();
        for (String s : as)
            aSet.add(s);
        for (String s : bs)
            bSet.add(s);
        Set<String> unionSet = new HashSet<String>();
        unionSet.addAll(aSet);
        unionSet.addAll(bSet);
        Set<String> joinSet = new HashSet<String>();
        Iterator<String> iterator = aSet.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (bSet.contains(s))
                joinSet.add(s);
        }
        return (double) (joinSet.size()) / (double) (unionSet.size());
    }

    public static void main(String[] args) throws IOException {

        List<String> lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/distinctpageid.txt");
        HashSet<String> pageIds = new HashSet<String>();
        for (String line : lines) {
            pageIds.add(line);
        }
        lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/page.txt");
        HashMap<String, String> pages = new HashMap<String, String>();
        for (String line : lines) {
            pages.put(line.split("\t")[0], line);
        }
        lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/event_id2page_id.txt");
        HashMap<String, HashSet<String>> pageIdofevent = new HashMap<String, HashSet<String>>();
        for (String line : lines) {
            String[] TW = line.split("\t");
            if (!pageIdofevent.containsKey(TW[0]))
                pageIdofevent.put(TW[0], new HashSet<String>());
            pageIdofevent.get(TW[0]).add(TW[1]);
        }
        FileOutputStream fis = new FileOutputStream("/Users/binbin/Desktop/edukg2015data/timeline.txt", false);
        OutputStreamWriter fw = new OutputStreamWriter(fis, "utf-8");
        for (String eventId : pageIdofevent.keySet()) {
            List<Record> records = new ArrayList<Record>();
            boolean flag = true;
            for (String pageId : pageIdofevent.get(eventId)) {
                if (!pageIds.contains(pageId))
                    continue;
                String[] TW = pages.get(pageId).split("\t");
                String time = TW[3];
                String title = TW[4];
                String content = TW[5];
                String topicdis = "";
                if (TW.length > 6)
                    topicdis = TW[6];
                if (topicdis.equals("")) {
                    flag = false;
                    break;
                }
                Record record = new Record(pageId, title, time, content, topicdis);
                records.add(record);
            }
            if (flag) {
                List<String> result = getTimeline(records);
                System.out.println(eventId + "\t" + result);
//                for (String pageId : result)
//                    fw.write(eventId + "\t" + pageId + "\n");
            }
        }
        fw.close();
    }

}
