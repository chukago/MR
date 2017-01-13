package org.tco.tfm.mr;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.tco.tfm.mr.DatabaseManager.SimpleListRecord;

public class WhsListModel extends AbstractListModel<SimpleListRecord> implements ComboBoxModel<SimpleListRecord>{
	
	private static final long serialVersionUID = 1L;

	private DatabaseManager dbManager = DatabaseManager.getInstance();
	
	private ArrayList<SimpleListRecord> data;
	
	private SimpleListRecord selectedItem;
	
	public WhsListModel() {
		
		data = dbManager.findSimpleListRecord(DatabaseManager.WHS_TABLE_NAME, null);
	}
	
	@Override
	public SimpleListRecord getElementAt(int pos) {
		
		return data.get(pos);
	}

	@Override
	public int getSize() {
		
		return data.size();
	}

	@Override
	public Object getSelectedItem() {
		
		return selectedItem;
	}

	@Override
	public void setSelectedItem(Object item) {
		
		selectedItem = (SimpleListRecord)item;
	}
	
	public String getValue(int id) {
		
		for (SimpleListRecord r : data) {
			
			if (r.id == id) {
				
				return r.value;
			}
		}
		
		return null;
	}
}
