package org.tco.railroad.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteResultSet {
	
	private List<String> _columnNames = new ArrayList<String>();
	private List<List<Object>> _data = new ArrayList<List<Object>>();
	
	private int _cursorPosition = -1;
	
	public SQLiteResultSet(ResultSet s) {
		
		if (s == null) {
			
			return;
		}
		
		try {
			
			for (int i = 0; i < s.getMetaData().getColumnCount(); ++i) {
				
				_columnNames.add(s.getMetaData().getColumnName(i + 1));
			}
			
			while(s.next()) {
				
				ArrayList<Object> element = new ArrayList<Object>();
				
				for (int i = 0; i < _columnNames.size(); ++i) {
				
					element.add(s.getObject(i+1));
				}
				
				_data.add(element);
			}
		}
		catch (SQLException e) {
			
			_columnNames.clear();
			e.printStackTrace();
			return;
		}
	}
	
	public int getFetchSize() {
		
		return _data.size();
	}
	
	public boolean next() {
		
		_cursorPosition += 1;
		
		if (_cursorPosition < _data.size()) {
			
			return true;
		}
		
		return false;
	}
	
	public Integer getInt(int columnIndex) {
		
		if (columnIndex < 1 || columnIndex > _columnNames.size()) {
			
			return null;
		}
		
		return (Integer)_data.get(_cursorPosition).get(columnIndex - 1);
	}
	
	public Double getDouble(int columnIndex) {
		
		if (columnIndex < 1 || columnIndex > _columnNames.size()) {
			
			return null;
		}
		
		return (Double)_data.get(_cursorPosition).get(columnIndex - 1);
	}
	
	public String getString(int columnIndex) {
		
		if (columnIndex < 1 || columnIndex > _columnNames.size()) {
			
			return null;
		}
		
		return (String)_data.get(_cursorPosition).get(columnIndex - 1);
	}
}
