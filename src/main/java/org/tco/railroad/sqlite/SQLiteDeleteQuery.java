package org.tco.railroad.sqlite;

import java.util.ArrayList;

public class SQLiteDeleteQuery {

	private String _tableName = null;
	private ArrayList<SQLiteWhereCondition> _conditions = new ArrayList<SQLiteWhereCondition>();

	public SQLiteDeleteQuery(String tableName) {
		
		_tableName = tableName;
	}
	
	public void addWhereCondition(SQLiteWhereCondition condition) {
		
		_conditions.add(condition);
	}
	
	public String getQueryString() {
		
		if (_tableName == null || _tableName.isEmpty()) {
			
			System.out.println("Wrong table name");
			return null;
		}
		
		String queryString = "DELETE FROM " + _tableName;
		
		if (!_conditions.isEmpty()) {
			
			queryString += " WHERE ";
			
			for (int i = 0; i < _conditions.size(); ++i) {
				
				String conditionString;
				
				if (i == 0) {
					
					conditionString = _conditions.get(i).getQueryString(true);
				}
				else {
					
					conditionString = _conditions.get(i).getQueryString(false);
				}
				
				
				if (conditionString == null) {
					
					System.out.println("Wrong condition format");
					return null;
				}
				
				queryString += conditionString;
				
				if (i != _conditions.size() - 1) {
					
					queryString += " ";
				}
			}
		}
		
		return queryString;
	}
}
