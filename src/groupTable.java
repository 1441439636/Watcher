import java.util.Vector;

import javax.swing.table.AbstractTableModel;


public class groupTable extends AbstractTableModel{

	Vector<Vector<String>> table;
	Vector<String>column;
	public groupTable(Vector<Vector<String>>v,Vector columnt) {
		this.table=v;	
		this.column=columnt;
	}
	
	public groupTable(String [][]s,String[] sp) {
		table=new Vector<Vector<String>>();
		for(int i=0;i<s.length;i++)
		{
			Vector<String>v=new Vector<String>();
			for(int j=0;j<s[i].length;j++)
			{
				v.add(s[i][j]);
			}
			table.add(v);
		}
		column=new Vector<String>();
		for(int i=0;i<sp.length;i++)
		{
			column.add(sp[i]);
		}
	}
	
	public int getColumnCount() {
		return column.size();
	}
//	public void setTableData(Vector<Vector<String>>v) {
//		this.table=v;
//	}
//	public void setColumnName(String[] s) {
//		this.columnname=s;
//	}
//		

	public int getRowCount() {
		// TODO Auto-generated method stub
		return table.size();
	}

	public Object getValueAt(int row, int col) {
		
		return table.get(row).get(col);	
	}
	public String getColumnName(int col)
	{
		return column.get(col);
	}

	
	
	
	
	
	
	
	
}
