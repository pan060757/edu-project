package edu.ecnu.crawler.JybEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;

/**
 * Created by wlcheng on 12/7/15.
 */
public class JybEduCrawler extends BaseEduCrawler {

    public JybEduCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }


    public static void main(String[] args) throws Exception {
        BaseEduCrawler eduCrawler = new JybEduCrawler("jybedu-crawler",true);
       eduCrawler.addSeed("http://theory.jyb.cn/rcpy/");
        /*正则规则用于控制爬虫自动解析出的链接，用户手动添加的链接，例如添加的种子、或
          在visit方法中添加到next中的链接并不会参与正则过滤*/
        /*自动爬取类似"http://news.hfut.edu.cn/show-xxxxxxhtml"的链接*/
        eduCrawler.addRegex("http://theory.jyb.cn/rcpy/.*");
        /*不要爬取jpg|png|gif*/
        eduCrawler.addRegex("-.*\\.(jpg|png|gif).*");
        /*不要爬取包含"#"的链接*/
        eduCrawler.addRegex("-.*#.*");
        eduCrawler.start(2);
    }
}
