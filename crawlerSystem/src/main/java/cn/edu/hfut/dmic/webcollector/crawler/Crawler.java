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
package cn.edu.hfut.dmic.webcollector.crawler;

import cn.edu.hfut.dmic.webcollector.fetcher.Fetcher;
import cn.edu.hfut.dmic.webcollector.crawldb.DBManager;
import cn.edu.hfut.dmic.webcollector.crawldb.Generator;
import cn.edu.hfut.dmic.webcollector.fetcher.Visitor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.net.Requester;
import cn.edu.hfut.dmic.webcollector.util.Config;

import edu.ecnu.storage.InfoRecoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author hu
 */
public class Crawler {

    public static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

    protected int status;
    public final static int RUNNING = 1;
    public final static int STOPED = 2;
    protected boolean resumable = false;
    protected int threads = 50;

    protected int topN = -1;
    protected static int retry = 3;
    protected long retryInterval = 0;
    protected long visitInterval = 0;

    private static String charset = "UTF-8";
    private String mUrl;
    private HttpClientBuilder mClientBuilder;

    protected CrawlDatums seeds = new CrawlDatums();
    protected CrawlDatums forcedSeeds = new CrawlDatums();
    protected Fetcher fetcher;
    protected int maxRetry = -1;

    protected Requester requester;
    protected Visitor visitor;
    protected DBManager dbManager;
    protected Generator generator;

    public Crawler() {
        mUrl = "ERROR  :Wrong URL.";
        mClientBuilder = HttpClientBuilder.create();
    }

    public Crawler(String charset) {
        mUrl = "ERROR  :Wrong URL.";
        Crawler.setCharset(charset);
        mClientBuilder = HttpClientBuilder.create();
    }

    public static void setCharset(String charset) {
        Crawler.charset = charset;
    }

