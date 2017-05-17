package edu.ecnu.storage;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class PageDao {
    private static DataAccess dataAccess = DataAccess.getInstance();
    private static Connection connection = dataAccess.getConnection();
    private static PreparedStatement pstmt = null;
    private static ResultSet rs = null;
    public static int insertPage(int sourceId, String url, String title, String pubTime, String pubSource, String content) {
        int pageId = -1;
        try {
            Date date = Date.valueOf(pubTime);
            String sql = "insert into pages_2015(source_Id,url,title,pubTime,pubSource,content) values(?,?,?,?,?,?)";
            pstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, sourceId);
            pstmt.setString(2, url);
            pstmt.setString(3, title);
            pstmt.setDate(4, date);
            pstmt.setString(5, pubSource);
            pstmt.setString(6, content);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
                pageId = rs.getInt(1);
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageId;
    }


    public static int insertComment(int sourceId, String url, String title, String pubTime, String pubSource, String content) {
        int pageId = -1;
        try {
            Date date = Date.valueOf(pubTime);
            String sql = "insert into pages_2015(source_Id,url,title,pubTime,pubSource,content) values(?,?,?,?,?,?)";
            pstmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, sourceId);
            pstmt.setString(2, url);
            pstmt.setString(3, title);
            pstmt.setDate(4, date);
            pstmt.setString(5, pubSource);
            pstmt.setString(6, content);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if(rs.next())
                pageId = rs.getInt(1);
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageId;
    }

    public static void insertList(String url, int sourceId) {
        try {
            String sql = "insert into urlList_2015(url,source_Id) values(?,?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, url);
            pstmt.setInt(2, sourceId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String crawlList(int sourceId) {
        String url = "";
        try {
            String sql = "select url from urlList_2015 where source_id=" + sourceId;
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                url = rs.getString(1);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    public static void deleteList(String url) {
        try {
            String sql = "delete from urlList_2015 where url='" + url + "'";
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertExceptionList(String url, int sourceId,String exception_type) {
        try {
            String sql = "insert into exception_2015(url,source_Id,exception_type) values(?,?,?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,url);
            pstmt.setInt(2,sourceId);
            pstmt.setString(3,exception_type);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String crawlExceptionList(int sourceId) {
        String url = "";
        try {
            String sql = "select url from exception_2015 where source_id=" + sourceId;
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                url = rs.getString(1);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    public static void deleteExceptionList(String url) {
        try {
            String sql = "delete from exception_2015 where url='" + url + "'";
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int searchSource(String sourceName) {
        int source_id = 0;
        try {
            String sql = "select id from dataSource_2015 where sourceName=?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, sourceName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                source_id = rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source_id;
    }

    public void updateLastUrl(int crawlerId,String sectionUrl,String lastUrl) throws Exception{
        String sql="update section_info_2015 set last_url='"+lastUrl+"'"+" where section_url='"+sectionUrl+"'";
        pstmt=connection.prepareStatement(sql);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public HashMap<String,ArrayList<String>> getSectionInfoById(int crawlerId)throws Exception{
        HashMap<String,ArrayList<String>> sectionInfo=new HashMap<String,ArrayList<String>>();
        String sql="select * from section_info_2015 where source_Id="+crawlerId;
        pstmt=connection.prepareStatement(sql);
        rs=pstmt.executeQuery();
        ArrayList<String> sectionUrlList=new ArrayList<String>();
        ArrayList<String> lastUrlList=new ArrayList<String>();
        while(rs.next()){
            sectionUrlList.add(rs.getString(2));
            lastUrlList.add(rs.getString(3));
            sectionInfo.put("sectionUrl",sectionUrlList);
            sectionInfo.put("lastUrl",lastUrlList);
        }
        return sectionInfo;
    }

    public static void recordLastUrl(String url, int sourceId) {
        try {
            String sql = "select dataTime from scheduleStatistics_2015 where dataSource_id=" + sourceId;
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Date d = rs.getDate(1);
                Date dt = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dstr = sdf.format(d);
                String dtstr = sdf.format(dt);
                if (dstr.equals(dtstr)) {
                    String sql1 = "update scheduleStatistics_2015 set dataTime=?,last_Crawl_url=? where dataSource_id=" + sourceId;
                    PreparedStatement pstmt1 = connection.prepareStatement(sql1);
                    pstmt1.setDate(1, dt);
                    pstmt1.setString(2, url);
                    pstmt1.executeUpdate();
                } else {
                    String sql2 = "insert into scheduleStatistics_2015(dataSource_id,dataTime,last_Crawl_url) values(?,?,?)";
                    PreparedStatement pstmt2 = connection.prepareStatement(sql2);
                    pstmt2.setInt(1, sourceId);
                    pstmt2.setDate(2, dt);
                    pstmt2.setString(3, url);
                    pstmt2.executeUpdate();
                    pstmt2.close();
                }
            }
            else {
                String sql3 = "insert into scheduleStatistics_2015(dataSource_id,dataTime,last_Crawl_url) values(?,?,?)";
                PreparedStatement pstmt3 = connection.prepareStatement(sql3);
                Date dt = new Date(System.currentTimeMillis());
                pstmt3.setInt(1, sourceId);
                pstmt3.setDate(2, dt);
                pstmt3.setString(3, url);
                pstmt3.executeUpdate();
                pstmt3.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static int getPageIdByUrl(String url)throws  Exception{
        int pageId=0;
        try {
            String sql = "select page_Id from pages_2015 where url='" + url+"'";
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                pageId = rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();throw e;
        }

        return pageId;
    }

    public static String searchLastUrl(int dataSource_id) {
        String url = "";
        try {
            String sql = "select last_Crawl_url from scheduleStatistics_2015 where dataSource_id="+dataSource_id;
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                url = rs.getString(1);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static void Sum(int sourceId, int num) {
        try {
            String sql = "update dataSource_2015 set cur_Crawl_listSize=? where id=" + sourceId;
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, num);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void finishedUrl(int dataSource_id, int compeleteNum) {
        try {
            Date dt = new Date(System.currentTimeMillis());
            String sql = "select compeleteNum from scheduleStatistics_2015 where dataSource_id="+ dataSource_id+" and dataTime='"+dt+"'";
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                   compeleteNum = compeleteNum + rs.getInt(1);
                   String sql2 = "update scheduleStatistics_2015 set compeleteNum=? where dataSource_id="+dataSource_id+" and dataTime='"+dt+"'";
                   PreparedStatement pstmt2 = connection.prepareStatement(sql2);
                   pstmt2.setInt(1, compeleteNum);
                   pstmt2.executeUpdate();
            }
            else {
                    String sql3 ="insert into scheduleStatistics_2015(dataSource_id,dataTime,compeleteNum) values(?,?,?)";
                    PreparedStatement pstmt3 = connection.prepareStatement(sql3);
                    pstmt3.setInt(1,dataSource_id);
                    pstmt3.setDate(2, dt);
                    pstmt3.setInt(3, compeleteNum);
                    pstmt3.executeUpdate();
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertLinkurl(String linkurl, int sourceId,int panelId) {
        try {
            String sql = "insert into l(linkurl,source_Id,panel_id) values(?,?,?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,linkurl);
            pstmt.setInt(2, sourceId);
            pstmt.setInt(3,panelId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int searchLinkUrl(String linkUrl) {
        int  panelId =0;
        try {
            String sql = "select panel_id from l where linkUrl='" + linkUrl+"'" ;
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                panelId = rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panelId;
    }

    public static void insertSection(int sourceId,String url) {
        try {
            String sql = "insert into section_info_2015(source_Id,section_url) values(?,?)";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, sourceId);
            pstmt.setString(2, url);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
