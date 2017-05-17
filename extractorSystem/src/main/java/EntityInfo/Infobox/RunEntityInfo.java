package EntityInfo.Infobox;

import EntityInfo.db.EntityDAO;
import common.Entity;

import java.util.HashMap;

/**
 * Created by yan on 15/12/23.
 */
public class RunEntityInfo {

    //真正跑的时候只有这个redirectDuplicated要用
    public static final String redirectDuplicated = "extractorSystem/data/redirect_duplicated.txt";

    public static void getInfoByEntity(Entity entityPassedBy) throws Exception{
        //Entity entity = entityPassedBy;
        String word = entityPassedBy.getWord();
        String sentence = entityPassedBy.getSentence();
        //首先进行Infobox查询
        HashMap<Integer, String> result = EntityDAO.queryInfo(word);
        //如果infobox为空，则进行重定向再查询
        if(result==null||result.size()==0){
            HashMap<Integer, String> redirectResult = EntityMissing.runRedirectForOne(word, redirectDuplicated);
            //重定向以后还是找不到，就返回-1
            if(redirectResult==null||redirectResult.size()==0){
                entityPassedBy.setInfoId(-1);
            }else{
                //否则对关键句进行过滤
                int id = EntityOverlap.getIdBySentences(sentence,redirectResult);
                entityPassedBy.setInfoId(id);
            }
        }
        else{
            //否则对关键句进行过滤
            int id = EntityOverlap.getIdBySentences(sentence,result);
            entityPassedBy.setInfoId(id);
        }
        //System.out.println(entity.getInfoId());
        //return entity;
    }

    public static void main(String[] args) throws Exception{
        //是全部流程走一遍，包括统计结果的
        //prepareData();
        //dealWithData();

        //对某个实体来说，调用下面的方法
//        Entity e = new Entity();
//        e.setWord("苏州市");
//        e.setSentence("江苏省城市");
//        getInfoByEntity(e);

    }
}
