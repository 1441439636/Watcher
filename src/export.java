import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableModel;


public class export extends JFrame {
	
	export(final JTable jt)
	{
		setLayout(new FlowLayout());
		setTitle("�����ļ�");
		setSize(300,150);
		setFeel(plaf);
		add(new JLabel("�ļ���"));
		path=new JTextField();
		path.setPreferredSize(new Dimension(150,20));
		add(path);
		open=new JButton("ѡ��·��");
		add(open);
		jck=new JCheckBox();
		add(jck);
		add(new JLabel("��������ļ�"));
		yes=new JButton("ȷ��");
		add(yes);
		open.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				 JFileChooser jfc=new JFileChooser();  
			        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
			        jfc.showDialog(new JLabel(), "ѡ��");  
			        File file=jfc.getSelectedFile();  
			        path.setText(file.getAbsolutePath());		        
			}
		});
		yes.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				try {
					File f=new File(path.getText());
					exportTable(jt,f);
					if(jck.isSelected())
					{
						java.awt.Desktop.getDesktop().open(f);;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void exportTable(JTable table, File file) throws IOException {
	       TableModel model = table.getModel();
	       BufferedWriter bWriter = new BufferedWriter(new FileWriter(file));
	       for(int i=0; i < model.getColumnCount(); i++) {
	           bWriter.write(model.getColumnName(i));
	           bWriter.write("\t");
	       }
	       bWriter.newLine();
	       for(int i=0; i< model.getRowCount(); i++) {
	           for(int j=0; j < model.getColumnCount(); j++) {
	               bWriter.write(model.getValueAt(i,j).toString());
	               bWriter.write("\t");
	           }
	           bWriter.newLine();
	       }
	       bWriter.close();
	      
	   }
	public static void main(String[] args) {
		
		
		Object[][] tableData =   
			    {  
			        new Object[]{"������" , 29 , "Ů"},  
			        new Object[]{"�ո�����", 56 , "��"},  
			        new Object[]{"���", 35 , "��"},  
			        new Object[]{"Ū��", 18 , "Ů"},  
			        new Object[]{"��ͷ" , 2 , "��"}  
			    };  
			    //����һά������Ϊ�б���  
		Object[] columnTitle = {"����" , "����" , "�Ա�"};  
		JTable table = new JTable(tableData , columnTitle);
		
		export e=new export(table);
		e.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		e.setVisible(true);
	}
	static final String plaf="com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	final  void  setFeel(String f)
	{
		try {
			UIManager.setLookAndFeel(f);
			SwingUtilities.updateComponentTreeUI(this);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	JCheckBox jck;
	JTextField path;
	JButton open;
	JButton yes;
	
}
