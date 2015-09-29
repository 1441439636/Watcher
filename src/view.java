import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
/*
 * 自定义查询保存在服务器 那么查询条件表中必须有账号id 
 * oracle的自增主键我不是很熟悉 一定要给每个表有一个id
 * 
 * 我先把逻辑写下 剩下的就要大大大大大大改
 * 客户端
 */
public class view extends JFrame {
	static final String plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	private static final int WIDTH = 600;
	private static final int HEIGHT = 700;
	private static final int SELECTHIGHT = 80;

	private JPanel contentPanel;
	private JScrollPane jsc;
	private JComboBox<String> table;
	private JComboBox<String> set;
	private JButton all;
	private JButton none;
	private JButton save;
	private JButton delete;
	private JButton yes;
	private JButton out;
	private DatabaseConnect db;
	private ResultSet rs;
	ArrayList<String> t = null;
	ArrayList<String> query = null;
	private Component[] comp = null;
	Vector<String> cmbTable;
	private static final long serialVersionUID = 1L;

	public view() throws Exception {
		if(log())
		{
			initFrame();
			addListener();
			setVisible(true);
			System.out.println("view");
		}
		else
		{
			JOptionPane.showMessageDialog(null, "你的账号/密码错误", "+_+",
					JOptionPane.ERROR_MESSAGE);
		}	
	}
	private Boolean log() {
		//从某个配置文件中读到数据库账号密码 
		// 这里就直接写了
		System.out.println("log");
		try {
			db = new DatabaseConnect();
			if (!db.connect("127.0.0.1:1521", "orcl", "scott", "tiger"))
				return false;

			logDialog log;

			log = new logDialog();
			
			if (log.showdia(this, "登录")) {
				cmbTable = db
						.logByAccount(log.getUserName(), log.getPassword().toString());
				for(int i=0;i<cmbTable.size();i++)
				{
					System.out.println(cmbTable.get(i));
				}
				System.out.println(cmbTable.isEmpty()+" "+cmbTable==null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	final void initFrame() {
		setLayout(null);
		setTitle("查询");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setFeel(plaf);

		JPanel select = new JPanel();
		select.setBounds(0, 0, WIDTH, SELECTHIGHT);
		select.setLayout(new FlowLayout());

		select.add(new JLabel("选择表"));

		table = new JComboBox<String>();
		table.setPreferredSize(new Dimension(120, 20));
		select.add(table);

		select.add(new JLabel("选择已有的设置"));
		set = new JComboBox<String>();
		set.setPreferredSize(new Dimension(120, 20));
		select.add(set);

		save = new JButton("保存设置");
		select.add(save);

		delete = new JButton("删除设置");
		select.add(delete);

		add(select);

		select.add(new JLabel("选择显示项和设置查询条件"));
		all = new JButton("全选");
		none = new JButton("全不选");

		select.add(all);
		select.add(none);

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

		jsc = new JScrollPane(contentPanel);
		jsc.setBounds(50, 80, 500, 500);
		add(jsc);
		yes = new JButton("确定");
		out = new JButton("输出");

		JPanel footer = new JPanel();
		footer.setBounds(0, 600, 600, 100);
		footer.setLayout(new FlowLayout());

		footer.add(yes);
		footer.add(out);

		add(footer);
	}

	private void addListener() throws Exception {
		
		for(int i=0;i<cmbTable.size();i++)
		{
			table.addItem(cmbTable.get(i));
		}
		
		rs = db.getSetname();
		while (rs.next())
		{
			set.addItem(rs.getString(1));
		}
		table.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String cname = (String) table.getSelectedItem();
				try {
					rs = db.getcollist(cname);
					contentPanel.removeAll();
					while (rs.next()) {
						contentPanel.add(new item("N", rs.getString(1), "", ""));
					}
					contentPanel.updateUI();
					comp = contentPanel.getComponents();

				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		});

		all.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				item r;
				for (Component c : comp) {
					r = (item) c;
					r.select(true);
				}
				contentPanel.updateUI();
			}
		});
		none.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				item r;
				for (Component c : comp) {
					r = (item) c;
					r.select(false);
				}
				contentPanel.updateUI();
			}
		});

		set.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				try {
					String setname = (String) set.getSelectedItem();

					String tablename = null;
					rs = db.gettablename(setname);
					if (rs.next()) {
						tablename = rs.getString(1);
						table.setSelectedItem(tablename);

						contentPanel.removeAll();

						rs = db.getsetedcollist(setname);

						while (rs.next()) {
							contentPanel.add(new item(rs.getString(1), rs
									.getString(2), rs.getString(3), rs
									.getString(4)));
						}
						comp = contentPanel.getComponents();
						contentPanel.updateUI();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (comp == null) {
					JOptionPane.showMessageDialog(null, "未选择表", "Warnning",
							JOptionPane.ERROR_MESSAGE);
				} else {
					String setname = null;
					setname = JOptionPane.showInputDialog("输入此查询设置名");
					if (setname == null || setname.equals("")) {
						JOptionPane.showMessageDialog(null, "设置名不能为空",
								"Warnning", JOptionPane.ERROR_MESSAGE);
					} else {
						item r;
						for (Component c : comp) {
							r = (item) c;
							try {
								db.insertintoquerycondition(
										(String) table.getSelectedItem(),
										setname, r.getcolname(), r.getjck(),
										r.getcon1(), r.getcon2());
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						set.addItem(setname);
					}
				}

			}
		});
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					db.deletequerycondition((String) set.getSelectedItem());
					set.removeItem(set.getSelectedItem());
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		});

		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				item r;
				t = new ArrayList<String>();
				query = new ArrayList<String>();
				for (Component c : comp) {
					r = (item) c;
					if (r.isSelect()) {
						t.add(r.getcolname());
						if (r.isAllNone()) {
							query.add("?NONE?");
						} else
							query.add(r.getsql());
					}
				}

				Table ta;
				try {
					Vector columnt = new Vector(t);
					Vector<Vector<String>> tabledata = new Vector<Vector<String>>();

					try {
						rs = db.getTable(t, query,
								(String) table.getSelectedItem());
						while (rs.next()) {
							Vector<String> col = new Vector<String>();
							for (int i = 0; i < t.size(); i++) {
								col.add(rs.getString(i + 1));
							}
							tabledata.add(col);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					ta = new Table((String) table.getSelectedItem(), tabledata,
							columnt);

					ta.setDefaultCloseOperation(HIDE_ON_CLOSE);
					ta.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		});

		out.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

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

	class Table extends JFrame {

		static final String plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		JPanel foot;
		JPanel TablePanel;

		JComboBox<String> hz;
		JComboBox<String> tj;
		JButton yes;
		JButton out;
		JScrollPane jsc;
		JTable table;
		export ex;

		private ResultSet rs;

		Table(final String tablename, Vector tabledata, Vector columnt) {
			setLayout(new BorderLayout());
			setTitle("查询结果");
			setSize(600, 700);
			setResizable(true);
			setFeel(plaf);

			foot = new JPanel();
			foot.setLayout(new FlowLayout());
			hz = new JComboBox<String>();
			hz.setPreferredSize(new Dimension(120, 20));
			tj = new JComboBox<String>();
			tj.setPreferredSize(new Dimension(120, 20));
			foot.add(new JLabel("汇总项(数值)"));
			foot.add(hz);
			foot.add(new JLabel("统计项"));
			foot.add(tj);
			yes = new JButton("统计确定");
			foot.add(yes);
			out = new JButton("统计输出");
			foot.add(out);
			add(foot, BorderLayout.SOUTH);

			hz.addItem("请选择");
			tj.addItem("请选择");

			for (int i = 0; i < columnt.size(); i++) {
				hz.addItem(columnt.get(i).toString());
				tj.addItem(columnt.get(i).toString());

			}
			System.out.println(hz.getSelectedIndex());
			TablePanel = new JPanel();
			table = new JTable(new groupTable(tabledata, columnt));
			jsc = new JScrollPane(table);
			TablePanel.add(jsc);
			add(TablePanel, BorderLayout.CENTER);
			System.out.println("the name");

			out.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					outExcel();
				}
			});
			yes.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if (hz.getSelectedIndex() == 0
							&& tj.getSelectedIndex() == 0) {
						outExcel();
					}

					try {
						String hzx = hz.getSelectedItem().toString();
						String tjx = tj.getSelectedItem().toString();
						System.out.println(hzx + " " + tjx + " " + tablename);

						Vector v = view.this.db.getGroup(hzx, tjx, tablename);
						System.out.println("click");
						Vector<String> s = new Vector<String>();
						s.add(tjx);
						s.add(hzx);
						TablePanel.removeAll();
						table = new JTable(new groupTable(v, s));
						jsc = new JScrollPane(table);
						TablePanel.add(jsc);
						TablePanel.updateUI();

					} catch (Exception e) {
						// TODO Auto-generated catch block
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

}
