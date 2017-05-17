package EntityInfo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Created by yan on 15/12/23.
 */
public class EntityDAO {
    /** 输入一个entity，返回它对应的<id, <infobox>>
     * @param en
     * @return <id, <infobox>>
     * @throws Exception
     */
    public static HashMap<Integer, String> queryInfo(String en) throws Exception{
        HashMap<Integer, String> infoSet = new HashMap<Integer, String>();
        PreparedStatement pstmt = null;
        Connection conn = DBPool.getInstance().getConnection();
        ResultSet rs = null;
        String sql = "select * from entity_infobox where entity = ?";

        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, en);
            rs = pstmt.executeQuery();
            while(rs.next()){
                String result = rs.getString("infobox");
                int id = rs.getInt("id");
                infoSet.put(id, result.trim());
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            DBPool.releaseConnection(rs, pstmt, conn);
        }
        return infoSet;
    }

    public static void main(String[] args) throws Exception {
    }
}
