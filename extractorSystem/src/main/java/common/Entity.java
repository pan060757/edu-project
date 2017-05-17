package common;

/**
 * Created by binbin on 15/12/3.
 */
public class Entity {

    private String word;
    private String nature;
    private String sentence;
    private int infoId;
    private String infobox;

    public Entity(){

    }

    public Entity(String word, String nature, String sentence, String infobox){
        this.word = word;
        this.nature = nature;
        this.sentence = sentence;
        this.infobox = infobox;
    }

    public Entity(String word, String nature, String sentence){
        this.word = word;
        this.nature = nature;
        this.sentence = sentence;
    }

    public Entity(String word, String nature){
        this.word = word;
        this.nature = nature;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getInfoId() {
        return infoId;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public String getInfobox() {
        return infobox;
    }

    public void setInfobox(String infobox) {
        this.infobox = infobox;
    }

    public String getEntity(){
        if(sentence != null)
            return word + "\t" + nature + "\t" + sentence;
        else
            return word + "\t" + nature;
    }

}
