package edu.ecnu.crawler.Jiaoyuren;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;
import edu.ecnu.socket.SocketCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        sc = new SocketCommunication(ip, 4700,47);
    }

    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);
    }


    public static void main(String[] args) {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-jyredu.xml");
        Controller controller = new Controller(conf);
        String suffix = conf.get("crawler.suffix");
        String publisher = conf.get(suffix+".publisher");
        String sourceId = conf.get(suffix+".sourceId");
        sc.sendMessage(sourceId + "\t" + publisher + "\t" + "start");
        controller.setEduCrawlerClassName("edu.ecnu.crawler.Jiaoyuren.JyrEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.Jiaoyuren.JyrNextPageCrawler");
        controller.run();
        sc.sendMessage(sourceId + "\t" + publisher + "\t" + "stop");
    }
}
