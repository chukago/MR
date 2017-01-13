package org.tco.railroad.sqlite;

import java.util.ArrayList;

public class SQLiteUpdateQuery {
	
	private String _tableName = null;
	private ArrayList<SQLiteProperty<?>> _fields = new ArrayList<SQLiteProperty<?>>();
	private ArrayList<SQLiteWhereCondition> _conditions = new ArrayList<SQLiteWhereCondition>();

	public SQLiteUpdateQuery(String tableName) {
		
		_tableName = tableName;
	}
	
	public boolean addField(String fieldName, String value) {
		
		if (fieldName == null || fieldName.isEmpty()) {
			
			System.out.println("Wrong field name");
			return false;
		}
		
		/*
		if (value == null || value.isEmpty()) {
			
			System.out.println("Wrong field value");
			return false;
		}
		*/
		
		for (SQLiteProperty<?> f : _fields) {
			
			if (f.name.compareToIgnoreCase(fieldName) == 0) {
				
				System.out.println("Duplicate fields");
				return false;
			}
		}
		
		SQLiteProperty<String> f = new SQLiteProperty<String>(fieldName, value);
		_fields.add(f);
		
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
		
		String queryString = "UPDATE " + _tableName + " SET ";
		
		if (_fields.isEmpty()) {
			
			return null;
		}
	
		for (int i = 0; i < _fields.size(); ++i) {
			
			queryString += _fields.get(i).name + "="; 
			
			if (_fields.get(i) == null) {
				
				queryString += "NULL";
			}
			else {
				
				queryString += "'" + _fields.get(i).value.toString().replace("'", "''") + "'";
			}
			
			if (i != _fields.size() - 1) {
				
				queryString += ",";
			}
		}
		
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
