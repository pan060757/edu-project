package EntityInfo.Infobox;


/**
 * Created by yan on 15/12/1.
 * 这个类是在百度百科数据源中抽取实体和信息，所使用的存储类
 */
public class EntityPair {
    private String title;
    private StringBuffer properties;

    public void setTitle(String title){
        this.title = title;
    }

    public void setProperties(StringBuffer properties){
        this.properties = properties;
    }

    @Override
    public String toString() {
        return title + "\t" + properties;
    }

}
