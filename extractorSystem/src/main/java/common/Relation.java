package common;

/**
 * Created by binbin on 15/12/3.
 */
public class Relation {

    private Entity subject;
    private String predicate;
    private Entity object;
    private String sentence;

    public Relation(Entity subject, String predicate, Entity object, String sentence){
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.sentence = sentence;
    }

    public Relation(){

    }

    public Entity getSubject() {
        return subject;
    }

    public void setSubject(Entity subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public Entity getObject() {
        return object;
    }

    public void setObject(Entity object) {
        this.object = object;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getRelation(){

        return subject.getEntity() + "\t" + predicate + "\t" + object.getEntity() + "\t" + sentence;
    }

}