    public static boolean checkUrl(String url) {
        String regEx = "^(http|www|ftp|)(://)(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
    protected void inject() throws Exception {
        dbManager.inject(seeds);
    }

    public void injectForcedSeeds() throws Exception {
        dbManager.inject(forcedSeeds);
    }

    public void start(int depth) throws Exception {

        boolean needInject = true;

        if (resumable && dbManager.isDBExists()) {
            needInject = false;
        }

        if (!resumable) {
            if (dbManager.isDBExists()) {
                dbManager.clear();
            }

            if (seeds.isEmpty() && forcedSeeds.isEmpty()) {
                LOG.info("error:Please add at least one seed");
                return;
            }
        }
        dbManager.open();

        if (needInject) {
            inject();
        }

        if (!forcedSeeds.isEmpty()) {
            injectForcedSeeds();
        }

        status = RUNNING;
        for (int i = 0; i < depth; i++) {
            LOG.info("start depth " + (i + 1));
            long startTime = System.currentTimeMillis();

            if (maxRetry >= 0) {
                generator.setMaxRetry(maxRetry);
            } else {
                generator.setMaxRetry(Config.MAX_RETRY);
            }
            generator.setTopN(topN);
            fetcher = new Fetcher();
            fetcher.setRetryInterval(retryInterval);
            fetcher.setVisitInterval(visitInterval);
            fetcher.setDBManager(dbManager);
            fetcher.setRequester(requester);
            fetcher.setVisitor(visitor);
            fetcher.setRetry(retry);
            fetcher.setThreads(threads);
            fetcher.fetchAll(generator);
            long endTime = System.currentTimeMillis();
            long costTime = (endTime - startTime) / 1000;
            int totalGenerate = generator.getTotalGenerate();

            LOG.info("depth " + (i + 1) + " finish: \n\tTOTAL urls:\t" + totalGenerate + "\n\tTOTAL time:\t" + costTime + " seconds");
            if (totalGenerate == 0) {
                break;
            }

        }
        dbManager.close();

    }

    public void stop() {
        status = STOPED;
        fetcher.stop();
        InfoRecoder.flush();
    }

    public void addSeed(CrawlDatum datum, boolean force) {
        if (force) {
            forcedSeeds.add(datum);
        } else {
            seeds.add(datum);
        }
    }

    public void addSeed(CrawlDatum datum) {
        addSeed(datum, false);
    }

    public void addSeed(CrawlDatums datums, boolean force) {
        for (CrawlDatum datum : datums) {
            addSeed(datum, force);
        }
    }

    public void addSeed(CrawlDatums datums) {
        addSeed(datums, false);
    }

    public void addSeed(Links links, boolean force) {
        for (String url : links) {
            addSeed(url, force);
        }
    }

    public void addSeed(Links links) {
        addSeed(links, false);
    }

    public void addSeed(String url, boolean force) {
        CrawlDatum datum = new CrawlDatum(url);
        addSeed(datum, force);
    }

    public void addSeed(String url) {
        addSeed(url, false);
    }

    public boolean isResumable() {
        return resumable;
    }

    public void setResumable(boolean resumable) {
        this.resumable = resumable;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
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

    public DBManager getDbManager() {
        return dbManager;
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setUrl(String url) {
        if (checkUrl(url)) {
            mUrl = url;
        } else {
            mUrl = "ERROR  :Wrong URL.";
        }
    }

    public void setUrlnoCheck(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getContent(String encoding) {
        if (mUrl.length() < 8 || mUrl.substring(0, 5).toUpperCase().equals("ERROR"))
            return mUrl;
        String content = "ERROR  :Crawl failed. All " + Crawler.retry + " tries failed.";
        String error = "";
        int count = 0;
        while (true) {
            try {
                CloseableHttpClient client = mClientBuilder.build();
                HttpGet get = new HttpGet(mUrl);
                //模拟浏览器的访问请求
                get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
                get.setHeader("Cache-Control", "max-age=0");
                get.setHeader("Connection", "keep-alive");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).setCircularRedirectsAllowed(true).build();
                get.setConfig(requestConfig);
                HttpResponse response = client.execute(get);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String lines = "";
                    HttpEntity entity = response.getEntity();
                    InputStream instream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(instream, encoding));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        lines += "\r\n";
                        lines += line;
                    }
                    client.close();

                    if (!lines.equals("") && !lines.contains("抱歉，您所访问的页面不存在")) {
                        String regEx = "charset=(.*?)\"";
                        Pattern pattern = Pattern.compile(regEx);
                        Matcher matcher = pattern.matcher(lines);
                        if (matcher.find()) {
                            if (!matcher.group(1).equals(Crawler.charset)) {
                                Crawler.setCharset(matcher.group(1));

                                continue;
                            }
                        }
                        content = lines;
                        return content;
                    } else {
                        content = "ERROR: Empty page.";
                        return content;
                    }
                } else if (response.getStatusLine().getStatusCode() == 404) {
                    count++;
                    if (count >= 3) {
                        content += " " + error + "\r\n";
                        break;
                    } else {
                        continue;
                    }
                }
            } catch (Exception e) {
                if (e.toString().substring(0, e.toString().indexOf(":")).equals("java.net.UnknownHostException") || e.toString().substring(0, e.toString().indexOf(":")).equals("java.net.NoRouteToHostException")) {
                    System.out.println("Error");
                    continue;
                }
            }
        }
        return content;
    }

    public String getContent() {
        if (mUrl.length() < 8 || mUrl.substring(0, 5).toUpperCase().equals("ERROR"))
            return mUrl;
        String content = "ERROR  :Crawl failed. All " + Crawler.retry + " tries failed.";
        String error = "";
        int count = 0;
        while (true) {
            try {
                CloseableHttpClient client = mClientBuilder.build();
                HttpGet get = new HttpGet(mUrl);
                get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
                get.setHeader("Cache-Control", "max-age=0");
                get.setHeader("Connection", "keep-alive");
                get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
                RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(300000).setConnectTimeout(300000).setCircularRedirectsAllowed(true).build();
                get.setConfig(requestConfig);
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String lines = "";
                    HttpEntity entity = response.getEntity();
                    InputStream instream = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "GBK"));
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(instream,"UTF8"));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        lines += "\r\n";
                        lines += line;
                    }
                    client.close();

                    if (!lines.equals("") && !lines.contains("抱歉，您所访问的页面不存在")) {
                        String regEx = "charset=(.*?)\"";
                        Pattern pattern = Pattern.compile(regEx);
                        Matcher matcher = pattern.matcher(lines);
                        if (matcher.find()) {
                            if (!matcher.group(1).equals(Crawler.charset)) {
                                Crawler.setCharset(matcher.group(1));

                                continue;
                            }
                        }
                        content = lines;
                        return content;
                    } else {
                        content = "ERROR: Empty page.";
                        return content;
                    }
                } else if (response.getStatusLine().getStatusCode() == 404) {
                    count++;
                    if (count >= 3) {
                        content += " " + error + "\r\n";
                        break;
                    } else {
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error");
                continue;

            }
        }
        return content;
    }
}
