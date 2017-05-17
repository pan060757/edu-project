package common;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;


/**
 * Created by Howie on 2015/12/5.
 */

public class ANSJ {

    private static final String personLabel = "person";

    private static final String locationLabel = "location";

    private static final String orgLabel = "organization";

    private static List<String> stopWords;

    private static ArrayList<String> location;

    private static HashMap<String, String> cityMap;

    private static HashMap<String, String> schoolLocation;

    private static ArrayList<String> career;

    private static ArrayList<String> nwSuffix;

    public static String getPersonLabel() {
        return personLabel;
    }

    public static String getLocationLabel() {
        return locationLabel;
    }

    public static String getOrgLabel() {
        return orgLabel;
    }

    public static String formPlace(String place) {

        if (place.length() > 2 && (place.endsWith("省") || place.endsWith("市")
                //|| place.endsWith("县")
        ))
            place = place.substring(0, place.length() - 1);
        if (place.endsWith("自治区") || place.endsWith("自治州") || place.endsWith("自治县"))
            place = place.substring(0, place.length() - 3);
        return place;
    }

    public static boolean NoSensePersonCheck(String curWord, List<Term> parse, int index) {

        boolean result = false;
        if (curWord.length() == 2 && curWord.startsWith("小"))
            return true;
        if (curWord.length() == 3 && (curWord.endsWith("先生") || curWord.endsWith("女士") || curWord.endsWith("小姐")))
            return true;
        if (curWord.length() == 4 && (curWord.endsWith("男生") || curWord.endsWith("女生") || curWord.endsWith("新生")))
            return true;
        if (index - 1 >= 0) {
            String preWord = parse.get(index - 1).getName();
            if (preWord.equals("摄影师") || preWord.equals("实习生") || preWord.endsWith("记者") || preWord.equals("评论员")
                    || preWord.equals("通讯员"))
                return true;
        }
        if (index + 1 < parse.size()) {
            String postWord = parse.get(index + 1).getName();
            if (postWord.equals("摄") || postWord.equals("拍摄") || postWord.equals("报道") || postWord.equals("摄影师")
                    || postWord.equals("实习生") || postWord.endsWith("记者") || postWord.equals("评论员")
                    || postWord.equals("通讯员"))
                return true;
        }
        return result;
    }

    public static boolean isLocation(String word) {

        boolean result = false;
        for (String toponym : location) {
            if (word.equals(toponym)) {
                return true;
            }
        }
        return result;
    }

    public static String isOrganization(Term term, List<Term> parse, Counter counter) {

        String curTag = term.getNatureStr();
        String curWord = term.getName();
        String organization = "";
        boolean flag = false;

        if(curTag.equals("nw")){
            for(String suffix : nwSuffix){
                if(curWord.endsWith(suffix))
                    flag = true;
            }
            if(!flag)
                return "";
        }

        for (String toponym : location) {
            if (curWord.startsWith(toponym)) {
                return curWord;
            }
        }

        if (schoolLocation.containsKey(curWord)) {
            return curWord;
        }

        if (curTag.equals("nis") || curTag.equals("nit")|| curTag.equals("nw")
                || (curWord.length() <= 8 && curWord.endsWith("股份有限公司"))
                || (curWord.length() <= 6 && curWord.endsWith("有限公司"))
                || (curWord.length() <= 6 && curWord.endsWith("集团公司"))
                || (curWord.length() <= 4 && curWord.endsWith("公司"))){
            for (int j = 1; j <= 5 && (counter.getCount() - j) >= 0; j++) {
                String Localtag = parse.get(counter.getCount() - j).getNatureStr();
                String Localword = parse.get(counter.getCount() - j).getName();
                flag = false;
                if (Localtag.equals("null") || Localtag.startsWith("w") || Localtag.startsWith("m")
                        || Localtag.startsWith("z") || Localtag.startsWith("r")
                        || Localtag.startsWith("q") || Localtag.startsWith("d") || Localtag.startsWith("c")
                        || Localtag.startsWith("u") || Localtag.equals("e") || Localtag.startsWith("y")
                        || Localtag.startsWith("o") || Localtag.startsWith("h") || Localtag.startsWith("k")
                        || Localtag.startsWith("x")
                        || (Localtag.startsWith("t") && !Localword.equals("现代"))
                        || (Localtag.equals("s") && !Localword.equals("东方"))
                        || (Localtag.equals("n") && Localword.equals("原名"))
                        || (Localtag.startsWith("p") && !Localword.equals("与"))
                        || (Localtag.startsWith("p") && Localword.equals("与") && !(curWord.endsWith("学院") || curWord.endsWith("研究院") || curWord.endsWith("研究所") || curWord.endsWith("研究中心")))
                        || (Localtag.startsWith("f")) && !(Localword.equals("南方") || Localword.equals("北方") || Localword.equals("东部") || Localword.equals("西部") || Localword.equals("东南") || Localword.equals("西南") || Localword.equals("西北") || Localword.equals("东北"))
                        || (!curWord.endsWith("公司") && Localtag.startsWith("v") && !Localtag.equals("vn"))
                        )
                    break;
                if(Localtag.equals("school") || (Localtag.startsWith("nt") && !Localtag.equals("nt"))){
                    for (int k = counter.getCount() - j; k <= counter.getCount(); k++) {
                        organization += parse.get(k).getName();
                    }
                    counter.Sub(j);
                    break;
                }
                if (Localtag.startsWith("ns") || Localtag.equals("nw") || Localtag.startsWith("nt")) {
                    for (String toponym : location) {
                        if (Localword.contains(toponym)) {
                            if (toponym.endsWith("区") && (counter.getCount() - j - 1) >= 0 && parse.get(counter.getCount() - j - 1).getNatureStr().startsWith("ns"))
                                continue;
                            for (int k = counter.getCount() - j; k <= counter.getCount(); k++) {
                                organization += parse.get(k).getName();
                            }
                            counter.Sub(j);
                            flag = true;
                            break;
                        }
                    }
                }
                if(flag)
                    break;
            }
        }

        return organization;
    }

