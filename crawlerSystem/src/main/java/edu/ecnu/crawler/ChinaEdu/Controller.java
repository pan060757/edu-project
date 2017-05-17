package edu.ecnu.crawler.ChinaEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;
import edu.ecnu.socket.SocketCommunication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wlcheng on 12/7/15.
 */
public class Controller extends ControllerTemplate {
    private static SocketCommunication sc;
    private static ResourceBundle rb;
    private static String ip;
    public Controller(Configuration conf) {
        super(conf);
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");
        sc = new SocketCommunication(ip,4700,2);
    }

    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-chinaedu.xml");
        Controller controller = new Controller(conf);
        String suffix = conf.get("crawler.suffix");
        String publisher=conf.get("chinaedu.publisher");
        String sourceId=conf.get("chinaedu.sourceId");
        sc.sendMessage(sourceId +"\t" + publisher + "\t" + "start");
        controller.setEduCrawlerClassName("edu.ecnu.crawler.ChinaEdu.ChinaEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.ChinaEdu.ChinaEduNextPageCrawler");
        controller.run();
        sc.sendMessage(sourceId + "\t" +publisher+ "\t" + "stop");
    }
}
