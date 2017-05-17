package lucene;

import org.apache.lucene.document.Document;

import java.util.Map;

/**
 * Created by wlcheng on 12/18/15.
 */
public class QueryResult {

    private int recordCount;
    private Map<Integer,Document> recordList;

    public QueryResult(int recordCount, Map<Integer,Document> recordList) {
        super();
        this.recordCount = recordCount;
        this.recordList = recordList;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public Map<Integer,Document> getRecordList() {
        return recordList;
    }

    public void setRecordList(Map<Integer,Document> recordList) {
        this.recordList = recordList;
    }

}
