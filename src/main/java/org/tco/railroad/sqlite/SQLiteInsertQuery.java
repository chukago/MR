package org.tco.railroad.sqlite;

import java.util.ArrayList;

import org.tco.railroad.sqlite.SQLiteProperty;

public class SQLiteInsertQuery {
	
	private String _tableName = null;
	private ArrayList<SQLiteProperty<?>> _fields = new ArrayList<SQLiteProperty<?>>();
	
	public SQLiteInsertQuery(String tableName) {
		
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
	
	public String getQueryString() {
		
		if (_tableName.isEmpty()) {
			
			System.out.println("Table name is empty");
			return null;
		}
		
		if (_fields.isEmpty()) {
			
			System.out.println("Property list is empty");
			return null;
		}
		
		String queryString = "INSERT INTO " + _tableName + "(";
		
		for(int i = 0; i < _fields.size(); ++i) {
			
			if (_fields.get(i).name.isEmpty()) {
				
				System.out.println("Property name is empty");
				return null;
			}
			
			queryString += _fields.get(i).name;
			
			if (i != _fields.size() - 1) {
				
				queryString += ", ";
			}
		}
		
		queryString += ") VALUES(";
		
		for(int i = 0; i < _fields.size(); ++i) {
			
			if (_fields.get(i).value == null){
				
				queryString += "NULL";
			}
			else {
			
				queryString += "'" + _fields.get(i).value.toString().replace("'", "''") + "'";
			}
			
			if (i != _fields.size() - 1) {
				
				queryString += ",";
			}
		}
		
		queryString += ")";
		
		return queryString;
	}
}
