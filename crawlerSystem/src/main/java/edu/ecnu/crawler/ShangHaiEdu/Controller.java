package edu.ecnu.crawler.ShangHaiEdu;

import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;
import edu.ecnu.socket.SocketCommunication;

import java.util.ResourceBundle;

/**
 * Created by song on 2016/1/24.
 */
public class Controller extends ControllerTemplate {
    private static SocketCommunication sc;
    private static ResourceBundle rb;
    private static String ip;
    public Controller(Configuration conf) {
        super(conf);
    }
    public Controller(Configuration conf, boolean isComments,boolean isSelenium) {
        super(conf, isComments,isSelenium);
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");
        sc = new SocketCommunication(ip,4700,45);
    }
    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);

    }

    public static void main(String[] args) {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-shedu.xml");
        Controller controller = new Controller(conf,false,true);
        String suffix = conf.get("crawler.suffix");
        String publisher=conf.get("shedu.publisher");
        String sourceId=conf.get("shedu.sourceId");
        sc.sendMessage(sourceId +"\t"+publisher+"\t" +"start");
        controller.setEduCrawlerClassName("edu.ecnu.crawler.ShangHaiEdu.ShEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.ShangHaiEdu.ShNextPageCrawler");
        controller.run();
        sc.sendMessage(sourceId+ "\t" + publisher+"\t"+"stop");
        //   sc.sendMessage(sourceId + "\t" + suffix + "\t" + "stop");
    }
}
