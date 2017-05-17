package edu.ecnu.spcrawler.BaseEduCrawler;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import edu.ecnu.storage.InfoRecoder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by song on 2015/12/20.
 */
public  class BaseEduCrawler {
    private static final String cur_year = "2015";
    public static int cnt=0;

    public void crawlUrlList()throws IOException, InterruptedException{}           //爬取urlList



    public String date(int a) {
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-01-0" + a;
            return cur_year + "-01-" + a;
        }
        a = a - 31;
        if (a <= 28) {
            if (a < 10)
                return cur_year + "-02-0" + a;

            return cur_year + "-02-" + a;
        }
        a = a - 28;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-03-0" + a;
            return cur_year + "-03-" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-04-0" + a;
            return cur_year + "-04-" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-05-0" + a;
            return cur_year + "-05-" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-06-0" + a;
            return cur_year + "-06-" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-07-0" + a;
            return cur_year + "-07-" + a;
        }
        a = a - 31;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-08-0" + a;
            return cur_year + "-08-" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-09-0" + a;
            return cur_year + "-09-" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-10-0" + a;
            return cur_year + "-10-" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-11-0" + a;
            return cur_year + "-11-" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-12-0" + a;
            return cur_year + "-12-" + a;
        } else
            return "";
    }
}
