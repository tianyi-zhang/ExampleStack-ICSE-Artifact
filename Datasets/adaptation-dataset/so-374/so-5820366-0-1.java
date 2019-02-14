public class foo {
/**
 * Sets the preferred width of the visible column specified by vColIndex. The column
 * will be just wide enough to show the column head and the widest cell in the column.
 * margin pixels are added to the left and right
 * (resulting in an additional width of 2*margin pixels).
 */ 
public static void packColumn(JTable table, int vColIndex, int margin) {
    DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
    TableColumn col = colModel.getColumn(vColIndex);
    int width = 0;

    // Get width of column header
    TableCellRenderer renderer = col.getHeaderRenderer();
    if (renderer == null) {
        renderer = table.getTableHeader().getDefaultRenderer();
    }
    java.awt.Component comp = renderer.getTableCellRendererComponent(
        table, col.getHeaderValue(), false, false, 0, 0);
    width = comp.getPreferredSize().width;

    // Get maximum width of column data
    for (int r=0; r<table.getRowCount(); r++) {
        renderer = table.getCellRenderer(r, vColIndex);
        comp = renderer.getTableCellRendererComponent(
            table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
        width = Math.max(width, comp.getPreferredSize().width);
    }

    // Add margin
    width += 2*margin;

    // Set the width
    col.setPreferredWidth(width);
}
}