    public static String formatInput(String input) {

        input = input
                .replaceAll("\t", "").replaceAll("\n", "")
                .replaceAll("\\(.*?\\)", "").replaceAll("\\（.*?\\）", "").replaceAll("\\[.*?\\]", "").replaceAll("\\【.*?\\】", "")
                .replaceAll("    ", "。").replaceAll("　", "。").replaceAll(" ","。").trim()
                .replaceAll("[。]+", "。");

        if (input.equals(""))
            return input;

        String[] segSentence = input.split("。");
        if(segSentence.length == 0)
            return input;
        String firstSentence = segSentence[0];
        String lastSentence = segSentence[segSentence.length - 1];

        if (firstSentence.contains("日电"))
            input = input.substring(input.indexOf("日电") + 2, input.length());
        else if (firstSentence.contains("日讯"))
            input = input.substring(input.indexOf("日讯") + 2, input.length());

        if(input.startsWith("，"))
            input = input.substring(1,input.length());

        if (lastSentence.contains("实习生"))
            input = input.substring(0, input.lastIndexOf("实习生"));
        else if (lastSentence.contains("记者"))
            input = input.substring(0, input.lastIndexOf("记者"));
        else if (lastSentence.contains("评论员"))
            input = input.substring(0, input.lastIndexOf("评论员"));
        else if (lastSentence.contains("通讯员"))
            input = input.substring(0, input.lastIndexOf("通讯员"));
        else if (lastSentence.contains("摄影师"))
            input = input.substring(0, input.lastIndexOf("摄影师"));

        return input;
    }

    public static boolean isCareer(Term term) {

        String word = term.getName();
        String nature = term.getNatureStr();
        if(nature.equals("nnt")||nature.equals("nnd"))
            return true;
        if (word.endsWith("记者") || word.equals("实习生") || word.equals("摄影师") || word.equals("通讯员") || word.equals("评论员"))
            return false;
        for (String profession : career)
            if (word.endsWith(profession))
                return true;

        return false;
    }

    public static ArrayList<Entity> getEntityFromSentence(List<Term> parse, String sentence) {

        ArrayList<Entity> outcome = new ArrayList<Entity>();
        HashSet<String> entitySet = new HashSet<String>();
        for (int i = 0; i < parse.size(); i++) {
            Term term = parse.get(i);
            String tag = term.getNatureStr();
            String word = term.getName();
            if (entitySet.contains(word)) continue;
            if (word.length() == 1) continue;
            if (tag.startsWith("nr")) {
                entitySet.add(word);
                Entity entity = new Entity(word, personLabel, sentence);
                outcome.add(entity);
            } else if (tag.startsWith("ns")) {
                entitySet.add(formPlace(word));
                Entity entity = new Entity(formPlace(word), locationLabel, sentence);
                outcome.add(entity);
            } else if (tag.startsWith("nt") || tag.equals("school")) {
                entitySet.add(word);
                Entity entity = new Entity(word, orgLabel, sentence);
                outcome.add(entity);
            }
        }
        return outcome;
    }

