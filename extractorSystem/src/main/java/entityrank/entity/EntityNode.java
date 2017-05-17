package entityrank.entity;

/**
 * Created by binbin on 15/12/17.
 */
public class EntityNode extends Node {

    private String entityName;
    private int tfInDoc;

    public EntityNode(String entityName, int tfInDoc) {
        super();
        this.entityName = entityName;
        this.tfInDoc = tfInDoc;
    }

    public int getTfInDoc() {
        return tfInDoc;
    }

    public void setTfInDoc(int tfInDoc) {
        this.tfInDoc = tfInDoc;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

}
