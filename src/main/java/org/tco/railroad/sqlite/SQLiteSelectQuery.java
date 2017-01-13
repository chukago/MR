package org.tco.railroad.sqlite;

import java.util.ArrayList;

public class SQLiteSelectQuery {

	private String _tableName = null;
	private ArrayList<String> _fieldNames = new ArrayList<String>();
	private ArrayList<SQLiteWhereCondition> _conditions = new ArrayList<SQLiteWhereCondition>();
	
	public SQLiteSelectQuery(String tableName) {
		 
		if (tableName == null || tableName.isEmpty()) {
			
			System.out.println("Wrong table name");
			return;
		}
		
		_tableName = tableName;
	}
	
	public boolean addFieldName(String fieldName) {
		
		if (fieldName == null || fieldName.isEmpty())
		{
			System.out.println("Wring field name");
			return false;
		}
		
		for (String fn : _fieldNames) {
			
			if (fn.compareToIgnoreCase(fieldName) == 0) {
				
				System.out.println("Field is already in the list");
				return true;
			}
		}
		
		_fieldNames.add(fieldName);
		
		return true;
	}
	
	public void addWhereCondition(SQLiteWhereCondition condition) {
		
		_conditions.add(condition);
	}
	
	public String getQueryString() {
		
		if (_tableName == null || _tableName.isEmpty()) {
			
			System.out.println("Wrong table name");
			return null;
		}
		
		String queryString = "SELECT ";
		
		if (_fieldNames.isEmpty()) {
			
			queryString += "*";
		}
		else {
			
			for (int i = 0; i < _fieldNames.size(); ++i) {
				
				queryString += _fieldNames.get(i);
				
				if (i != _fieldNames.size() - 1) {
					
					queryString += ",";
				}
			}
		}
		
		queryString += " FROM " + _tableName;
		
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
