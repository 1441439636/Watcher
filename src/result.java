import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class result extends JFrame {

	result()
	{
		setTitle("��ѯ���");
		setSize(600,600);
		add(new JLabel("������(��ֵ)"));
		
		hzx=new JComboBox<String>();
		hzx.setPreferredSize(new Dimension(120, 20));
		
		add(hzx);
		
		
		add(new JLabel("ͳ����"));
	
		tjx=new JComboBox<String>();
		tjx.setPreferredSize(new Dimension(120, 20));
		
		add(tjx);
	
		yes=new JButton("ͳ��ȷ��");
		
		add(yes);
		
		out=new JButton("ͳ�����");
		
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