    public static ArrayList<Relation> person_R_organization(List<Term> parse, String sentence) {

        ArrayList<Relation> results = new ArrayList<Relation>();
        for (int i = 0; i < parse.size(); i++) {
            if (parse.get(i).getNatureStr().startsWith("nr") && parse.get(i).getName().length() > 1 && (i - 2) >= 0 && parse.get(i - 1).getNatureStr().startsWith("n")
                    && isCareer(parse.get(i - 1))
                    && (parse.get(i - 2).getNatureStr().startsWith("nt")
                    || parse.get(i - 2).getNatureStr().equals("school")
            )) {
                Entity entity1 = new Entity(parse.get(i).getName(), personLabel);
                String predicate = parse.get(i - 1).getName();
                Entity entity2 = new Entity(parse.get(i - 2).getName(), orgLabel);
                Relation relation = new Relation(entity1, predicate, entity2, sentence);
                results.add(relation);
            } else if (parse.get(i).getNatureStr().startsWith("nr") && (i + 4) < parse.size() && parse.get(i + 1).getName().equals("是")
                    &&
                    (parse.get(i + 2).getNatureStr().startsWith("nt")
                            || parse.get(i + 2).getNatureStr().equals("school"))
                    && parse.get(i + 3).getName().equals("的") && isCareer(parse.get(i + 4))) {
                Entity entity1 = new Entity(parse.get(i).getName(), personLabel);
                String predicate = parse.get(i + 4).getName();
                Entity entity2 = new Entity(parse.get(i + 2).getName(), orgLabel);
                Relation relation = new Relation(entity1, predicate, entity2, sentence);
                results.add(relation);
            }
        }

        return results;
    }

    public static ArrayList<Relation> organization_R_location(List<Term> parse, String sentence, HashSet<String> locationEntity) {

        ArrayList<Relation> results = new ArrayList<Relation>();
        for (int i = 0; i < parse.size(); i++) {
            Term term = parse.get(i);
            String tag = term.getNatureStr();
            String word = term.getName();
            if (word.length() == 1) continue;
            if (tag.equals("school") || tag.startsWith("nt")) {
                boolean flag = true;
                if (schoolLocation.containsKey(word) && locationEntity.contains(schoolLocation.get(word))) {
                    Entity entity1 = new Entity(word, orgLabel);
                    String predicate = "位于";
                    Entity entity2 = new Entity(schoolLocation.get(word), locationLabel);
                    Relation relation = new Relation(entity1, predicate, entity2, sentence);
                    results.add(relation);
                    flag = false;
                }
                if (flag == false)
                    continue;
                for (String city : location)
                    if (word.contains(city) && locationEntity.contains(city)) {
                        if (city.endsWith("区"))
                            city = cityMap.get(city);
                        Entity entity1 = new Entity(word, orgLabel);
                        String predicate = "位于";
                        Entity entity2 = new Entity(city, locationLabel);
                        Relation relation = new Relation(entity1, predicate, entity2, sentence);
                        results.add(relation);
                        flag = false;
                        break;
                    }
                if (flag == false)
                    continue;
                for (int j = 1; j <= 3 && (i - j) >= 0 && !parse.get(i - j).getNatureStr().equals("w"); j++) {
                    for (String city : location) {
                        if ((parse.get(i - j).getNatureStr().startsWith("ns") || parse.get(i - j).getNatureStr().startsWith("nt") || parse.get(i - j).getNatureStr().equals("school"))
                                && parse.get(i - j).getName().contains(city) && locationEntity.contains(city)) {
                            if (city.endsWith("区"))
                                city = cityMap.get(city);
                            Entity entity1 = new Entity(word, orgLabel);
                            String predicate = "位于";
                            Entity entity2 = new Entity(city, locationLabel);
                            Relation relation = new Relation(entity1, predicate, entity2, sentence);
                            results.add(relation);
                            break;
                        }
                    }
                }
            }
        }

        return results;
    }

