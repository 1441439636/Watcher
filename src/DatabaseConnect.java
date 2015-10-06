import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import javax.swing.JOptionPane;

public class DatabaseConnect 
{	
	Connection con = null;// 创建一个数据库连接
    PreparedStatement pre = null;// 创建预编译语句对象，一般都是用这个而不用Statement
    ResultSet result = null;// 创建一个结果集对象
   
    public Boolean connect(String address,String databasename,String user,String password){    	
    	try {
    		Class.forName("oracle.jdbc.driver.OracleDriver");// 加载Oracle驱动程序
    	    String url = "jdbc:oracle:" + "thin:@"+address+"/"+databasename;// 127.0.0.1是本机地址， orcl是你的数据库名        

    	    Properties common = new Properties();
    	    common.put("user", user);
    	    common.put("password",password);

			con=DriverManager.getConnection(url, common);
			
			if(!con.prepareStatement("select table_name from user_tables where table_name='QUERYCONDITION'").executeQuery().next())
			{
				JOptionPane.showMessageDialog(null, "数据库错误", "+_+",JOptionPane.ERROR_MESSAGE);
				System.exit(0);
	    	}	
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return true;	
    }


	
	public ArrayList<String> getcollist(int role_id, int  table_id)
	{
		ArrayList<String>list=new ArrayList<String>();
		try {
			pre=con.prepareStatement(getcollistSql);
		
	  		pre.setInt(1,table_id);
	  		pre.setInt(2,role_id);
	  		pre.setInt(3,table_id);
	  		result=pre.executeQuery();
	  		while(result.next())
	  		{
	  			list.add(result.getString(1));
	  		}
	  		return list;
		} catch (SQLException e) {
			e.printStackTrace();
	  		return list;
		}
  		
  	}

	public int getTableid(String tablename) throws SQLException {
		pre=con.prepareStatement(getTableid);
  		pre.setString(1, tablename);
  		result=pre.executeQuery();
  		if(result.next())
  			return result.getInt(1);
  		else return -1;
	}

	public ResultSet getsetedcollist(String setname,int account_id) throws SQLException
	{
		pre=con.prepareStatement(getsetedcollistSql);
  		pre.setString(1, setname);
  		pre.setInt(2, account_id);
  		return pre.executeQuery();
	}
	
	
	public void setquerycondition(int account_id,int table_id,String flag,String setName,String colname,String con1,String con2) throws SQLException
	{
		int column_id=getcolumnid(table_id,colname);
		pre=con.prepareStatement(insertintoqueryconditionSql);
		pre.setInt(1,account_id);
		pre.setInt(2, table_id);
		pre.setString(3, setName);	
  		pre.setInt(4, column_id);
  		pre.setString(5,flag);
  		pre.setString(6, con1);
  		pre.setString(7, con2);		
  		pre.executeQuery();
	}
	public void deletequerycondition(int account_id, String setname) throws SQLException {
		pre=con.prepareStatement(deletequerycondition);
		pre.setInt(1,account_id);
		pre.setString(2, setname);
		pre.executeQuery();
		
	}

	private int getcolumnid(int table_id, String colname) throws SQLException {
		pre=con.prepareStatement(getcolumnid);
		pre.setInt(1,table_id);
		pre.setString(2, colname);
		result=pre.executeQuery();
  		if(result.next())
  			return result.getInt(1);
  		else return -1;
	}

	public String getEnglishTablenameById(int table_id) throws SQLException
	{
    	String trSql="select table_name from tablename where table_id='"+table_id+"'";
    	ResultSet rs=con.prepareStatement(trSql).executeQuery();
		if(rs.next())
			return (String)rs.getString(1);
		else return null;
	}	

	public ArrayList<String> getSetname(int account_id)
	{
		ArrayList<String> list=new ArrayList<String>();
		try {
			pre=con.prepareStatement(getsetnameSql);	
			pre.setInt(1,account_id);
			result=pre.executeQuery();
			while(result.next())
			{
				list.add(result.getString(1));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			return list;
		}
	
	}
	
	public String gettablename(String setname,int account_id) throws SQLException
	{
		pre=con.prepareStatement(gettablenameSql);
		pre.setString(1, setname);
		pre.setInt(2,account_id);
		result= pre.executeQuery();
		if(result.next())return result.getString(1);
		return null;
	}
	//列名 t 对应列的查询条件query 表名 tablename	
	//TODO 这里有很多种方法
	public ResultSet getresultTable(ArrayList<String> column,ArrayList<String> query,int table_id) throws Exception {
		
		String table=getEnglishTablenameById(table_id);
		StringBuilder sql=new StringBuilder("select ");	
		int size=query.size();
	
		ArrayList<String>unadorncolumn=new ArrayList<String>();
		for(int i=0;i<query.size();i++)
		{
			unadorncolumn.add(getcolumn(column.get(i),table_id));
			System.out.println(unadorncolumn.get(i));
		}
		
		for(int i=0;i<size-1;i++)
		{	
			sql.append(unadorncolumn.get(i)+",");
		}
		sql.append(unadorncolumn.get(size-1)+" from "+table);
		
		boolean isStart=true;
		for(int i=0;i<size;i++)
		{
			if(!query.get(i).equals(""))
			{
				if(isStart)
				{
					sql.append(" where "+unadorncolumn.get(i)+" "+query.get(i));
					isStart=false;
				}
				else
				{
					sql.append(" and "+unadorncolumn.get(i)+" "+query.get(i));
				}
			}	
		}
		System.out.println(sql.toString());
		return con.prepareStatement(sql.toString()).executeQuery();
	}




	public int[] logByAccount(String userName, String password) throws SQLException 
	{
		int []id=new int[2];
		pre=con.prepareStatement(loginSql);
		pre.setString(1, userName);
		pre.setString(2, password);	
		result=pre.executeQuery();
		if(result.next())
		{
			id[0]=result.getInt(1);
			id[1]=result.getInt(2);
			return id;	
		}
		else return null;	
	}
	public ArrayList<String> getTableList(int role_id) 
	{
		 ArrayList<String>list=new ArrayList<String>();
		 try {
			pre=con.prepareStatement(getTablelist);
	
			pre.setInt(1, role_id);
			result=pre.executeQuery();
			while(result.next())
			{
				list.add(result.getString(1));
			}
			return list;
	} catch (SQLException e) {
		e.printStackTrace();	
		return list;
	}
}
	
	public String[] getquerycondition(int account_id, int table_id,String setname,String columnname)
	{
		String[] set={"N","",""};
		try {
			int column_id=getcolumnid(table_id, columnname);
			pre=con.prepareStatement(getquerycondition);
			
			pre.setInt(1,account_id);
			pre.setInt(2, table_id);
			pre.setInt(3, column_id);
			pre.setString(4, setname);
			result=pre.executeQuery();
			if(result.next())
			{
				set[0]=result.getString(1);
				set[1]=result.getString(2);
				set[2]=result.getString(3);
				
			}		
		} catch (SQLException e) {	
			e.printStackTrace();
			return set;
		}
		return set;
	}	
	
	public ArrayList<String> tranColumnlist(ArrayList<String> column, int table_id) {
		for(int i=0;i<column.size();i++)
		{
			column.set(i, getcolumn(column.get(i),table_id));
		}
		return column;
	}
	
	
	private String getcolumn(String column, int table_id) {
		try {
			pre=con.prepareStatement(getcolumn);
			pre.setInt(1, table_id);
			pre.setInt(2, table_id);
			pre.setString(3, column);
			result=pre.executeQuery();
			if(result.next())
			{
				return result.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return "wrong";
	}
	public ArrayList<ArrayList<String>> getGroup(int table_id, String hzx,String hzxsql, String tjx, String tjxsql) throws SQLException {
		
		String tablename=getUnadornTablenameById(table_id);
		String uhzx=getUnadornColumnByadorn(table_id,hzx);
		String utjx=getUnadornColumnByadorn(table_id,tjx);
		String wheresql;
		if(hzxsql.equals("")&&tjxsql.equals(""))
		{
			wheresql="";
		}
		else if(!hzxsql.equals("")&&tjxsql.equals(""))
		{
			wheresql="where"+hzx+" "+hzxsql;
		}
		else if(hzxsql.equals("")&&!tjxsql.equals(""))
		{
			wheresql="where"+tjx+" "+tjxsql;
		}
		else
		{
			wheresql="where"+hzx+" "+hzxsql+" and "+tjx+" "+tjxsql;
		}
		String sql="select "+utjx+", sum("+uhzx+") as "+hzx+"统计"+" from "+tablename+wheresql+" group by "+utjx;
		System.out.println(sql);
		ArrayList<ArrayList<String>>data=new ArrayList<ArrayList<String>>();
		result=con.prepareStatement(sql).executeQuery();
		 
		while(result.next())
		{
			ArrayList<String>temp=new ArrayList<String>();
			temp.add(result.getString(1));
			temp.add(result.getString(2));
			data.add(temp);
		}
		return data;
	}


    
    private String getUnadornColumnByadorn(int table_id, String col) throws SQLException {
    	pre=con.prepareStatement(getUnadornColumnByadorn);
		pre.setInt(1, table_id);
		pre.setInt(2, table_id);
		pre.setString(3, col);	
		result= pre.executeQuery();
		if(result.next())return result.getString(1);
		return null;
	}

	private String getUnadornTablenameById(int table_id) throws SQLException {
    	pre=con.prepareStatement(getUnadornTablenameById);
		pre.setInt(1, table_id);
		result= pre.executeQuery();
		if(result.next())return result.getString(1);
		return null;
	}



	private final static String getcollistSql="select c.adorn_name from columnname c where c.table_id=? and c.column_id in (select r.column_id from rolepermission r where r.role_id=? and r.table_id=?)";
    private final static String getsetedcollistSql="select focus,colname,con1,con2 from querycondition where querycondition.setname=? and account_id=? ";

    private final static String gettablenameSql="select adorn_name from tablename where table_id in (select distinct table_id from querycondition where setname=? and account_id=?)";

    private final static String getsetnameSql="select distinct setname from querycondition where account_id=?";
 
    private final static String insertintoqueryconditionSql="insert into querycondition(account_id,table_id,setname,column_id,flag,con1,con2)"
    		+"values(?,?,?,?,?,?,?)";

    private final static String loginSql="select role_id,account_id from roleaccount where account_id in (select account_id from account where name=? and password=?)";

	private final static String getTablelist="select adorn_name from tablename where table_id in (select  distinct table_id from rolepermission where role_id=?)";
	
	private final static String getTableid="select table_id from tablename where adorn_name=?";

	private final static String getcolumnid="select column_id from columnname where table_id=? and adorn_name=?";

	private final static String deletequerycondition="delete from querycondition where account_id=? and setname=?";

	private final static String getquerycondition="select flag,con1,con2 from  querycondition where account_id=? and table_id=? and column_id=? and setname=?";

	private final static String getcolumn="select column_name from user_tab_cols where table_name in (select table_name from tablename where table_id=?) and column_id  in (select column_id from columnname where table_id=? and adorn_name=?)";

	private final static String getUnadornTablenameById="select table_name from tablename where table_id=?";
	
	private final static String getUnadornColumnByadorn="select column_name from user_tab_cols where table_name in(select object_name from  user_objects where object_id=?) and column_id in (select column_id  from columnname where table_id=? and adorn_name=?)";
    
}