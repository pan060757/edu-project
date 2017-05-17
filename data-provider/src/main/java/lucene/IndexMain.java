package lucene;

/**
 * Created by wlcheng on 1/4/16.
 */
public class IndexMain {

    public static void main(String[] args) {
        Index index = Index.getInstance();
        String[] indexesPath = new String[]{"./crawlerSystem/Indexes/GxEdu/luceneIndex",
                "./crawlerSystem/Indexes/GzEdu/luceneIndex"
        ,"./crawlerSystem/Indexes/HainanEdu/luceneIndex",
                "./crawlerSystem/Indexes/JxEdu/luceneIndex",
                "./crawlerSystem/Indexes/Wangyi/luceneIndex",
                "./crawlerSystem/Indexes/XinlangEdu/luceneIndex"};
        index.mergeIndex(indexesPath,"./crawlerSystem/MergedIndexes");

    }
}
