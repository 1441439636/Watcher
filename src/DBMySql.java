import tool.L;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

// customers
//drink_info
//fish_info
//my_contacts
//OrderItems
//Orders
//Products
//toys
//Vendors
//
public class DBMySql implements Database {
    Connection con = null;// 创建一个数据库连接

    //true
    public Boolean connect() {
        String[] dba = RegistUtil.read();
        L.pr(dba);
        try {
            String url = "jdbc:mysql://" + dba[3] + ":3306/" + dba[4] + "?useUnicode=true&characterEncoding=UTF-8";
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, dba[1], dba[2]);
            System.out.println(" --------------------      connect    DBMySql    succeed  -------------------------");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "数据库错误", "+_+", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return true;
    }

    //true
    public boolean isRootAccount(int account_id) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select role_name from role where role_id in (select role_id from roleaccount where account_id=?)");
            pre.setInt(1, account_id);
            ResultSet result = pre.executeQuery();
            if (result.next() && result.getString(1).equals("root")) return true;
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //true
    public ArrayList<String> getcollist(int account_id, int table_id) {
        PreparedStatement pre;
        ResultSet result = null;
        String sql = "select c.adorn_name from columnname c where c.table_id=? and c.column_name in (select r.column_name from rolepermission r where r.role_id in (select role_id from roleaccount where account_id=?) and r.table_id=?) order by c.no";
        ArrayList<String> list = new ArrayList<String>();
        try {
            pre = con.prepareStatement(sql);
            pre.setInt(1, table_id);
            pre.setInt(2, account_id);
            pre.setInt(3, table_id);
            result = pre.executeQuery();
            while (result.next()) {
                list.add(result.getString(1));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    //true
    public int getTableid(String tablename) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select table_id from tablename where adorn_name=?");
            pre.setString(1, tablename);
            ResultSet result = pre.executeQuery();
            if (result.next())
                return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //需要处理
    public ResultSet getsetedcollist(String setname, int account_id) {
        PreparedStatement pre;
        String getsetedcollistSql = "select focus,colname,con1,con2 from querycondition where querycondition.setname=? and account_id=? ";
        try {
            pre = con.prepareStatement(getsetedcollistSql);
            pre.setString(1, setname);
            pre.setInt(2, account_id);
            return pre.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //true
    public void setquerycondition(int account_id, int table_id, String setName, String colname, String select, String con1, String con2) {
        PreparedStatement pre;
        try {
            //insert into querycondition(account_id,table_id,setname,column_name,flag,con1,con2)"
            String column_name = getcolumnname(table_id, colname);
            pre = con.prepareStatement("insert into querycondition(account_id,table_id,setname,column_name,flag,con1,con2) values(?,?,?,?,?,?,?)");
            pre.setInt(1, account_id);
            pre.setInt(2, table_id);
            pre.setString(3, setName);
            pre.setString(4, column_name);
            pre.setString(5, select);
            pre.setString(6, con1);
            pre.setString(7, con2);
            pre.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletequerycondition(int account_id, String setname) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("delete from querycondition where account_id=? and setname=?");
            pre.setInt(1, account_id);
            pre.setString(2, setname);
            pre.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getcolumnname(int table_id, String colname) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select column_name from columnname where table_id=? and adorn_name=?");
            pre.setInt(1, table_id);
            pre.setString(2, colname);
            ResultSet result = pre.executeQuery();
            if (result.next())
                return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getEnglishTablenameById(int table_id) {
        String trSql = "select table_name from tablename where table_id='" + table_id + "'";
        try {
            ResultSet result = con.prepareStatement(trSql).executeQuery();
            if (result.next())
                return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getSetname(int account_id) {
        PreparedStatement pre;
        ArrayList<String> list = new ArrayList<String>();
        try {
            pre = con.prepareStatement("select distinct setname from querycondition where account_id=?");
            pre.setInt(1, account_id);
            ResultSet result = pre.executeQuery();
            while (result.next()) {
                list.add(result.getString(1));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }

    }

    public String gettablename(String setname, int account_id) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select adorn_name from tablename where table_id in (select distinct table_id from querycondition where setname=? and account_id=?)");
            pre.setString(1, setname);
            pre.setInt(2, account_id);
            ResultSet result = pre.executeQuery();
            if (result.next())
                return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //列名 t 对应列的查询条件query 表名 tablename
    //TODO 这里有很多种方法
    public ResultSet getresultTable(ArrayList<String> column, ArrayList<String> query, int table_id) throws Exception {
        String table = getEnglishTablenameById(table_id);
        StringBuilder sql = new StringBuilder("select ");
        int size = query.size();
        ArrayList<String> unadorncolumn = new ArrayList<>();
        for (int i = 0; i < query.size(); i++) {
            unadorncolumn.add(getcolumn(column.get(i), table_id));
        }
        for (int i = 0; i < size - 1; i++) {
            sql.append(unadorncolumn.get(i) + ",");
        }
        sql.append(unadorncolumn.get(size - 1) + " from " + table);
        boolean isStart = true;
        for (int i = 0; i < size; i++) {
            if (!query.get(i).equals("")) {
                if (isStart) {
                    sql.append(" where " + unadorncolumn.get(i) + " " + query.get(i));
                    isStart = false;
                } else {
                    sql.append(" and " + unadorncolumn.get(i) + " " + query.get(i));
                }
            }
        }
        return con.prepareStatement(sql.toString()).executeQuery();
    }

    //true
    public String getcolumn(String column, int table_id) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select column_name from  columnname where table_id=? and adorn_name=?");
            pre.setInt(1, table_id);
            pre.setString(2, column);
            ResultSet result = pre.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "wrong";
    }

    public static void main(String[] args) {
        DBMySql db = new DBMySql();
        db.connect();
        int acount_id = db.logByAccount("xz", "1");
        System.out.println(acount_id);
    }

    //true
    public int logByAccount(String userName, String password) {
        System.out.println("userName=" + userName + " password=" + password);
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("select account_id from account where name=? and password=?");
            ps.setString(1, userName);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //true
    public ArrayList<String> getTableList(int account_id) {
        PreparedStatement pre;
        ArrayList<String> list = new ArrayList<String>();
        try {
            if (isRootAccount(account_id)) {
                pre = con.prepareStatement("select adorn_name from tablename");
                ResultSet result = pre.executeQuery();
                while (result.next()) {
                    list.add(result.getString(1));
                }
                result.close();
            } else {
                pre = con.prepareStatement("select distinct adorn_name from tablename where table_id in (select  distinct table_id from rolepermission where role_id in (select role_id from roleaccount where account_id=?))");
                pre.setInt(1, account_id);
                ResultSet result = pre.executeQuery();
                while (result.next()) {
                    list.add(result.getString(1));
                }
                result.close();
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
    }

    //    true
    public String[] getquerycondition(int account_id, int table_id, String setname, String columnname) {
        PreparedStatement pre;
        String[] set = {"N", "", ""};
        try {
            String column_name = getcolumnname(table_id, columnname);
            pre = con.prepareStatement("select flag,con1,con2 from  querycondition where account_id=? and table_id=? and column_name=? and setname=?");
            pre.setInt(1, account_id);
            pre.setInt(2, table_id);
            pre.setString(3, column_name);
            pre.setString(4, setname);
            ResultSet result = pre.executeQuery();
            if (result.next()) {
                for (int i = 0; i < set.length; i++) {
                    set[i] = result.getString(i + 1);
                }
            }
            return set;
        } catch (SQLException e) {
            e.printStackTrace();
            return set;
        }
    }

    //true
    public ArrayList<String> tranColumnlist(ArrayList<String> column, int table_id) {
        for (int i = 0; i < column.size(); i++) {
            column.set(i, getcolumn(column.get(i), table_id));
        }
        return column;
    }

    //true
    public ArrayList<ArrayList<String>> getGroup(int table_id, String hzx, String hzxsql, String tjx, String tjxsql) {

        String tablename = getUnadornTablenameById(table_id);
        String uhzx = getUnadornColumnByadorn(table_id, hzx);
        String utjx = getUnadornColumnByadorn(table_id, tjx);
        String wheresql;
        if (hzxsql.equals("") && tjxsql.equals("")) {
            wheresql = "";
        } else if (!hzxsql.equals("") && tjxsql.equals("")) {
            wheresql = "where" + hzx + " " + hzxsql;
        } else if (hzxsql.equals("") && !tjxsql.equals("")) {
            wheresql = "where" + tjx + " " + tjxsql;
        } else {
            wheresql = "where" + hzx + " " + hzxsql + " and " + tjx + " " + tjxsql;
        }
        String sql = "select " + utjx + ", sum(" + uhzx + ") as " + hzx + "统计" + " from " + tablename + wheresql + " group by " + utjx;
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try {
            ResultSet result = con.prepareStatement(sql).executeQuery();
            while (result.next()) {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(result.getString(1));
                temp.add(result.getString(2));
                data.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    //true
    public String getUnadornColumnByadorn(int table_id, String col) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select column_name from  columnname where table_id=? and adorn_name=?");
            pre.setInt(1, table_id);
            pre.setString(2, col);
            ResultSet result = pre.executeQuery();
            if (result.next()) return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //true
    public String getUnadornTablenameById(int table_id) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select table_name from tablename where table_id=?");
            pre.setInt(1, table_id);
            ResultSet result = pre.executeQuery();
            if (result.next()) return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //true
    public ArrayList<Integer> getRoleListByAccount(int account_id) {
        ArrayList<Integer> list = new ArrayList<>();
        try {
            PreparedStatement pre = null;
            pre.setInt(1, account_id);
            pre = con.prepareStatement("select role_id from roleaccount where account_id=?");
            ResultSet result = pre.executeQuery();
            while (result.next()) {
                list.add(result.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }

    //true
    public boolean hasSetname(int account_id, String name) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("select count(*) from querycondition  where account_id=? and setname=?");
            pre.setInt(1, account_id);
            pre.setString(2, name);
            ResultSet result = pre.executeQuery();
            if (result.next() && result.getInt(1) != 0) {
                result.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    //true
    public boolean getTypeColumn(String column, int table_id) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("  SELECT a.COLUMN_TYPE  FROM information_schema.COLUMNS a,tablename t WHERE a.table_name=t.table_name AND t.table_id=? AND a.COLUMN_NAME=?");
            pre.setInt(1, table_id);
            pre.setString(2, column);
            ResultSet result = pre.executeQuery();
            if (result.next()) {
                String s = result.getString(1);
                System.out.println("column=" + column + "   type=>" + s);
                if (s.contains("int") || s.contains("double") || s.contains("decimal")) {
                    result.close();
                    return true;
                }
                result.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    //true
    public String getColumnName(String s) {
        PreparedStatement pre;
        try {
            pre = con.prepareStatement("  select COLUMN_NAME from columnname   where adorn_name=?");
            pre.setString(1, s);
            ResultSet result = pre.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}