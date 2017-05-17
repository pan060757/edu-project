package timeline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Record implements Comparable<Record> {

    private String id;
    private String title;
    private Date date;
    private String content;
    private double[] topicDist;

    public Record() {

    }

    public Record(String pageid, String title, String date, String content, String topicDist) {
        id = pageid;
        this.title = title;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            this.date = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.content = content;
        this.topicDist = parseDist(topicDist);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            this.date = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double[] getTopicDist() {
        return topicDist;
    }

    public void setTopicDist(double[] topicDist) {
        this.topicDist = topicDist;
    }

    private double[] parseDist(String item) {
        String[] items = item.split(" ");
        double[] topicDist = new double[items.length];
        for (int i = 0; i < topicDist.length; i++)
            topicDist[i] = Double.parseDouble(items[i]);
        return topicDist;
    }

    public int compareTo(Record o) {
        return date.compareTo(o.date);
    }

    public String getDateStr() {
        String output = String.valueOf(date);
        //	return dateTime.getMonthOfYear()+"月"+dateTime.getDayOfMonth()+"日";
        return output;
    }

}
