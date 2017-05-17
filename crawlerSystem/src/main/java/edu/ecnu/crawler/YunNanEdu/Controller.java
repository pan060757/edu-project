package edu.ecnu.crawler.YunNanEdu;

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
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");
        sc = new SocketCommunication(ip, 4700,43);
    }

    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-ynedu.xml");
        Controller controller = new Controller(conf);
        String suffix = conf.get("crawler.suffix");
        String publisher = conf.get(suffix+".publisher");
        String sourceId = conf.get(suffix+".sourceId");
        sc.sendMessage(sourceId + "\t" + publisher + "\t" + "start");
        controller.setEduCrawlerClassName("edu.ecnu.crawler.YunNanEdu.YnEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.YunNanEdu.YnNextPageCrawler");
        controller.run();
        sc.sendMessage(sourceId + "\t" + publisher + "\t" + "stop");
    }
}
