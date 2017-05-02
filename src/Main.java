/**
 * Created by 14414 on 2017/5/1.
 */
public class Main {
    public static void main(String[] args) {
        String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=misaki";
        String userName = "sa";
        String userPwd = "19961022lS";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("连接数据库成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("连接失败");
        }
    }
}
