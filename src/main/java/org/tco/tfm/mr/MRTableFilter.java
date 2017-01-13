package org.tco.tfm.mr;

import java.util.ArrayList;

import javax.swing.RowFilter;

import org.tco.tfm.mr.Common.Discipline_t;
import org.tco.tfm.mr.Common.Status_t;

public class MRTableFilter extends RowFilter<MRTableModel, Object> {
	
	private static MRTableFilter instance = null;
	
	public static MRTableFilter getInstance() {
		
		if (instance == null) {
			
			instance = new MRTableFilter();
		}
		
		return instance;
	}
	
	private MRTableFilter() {
		
	}
	
	public static class FilterData_t {
		
		public ArrayList<Discipline_t> disciplineList;
		public ArrayList<Status_t> statusList;
	}
	
	private FilterData_t filterData;
	
	@Override
	public boolean include(Entry<? extends MRTableModel, ? extends Object> entry) {
		
		if (filterData == null) {
			
			return true;
		}
		
		MRTableModel model = entry.getModel();
		Integer row = (Integer)entry.getIdentifier();
		
		if (!processStatus(model.getDataAt(row).status)) {
			
			return false;
		}
		
		if (!processDiscipline(model.getDataAt(row).discipline)) {
			
			return false;
		}
		
		return true;
	}
	
	public void setFilterData(FilterData_t sortData) {
		
		this.filterData = sortData;
		
		MRTableSorter.getInstance().sort();
	}

	private boolean processStatus(Common.Status_t status) {
		
		if (filterData == null || filterData.statusList == null || filterData.statusList.size() == 0) {
			
			return true;
		}
		
		return filterData.statusList.contains(status);
	}
	
	private boolean processDiscipline(int discipline) {
		
		if (filterData == null || filterData.disciplineList == null || filterData.disciplineList.size() == 0) {
			
			return true;
		}
		
		for (Discipline_t d : filterData.disciplineList) {
			
			if (d.id == discipline) {
				
				return true;
			}
		}
		
		return false;
	}
}
