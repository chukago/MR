package org.tco.railroad.sqlite;

import java.util.ArrayList;

public class SQLiteCreateTableQuery {
	
	private String _tableName = null;
	private ArrayList<SQLiteTableField> _fields = new ArrayList<SQLiteTableField>();
	private ArrayList<SQLiteTableConstraint> _constraints = new ArrayList<SQLiteTableConstraint>();
	
	public SQLiteCreateTableQuery(String tableName) {
		
		if (tableName == null || tableName.isEmpty())
		{
			System.out.println("Wrong table name");
			return;
		}
		
		_tableName = tableName;
	}
	
	public void addField(SQLiteTableField field) {
		
		_fields.add(field);
	}
	
	public void addConstraint(SQLiteTableConstraint c) {

		_constraints.add(c);
	}
	
	public String getQueryString() {
		
		if (_tableName == null || _tableName.isEmpty()) {
			
			System.out.println("Wrong table name");
			return null;
		}
		
		if (_fields.isEmpty()) {
			
			System.out.println("Field list is empty");
			return null;
		}
		
		String queryString = "CREATE TABLE " + _tableName + "(";
		
		for (int i = 0; i < _fields.size(); ++ i) {
			
			String fieldDescription = _fields.get(i).getQueryString();
			
			if (fieldDescription == null) {
				
				System.out.println("Wrong field description");
				return null;
			}
			
			queryString += fieldDescription;
			
			if (i != _fields.size()-1) {
				
				queryString += ",";
			}
		}
		
		if (!_constraints.isEmpty()) {
			
			for (int i = 0; i < _constraints.size(); ++i) {
			
				queryString += "," + _constraints.get(i).getQueryString();
			}
		}
		
		queryString += ")";
		
		return queryString;
	}
}
