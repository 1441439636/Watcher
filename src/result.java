import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class result extends JFrame {

	result()
	{
		setTitle("查询结果");
		setSize(600,600);
		add(new JLabel("汇总项(数值)"));
		
		hzx=new JComboBox<String>();
		hzx.setPreferredSize(new Dimension(120, 20));
		
		add(hzx);
		
		
		add(new JLabel("统计项"));
	
		tjx=new JComboBox<String>();
		tjx.setPreferredSize(new Dimension(120, 20));
		
		add(tjx);
	
		yes=new JButton("统计确认");
		
		add(yes);
		
		out=new JButton("统计输出");
		
		add(out);
		
	}
	private JComboBox<String> tjx;
	private JComboBox<String>  hzx;
	private JButton yes;
	private JButton out;
	
	public static void main(String[] args) {
	result r=new result();
	r.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	r.setVisible(true);
	}

}
