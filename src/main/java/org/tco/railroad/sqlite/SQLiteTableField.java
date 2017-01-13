package org.tco.railroad.sqlite;

public class SQLiteTableField {

	public enum FieldType_t {
		
		NULL,
		INTEGER,
		REAL,
		TEXT,
		BLOB
	}
	
	public enum ConstraintType_t {
		
		PRIMARY_KEY,
		NOT_NULL,
		UNIQUE,
		AUTOINCREMENT
	}
	
	private String _name = null;
	private FieldType_t _type = null;
	private ConstraintType_t _constraint = null;
	
	public SQLiteTableField(String name) {
		
		_name = name;
	}
	
	public SQLiteTableField(String name, FieldType_t type) {
		
		_name = name;
		_type = type;
	}
	
	public SQLiteTableField(String name, FieldType_t type, ConstraintType_t constraint) {
		
		_name = name;
		_type = type;
		_constraint = constraint;
	}
	
	public void setType(FieldType_t type) {
		
		_type = type;
	}
	
	public void setConstraint(ConstraintType_t constraint) {
		
		_constraint = constraint;
	}
	
	public String getQueryString() {
		
		if (_name == null || _name.isEmpty()) {
			
			System.out.println("Wrong field name");
			return null;
		}
		
		String queryString = _name;
		
		if (_type != null) {
			
			switch (_type) {
			
			case NULL:
				
				System.out.println("NULL cannot be used in query string");
				return null;
				
			case INTEGER:
				queryString += " INTEGER";
				break;
				
			case REAL:
				queryString += " REAL";
				break;
				
			case TEXT:
				queryString += " TEXT";
				break;
				
			case BLOB:
				queryString += " BLOB";
				break;
				
			default:
				System.out.println("Unknown field type");
				return null;
			}
		}
		
		if (_constraint != null) {
			
			switch(_constraint) {
			
			case PRIMARY_KEY:
				queryString += " PRIMARY KEY";
				break;
				
			case NOT_NULL:
				queryString += " NOT NULL";
				break;
				
			case UNIQUE:
				queryString += " UNIQUE";
				break;
				
			case AUTOINCREMENT:
				queryString += " PRIMARY KEY AUTOINCREMENT";
				break;
				
			default:
				System.out.println("Unknown field constraint");
				return null;
			}
		}
		
		return queryString;
	}
}
