import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import tool.L;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

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

    export(final JTable jt) {
        setLayout(new FlowLayout());
        setTitle("导出文件");
        setSize(300, 150);
        setFeel(plaf);
        add(new JLabel("文件名"));
        path = new JTextField();
        path.setPreferredSize(new Dimension(150, 20));
        add(path);
        open = new JButton("选择路径");
        add(open);
        jck = new JCheckBox();
        add(jck);
        add(new JLabel("导出后打开文件"));
        yes = new JButton("确定");
        add(yes);
        open.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                jfc.showDialog(new JLabel(), "选择");
                File file = jfc.getSelectedFile();
                path.setText(file.getAbsolutePath());
            }
        });
        yes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                try {
                    File f = new File(path.getText());
                    exportTable(jt, f);
                    if (jck.isSelected()) {
                        java.awt.Desktop.getDesktop().open(f);
                        ;
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
        FileOutputStream out = new FileOutputStream(file);
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet("默认");
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);


        HSSFRow row = sheet.createRow(0);


        for (int i = 0; i < model.getColumnCount(); i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(model.getColumnName(i));
            cell.setCellValue(text);
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object oval = model.getValueAt(i, j);
                String val = "";
                if (oval != null) {
                    val = oval.toString();
                }
                row.createCell(j).setCellValue(val);
            }
        }

        workbook.write(out);
    }

    public static final String plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    final void setFeel(String f) {
        try {
            UIManager.setLookAndFeel(f);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JCheckBox jck;
    JTextField path;
    JButton open;
    JButton yes;

}
