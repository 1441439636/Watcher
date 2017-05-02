import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class item extends JPanel{
	
	private static final long serialVersionUID = 1L;

	public item(String Flag,String colName,String s1,String s2){
		
		setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
		this.setMaximumSize(new Dimension(500, 35));

		jck=new JCheckBox();
		bn=new JLabel();

		if (Flag.equals("Y")) {
			jck.setSelected(true);
		}
		else jck.setSelected(false);
		
		bn.setPreferredSize(new Dimension(100, 35));		
		bn.setText(colName);
		
		tf1=new JTextField(s1);
			tf1.setPreferredSize(new Dimension(150, 20));		
	
		tf2=new JTextField(s2);
		tf2.setPreferredSize(new Dimension(150, 20));		

		add(jck);
		add(bn);
		add(tf1);
		add(tf2);
	}
private	JTextField tf1;
private	JTextField tf2;
private	JCheckBox jck;
private	JLabel bn;

	public void select(Boolean f) {
		jck.setSelected(f);
	}
	public String getcon1() {
		return tf1.getText();
	}
	public String getcon2() {
		return tf2.getText();
	}
	
	public void setcon1(String s) {
		 tf1.setText(s);
	}

	public void setcon2(String s) {
		 tf2.setText(s);
	}
	
	public String getjck() {
		return jck.isSelected()?"Y":"N";
	}
	public Boolean isSelect() {
		return jck.isSelected();
	}
	public boolean isAllNone() {
		return isNone(tf1.getText())&&isNone(tf2.getText());
	}
	public  boolean isAngle() {
		return (!isNone(tf1.getText()))&&(isNone(tf2.getText()));
	}
	private boolean isNone(String s)
	{
		if(s==null)return true;
		s=s.trim();
		return s.equals("");
	}

	
	public String getcolname() {
		return bn.getText();
	}
	public String getsql() {
		String sql;
		if(isAllNone())return "";
		if(isAngle())
		{
			sql=tf1.getText();
			return "LIKE '"+sql+"'";
		}
		else 
		{
			return "BETWEEN "+tf1.getText()+" AND "+tf2.getText();
		}
	}

	
	

}

