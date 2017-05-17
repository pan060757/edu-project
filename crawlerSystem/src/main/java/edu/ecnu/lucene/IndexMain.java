package edu.ecnu.lucene;

import java.io.File;

/**
 * Created by wlcheng on 1/4/16.
 */
public class IndexMain {
    public static void main(String[] args) {
        Index index = Index.getInstance();
        String fromPathRoot = "./crawlerSystem/Indexes/";

        File dir = new File(fromPathRoot);
        String[] fromPath = new String[]{"./crawlerSystem/Indexes/GxEdu/luceneIndex/",
                "./crawlerSystem/Indexes/GzEdu/luceneIndex/"};
        index.merge(fromPath,"./crawlerSystem/Indexes/luceneIndex");
    }
}
