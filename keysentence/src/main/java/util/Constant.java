package util;

/**
 * Created by sunkai on 2015/5/18.
 */
public class Constant {
    public final static String DataBasePath = "D:\\EduProject\\2015.01.22";

    public static final int SetingKeyWords = 1000; //选取多少个关键词加入到BOW中
    public static final int CandidatePageNum = 1; //选取多少个维基页面作为候选
    public static final double initVector = 0.0001; //初始vector的值

    /**
     * 操作数据库
     */
    public static final String mysqluser = "root";
    public static final String mysqlpwd = "1234";
    public static final String mysqlclassName = "com.mysql.jdbc.Driver";

    public static final String KeySentencesmysqlurl = "jdbc:mysql://58.198.176.213:3306/kg2015?characterEncoding=UTF-8";

}
