package org.tco.tfm.mr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class CheckListModel<T> extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<Boolean> checked;
	private List<T> data;
	
	public CheckListModel(ArrayList<T> data) {
		
		this.data = data;
		checked = Arrays.asList(new Boolean[data.size()]);
		
		for (int i = 0; i < checked.size(); ++i) {
			
			checked.set(i, false);
		}
	}
	
	public CheckListModel(T[] data) {
		
		this.data = Arrays.asList(data);
		checked = Arrays.asList(new Boolean[this.data.size()]);
		
		for (int i = 0; i < checked.size(); ++i) {
			
			checked.set(i, false);
		}
	}
	
	@Override
	public int getColumnCount() {
		
		return 2;
	}

	@Override
	public int getRowCount() {
		
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if (rowIndex < 0 || rowIndex >= data.size()) {
		
			return null;
		}
		
		switch(columnIndex) {
		
		case 0:
			
			return checked.get(rowIndex);
			
		case 1:
			
			return data.get(rowIndex).toString();
		}
		
		return null;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		
		switch(columnIndex) {
		
		case 0:
			
			return "Selected";
			
		case 1:
			
			return "Value";
		}
		
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
		if (columnIndex == 0) {
			
			checked.set(rowIndex, (Boolean)aValue);
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		
		if (columnIndex == 0) {
			
			return true;
		}
		
		return false;
	}
	
	@Override
    public Class<?> getColumnClass(int column) {
        
		switch (column) {
            case 0:
                
            	return Boolean.class;
            
            case 1:
                
            	return String.class;
            	
            default:
            	
            	return Boolean.class;
        }
    }
	
	public void select(T item) {
		
		for (int i = 0; i < data.size(); ++i) { 
			
			if (data.get(i).toString().compareToIgnoreCase(item.toString()) == 0) {
				
				checked.set(i, true);
				this.fireTableCellUpdated(i, i);
				return;
			}
		}
	}
	
	public void select(List<T> items) {
		
		for (int i = 0; i < data.size(); ++i) {
			
			for (T toSelect : items) {
				
				if (data.get(i).toString().compareToIgnoreCase(toSelect.toString()) == 0) {
					
					checked.set(i, true);
					this.fireTableCellUpdated(i, i);
					break;
				}
			}
		}
	}
	
	public ArrayList<T> getSelected() {
		
		ArrayList<T> selected = new ArrayList<T>();
		
		for (int i = 0; i < checked.size(); ++i) {
			
			if (checked.get(i)) {
				
				selected.add(data.get(i));
			}
		}
		
		return selected;
	}
	
	public void clear() {
		
		for (int i = 0; i < data.size(); ++i) {
			
			checked.set(i, false);
			this.fireTableCellUpdated(i, i);
		}
	}
}
