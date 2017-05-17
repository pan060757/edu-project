package edu.ecnu.util;

/**
 * Created by wlcheng on 12/18/15.
 */
public class Config {

    public static final String INDEX_PATH="./luceneIndex/";
    public static final String STOP_WORDS_PATH="stopwords.txt";
    public static final String spliter = "###";


    public static final String crawlerRoot = "crawlerSystem";




    /************************** config for mongodb *****************************/
    public static final String NEWS_PAGES_COLLECTION_NAME = "newspages";
    public static final String COMMENTS_COLLECTION_NAME = "comments";


    public static final String[] indexsKeys = new String[]{"entity","relation_extraction","keysentence"
            ,"classification","deduplication","event","pageId","publisher","sourceId","time"};


    public static final String[] attachedKeys = new String[]{"entity","relation_extraction","keysentence"
            ,"classification","deduplication","event"};
}
