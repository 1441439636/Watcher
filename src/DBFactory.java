/**
 * Created by ZLS on 2017/5/10.
 */
public class DBFactory {
    public Database createDB(String dbName) {
        switch (dbName) {
            case "Oracle":
                System.out.println(" --------------------      DBFactory    Oracle      -------------------------");
                return new DBOracle();
            case "MySql":
                System.out.println(" --------------------      DBFactory    MySql      -------------------------");
                return new DBMySql();
            case "SqlServer":
                System.out.println(" --------------------      DBFactory    SqlServer      -------------------------");
                return new DBSqlServer();
        }
        return null;
    }
}
