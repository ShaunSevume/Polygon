import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class MainUI {
    private JPanel rootPanel;
    private JTable showTable;
    private JLabel labelA;

    // this is to help see the table!
    public MainUI(){
        createTable();
    }
    public JPanel getRootPanel(){
        return rootPanel;
    }

    private void createTable() {
        // adding data into the rows
        Object [][] data = {
                { "", "", "", "", "", "","" },
                { "", "", "", "", "", "","" },
                { "", "", "", "", "", "","" },
                { "", "", "", "", "", "","" },
                { "", "", "", "", "", "","" }
        };
        String[] columnNames = { "Name", "Month 1", "Month 2", "Month 3", "Month 4", "Month 5" };
        showTable.setModel(new DefaultTableModel(
                //data
                data ,
                // the info for first column!
                columnNames

        ));

        TableColumnModel columns = showTable.getColumnModel();
        columns.getColumn(0).setMinWidth(100);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // centers the text!
        columns.getColumn(1).setCellRenderer(centerRenderer);
        columns.getColumn(2).setCellRenderer(centerRenderer);
        columns.getColumn(3).setCellRenderer(centerRenderer);
        columns.getColumn(4).setCellRenderer(centerRenderer);
        columns.getColumn(5).setCellRenderer(centerRenderer);
        //set the resize mode to off
        showTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //showTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        showTable.setPreferredScrollableViewportSize(showTable.getPreferredSize());
    }

    public void refreshTable(String[] columns, Object[][] data){
        showTable.setModel(new DefaultTableModel(
                //data
                data ,
                // the info for first column!
                columns
        ));
        showTable.repaint();
    }
}
