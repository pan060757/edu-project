package model;

public class Comment {

    protected String sourceId;
    protected String pageId;
    protected String user_name;
    protected String gender;
    protected String location;
    protected String content;

    protected String time;
    protected String ip;

    protected int support;
    protected int against;
    protected int replyCount;
    protected String userType;

    protected int commentId;
    protected String province;
    protected String label;
    protected double score;




    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getAgainst() {
        return against;
    }

    public void setAgainst(int against) {
        this.against = against;
    }


    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public Comment(){

    }

    public Comment(String pageId, String user_name, String gender, String location, String content){
        this.pageId = pageId;
        this.user_name = user_name;
        this.gender = gender;
        this.location = location;
        this.content = content;
    }

    public Comment(String pageId, String user_name, String gender, String location, String content,String sourceId){
        this.pageId = pageId;
        this.user_name = user_name;
        this.gender = gender;
        this.location = location;
        this.content = content;
        this.sourceId = sourceId;
    }

    public Comment(int commentId, String pageId, String user_name, String gender, String location, String content, String sourceId, String province, String label, double score, String time) {
        this.commentId = commentId;
        this.pageId = pageId;
        this.user_name = user_name;
        this.gender = gender;
        this.location = location;
        this.content = content;
        this.sourceId = sourceId;
        this.province = province;
        this.label = label;
        this.score = score;
        this.time = time;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


}