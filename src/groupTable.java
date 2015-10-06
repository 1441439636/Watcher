import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;


public class groupTable extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<ArrayList<String>> table;
	ArrayList<String>column;
	public groupTable(ArrayList<String>column,ArrayList<ArrayList<String>>table ) {
		this.table=table;	
		this.column=column;
	}	
	public int getColumnCount() {
		return column.size();
	}

	public int getRowCount() {
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
