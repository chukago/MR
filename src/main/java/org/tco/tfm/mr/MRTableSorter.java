package org.tco.tfm.mr;

import javax.swing.table.TableRowSorter;

public class MRTableSorter extends TableRowSorter<MRTableModel> {

	private static TableRowSorter<MRTableModel> instance = null;
	
	public static TableRowSorter<MRTableModel> getInstance() {
		
		if (instance == null) {
			
			instance = new MRTableSorter();
		}
		
		return instance;
	}
	
	private MRTableSorter() {
		
		super(MRTableModel.getInstance());
		
		this.setRowFilter(MRTableFilter.getInstance());
	}
}
