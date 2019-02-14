public class foo{
	/**
	 * See http://stackoverflow.com/questions/5820238/how-to-resize-jtable-column-to-string-length
	 * 
	 * @param vColIndex
	 * @param margin
	 */
	public static void  packColumn(JTable table, int vColIndex, int margin, int minWidth) {
		TableColumnModel colModel = table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null && table.getTableHeader()!=null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}
		
		java.awt.Component comp=null;
		if(renderer!=null){
			comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
			width = comp.getPreferredSize().width;			
		}

		// Get maximum width of column data (first 1000 rows only incase table is huge)
		int nr=table.getRowCount();
		nr = Math.min(nr, 1000);
		for (int r = 0; r < nr; r++) {
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2 * margin;

		// Ensure min width
		if(width<minWidth){
			width = minWidth;
		}
		
		// Set the width
		col.setPreferredWidth(width);
	}
}