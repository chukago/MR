package org.tco.railroad.sqlite;

public class SQLiteWhereCondition {

	public enum LogicOperation_t {
		
		NONE,
		AND,
		OR
	}
	
	public enum ConditionType_t {
		
		LESS,
		LESS_OR_EQUAL,
		EQUAL,
		NOT_EQUAL,
		GREATER_OR_EQUAL,
		GREATER
	}
	
	private LogicOperation_t _logicOperation = LogicOperation_t.NONE;
	private String _fieldName = null;
	private ConditionType_t _condition = null;
	private String _value = null;
	
	/*
	 * If value==null then NULL value will be inserted in the result query
	 * That is why we don't check "value" parameter
	 */
	public SQLiteWhereCondition(String fieldName, ConditionType_t condition, String value) {
		
		if (fieldName == null || fieldName.isEmpty()) {
			
			System.out.println("Wrong field name");
			return;
		}
		
		_fieldName = fieldName;
		_condition = condition;
		_value = value;
		_logicOperation = LogicOperation_t.AND;
	}
	
	public SQLiteWhereCondition(String fieldName, ConditionType_t condition, String value,
			LogicOperation_t operation) {
		
		if (fieldName == null || fieldName.isEmpty()) {
			
			System.out.println("Wrong field name");
			return;
		}
		
		_fieldName = fieldName;
		_condition = condition;
		_value = value;
		_logicOperation = operation;
	}
	
	public String getQueryString(boolean exceptLogicOp) {
		
		if (_fieldName == null || _fieldName.isEmpty()) {
			
			System.out.println("Wrong field name");
			return null;
		}
		
		String queryString = new String();
		
		if (!exceptLogicOp)
		{
			switch(_logicOperation) {
			
			case NONE:
				break;
				
			case AND:
				queryString += "AND ";
				break;
				
			case OR:
				queryString += "OR ";
				break;
				
			default:
				System.out.println("Wrong logic operation");
				return null;
			}
		}
		
		queryString += _fieldName;
		
		switch(_condition) {
		case LESS:
			queryString += "<";
			break;
			
		case LESS_OR_EQUAL:
			queryString += "<=";
			break;
			
		case EQUAL:
			queryString += "=";
			break;
			
		case NOT_EQUAL:
			queryString += "<>";
			break;
			
		case GREATER_OR_EQUAL:
			queryString += ">=";
			break;
			
		case GREATER:
			queryString += ">";
			break;
			
		default:
			System.out.println("Undefined condition");
			return null;
		}
		
		if (_value == null) {
			
			queryString += "NULL";
		}
		else {
			
			queryString += "'" + _value + "'";
		}
		
		return queryString;
	}
}