    public static ArrayList<Relation> person_R_person(List<Term> parse, String sentence) {

        ArrayList<Relation> results = new ArrayList<Relation>();
        for (int i = 0; i < parse.size(); i++) {
            if ((i - 2) >= 0 && parse.get(i - 2).getNatureStr().startsWith("nr") && parse.get(i - 1).getNatureStr().startsWith("n")
                    && parse.get(i).getNatureStr().startsWith("nr")) {
                Entity entity1 = new Entity(parse.get(i - 2).getName(), personLabel);
                String predicate = parse.get(i - 1).getName();
                Entity entity2 = new Entity(parse.get(i).getName(), personLabel);
                Relation relation = new Relation(entity1, predicate, entity2, sentence);
                results.add(relation);
            } else if ((i - 3) >= 0 && parse.get(i - 3).getNatureStr().startsWith("nr") && parse.get(i - 1).getNatureStr().startsWith("n")
                    && parse.get(i - 3).getName().equals("的")
                    && parse.get(i).getNatureStr().startsWith("nr")) {
                Entity entity1 = new Entity(parse.get(i - 2).getName(), personLabel);
                String predicate = parse.get(i - 1).getName();
                Entity entity2 = new Entity(parse.get(i).getName(), personLabel);
                Relation relation = new Relation(entity1, predicate, entity2, sentence);
                results.add(relation);
            }
        }
        return results;
    }

    public static ArrayList<Relation> location_R_location(List<Term> parse, HashSet<String> locationEntity) {

        ArrayList<Relation> results = new ArrayList<Relation>();
        for (int i = 0; i < parse.size(); i++) {
            String word = parse.get(i).getName();
            String nature = parse.get(i).getNatureStr();
            if(nature.equals("ns") && cityMap.containsKey(word) && locationEntity.contains(cityMap.get(word))){
                Entity entity1 = new Entity(word, locationLabel);
                String predicate = "位于";
                Entity entity2 = new Entity(cityMap.get(word), locationLabel);
                Relation relation = new Relation(entity1, predicate, entity2, "");
                results.add(relation);
            }
        }
        return results;
    }

    public static ArrayList<Term> NerSegContent(String input) {

        ArrayList<Term> tempList = new ArrayList<Term>();
        ArrayList<Term> result = new ArrayList<Term>();
        input = formatInput(input);
        List<Term> parse = NlpAnalysis.parse(input);
        Counter counter = new Counter(parse.size() - 1);
        for (; counter.getCount() >= 0; counter.Des()) {
            Term term = parse.get(counter.getCount());
            String tag = term.getNatureStr();
            String word = term.getName();
            boolean flag = false;
            if (tag.startsWith("nr")){
                if(!NoSensePersonCheck(word, parse, counter.getCount())) {
                    int index = counter.getCount();
                    int lastIndex = counter.getCount();
                    boolean isFnr = false;
                    while((index - 2 >= 0) && parse.get(index - 1).getName().equals("・") && parse.get(index - 2).getNatureStr().startsWith("nr")) {
                        index -= 2;
                        counter.Sub(2);
                        isFnr = true;
                    }
                    if(isFnr){
                        String name = "";
                        for(int i = index; i <= lastIndex; i++)
                            name += parse.get(i).getName();
                        term.setName(name);
                    }
                    tempList.add(term);
                }
                else
                    flag = true;
            }
            else if (tag.startsWith("ns")){
                if(isLocation(word)) {
                    term.setName(formPlace(word));
                    tempList.add(term);
                }
                else
                    flag = true;
            }
            else if(tag.equals("nt") || tag.equals("nis") || tag.equals("nit") || tag.equals("nw")){
                String organization = isOrganization(term, parse, counter);
                if (!organization.equals("")) {
                    term.setName(organization);
                    Nature nature = term.natrue();
                    Nature newNature = new Nature("nt", nature.index, nature.natureIndex, nature.allFrequency);
                    term.setNature(newNature);
                    tempList.add(term);
                }
                else
                    flag = true;
            }
            else if(tag.startsWith("nt") || tag.equals("school")){
                tempList.add(term);
            }
            else {
                tempList.add(term);
            }

            if(flag){
                Nature nature = term.natrue();
                Nature newNature = new Nature("n", nature.index, nature.natureIndex, nature.allFrequency);
                term.setNature(newNature);
                tempList.add(term);
            }
        }
        for (int i = tempList.size() - 1; i >= 0; i--)
            result.add(tempList.get(i));
        return result;
    }

