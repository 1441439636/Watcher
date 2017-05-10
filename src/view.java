import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

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
    private JButton out;

    private Database db;
    private ResultSet rs;
    private final setActionListener setlis = new setActionListener();
    private Vector<String> cmbTable;
    private int account_id = 0;
    private int table_id = 0;
    private int screen_height;
    private int screen_width;
    private static final int log_height = 300;
    private static final int log_width = 200;
    private static final int height = 600;
    private static final int width = 600;

    private static final long serialVersionUID = 1L;

    public view() throws Exception {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        screen_width = (int) screensize.getWidth();
        screen_height = (int) screensize.getHeight();
        if (log()) {
            initFrame();
            addListener();
            setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "你的账号/密码错误", "+_+",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private Boolean log() {
        db = new DBFactory().createDB(RegistUtil.read()[0]);
        db.connect();
        logDialog log = new logDialog(screen_width / 2 - (log_width / 2), screen_height / 2 - (log_height / 2), log_width, log_height);
        int state = log.showdia(this, "登录");
        if (state == 1) {
            try {
                account_id = db.logByAccount(log.getUserName(), log.getPassword().toString());
            } catch (SQLException e) {
                System.out.println("   ----------------   fail   ----------------  ");
            }
            System.out.println("---   log    -     account_id=" + account_id);
            if (account_id == -1) {
                return false;
            }
        } else {
            System.exit(0);
        }
        return true;
    }

    final void initFrame() {
        setFeel(plaf);
        setTitle("查询");
        setLayout(new BorderLayout());
        setSize(600, 600);
        JPanel head = new JPanel();
        head.setLayout(new GridLayout(2, 1));
        JPanel tmp = new JPanel();
        tmp.add(new JLabel("选择表"));
        table = new JComboBox<String>();
        table.setPreferredSize(new Dimension(120, 20));
        tmp.add(table);
        tmp.add(new JLabel("选择已有的设置"));
        set = new JComboBox<>();
        set.setPreferredSize(new Dimension(120, 20));
        tmp.add(set);
        save = new JButton("保存设置");
        tmp.add(save);
        delete = new JButton("删除设置");
        tmp.add(delete);
        head.add(tmp);
        tmp = new JPanel();
        tmp.add(new JLabel("选择显示项和设置查询条件"));
        selectall = new JButton("全选");
        selectnone = new JButton("全不选");
        tmp.add(selectall);
        tmp.add(selectnone);
        head.add(tmp);
        add(head, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane jsc = new JScrollPane(contentPanel);
        add(jsc, BorderLayout.CENTER);

        confirm = new JButton("查看");
        out = new JButton("输出");
        JPanel footer = new JPanel();
        footer.add(confirm);
        footer.add(out);


        add(footer, BorderLayout.SOUTH);
        setBounds((screen_width - width) / 2, (screen_height - height) / 2, width, height);

    }

    private void loadTable() {
        table.removeAllItems();
        ArrayList<String> list = db.getTableList(account_id);
        for (int i = 0; i < list.size(); i++) {
            table.addItem(list.get(i));
        }
    }

    private void loadUserSet() {
        set.removeAllItems();
        ArrayList<String> list = db.getSetname(account_id);
        for (int i = 0; i < list.size(); i++) {
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
                    ArrayList<String> list = db.getcollist(account_id, table_id);
                    contentPanel.removeAll();
                    for (int i = 0; i < list.size(); i++) {
                        contentPanel.add(new item("N", list.get(i), "", ""));
                    }
                    contentPanel.updateUI();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        if (table.getSelectedItem() != null) {
            table.setSelectedItem(table.getSelectedItem().toString());
        }

        //全选就是把全部列选中
        selectall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Component[] comp = contentPanel.getComponents();
                item item;
                for (Component c : comp) {
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

        //保存设置
        //遍历列名 保存设置
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String name = JOptionPane.showInputDialog("请输入设置名");
                if (name != null && !name.trim().equals("") && !db.hasSetname(account_id, name)) {
                    Component[] comp = contentPanel.getComponents();
                    if (comp.length == 0) {
                        JOptionPane.showMessageDialog(null, "未选择表", "Warnning", JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {
                        item item;
                        for (Component c : comp) {
                            item = (item) c;
                            try {
                                table_id = db.getTableid((String) table.getSelectedItem());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            db.setquerycondition(account_id, table_id, name, item.getcolname(), item.getjck(), item.getcon1(), item.getcon2());
                        }
                    }
                    set.addItem(name);
                } else {
                    JOptionPane.showMessageDialog(null, "设置名不能为空 不能重复", "Warnning", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });


        //删除用户设置 就是删除
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (set.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(null, "当前没有可以删除的设置", "Warnning", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                db.deletequerycondition(account_id, set.getSelectedItem().toString());
                //停止监听
//                set.removeActionListener(setlis);
                set.removeItem(set.getSelectedItem().toString());
                contentPanel.removeAll();
                contentPanel.updateUI();
            }
        });

        //点确定的时候遍历列名 生成sql语句 弹出一个窗口显示表
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    item item;
                    ArrayList<String> column = new ArrayList<>();
                    ArrayList<String> query = new ArrayList<>();
                    Component[] comp = contentPanel.getComponents();
                    for (Component c : comp) {
                        item = (item) c;
                        if (item.isSelect()) {
                            column.add(item.getcolname());
                            query.add(item.getsql());
                        }
                    }
                    rs = db.getresultTable(column, query, table_id);

                    ArrayList<ArrayList<String>> tabledata = new ArrayList<ArrayList<String>>();
                    while (rs.next()) {
                        ArrayList<String> col = new ArrayList<String>();
                        for (int i = 0; i < column.size(); i++) {
                            col.add(rs.getString(i + 1));
                        }
                        tabledata.add(col);
                    }
                    resultFrame res = new resultFrame(table_id, column, query, tabledata);
                    res.setDefaultCloseOperation(HIDE_ON_CLOSE);
                    res.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        out.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {

                try {


                    item item;
                    ArrayList<String> column = new ArrayList<String>();
                    ArrayList<String> query = new ArrayList<String>();

                    Component[] comp = contentPanel.getComponents();
                    for (Component c : comp) {
                        item = (item) c;
                        if (item.isSelect()) {
                            column.add(item.getcolname());
                            query.add(item.getsql());
                        }
                    }
                    rs = db.getresultTable(column, query, table_id);

                    ArrayList<ArrayList<String>> tabledata = new ArrayList<ArrayList<String>>();
                    while (rs.next()) {
                        ArrayList<String> col = new ArrayList<String>();
                        for (int i = 0; i < column.size(); i++) {
                            col.add(rs.getString(i + 1));
                        }
                        tabledata.add(col);
                    }

                    JTable t = new JTable(new groupTable(column, tabledata));

                    export ex = new export(t);
                    ex.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    ex.setVisible(true);
                } catch (Exception e) {
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

    class resultFrame extends JFrame {
        private static final long serialVersionUID = 1L;
        private JComboBox<String> cz = null;
        private JComboBox<String> tj = null;
        private JComboBox<String> tjfs = null;
        private JButton confirm = null;
        private JButton output = null;
        private JTable table = null;
        private export ex = null;
        JScrollPane jsc = null;

        resultFrame(final int table_id, final ArrayList<String> columnt, final ArrayList<String> query, ArrayList<ArrayList<String>> tabledata) {
            setLayout(new BorderLayout());
            setTitle("查询结果");
            setSize(800, 700);
            setResizable(true);
            JPanel foot = new JPanel();
            foot.setLayout(new FlowLayout());
            cz = new JComboBox<>();
            cz.setPreferredSize(new Dimension(120, 20));
            tj = new JComboBox<>();
            tj.setPreferredSize(new Dimension(120, 20));
            foot.add(new JLabel("操作项(数值)"));
            foot.add(cz);
            foot.add(new JLabel("统计项"));
            foot.add(tj);
            foot.add(new JLabel("统计方式"));
            tjfs = new JComboBox<>();
            tjfs.setPreferredSize(new Dimension(100, 20));
            tjfs.addItem("汇总");
            tjfs.addItem("和");
            tjfs.addItem("平均");
            tjfs.addItem("最大");
            tjfs.addItem("最小");
            foot.add(tjfs);
            confirm = new JButton("确定");
            foot.add(confirm);
            output = new JButton("输出");
            foot.add(output);
            add(foot, BorderLayout.SOUTH);
            cz.addItem("请选择");
            tj.addItem("请选择");
            for (int i = 0; i < columnt.size(); i++) {
                boolean flag = db.getTypeColumn(db.getColumnName(columnt.get(i).toString()), table_id);
                if (flag) {
                    cz.addItem(columnt.get(i).toString());
                }
                tj.addItem(columnt.get(i).toString());
            }
            tj.addItem("ALL");
            if (cz.getItemCount() == 1) {
                cz.removeAllItems();
                tj.removeAllItems();
                tjfs.removeAllItems();
                cz.addItem("无可选项");
                tj.addItem("无可选项");
                tjfs.addItem("无可选项");
            }

            table = new JTable(new groupTable(columnt, tabledata));
            jsc = new JScrollPane(table);

            add(jsc, BorderLayout.CENTER);

            output.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    outExcel();
                }
            });

            cz.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tj.setSelectedItem(cz.getSelectedItem());
                }
            });
            confirm.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (cz.getSelectedIndex() == 0
                            && tj.getSelectedIndex() == 0) {
                        return;
                    }
                    String tjVal = tj.getSelectedItem().toString();
                    String czVal = cz.getSelectedItem().toString();
                    ArrayList<String> col = new ArrayList<>();
                    col.add(tjVal);
                    String tjfsVal = tjfs.getSelectedItem().toString();
                    switch (tjfsVal) {
                        case "汇总":
                            col.add(czVal + "汇总");
                            count(col);
                            break;
                        case "和":
                            col.add(czVal + "和");
                            sum(col);
                            break;
                        case "平均":
                            col.add(czVal + "平均值");
                            avg(col);
                            break;
                        case "最大":
                            col.add(czVal + "最大值");
                            max(col);
                            break;
                        case "最小":
                            col.add(czVal + "最小值");
                            min(col);
                            break;
                    }
                }

                private void min(ArrayList<String> col) {
                    String key = col.get(1).substring(0, col.get(1).indexOf("最小值"));
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    if (!col.get(0).equals("ALL")) {
                        for (ArrayList<String> val : tabledata) {
                            boolean flag = true;
                            for (ArrayList<String> l : data) {
                                if (l.get(0).equals(val.get(columnt.indexOf(col.get(0)))))
                                    flag = false;
                            }
                            if (!flag) {
                                for (ArrayList<String> l : data) {
                                    if (val.get(columnt.indexOf(key)) != null && !val.get(columnt.indexOf(key)).trim().equals("")) {
                                        if (l.get(0).equals(val.get(columnt.indexOf(col.get(0))))) {
                                            double num = Math.min(Double.parseDouble(l.get(1)), Double.parseDouble(val.get(columnt.indexOf(key))));
                                            l.set(1, String.valueOf(num));
                                        }
                                    }
                                }
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(val.get(columnt.indexOf(col.get(0))));
                                list.add(val.get(columnt.indexOf(key)));
                                data.add(list);
                            }
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(key);
                        double num = 999999999;
                        for (ArrayList<String> val : tabledata) {
                            num = Math.min(Double.parseDouble(val.get(columnt.indexOf(list.get(0)))), num);
                        }
                        list.add(num + "");
                        data.add(list);
                    }
                    table = new JTable(new groupTable(col, data));
                    remove(jsc);
                    jsc = new JScrollPane(table);
                    add(jsc, BorderLayout.CENTER);
                    resultFrame.this.setVisible(true);
                }

                private void max(ArrayList<String> col) {
                    String key = col.get(1).substring(0, col.get(1).indexOf("最大值"));
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    if (!col.get(0).equals("ALL")) {
                        for (ArrayList<String> val : tabledata) {
                            boolean flag = true;
                            for (ArrayList<String> l : data) {
                                if (l.get(0).equals(val.get(columnt.indexOf(col.get(0)))))
                                    flag = false;
                            }
                            if (!flag) {
                                for (ArrayList<String> l : data) {
                                    if (val.get(columnt.indexOf(key)) != null && !val.get(columnt.indexOf(key)).trim().equals("")) {
                                        if (l.get(0).equals(val.get(columnt.indexOf(col.get(0))))) {
                                            double num = Math.max(Double.parseDouble(l.get(1)), Double.parseDouble(val.get(columnt.indexOf(key))));
                                            l.set(1, String.valueOf(num));
                                        }
                                    }
                                }
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(val.get(columnt.indexOf(col.get(0))));
                                list.add(val.get(columnt.indexOf(key)));
                                data.add(list);
                            }
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(key);
                        double num = 0;
                        for (ArrayList<String> val : tabledata) {
                            num = Math.max(Double.parseDouble(val.get(columnt.indexOf(list.get(0)))), num);
                        }
                        list.add(num + "");
                        data.add(list);
                    }

                    table = new JTable(new groupTable(col, data));
                    remove(jsc);
                    jsc = new JScrollPane(table);
                    add(jsc, BorderLayout.CENTER);
                    resultFrame.this.setVisible(true);
                }

                private void avg(ArrayList<String> col) {
                    String key = col.get(1).substring(0, col.get(1).indexOf("平均值"));
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    ArrayList<Integer> count = new ArrayList<>();
                    if (!col.get(0).equals("ALL")) {
                        for (ArrayList<String> val : tabledata) {
                            boolean flag = true;
                            for (ArrayList<String> l : data) {
                                if (l.get(0).equals(val.get(columnt.indexOf(col.get(0)))))
                                    flag = false;
                            }
                            if (!flag) {
                                for (ArrayList<String> l : data) {
                                    if (val.get(columnt.indexOf(key)) != null && !val.get(columnt.indexOf(key)).trim().equals("")) {
                                        if (l.get(0).equals(val.get(columnt.indexOf(col.get(0))))) {
                                            double sum = Double.parseDouble(l.get(1)) + Double.parseDouble(val.get(columnt.indexOf(key)));
                                            l.set(1, String.valueOf(sum));
                                            count.set(data.indexOf(l), count.get(data.indexOf(l)) + 1);
                                        }
                                    }
                                }
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(val.get(columnt.indexOf(col.get(0))));
                                list.add(val.get(columnt.indexOf(key)));
                                count.add(1);
                                data.add(list);
                            }
                        }
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).set(1, String.valueOf(
                                    (Double.parseDouble(data.get(i).get(1)) * 1.0) / count.get(i)
                            ));
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(key);
                        double sum = 0;
                        for (ArrayList<String> val : tabledata) {
                            sum += Double.parseDouble(val.get(columnt.indexOf(list.get(0))));
                        }
                        list.add(sum * 1.0 / tabledata.size() + "");
                        data.add(list);
                    }

                    table = new JTable(new groupTable(col, data));
                    remove(jsc);
                    jsc = new JScrollPane(table);
                    add(jsc, BorderLayout.CENTER);
                    resultFrame.this.setVisible(true);

                }

                private void sum(ArrayList<String> col) {
                    String key = col.get(1).substring(0, col.get(1).indexOf("和"));
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    if (!col.get(0).equals("ALL")) {
                        for (ArrayList<String> val : tabledata) {
                            boolean flag = true;
                            for (ArrayList<String> l : data) {
                                if (l.get(0).equals(val.get(columnt.indexOf(col.get(0)))))
                                    flag = false;
                            }
                            if (!flag) {
                                for (ArrayList<String> l : data) {
                                    if (l.get(0).equals(val.get(columnt.indexOf(col.get(0))))) {
                                        if (val.get(columnt.indexOf(key)) != null || !val.get(columnt.indexOf(key)).trim().equals("")) {
                                            double sum = Double.parseDouble(l.get(1)) + Double.parseDouble(val.get(columnt.indexOf(key)));
                                            l.set(1, String.valueOf(sum));
                                        }
                                    }
                                }
                            } else {
                                ArrayList<String> list = new ArrayList<>();
                                list.add(val.get(columnt.indexOf(col.get(0))));
                                list.add(val.get(columnt.indexOf(key)));
                                data.add(list);
                            }
                        }

                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(key);
                        double sum = 0;
                        for (ArrayList<String> val : tabledata) {
                            sum += Double.parseDouble(val.get(columnt.indexOf(list.get(0))));
                        }
                        list.add(sum + "");
                        data.add(list);
                    }

                    table = new JTable(new groupTable(col, data));
                    remove(jsc);
                    jsc = new JScrollPane(table);
                    add(jsc, BorderLayout.CENTER);
                    resultFrame.this.setVisible(true);

                }

                private void count(ArrayList<String> col) {
                    ArrayList<ArrayList<String>> data = new ArrayList<>();
                    if (!col.get(0).equals("ALL")) {
                        for (ArrayList<String> val : tabledata) {
                            boolean flag = true;
                            for (ArrayList<String> l : data) {
                                if (l.get(0).equals(val.get(columnt.indexOf(col.get(0)))))
                                    flag = false;
                            }
                            if (!flag) {
                                for (ArrayList<String> l : data) {
                                    if (l.get(0).equals(val.get(columnt.indexOf(col.get(0))))) {
                                        l.set(1, String.valueOf(Integer.parseInt(l.get(1)) + 1));
                                    }
                                }
                            } else {
                                ArrayList<String> l = new ArrayList<>();
                                l.add(val.get(columnt.indexOf(col.get(0))));
                                l.add(1 + "");
                                data.add(l);
                            }
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("ALL");
                        list.add(tabledata.size() + "");
                        data.add(list);
                    }
                    table = new JTable(new groupTable(col, data));
                    remove(jsc);
                    jsc = new JScrollPane(table);
                    add(jsc, BorderLayout.CENTER);
                    resultFrame.this.setVisible(true);
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

    class setActionListener implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            try {
                String setname = (String) set.getSelectedItem();
                String tablename = db.gettablename(setname, account_id);
                table.setSelectedItem(tablename);
                Component[] com = contentPanel.getComponents();
                for (int i = 0; i < com.length; i++) {
                    item item = (item) com[i];
                    String[] set = db.getquerycondition(account_id, table_id, setname, item.getcolname());
                    item.select(set[0].equals("Y") ? true : false);
                    item.setcon1(set[1]);
                    item.setcon2(set[2]);
                }
                contentPanel.updateUI();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}