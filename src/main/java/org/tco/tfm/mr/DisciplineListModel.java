package org.tco.tfm.mr;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.tco.tfm.mr.Common.Discipline_t;

public class DisciplineListModel extends AbstractListModel<Discipline_t> implements ComboBoxModel<Discipline_t>{
	
	private static final long serialVersionUID = 1L;

	private static DisciplineListModel instance = null;
	
	private DatabaseManager dbManager = DatabaseManager.getInstance();
	private ArrayList<Discipline_t> data;
	private Discipline_t selectedItem;
	
	public static DisciplineListModel getInstance() {
		
		if(instance == null) {
			
			instance = new DisciplineListModel();
		}
		
		return instance;
	}
	
	private DisciplineListModel() {
		
		data = dbManager.findDiscipline(null, DatabaseManager.DISCIPLINE_TABLE_NAME);
	}
	
	@Override
	public Discipline_t getElementAt(int pos) {
		
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
		
		selectedItem = (Discipline_t)item;
	}
	
	public Discipline_t getItem(int id) {
		
		for (Discipline_t d : data) {
			
			if (d.id == id) {
				
				return d;
			}
		}
		
		return null;
	}
	
}
