import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
public class view extends JFrame {
	static final String plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	private JPanel contentPanel;
	private JComboBox<String> table;
	private JComboBox<String> set;
	private JButton selectall;
	private JButton selectnone;
	private JButton save;
	private JButton delete;
	private JButton confirm;
	private DatabaseConnect db;
	private ResultSet rs;
	private final setActionListener setlis=new setActionListener();
	Vector<String> cmbTable;
	int role_id=0;
	int account_id=0;
	int table_id=0;
	private static final long serialVersionUID = 1L;

	public view() throws Exception {
		if(log())
		{
			initFrame();
			addListener();
			setVisible(true);
		}
		else
		{
			JOptionPane.showMessageDialog(null, "你的账号/密码错误", "+_+",
					JOptionPane.ERROR_MESSAGE);
		}	
	}
	//从某个配置文件中读到数据库账号密码 
	// 这里就直接写了
	//登录 是顺便获取角色id 和账号id

	private Boolean log() {
		try {
			db = new DatabaseConnect();
			if (!db.connect("127.0.0.1:1521", "orcl", "scott", "tiger"))
				return false;

			logDialog log;

			log = new logDialog();
			
			if (log.showdia(this, "登录")) {
				int[] id = db.logByAccount(log.getUserName(), log.getPassword().toString());
				if(id==null)return false;
				role_id=id[0];
				account_id=id[1];
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	final void initFrame() 
	{
		setFeel(plaf);
		setTitle("查询");
		setLayout(new BorderLayout());
		setSize(600, 600);	
		JPanel head=new JPanel();
		head.setLayout(new GridLayout(2,1));
		JPanel tmp=new JPanel();
		tmp.add(new JLabel("选择表"));
		table = new JComboBox<String>();
		table.setPreferredSize(new Dimension(120, 20));
		tmp.add(table);
		tmp.add(new JLabel("选择已有的设置"));
		set = new JComboBox<String>();
		set.setPreferredSize(new Dimension(120, 20));
		tmp.add(set);
		save = new JButton("新增设置");
		tmp.add(save);
		delete = new JButton("删除设置");
		tmp.add(delete);
		head.add(tmp);
		tmp=new JPanel();
		tmp.add(new JLabel("选择显示项和设置查询条件"));
		selectall = new JButton("全选");
		selectnone = new JButton("全不选");
		tmp.add(selectall);
		tmp.add(selectnone);
		head.add(tmp);
		add(head,BorderLayout.NORTH);
		
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
	    JScrollPane	jsc = new JScrollPane(contentPanel);
		add(jsc,BorderLayout.CENTER);
		
		confirm = new JButton("确定");
		JPanel footer = new JPanel();		
		footer.add(confirm);
		add(footer,BorderLayout.SOUTH);
	}
	
	private void loadTable()
	{
		table.removeAllItems();
		ArrayList<String>list=db.getTableList(role_id);
		for(int i=0;i<list.size();i++)
		{
			table.addItem(list.get(i));
		}	
	}
	private void loadUserSet()
	{
		set.removeAllItems();
		ArrayList<String>list=db.getSetname(account_id);
		for(int i=0;i<list.size();i++)
		{
			set.addItem(list.get(i));
		}		
	}
	
	private void addListener() throws Exception {
		loadTable();
		loadUserSet();
		//当表被选中时加载列
		table.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
				table_id = db.getTableid((String) table.getSelectedItem());
						
				ArrayList<String>list=db.getcollist(role_id,table_id);
				
				contentPanel.removeAll();
				for(int i=0;i<list.size();i++)
				{
					contentPanel.add(new item("N",list.get(i), "", ""));
				}
				contentPanel.updateUI();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
		});
		//全选就是把全部列选中
		selectall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Component[] comp = contentPanel.getComponents();
				item item;
				for (Component c : comp)
				{
					item = (item) c;
					item.select(true);
				}
				contentPanel.updateUI();
			}
		});

		selectnone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				item item;
				Component[] comp = contentPanel.getComponents();
				for (Component c : comp) {
					item = (item) c;
					item.select(false);
				}
				contentPanel.updateUI();
			}
		});
		//当用户设置被选中时 先获取设置的表名 将表名下拉框设置为选中
		//然后根据表名得到列名 根据 用户设置更新选中状态 查询条件
		set.addActionListener(setlis);

		//保存设置 弹出框输入设置名
		//遍历列名 保存设置
		//保存设置事先删除数据库中之前的设置  因为判断很繁琐
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
						Component[] comp = contentPanel.getComponents();
						if (comp.length==0)
						{
							JOptionPane.showMessageDialog(null, "未选择表", "Warnning",JOptionPane.ERROR_MESSAGE);
						} else
						{
							String setname = null;
							setname = JOptionPane.showInputDialog("输入此查询设置名");
							if (setname == null || setname.equals(""))
							{
								JOptionPane.showMessageDialog(null, "设置名不能为空","Warnning", JOptionPane.ERROR_MESSAGE);
							}else 
							{
								item item;
								db.deletequerycondition(account_id,setname);
								for (Component c : comp)
								{
									item= (item) c;
									db.setquerycondition(account_id,table_id,item.getjck(),setname, item.getcolname(),item.getcon1(), item.getcon2());
								}
							}
							set.addItem(setname);
						}
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		//删除用户设置 就是删除
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					db.deletequerycondition(account_id,set.getSelectedItem().toString());
					set.removeActionListener(setlis);
					set.removeItem(set.getSelectedItem().toString());
					contentPanel.removeAll();
					contentPanel.updateUI();
					} catch (SQLException e)
					{
						e.printStackTrace();
					}
			}
		});

		//点确定的时候遍历列名 生成sql语句 弹出一个窗口显示表
		confirm.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try {
				item item;
				ArrayList<String> column = new ArrayList<String>();
				ArrayList<String> query = new ArrayList<String>();
				Component[] comp = contentPanel.getComponents();
				for (Component c : comp)
				{
					item = (item) c;
					if (item.isSelect())
					{
						column.add(item.getcolname());
						query.add(item.getsql());
					}
				}
				rs = db.getresultTable(column,query,table_id);
				ArrayList<ArrayList<String>>tabledata=new ArrayList<ArrayList<String>>();
				while (rs.next()) 
				{
					ArrayList<String> col = new ArrayList<String>();
					for (int i = 0; i < column.size(); i++)
					{
						col.add(rs.getString(i + 1));
					}
					tabledata.add(col);
				}
				resultFrame res= new resultFrame(table_id,column,query,tabledata);
				res.setDefaultCloseOperation(HIDE_ON_CLOSE);
				res.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	final void setFeel(String f) {
		try {
			UIManager.setLookAndFeel(f);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		view frame = new view();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class resultFrame extends JFrame
	{	
		private static final long serialVersionUID = 1L;
		private JComboBox<String> hz=null;
		private JComboBox<String> tj=null;
		private JButton confirm=null;
		private JButton output=null;
		private JTable table=null;
		private export ex=null;
		JScrollPane jsc=null;
		resultFrame(final int table_id,final ArrayList<String>  columnt,final ArrayList<String> query,ArrayList<ArrayList<String>> tabledata)
		{
			setLayout(new BorderLayout());
			setTitle("查询结果");
			setSize(600, 700);
			setResizable(true);
			JPanel foot=new JPanel();
			foot.setLayout(new FlowLayout());
			hz = new JComboBox<String>();
			hz.setPreferredSize(new Dimension(120, 20));
			tj = new JComboBox<String>();
			tj.setPreferredSize(new Dimension(120, 20));
			foot.add(new JLabel("汇总项(数值)"));
			foot.add(hz);
			foot.add(new JLabel("统计项"));
			foot.add(tj);
			confirm= new JButton("统计确定");
			foot.add(confirm);
			output = new JButton("统计输出");
			foot.add(output);
			add(foot, BorderLayout.SOUTH);
			hz.addItem("请选择");
			tj.addItem("请选择");
			for (int i = 0; i < columnt.size(); i++) {
				hz.addItem(columnt.get(i).toString());
				tj.addItem(columnt.get(i).toString());
			}
			table=new JTable(new groupTable(columnt,tabledata));
			jsc = new JScrollPane(table);
			
			add(jsc, BorderLayout.CENTER);

			output.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					outExcel();
				}
			});
			confirm.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					if (hz.getSelectedIndex() == 0
							&& tj.getSelectedIndex() == 0) {
						outExcel();
					}
					try {
						String hzx = hz.getSelectedItem().toString();
						String tjx = tj.getSelectedItem().toString();
						String hzxsql="";
						String tjxsql="";
						int count=0;
						
						for(int i=0;i<columnt.size();i++)
						{
							if(count==2)break;
							if(columnt.get(i).equals(hzx))
							{
								hzxsql=query.get(i);
								count++;
							}
							else if(columnt.get(i).equals(tjx))
							{
								tjxsql=query.get(i);
								count++;
							}
							
						}
						
						ArrayList<ArrayList<String>> data=db.getGroup(table_id,hzx,hzxsql,tjx,tjxsql);
						ArrayList<String>col=new ArrayList<String>();
						col.add(hzx);
						col.add(tjx+"汇总");

					//	jsc.remove(table);
						table=new JTable(new groupTable(col,data));						
					//	jsc.add(table);
						remove(jsc);
						jsc = new JScrollPane(table);
						add(jsc,BorderLayout.CENTER);
//						table.paintImmediately(table.getBounds());
						//table.repaint();
						//jsc.repaint();
						resultFrame.this.setVisible(true);
						//jsc.getParent().repaint();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}

		final void setFeel(String f) {
			try {
				UIManager.setLookAndFeel(f);
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		private void outExcel() {
			ex = new export(table);
			ex.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			ex.setVisible(true);
		}
	}


	class setActionListener implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) {
			try {
				System.out.println("here");
				String setname = (String) set.getSelectedItem();
			 	String tablename = db.gettablename(setname,account_id);
			 	System.out.println(setname+" "+tablename);
			 	table.setSelectedItem(tablename);
				
			 	ArrayList<String>list=db.getcollist(role_id,table_id);
				System.out.println(list.size());
				contentPanel.removeAll();
				for(int i=0;i<list.size();i++)
				{
					String columnname=list.get(i);
					String[]set=db.getquerycondition(account_id,table_id,setname,columnname);
					item item=new item(set[0],columnname,set[1],set[2]);
					contentPanel.add(item);
				}
				contentPanel.updateUI();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}














}