    public ANSJ() {

        location = new ArrayList<String>();
        schoolLocation = new HashMap<String, String>();
        cityMap = new HashMap();
        career = new ArrayList<String>();
        nwSuffix = new ArrayList<String>();
        if (stopWords == null) {
            stopWords = Myutil.readByLine("extractorSystem/data/stopwords.txt");
            FilterModifWord.insertStopWords(stopWords);
        }
        ArrayList<String> lines1 = Myutil.readByLine("extractorSystem/data/中国省份.txt");
        ArrayList<String> lines2 = Myutil.readByLine("extractorSystem/data/中国城市.txt");
        ArrayList<String> lines3 = Myutil.readByLine("extractorSystem/data/collegeLocation.txt");
        ArrayList<String> lines4 = Myutil.readByLine("extractorSystem/library/userlibrary.dic");
        ArrayList<String> lines5 = Myutil.readByLine("extractorSystem/data/career.txt");
        ArrayList<String> lines6 = Myutil.readByLine("extractorSystem/data/NewWordSuffix.txt");
        for (String line1 : lines1) {
            String[] TW = line1.split("\t");
            String[] cities = TW[1].split(",");
            location.add(TW[0]);
            location.add(formPlace(TW[0]));
            for (String city : cities) {
                if (city.endsWith("区")) {
                    location.add(city);
                    cityMap.put(city, TW[0]);
                    continue;
                }
                if (city.endsWith("镇") || city.endsWith("街道"))
                    continue;
                location.add(city);
                location.add(formPlace(city));
                cityMap.put(city, formPlace(TW[0]));
                cityMap.put(formPlace(city), formPlace(TW[0]));
            }
        }
        for (String line2 : lines2) {
            String[] TW = line2.split("\t");
            String[] cities = TW[1].split(",");
            for (String city : cities) {
                if (city.endsWith("区")) {
                    location.add(city);
                    cityMap.put(city, TW[0]);
                    continue;
                }
                if (city.endsWith("镇") || city.endsWith("街道"))
                    continue;
                location.add(city);
                location.add(formPlace(city));
                cityMap.put(city, TW[0]);
                cityMap.put(formPlace(city), TW[0]);
            }
        }
        for (String line3 : lines3) {
            schoolLocation.put(line3.split("\t")[0], formPlace(line3.split("\t")[1]));
        }
        for (String line : lines4) {
            String[] TW = line.split("\t");
            UserDefineLibrary.insertWord(TW[0], TW[1], Integer.parseInt(TW[2]));
        }
//        for (String Loc : location) {
//            UserDefineLibrary.insertWord(Loc, "ns", 10000);
//        }
        for (String line : lines5)
            career.add(line);
        for(String line : lines6)
            nwSuffix.add(line);
    }

    //分词(以空格分隔)
    public static String segContent(String input) {

        String result = "";
        input = input.replaceAll("\t", "").replaceAll("\n", "");
        List<Term> parse = NlpAnalysis.parse(input);
        parse = FilterModifWord.modifResult(parse);
        for (Term term : parse) {
            String nature = term.getNatureStr();
            String word = term.getName().trim();
            if (nature.equals("w") || word.equals("") || word.charAt(0) < 0x4e00 || word.charAt(0) > 0x9fa5) {
                continue;
            }
            if (word.length() > 1) {
                result += word + " ";
            }
        }
        if (result != "")
            result = result.substring(0, result.length() - 1);
        return result;
    }

