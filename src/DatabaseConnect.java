import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DatabaseConnect {
    Connection con = null;// 创建一个数据库连接
    PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
    ResultSet result = null;// 创建一个结果集对象

    Boolean connect(String user, String password, String address, String databasename);

    boolean isRootAccount(int account_id);


    ArrayList<String> getcollist(int account_id, int table_id);

    int getTableid(String tablename) throws SQLException;

    ResultSet getsetedcollist(String setname, int account_id) throws SQLException;


    void setquerycondition(int account_id, int table_id, String setName, String colname, String select, String con1, String con2);

    void deletequerycondition(int account_id, String setname);

    String getcolumnname(int table_id, String colname) throws SQLException;

    String getEnglishTablenameById(int table_id) throws SQLException;


    ArrayList<String> getSetname(int account_id);


    String gettablename(String setname, int account_id) throws SQLException;


    //列名 t 对应列的查询条件query 表名 tablename
//TODO 这里有很多种方法
//
    ResultSet getresultTable(ArrayList<String> column, ArrayList<String> query, int table_id) throws Exception;


    int logByAccount(String userName, String password) throws SQLException;

    ArrayList<String> getTableList(int account_id);

    String[] getquerycondition(int account_id, int table_id, String setname, String columnname);

    ArrayList<String> tranColumnlist(ArrayList<String> column, int table_id);


    String getcolumn(String column, int table_id);

    ArrayList<ArrayList<String>> getGroup(int table_id, String hzx, String hzxsql, String tjx, String tjxsql) throws SQLException;


    String getUnadornColumnByadorn(int table_id, String col) throws SQLException;

    String getUnadornTablenameById(int table_id) throws SQLException;


    ArrayList<Integer> getRoleListByAccount(int account_id);

    boolean hasSetname(int account_id, String name);



}