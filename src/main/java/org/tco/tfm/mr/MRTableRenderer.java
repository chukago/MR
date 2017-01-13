package org.tco.tfm.mr;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class MRTableRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	
	public MRTableRenderer() {
		
		super();
		
		setLineWrap(true);
		setWrapStyleWord(true);
		//setOpaque(true);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		
		if (isSelected) {
			
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		}
		else {
			
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		
		if (column == 0) {
			
			table.setRowHeight(row, table.getRowHeight(row));
		}
		
		if (value != null) {
		
			this.setText(value.toString());
		}
		else {
			
			this.setText(null);
		}
		
		setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
		
		if (table.getRowHeight(row) < getPreferredSize().height) {
			
			table.setRowHeight(row, getPreferredSize().height);
		}
		
		return this;
	}
	
}