    //分句
    public static ArrayList<String> segToSentence(String content) {

        ArrayList<String> sentences = new ArrayList<String>();
        content = content.replaceAll("\t", "").replaceAll("\n", "");
        List<Term> results = NlpAnalysis.parse(content);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            Term term = results.get(i);
            String word = term.getName().trim();
            if ((word.equals("。") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || (word.equals("！") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || (word.equals("？") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || word.equals("；")) {
                sb.append(word);
                sentences.add(sb.toString());
                sb.delete(0, sb.length());
            } else {
                sb.append(word);
            }
        }

        return sentences;
    }

    //实体抽取
    public static ArrayList<Entity> extractEntity(String input) {

        //System.out.println("start extract entity");
        ArrayList<Entity> outcome = new ArrayList<Entity>();
        HashMap<String,ArrayList<Entity>> wordMap = new HashMap<String, ArrayList<Entity>>();
        List<Term> parse = new ArrayList<Term>();
        input = formatInput(input);
        List<Term> results = NerSegContent(input);
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            Term term = results.get(i);
            String word = term.getName().trim();
            if ((word.equals("。") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || (word.equals("！") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || (word.equals("？") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || word.equals("；")) {
                parse.add(term);
                sentence.append(word);
                ArrayList<Entity> entityArrayList = getEntityFromSentence(parse, sentence.toString());
                for(Entity entity : entityArrayList){
                    String name = entity.getWord();
                    if(!wordMap.containsKey(name))
                        wordMap.put(name,new ArrayList<Entity>());
                    wordMap.get(name).add(entity);
                }
                parse.clear();
                sentence.delete(0, sentence.length());
            } else {
                parse.add(term);
                sentence.append(word);
            }

        }
        for(String word : wordMap.keySet()){
            String nature = wordMap.get(word).get(0).getNature();
            String sentences = "";
            for(Entity entity : wordMap.get(word))
                sentences += entity.getSentence();
            Entity entity = new Entity(word,nature,sentences);
            outcome.add(entity);
        }
        //System.out.println("finish extract entity");
        return outcome;
    }

    //关系抽取
    public static ArrayList<Relation> extractRelation(String input) {

        //System.out.println("start extract relation");
        List<Term> results = NerSegContent(input);
        List<Entity> entityList = extractEntity(input);
        HashSet<String> locationEntity = new HashSet<String>();
        for(Entity entity : entityList){
            locationEntity.add(entity.getWord());
        }
        List<Term> parse = new ArrayList<Term>();
        HashMap<String,ArrayList<Relation>> relationMap = new HashMap<String, ArrayList<Relation>>();
        ArrayList<Relation> outcome = new ArrayList<Relation>();
        StringBuilder sentence = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            Term term = results.get(i);
            String word = term.getName().trim();
            if ((word.equals("。") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || (word.equals("！") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || (word.equals("？") && (i + 1) < results.size() && !results.get(i + 1).getName().equals("”"))
                    || word.equals("；")) {
                parse.add(term);
                sentence.append(word);
                ArrayList<Relation> relationArrayList = person_R_organization(parse, sentence.toString());//人名和机构名关系
                for(Relation relation : relationArrayList){
                    String label = relation.getSubject().getWord() + relation.getPredicate() + relation.getObject().getWord();
                    if(!relationMap.containsKey(label))
                        relationMap.put(label,new ArrayList<Relation>());
                    relationMap.get(label).add(relation);
                }
                relationArrayList = organization_R_location(parse, sentence.toString(), locationEntity);//机构名和地名关系
                for(Relation relation : relationArrayList){
                    String label = relation.getSubject().getWord() + relation.getPredicate() + relation.getObject().getWord();
                    if(!relationMap.containsKey(label))
                        relationMap.put(label,new ArrayList<Relation>());
                    relationMap.get(label).add(relation);
                }
                relationArrayList = person_R_person(parse, sentence.toString());//人名和人名关系
                for(Relation relation : relationArrayList){
                    String label = relation.getSubject().getWord() + relation.getPredicate() + relation.getObject().getWord();
                    if(!relationMap.containsKey(label))
                        relationMap.put(label,new ArrayList<Relation>());
                    relationMap.get(label).add(relation);
                }
                relationArrayList = location_R_location(parse, locationEntity);//地名和地名关系
                for(Relation relation : relationArrayList){
                    String label = relation.getSubject().getWord() + relation.getPredicate() + relation.getObject().getWord();
                    if(!relationMap.containsKey(label))
                        relationMap.put(label,new ArrayList<Relation>());
                    relationMap.get(label).add(relation);
                }
                parse.clear();
                sentence.delete(0, sentence.length());
            } else {
                parse.add(term);
                sentence.append(word);
            }
        }
        for(String label : relationMap.keySet()){
            Relation tempRelation = relationMap.get(label).get(0);
            Entity entity1 = tempRelation.getSubject();
            String predicate = tempRelation.getPredicate();
            Entity entity2 = tempRelation.getObject();
            String sentences = "";
            for(Relation relation : relationMap.get(label)){
                sentences += relation.getSentence();
            }
            Relation relation = new Relation(entity1,predicate,entity2,sentences);
            outcome.add(relation);
        }
        //System.out.println("finish extract relation");
        return outcome;
    }


    public static void main(String[] args) throws IOException {
        

//        ANSJ ansj = new ANSJ();
//        ArrayList<String> lines = Myutil.readByLine("/Users/binbin/Desktop/edukg2015data/page.txt");
//        HashMap<String,String> contentOfpage = new HashMap<String, String>();
//        for(String line : lines){
//            String []TW = line.split("\t");
//            contentOfpage.put(TW[0],TW[4] + "\t" + TW[5]);
//        }
//        FileOutputStream fis1 = new FileOutputStream("/Users/binbin/Desktop/edukg2015data/entity.txt", false);
//        OutputStreamWriter fw1 = new OutputStreamWriter(fis1, "utf-8");
//        FileOutputStream fis2 = new FileOutputStream("/Users/binbin/Desktop/edukg2015data/relation.txt", false);
//        OutputStreamWriter fw2 = new OutputStreamWriter(fis2, "utf-8");
//        for(String key : contentOfpage.keySet()){
//            String content = contentOfpage.get(key);
//            ArrayList<Entity> entityArrayList = ansj.extractEntity(content);
//            for (Entity entity : entityArrayList) {
//                fw1.write(key + "\t" + entity.getEntity() + "\n");
//                System.out.println(key + "\t" + entity.getEntity());
//            }
//            ArrayList<Relation> relationArrayList = ansj.extractRelation(content);
//            for (Relation relation : relationArrayList) {
//                fw2.write(key + "\t" + relation.getRelation() + "\n");
//                System.out.println(key + "\t" + relation.getRelation());
//            }
//        }
//        fw1.flush();
//        fw2.flush();
//        fw1.close();
//        fw2.close();

        ANSJ ansj = new ANSJ();
        String content = "2015年5月18日讯，据了解，去年底，中国与全球化研究中心援引美国大学一项统计数据称，美国“野鸡大学”泛滥成灾，而中国成为其最大的受害国，美国的野鸡大学每年的学位证书95%给了中国人。而在国内，一家网站近日发布了“中国虚假大学警示榜”，曝光了国内210所不具招生资格、没有办学资质、涉嫌非法招生和网络诈骗的虚假大学，北京为虚假大学的高发地。 　　野鸡大学的名字实在不好听，可这恰恰说明了人们对此类大学的不屑，这与《围城》中方鸿渐所去的那所克莱登大学是完全一个意思，都是一种凭借公众盲目崇拜国外大学之风，以各种让人眼花缭乱的头衔赋予这所大学看起来很美的光环，这种营销是赤裸裸的欺骗。但随着信息的越来越透明，这种大学必然会受到法律的制裁和公众的唾弃。 　　野鸡大学一般都有几个相同的主演特征。1.承诺只需几百元即可在7天内取得学位；2.没有课程也没有校园；3.不上课，假称根据学生人生经历直接颁发学位；4.透过垃圾邮件宣传，声称只须缴付金钱便立刻取得学位证书；5.通过报刊杂志等角落做小型广告；6.超低门槛(入学无任何要求等)； 　　大家都知道，所谓的“野鸡大学”，是指虽然是合法机构，但不被所在国社会、用人企业认可的学校，主要指标是花钱买文凭，而买来的文凭不被用人单位认可。通常，野鸡学校都有非常光鲜的名字，有的和正规的大学只有一字之差，有钓鱼网站的感觉，所以，一定要非常的小心。下面我们就一起来看看，都有哪些“野鸡大学”上榜。 　　210所“虚假大学”完整名录 　　中国邮电大学上海工商学院 　　中国科技管理学院长江科技学院 　　中国民航学院西安信息技术学院 　　中国师范学院西安工商管理学院 　　中国信息科技学院西安工业科技技术学院 　　中国电子信息科技学院西安工业科技学院 　　中国电子科技学院西安建设科技学院 　　中国科技工程学院陕西国防工业技术学院 　　中国传媒艺术学院南京科技管理学院 　　中国金融管理学院南京金陵科技管理学院 　　中国国际经济管理学院江苏信息工程学院 　　中国工商行政管理学院江苏都市建设学院 　　中国经济贸易大学江苏理工职业学院 　　中国科贸管理学院石家庄工程管理学院 　　中国经济贸易学院华北建筑工业学院 　　中国北方理工学院华北建筑工程学院 　　中国工业工程学院中原工业大学 　　中国现代财经学院河南科技师范学院 　　中国国际工商管理学院杭州建设管理大学 　　华北科技大学对外经济贸易管理学院 　　华北理工学院广东科技管理大学 　　中联司法学院广东经济管理学院 　　北方国际经济学院福建海峡经贸技术学院 　　北方经济管理学院福建经济贸易大学 　　北方医科大学常德经贸学院 　　北京京华医科大学湖南经济管理大学 　　中北科技学院山西信息工程学院 　　华北应用科技学院山西远东外国语学院 　　华北师范学院沈阳盛京大学 　　中原金融学院对外经贸管理学院 　　华侨国际商务学院武汉建筑职工大学 　　首都科技信息管理学院江西赣南学院 　　首都科技管理学院安徽城建大学 　　首都财贸管理大学四川华商学院 　　北京财贸科技学院兰州对外经济贸易学院 　　首都经济管理学院北京财经政法大学 　　首都经济贸易管理学院北京现代商务学院 　　恒远教育北京工程经济学院 　　首都科技职业技术学院北京实验大学 　　首都科技学院北京商贸职工大学 　　首都医学院北京工学院 　　首都文理大学辽宁轻工职工大学 　　北京经济贸易大学天津联合大学 　　北京经济贸易学院北京科技工程学院 　　北京对外贸易学院北京工程技术大学 　　首都财经贸易大学北京现代工程学院 　　北京燕京华侨大学北京经济信息学院 　　北京经贸联合大学北京京桥大学 　　北京国际金融学院北京电子科技管理学院 　　北京国际经济管理学院北京前进大学 　　淄博理工学院北京工商学院 　　山东邮电大学北京国际医学院 　　山东经济贸易大学北京财经贸易学院 　　山东科技工程学院北京财贸管理学院 　　上海东方科技学院北京财贸管理大学 　　上海工程管理学院北京建筑工业学院 　　上海华夏学院北京法商学院 　　上海建筑工业学院北京经济工程学院 　　上海商贸管理大学北京经济工程大学 　　南京工商大学北京城市建设学院 　　华东农林科技大学北京商贸管理学院 　　安徽城市建设学院北京贸易管理大学 　　江西科技工程大学北京商贸管理大学 　　江西科技管理学院京师科技学院 　　江西经贸管理学院北京科技学院 　　华中工商学院北京中山学院 　　中原工商管理学院北京京城学院 　　湖南屈原大学上海经济贸易大学 　　株洲航空旅游学院上海工业科技大学 　　四川财经管理学院上海工业科技学院 　　四川中山学院上海财经贸易学院 　　广东电子信息技术学院上海金融管理学院 　　厦门师范学院天津文理大学 　　北京经济管理学院西安电子信息学院 　　中国信息科技大学杭州工商管理大学 　　中国信息工程学院杭州理工大学 　　首都财经管理学院杭州理工学院 　　北京财经管理大学南京科技学院 　　北京财经管理学院南京商学院 　　北京财商学院山西理工学院 　　神州大学新民大学 　　北京中加工商学院武汉科技工程学院 　　北京京文国际学院武汉工商管理大学 　　北京商贸大学湖北工商管理学院 　　北方交通工程学院广州理工学院 　　华北工业大学河南华夏医学院 　　北京英迪经贸学院石家庄电子科技学院 　　北京高级财务管理专修学院青岛博洋商务学院 　　青岛远洋大学山东经济技术学院 　　山东文理学院山东沂蒙学院 　　山东建设学院北京财经大学 　　青岛现代经贸学院北京城建大学 　　山东经济信息学院中外经贸管理学院 　　山东城市学院西安理工学院 　　华东财经学院西安工商学院 　　上海同济医科大学西安科技师范大学 　　上海华文外国语学院华北经贸管理学院 　　上海城市建设大学山西经济技术学院 　　河北东亚大学山东东岳学院 　　北京经贸科技学院北京财经学院 　　(来源:北京晚报) 齐鲁网教育频道高校投稿邮箱为：iqilujy@163.com；中小学和学前教育投稿邮箱为：qljygzs@163.com，稿件请勿QQ在线或离线传送，咨询电话和传真：0531-81695085 。";
        //System.out.println(NlpAnalysis.parse(content));
        System.out.println(formatInput(content));
        List<Entity> result = ansj.extractEntity(content);
        for(Entity entity : result)
            System.out.println(entity.getEntity());
    }


}
