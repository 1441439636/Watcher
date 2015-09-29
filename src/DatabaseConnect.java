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
				JOptionPane.showMessageDialog(null, "数据库错误", "+_+",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
	    	}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return true;	
		
    }

	public ResultSet getChineseTableName() throws Exception {
		return con.prepareStatement(getChineseTableNameSql).executeQuery();
	}
	
	public ResultSet getcollist(String col) throws SQLException
	{
		pre=con.prepareStatement(getcollistSql);
  		pre.setString(1, col);
  		
  		return pre.executeQuery();
	}

	public ResultSet getsetedcollist(String setname) throws SQLException
	{
		pre=con.prepareStatement(getsetedcollistSql);
  		pre.setString(1, setname);
  		return pre.executeQuery();
	}
	
	public void insertintoquerycondition(String tablename,String setName,String colname,String focus,String con1,String con2) throws SQLException
	{
		pre=con.prepareStatement(insertintoqueryconditionSql);		
		pre.setString(1, tablename);
		pre.setString(2, setName);		
  		pre.setString(3, colname);
  		pre.setString(4, focus);
  		pre.setString(5, con1);
  		pre.setString(6, con2);		
  		pre.executeQuery();
	}
	public String getEnglishTablenameByChinese(String tablename) throws SQLException
	{
    	String trSql="select name from tablename where chinese='"+tablename+"'";
    	ResultSet rs=con.prepareStatement(trSql).executeQuery();
		if(rs.next())
			return (String)rs.getString(1);
		else return null;
	}	
	public String getEnglishColnameByChinese(String colname,String tablename) throws SQLException
	{
		String trSql="select col_name from colname where chinese='"+colname+"' AND table_name= '"+tablename+"'";
		System.out.println(trSql);
    	ResultSet rs=con.prepareStatement(trSql).executeQuery();
		if(rs.next())
			return (String)rs.getString(1);
		else return null;
	}
	public Vector getGroup(String hz,String tj,String tablename) throws SQLException
	{
		String etn=getEnglishTablenameByChinese(tablename);	
		String ehz=getEnglishColnameByChinese(hz, etn);
		String etj=getEnglishColnameByChinese(tj, etn);
		
		Vector<Vector<String>> v=new Vector<Vector<String>>();
		String sql="select "+etj+",sum("+ehz+") from "+etn+" group by "+etj;
		pre=con.prepareStatement(sql);
  		ResultSet rs=pre.executeQuery();
		while(rs.next())
		{
			Vector<String>s=new Vector<String>();
			s.add(rs.getString(1));
			s.add(rs.getString(2));
			v.add(s);		
		}
		return v;
	}
	public void deletequerycondition(String setname) throws SQLException
	{
		pre=con.prepareStatement(deletetset);
		pre.setString(1, setname);
		pre.executeQuery();
	}
	public ResultSet getSetname() throws SQLException
	{
		return con.prepareStatement(getsetnameSql).executeQuery();
	}
	
	public ResultSet gettablename(String setname) throws SQLException
	{
		pre=con.prepareStatement(gettablenameSql);
		pre.setString(1, setname);
		return pre.executeQuery();
	}

	public ResultSet getTable(ArrayList<String> t, ArrayList<String> query, String tablename) throws Exception {
		String sql="select ";	
		tablename=tr(tablename);
		
		t=tr(t,tablename);
		for(int i=0;i<t.size()-1;i++)
		{
			sql+=t.get(i)+" , ";
		}
		sql+=t.get(t.size()-1)+" from "+tablename+" where ";
		for(int i=0;i<query.size();i++)
		{
			String s=query.get(i);
			if(s.equals("?NONE?"));
			else 
			{
				sql+=t.get(i)+" "+query.get(i);
				if((i!=(query.size()-1))&&(!query.get(i+1).equals("?NONE?")))
				{
					sql+=" AND ";
				}

			}
		}	
		if(sql.endsWith("where ")){
			sql=sql.substring(0,sql.length()-6);
		}
		return con.prepareStatement(sql).executeQuery();
	}

    public String tr(String tablename) throws Exception {
    	String trSql="select name from tablename where chinese='"+tablename+"'";
    	ResultSet rs=con.prepareStatement(trSql).executeQuery();
		if(rs.next())
			return (String)rs.getString(1);
		else return null;
	}
    public String getEnglishColname(String colname) throws Exception {
    	String trSql="select col_name from colname where chinese="+colname;
    	ResultSet rs=con.prepareStatement(trSql).executeQuery();
		if(rs.next())
			return (String)rs.getString(1);
		else return null;
	}
	private ArrayList<String> tr(ArrayList<String> t,String tabname) throws SQLException {
    	String trSql="select col_name from colname where table_name='"+tabname+"' AND chinese in ( ";
    	for(int i=0;i<t.size()-1;i++)
    	{
    		trSql+="'"+t.get(i)+"' , ";
    	}
    	trSql+="'"+t.get(t.size()-1)+"')";
    	ResultSet rs=con.prepareStatement(trSql).executeQuery();
    	t.clear();
    	while(rs.next())
    	{
    		t.add(rs.getString(1));
    	}
		return t;
	}

	private static 	String getChineseTableNameSql="select chinese from tablename";
    
    private static 	String getcollistSql="select colname.chinese from colname,tablename where tablename.chinese = ? and colname.table_name=tablename.name and colname.flag='Y'";
    
    private static 	String getsetedcollistSql="select focus,colname,con1,con2 from querycondition where querycondition.setname=? ";

    
    private static 	String gettablenameSql="select tablename from querycondition where setname=?";

    private static 	String getsetnameSql="select distinct setname from querycondition";
 
    private static 	String insertintoqueryconditionSql="insert into querycondition(tablename,setname,colname,focus,con1,con2)"
    		+"values(?,?,?,?,?,?)";

    private static 	String deletetset="delete  from querycondition where setname=?";
    private static 	String groupsql="select ?,sum(?) from ? group by ?";
	private final static String loginSql="select tablename.chinese from tablename where id in "
			+"(select ROLEPERMISSION.TABLE_NAME_ID from ROLEPERMISSION where ROLEPERMISSION.Role_Name_Id in"
			+"(select role_name_id from account where account.account=? and account.password=?))";

	public Vector<String> logByAccount(String userName, String password) {
		Vector<String> s=new Vector<String>();	
		System.out.println("logaccount");
		try {
		pre=con.prepareStatement(loginSql);
		pre.setString(1, userName);
		pre.setString(2, password);	
		result=pre.executeQuery();
		
		while(result.next())
		{
			s.add(result.getString(1));
			System.out.println(result.getString(1));
		}
		} catch (SQLException e) {	
			e.printStackTrace();
			return null;
		}
		System.out.println("log end"+s.isEmpty());
		return s;
	}

    
}