package org.tco.railroad.sqlite;

import java.util.ArrayList;

public class SQLiteTableConstraintUnique implements SQLiteTableConstraint{

	private ArrayList<String> _columns = new ArrayList<String>();
	
	public SQLiteTableConstraintUnique(String columName) {
		
		_columns.add(columName);
	}
	
	public SQLiteTableConstraintUnique(ArrayList<String> columnNames) {
		
		_columns.addAll(columnNames);
	}

	public String getQueryString() {
		
		String queryString = "UNIQUE (";
		
		for (int i = 0; i < _columns.size(); ++i) {
			
			queryString += _columns.get(i);
			
			if (i != _columns.size() - 1) {
				
				queryString += ",";
			}
		}
		
		queryString += ")";
		
		return queryString;
	}
}
