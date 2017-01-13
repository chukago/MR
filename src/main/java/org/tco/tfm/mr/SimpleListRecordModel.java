package org.tco.tfm.mr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.tco.tfm.mr.DatabaseManager.SimpleListRecord;

public class SimpleListRecordModel extends AbstractListModel<SimpleListRecord> implements ComboBoxModel<SimpleListRecord>{

	private static final long serialVersionUID = 1L;
	
	private DatabaseManager dbManager = DatabaseManager.getInstance();
	
	private String listName;
	private SimpleListRecord selectedItem;
	private ArrayList<SimpleListRecord> data = new ArrayList<SimpleListRecord>();
	
	public SimpleListRecordModel(String listName) {
		
		if (listName == null || listName.isEmpty()) {
			
			return;
		}
		
		this.listName = listName;
		
		ArrayList<DatabaseManager.SimpleListRecord> newData = dbManager.findSimpleListRecord(listName, null);
		
		if (newData != null) {
			
			Collections.sort(newData, new Comparator<DatabaseManager.SimpleListRecord>() {

				@Override
				public int compare(SimpleListRecord first, SimpleListRecord second) {
					
					return first.value.compareToIgnoreCase(second.value);
				}
				
			});
			
			data = newData;
		}
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
		
		selectedItem = (DatabaseManager.SimpleListRecord)item;
	}
	
	public String getValue(int id) {
		
		for (SimpleListRecord r : data) {
			
			if (r.id == id) {
				
				return r.value;
			}
		}
		
		return null;
	}
	
	public SimpleListRecord add(String value) {
		
		for (SimpleListRecord r : data) {
			
			if (r.value.compareToIgnoreCase(value) == 0) {
				
				return r;
			}
		}
		
		SimpleListRecord newRecord = new SimpleListRecord(0, value);
		
		if (!dbManager.saveSimpleListRecord(listName, newRecord)) {
			
			System.out.println("Cannot add new list record in database");
			return null;
		}
		
		data.add(newRecord);
		
		this.fireIntervalAdded(this, data.size() - 1, data.size() - 1);
		
		return newRecord;
	}

}
