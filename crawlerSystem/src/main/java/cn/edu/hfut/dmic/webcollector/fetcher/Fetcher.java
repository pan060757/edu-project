/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.fetcher;

import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.net.Requester;
import cn.edu.hfut.dmic.webcollector.util.Config;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * deep web抓取器
 *
 * @author hu
 */
public class Fetcher {

    public static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);



    public DBManager dbManager;

    public Requester requester;

    public Visitor visitor;

    private AtomicInteger activeThreads;
    private AtomicInteger spinWaiting;
    private AtomicLong lastRequestStart;
    private QueueFeeder feeder;
    private FetchQueue fetchQueue;
    private int retry = 3;
    private long retryInterval = 0;
    private long visitInterval = 0;

    /**
     *
     */
    public static final int FETCH_SUCCESS = 1;

    /**
     *
     */
    public static final int FETCH_FAILED = 2;
    private int threads = 50;
    private boolean isContentStored = false;

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    /**
     *
     */
    public static class FetchItem {

        /**
         *
         */
        public CrawlDatum datum;

        /**
         *
         * @param datum
         */
        public FetchItem(CrawlDatum datum) {
            this.datum = datum;
        }
    }

    /**
     *
     */
    public static class FetchQueue {

        /**
         *
         */
        public AtomicInteger totalSize = new AtomicInteger(0);

        /**
         *
         */
        public final List<FetchItem> queue = Collections.synchronizedList(new LinkedList<FetchItem>());

        /**
         *
         */
        public void clear() {
            queue.clear();
        }

        /**
         *
         * @return
         */
        public int getSize() {
            return queue.size();
        }

        /**
         *
         * @param item
         */
        public synchronized void addFetchItem(FetchItem item) {
            if (item == null) {
                return;
            }
            queue.add(item);
            totalSize.incrementAndGet();
        }

        /**
         *
         * @return
         */
        public synchronized FetchItem getFetchItem() {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.remove(0);
        }

        /**
         *
         */
        public synchronized void dump() {
            for (int i = 0; i < queue.size(); i++) {
                FetchItem it = queue.get(i);
                LOG.info("  " + i + ". " + it.datum.getUrl());
            }

        }

    }

    /**
     *
     */
    public static class QueueFeeder extends Thread {

        /**
         *
         */
        public FetchQueue queue;

        /**
         *
         */
        public Generator generator;

        /**
         *
         */
        public int size;

        /**
         *
         * @param queue
         * @param generator
         * @param size
         */
        public QueueFeeder(FetchQueue queue, Generator generator, int size) {
            this.queue = queue;
            this.generator = generator;
            this.size = size;
        }

        public void stopFeeder() {
            running = false;
            while (this.isAlive()) {
                try {
                    Thread.sleep(1000);
                    LOG.info("stopping feeder......");
                } catch (InterruptedException ex) {
                }
            }
        }
        public boolean running = true;

        @Override
        public void run() {

            boolean hasMore = true;
            running = true;
            while (hasMore && running) {

                int feed = size - queue.getSize();
                if (feed <= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    continue;
                }
                while (feed > 0 && hasMore && running) {

                    CrawlDatum datum = generator.next();
                    hasMore = (datum != null);

                    if (hasMore) {
                        queue.addFetchItem(new FetchItem(datum));
                        feed--;
                    }

                }

            }

        }

    }

    private class FetcherThread extends Thread {

        @Override
        public void run() {
            activeThreads.incrementAndGet();
            FetchItem item = null;
            try {

                while (running) {
                    try {
                        item = fetchQueue.getFetchItem();
                        if (item == null) {
                            if (feeder.isAlive() || fetchQueue.getSize() > 0) {
                                spinWaiting.incrementAndGet();

                                try {
                                    Thread.sleep(500);
                                } catch (Exception ex) {
                                }

                                spinWaiting.decrementAndGet();
                                continue;
                            } else {
                                return;
                            }
                        }

                        lastRequestStart.set(System.currentTimeMillis());

                        CrawlDatum crawlDatum = item.datum;
                        String url = crawlDatum.getUrl();
                        Page page = getPage(crawlDatum);

                        crawlDatum.incrRetry(page.getRetry());
                        crawlDatum.setFetchTime(System.currentTimeMillis());

                        CrawlDatums next = new CrawlDatums();
                        if (visit(crawlDatum, page, next)) {
                            try {
                                /*写入fetch信息*/
                                dbManager.wrtieFetchSegment(crawlDatum);
                                if (page.getResponse() == null) {
                                    continue;
                                }
                                if (page.getResponse().isRedirect()) {
                                    if (page.getResponse().getRealUrl() != null) {
                                        dbManager.writeRedirectSegment(crawlDatum, page.getResponse().getRealUrl().toString());
                                    }
                                }
                                if (!next.isEmpty()) {
                                    dbManager.wrtieParseSegment(next);
                                }

                            } catch (Exception ex) {
                                LOG.info("Exception when updating db", ex);
                            }
                        }
                        if (visitInterval > 0) {
                            try {
                                Thread.sleep(visitInterval);
                            } catch (Exception sleepEx) {
                            }
                        }

                    } catch (Exception ex) {
                        LOG.info("Exception", ex);
                    }
                }

            } catch (Exception ex) {
                LOG.info("Exception", ex);

            } finally {
                activeThreads.decrementAndGet();
            }

        }

    }

    /**
     * 抓取当前所有任务，会阻塞到爬取完成
     *
     * @param generator 给抓取提供任务的Generator(抓取任务生成器)
     * @throws IOException
     */
    public void fetchAll(Generator generator) throws Exception {
        if (visitor == null) {
            LOG.info("Please Specify A Visitor!");
            return;
        }

        try {
            if (dbManager.isLocked()) {
                dbManager.merge();
                dbManager.unlock();
            }
        } catch (Exception ex) {
            LOG.info("Exception when merging history");
        }
        try {
            dbManager.lock();
            generator.open();
            LOG.info("open generator:" + generator.getClass().getName());
            dbManager.initSegmentWriter();
            LOG.info("init segmentWriter:" + dbManager.getClass().getName());
            running = true;

            lastRequestStart = new AtomicLong(System.currentTimeMillis());

            activeThreads = new AtomicInteger(0);
            spinWaiting = new AtomicInteger(0);
            fetchQueue = new FetchQueue();
            feeder = new QueueFeeder(fetchQueue, generator, 1000);
            feeder.start();

            FetcherThread[] fetcherThreads = new FetcherThread[threads];
            for (int i = 0; i < threads; i++) {
                fetcherThreads[i] = new FetcherThread();
                fetcherThreads[i].start();
            }

            do {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                LOG.info("-activeThreads=" + activeThreads.get()
                        + ", spinWaiting=" + spinWaiting.get() + ", fetchQueue.size="
                        + fetchQueue.getSize());

                if (!feeder.isAlive() && fetchQueue.getSize() < 5) {
                    fetchQueue.dump();
                }

                if ((System.currentTimeMillis() - lastRequestStart.get()) > Config.THREAD_KILLER) {
                    LOG.info("Aborting with " + activeThreads + " hung threads.");
                    break;
                }

            } while (activeThreads.get() > 0 && running);
            running = false;
            long waitThreadEndStartTime = System.currentTimeMillis();
            if (activeThreads.get() > 0) {
                LOG.info("wait for activeThreads to end");
            }
            /*等待存活线程结束*/
            while (activeThreads.get() > 0) {
                LOG.info("-activeThreads=" + activeThreads.get());
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
                if (System.currentTimeMillis() - waitThreadEndStartTime > Config.WAIT_THREAD_END_TIME) {
                    LOG.info("kill threads");
                    for (int i = 0; i < fetcherThreads.length; i++) {
                        if (fetcherThreads[i].isAlive()) {
                            try {
                                fetcherThreads[i].stop();
                                LOG.info("kill thread " + i);
                            } catch (Exception ex) {
                                LOG.info("Exception", ex);
                            }
                        }
                    }
                    break;
                }
            }
            LOG.info("clear all activeThread");
            feeder.stopFeeder();
            fetchQueue.clear();
        } finally {
            generator.close();
            LOG.info("close generator:" + generator.getClass().getName());
            dbManager.closeSegmentWriter();
            LOG.info("close segmentwriter:" + dbManager.getClass().getName());
            dbManager.merge();
            dbManager.unlock();
        }
    }

    boolean running;

    /**
     * 停止爬取
     */
    public void stop() {
        running = false;
    }

    /**
     * 返回爬虫的线程数
     *
     * @return 爬虫的线程数
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置爬虫的线程数
     *
     * @param threads 爬虫的线程数
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * 返回是否存储网页/文件的内容
     *
     * @return 是否存储网页/文件的内容
     */
    public boolean isIsContentStored() {
        return isContentStored;
    }

    /**
     * 设置是否存储网页／文件的内容
     *
     * @param isContentStored 是否存储网页/文件的内容
     */
    public void setIsContentStored(boolean isContentStored) {
        this.isContentStored = isContentStored;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public DBManager getDBManager() {
        return dbManager;
    }

    public void setDBManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public boolean visit(CrawlDatum crawlDatum, Page page, CrawlDatums next) {
        String url = crawlDatum.getUrl();
        if (page.getStatus() == Page.STATUS_FETCH_SUCCESS) {

            crawlDatum.setStatus(CrawlDatum.STATUS_DB_FETCHED);
            crawlDatum.setHttpCode(page.getResponse().getCode());

            try {
                visitor.visit(page, next);
            } catch (Exception ex) {
                LOG.info("Exception when visit URL: " + url, ex);
                return false;
            }

            try {
                visitor.afterVisit(page, next);
            } catch (Exception ex) {
                LOG.info("Exception after visit URL: " + url, ex);
                return false;
            }

        } else if (page.getStatus() == Page.STATUS_FETCH_FAILED) {

            crawlDatum.setStatus(CrawlDatum.STATUS_DB_UNFETCHED);

            try {
                visitor.fail(page, next);
            } catch (Exception ex) {
                LOG.info("Exception when execute failed URL: " + url, ex);
                return false;
            }
        }
        return true;
    }

    public Page getPageAfterLogin(CrawlDatum crawlDatum){

        String url = crawlDatum.getUrl();
        Page page;
        HttpResponse response = null;
        int retryIndex = 0;
        Exception lastException = null;
        int retryCount = 0;
        for (; retryIndex <= retry; retryIndex++) {
            try {
                response = requester.getResponse(crawlDatum);//this.getHttpResponse(crawlDatum);
                break;
            } catch (Exception ex) {

                String suffix = "th ";
                switch (retryIndex + 1) {
                    case 1:
                        suffix = "st ";
                        break;
                    case 2:
                        suffix = "nd ";
                        break;
                    case 3:
                        suffix = "rd ";
                        break;
                    default:
                        suffix = "th ";
                }

                lastException = ex;

                if (retryIndex < retry) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("retry ").append(retryIndex + 1).append(suffix).append("URL:")
                            .append(url).append(" after ").append(retryInterval)
                            .append("ms ").append("(").append(ex.toString()).append(")");
                    String logMessage = sb.toString();
                    LOG.info(logMessage);
                    retryCount++;
                    if (retryInterval > 0) {
                        try {
                            Thread.sleep(retryInterval);
                        } catch (Exception sleepEx) {
                        }
                    }
                }

            }
        }

        if (response != null) {
            LOG.info("fetch URL: " + url);
            page = Page.createSuccessPage(crawlDatum, retryCount, response);
        } else {
            LOG.info("failed URL: " + url + " (" + lastException + ")");
            page = Page.createFailedPage(crawlDatum, retryCount, lastException);
        }

        return page;
    }

    public Page getPage(CrawlDatum crawlDatum) {

        String url = crawlDatum.getUrl();
        Page page;
        HttpResponse response = null;
        int retryIndex = 0;
        Exception lastException = null;
        int retryCount = 0;
        for (; retryIndex <= retry; retryIndex++) {
            try {
                response = requester.getResponse(crawlDatum);//this.getHttpResponse(crawlDatum);
                break;
            } catch (Exception ex) {

                String suffix = "th ";
                switch (retryIndex + 1) {
                    case 1:
                        suffix = "st ";
                        break;
                    case 2:
                        suffix = "nd ";
                        break;
                    case 3:
                        suffix = "rd ";
                        break;
                    default:
                        suffix = "th ";
                }

                lastException = ex;

                if (retryIndex < retry) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("retry ").append(retryIndex + 1).append(suffix).append("URL:")
                            .append(url).append(" after ").append(retryInterval)
                            .append("ms ").append("(").append(ex.toString()).append(")");
                    String logMessage = sb.toString();
                    LOG.info(logMessage);
                    retryCount++;
                    if (retryInterval > 0) {
                        try {
                            Thread.sleep(retryInterval);
                        } catch (Exception sleepEx) {
                        }
                    }
                }

            }
        }

        if (response != null) {
            LOG.info("fetch URL: " + url);
            page = Page.createSuccessPage(crawlDatum, retryCount, response);
        } else {
            LOG.info("failed URL: " + url + " (" + lastException + ")");
            page = Page.createFailedPage(crawlDatum, retryCount, lastException);
        }

        String html = page.getHtml();
        if (html!=null&&html.contains("http://10.9.27.18")) {
            page = tryLogin(crawlDatum);
        }
        return page;
    }

    public Page tryLogin(CrawlDatum crawlDatum){

        System.out.println("掉线，重新登录！");
        login();
        Connection connection;
        Connection.Response Jresponse;
        Document document;
        while (true) {
            try {
                connection = Jsoup.connect("http://www.baidu.com");
                connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                Jresponse = connection.ignoreContentType(true).method(Connection.Method.GET).execute();
                document = Jresponse.parse();
                String html = document.body().toString();
                if (html.contains("http://10.9.27.18")) {
                    System.out.println("掉线");
                    login();
                    continue;
                } else {
                    System.out.println("在线");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        Page page = getPageAfterLogin(crawlDatum);
        return page;
    }

    private ArrayList<String> username = new ArrayList<String>() {{

        add("sei2012001");
        add("sei2012002");
        add("sei2012003");
        add("sei2012004");
        add("sei2012005");
        add("sei2012006");
        add("sei2012007");
        add("sei2012008");
        add("sei2012009");
        add("sei2012010");
        add("sei2012011");
        add("sei2012012");
        add("sei2012013");
        add("sei2012014");
        add("sei2012015");
        add("sei2012016");
        add("sei2012017");
        add("sei2012018");
        add("sei2012019");
        add("sei2012020");
        add("sei2012021");
        add("sei2012022");
        add("sei2012023");
        add("sei2012024");
        add("sei2012025");
        add("sei2012026");
        add("sei2012027");
        add("sei2012028");
        add("sei2012029");
        add("sei2012030");
        add("sei2012031");
        add("sei2012032");
        add("sei2012033");
        add("sei2012034");
        add("sei2012035");
        add("sei2012036");
        add("sei2012037");
        add("sei2012038");
        add("sei2012039");
        add("sei2012040");
        add("sei2012041");
        add("sei2012042");
        add("sei2012043");
        add("sei2012044");
        add("sei2012045");
        add("sei2012046");
        add("sei2012047");
        add("sei2012048");
        add("sei2012049");
        add("sei2012050");
        add("sei2012051");
        add("sei2012052");
        add("sei2012053");
        add("sei2012054");
        add("sei2012055");
        add("sei2012056");
        add("sei2012057");
        add("sei2012058");
        add("sei2012059");
        add("sei2012060");
        add("sei2012061");
        add("sei2012062");
        add("sei2012063");
        add("sei2012064");
        add("sei2012065");
        add("sei2012066");
        add("sei2012067");
        add("sei2012068");
        add("sei2012069");
        add("sei2012070");
        add("sei2012071");
        add("sei2012072");
        add("sei2012073");
        add("sei2012074");
        add("sei2012075");
        add("sei2012076");
        add("sei2012077");
        add("sei2012078");
        add("sei2012079");
        add("sei2012080");
        add("sei2012081");
        add("sei2012082");
        add("sei2012083");
        add("sei2012084");
        add("sei2012085");
        add("sei2012086");
        add("sei2012087");
        add("sei2012088");
        add("sei2012089");
        add("sei2012090");
        add("sei2012091");
        add("sei2012092");
        add("sei2012093");
        add("sei2012094");
        add("sei2012095");
        add("sei2012096");
        add("sei2012097");
        add("sei2012098");
        add("sei2012099");
        add("sei2012100");
        add("sei2012101");
        add("sei2012102");
        add("sei2012103");
        add("sei2012104");
        add("sei2012105");
        add("sei2012106");
        add("sei2012107");
        add("sei2012108");
        add("sei2012109");
        add("sei2012110");
        add("sei2012111");
        add("sei2012112");
        add("sei2012113");
        add("sei2012114");
        add("sei2012115");
        add("sei2012116");
        add("sei2012117");
        add("sei2012118");
        add("sei2012119");
        add("sei2012120");
        add("sei2012121");
        add("sei2012122");
        add("sei2012123");
        add("sei2012124");
        add("sei2012125");
        add("sei2012126");
        add("sei2012127");
        add("sei2012128");
        add("sei2012129");
        add("sei2012130");
        add("sei2012131");
        add("sei2012132");
        add("sei2012133");
        add("sei2012134");
        add("sei2012135");
        add("sei2012136");
        add("sei2012137");
        add("sei2012138");
        add("sei2012139");
        add("sei2012140");
        add("sei2012141");
        add("sei2012142");
        add("sei2012143");
        add("sei2012144");
        add("sei2012145");
        add("sei2012146");
        add("sei2012147");
        add("sei2012148");
        add("sei2012149");
        add("sei2012150");

    }};
    private String password = "123456";

    public void login(){

        HashMap<String, String> userDataMap = new HashMap<String, String>();
        userDataMap.put("action", "login");
        int index = (int) (Math.random() * (username.size() + 1));
        System.out.println(username.get(index));
        userDataMap.put("username", username.get(index));
        userDataMap.put("password", password);
        userDataMap.put("ac_id", "4");
        userDataMap.put("ajax", "1");

        Connection connection = Jsoup.connect("http://10.9.27.18/include/auth_action.php");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        try {
            connection.ignoreContentType(true).method(Connection.Method.POST).data(userDataMap).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public long getVisitInterval() {
        return visitInterval;
    }

    public void setVisitInterval(long visitInterval) {
        this.visitInterval = visitInterval;
    }

}
