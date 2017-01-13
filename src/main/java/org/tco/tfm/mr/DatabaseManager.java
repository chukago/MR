 package org.tco.tfm.mr;

import java.util.ArrayList;
import java.util.Currency;

import org.tco.railroad.sqlite.SQLiteCreateTableQuery;
import org.tco.railroad.sqlite.SQLiteDatabase;
import org.tco.railroad.sqlite.SQLiteDeleteQuery;
import org.tco.railroad.sqlite.SQLiteInsertQuery;
import org.tco.railroad.sqlite.SQLiteResultSet;
import org.tco.railroad.sqlite.SQLiteSelectQuery;
import org.tco.railroad.sqlite.SQLiteTableField;
import org.tco.railroad.sqlite.SQLiteUpdateQuery;
import org.tco.railroad.sqlite.SQLiteWhereCondition;

public class DatabaseManager {

	public static class SimpleListRecord {
		
		public int id;
		public String value;
		
		public SimpleListRecord(int id, String value) {
			
			this.id = id;
			this.value = value;
		}
		
		@Override
		public String toString() {
			
			return value;
		}
	}
	
	public static class CrossLink implements Cloneable {
		
		public int id;
		public int first;
		public int second;
		
		public CrossLink(int id, int first, int second) {
			
			this.id = id;
			this.first = first;
			this.second = second;
		}
		
		@Override
		public CrossLink clone() {
			
			return new CrossLink(id, first, second);
		}
	}
	
	public static String WHS_TABLE_NAME = "WHS";
	public static String LOCATION_TABLE_NAME = "LOCATION";
	public static String SUPPLIER_TABLE_NAME = "SUPPLIER";
	public static String DISCIPLINE_TABLE_NAME = "DISCIPLINE";
	
	// Account data tables
	public static String USER_TABLE_NAME = "USER";
	public static String USER_ROLE_LINK_TABLE_NAME = "USER_ROLE_LINK";
	public static String USER_DISCIPLINE_LINK_TABLE_NAME = "USER_DISCIPLINE_LINK";

	public static String MR_TABLE_NAME = "MR";
	public static String MR_LOCATION_LINK_TABLE_NAME = "MR_LOCATION_LINK";
	public static String JOURNAL_TABLE_NAME = "JOURNAL";
	
	//private static String databaseName = "C:\\Users\\rnza\\Desktop\\temp.db";
	
	//private static String databaseName = "/home/crtc/Desktop/MR/temp.db";
	public static DatabaseManager _instance = null;
	
	private SQLiteDatabase _database;
	
	public static DatabaseManager getInstance() {
		
		if (_instance == null) {
			
			_instance = new DatabaseManager();
		}
		
		return _instance;
	}
	
	private DatabaseManager() {
		
		_database = new SQLiteDatabase(SettingsManager.getInstance().getDatabasePath());
	}
	
	public boolean checkDbConsistency() {
		
		if (!isTableExist(WHS_TABLE_NAME)) {
			
			if (!createSimpleListTable(WHS_TABLE_NAME)) {
				
				System.out.println("Cannot create whs table");
				return false;
			}
		}
		
		if (!isTableExist(SUPPLIER_TABLE_NAME)) {
			
			if (!createSimpleListTable(SUPPLIER_TABLE_NAME)) {
				
				System.out.println("Cannot create supplier table");
				return false;
			}
		}
		
		if (!isTableExist(DISCIPLINE_TABLE_NAME)) {
			
			if (!createDisciplineTable(DISCIPLINE_TABLE_NAME)) {
				
				System.out.println("Cannot create discipline table");
				return false;
			}
		}
		
		if (!isTableExist(MR_TABLE_NAME)) {
			
			if (!createMrTable(MR_TABLE_NAME)) {
				
				System.out.println("Cannot create MR table");
				return false;
			}
		}
		
		if (!isTableExist(JOURNAL_TABLE_NAME)) {
			
			if (!createJournalTable(JOURNAL_TABLE_NAME)) {
				
				System.out.println("Cannot create journal table");
				return false;
			}
		}
		
		if (!isTableExist(LOCATION_TABLE_NAME)) {
			
			if (!createLocationTable(LOCATION_TABLE_NAME)) {
				
				System.out.println("Cannot create location table");
				return false;
			}
		}
		
		if (!isTableExist(MR_LOCATION_LINK_TABLE_NAME)) {
			
			if (!createCrossLinkTable(MR_LOCATION_LINK_TABLE_NAME)) {
				
				System.out.println("Cannot create mr<->location table");
				return false;
			}
		}
		
		if (!isTableExist(USER_TABLE_NAME)) {
			
			if (!createUserTable(USER_TABLE_NAME)) {
				
				System.out.println("Cannot create user table");
				return false;
			}
		}
		
		if (!isTableExist(USER_ROLE_LINK_TABLE_NAME)) {
			
			if (!createCrossLinkTable(USER_ROLE_LINK_TABLE_NAME)) {
				
				System.out.println("Cannot create user<->role table");
				return false;
			}
		}
		
		if (!isTableExist(USER_DISCIPLINE_LINK_TABLE_NAME)) {
			
			if (!createCrossLinkTable(USER_DISCIPLINE_LINK_TABLE_NAME)) {
				
				System.out.println("Cannot create user<->discipline table");
				return false;
			}
		}
		
		return true;
	}
	
	public boolean createSimpleListTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("value", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.UNIQUE));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveSimpleListRecord(String tableName, SimpleListRecord record) {
		
		if (record.id != 0) {
			
			if (!updateSimpleListRecord(tableName, record)) {
				
				System.out.println("Cannot update list record");
				return false;
			}
			
			return true;
		}
		
		if (!addSimpleListRecord(tableName, record)) {
			
			System.out.println("Cannot add list record");
			return false;
		}
		
		return true;
	}
	
	private boolean addSimpleListRecord(String tableName, SimpleListRecord record) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("value", record.value)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		record.id = id;
		
		return true;
	}
	
	private boolean updateSimpleListRecord(String tableName, SimpleListRecord record) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (!q.addField("value", record.value)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(record.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public ArrayList<SimpleListRecord> findSimpleListRecord(String tableName, SimpleListRecord record) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("value");
		
		if (record != null) {
			
			if (record.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(record.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (record.value != null) {
				
				q.addWhereCondition(new SQLiteWhereCondition("value", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(record.value), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<SimpleListRecord> result = new ArrayList<SimpleListRecord>();
		
		while (rs.next()) {
			
			result.add(new SimpleListRecord(rs.getInt(1), rs.getString(2)));
		}
		
		return result;
	}
	
	public boolean createCrossLinkTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("first", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("second", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveCrossLink(CrossLink link, String tableName) {
		
		if (link.id == 0) {
			
			return addCrossLink(link, tableName);
		}
		
		
		return updateCrossLink(link, tableName);
	}
	
	public boolean addCrossLink(CrossLink link, String tableName) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("first", String.valueOf(link.first))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("second", String.valueOf(link.second))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		link.id = id;
		
		return true;
	}
	
	public boolean updateCrossLink(CrossLink link, String tableName) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (!q.addField("first", String.valueOf(link.first))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("second", String.valueOf(link.second))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(link.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean removeCrossLink(CrossLink link, String tableName) {
		

		SQLiteDeleteQuery q = new SQLiteDeleteQuery(tableName);
		
		if (link.id != 0) {
		
			q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
					String.valueOf(link.id)));
		}
		
		if (link.first != 0) {
			
			q.addWhereCondition(new SQLiteWhereCondition("first", SQLiteWhereCondition.ConditionType_t.EQUAL,
					String.valueOf(link.first)));
		}
		
		if (link.second != 0) {
			
			q.addWhereCondition(new SQLiteWhereCondition("second", SQLiteWhereCondition.ConditionType_t.EQUAL,
					String.valueOf(link.second)));
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public ArrayList<CrossLink> findCrossLink(CrossLink condition, String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("first");
		q.addFieldName("second");
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.first != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("first", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.first), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.second != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("second", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.second), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<CrossLink> result = new ArrayList<CrossLink>();
		
		while (rs.next()) {
			
			result.add(new CrossLink(rs.getInt(1), rs.getInt(2), rs.getInt(3)));
		}
		
		return result;
	}
	
	public boolean createMrTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("date", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("number", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("supplier", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("equipment", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("cost", SQLiteTableField.FieldType_t.REAL));
		q.addField(new SQLiteTableField("currency", SQLiteTableField.FieldType_t.TEXT));
		q.addField(new SQLiteTableField("tfm_stock", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("tco_whs", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("whs", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("cc", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("discipline", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("justification", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("delivery", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("eta", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("link", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("status", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("asset_number", SQLiteTableField.FieldType_t.TEXT));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveMrRecord(String tableName, Common.MRRecord_t record) {
		
		if (record.id != 0) {
			
			if (!updateMrRecord(tableName, record)) {
				
				return false;
			}
			
			return true;
		}
		
		if (!addMrRecord(tableName, record)) {
			
			return false;
		}
		
		return true;
	}
	
	private boolean addMrRecord(String tableName, Common.MRRecord_t record) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("date", String.valueOf(Utils.toUnixTime(record.reqDate)))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("number", String.valueOf(record.number))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("equipment", record.equipment)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("supplier", String.valueOf(record.supplier))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("cost", String.valueOf(record.cost))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (record.currency != null) {
		
			if (!q.addField("currency", record.currency.getCurrencyCode())) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.tfmStock != null) {
			
			if (!q.addField("tfm_stock", String.valueOf(record.tfmStock ? 1 : 0))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.tcoWhs != null) {
			
			if (!q.addField("tco_whs", String.valueOf(record.tcoWhs ? 1 : 0))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (!q.addField("whs", String.valueOf(record.whs))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("cc", String.valueOf(record.cc))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("discipline", String.valueOf(record.discipline))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("justification", record.justification)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (record.delivery != null) {
			
			if (!q.addField("delivery", String.valueOf(Utils.toUnixTime(record.delivery)))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.eta != null) {
			
			if (!q.addField("eta", String.valueOf(Utils.toUnixTime(record.eta)))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (!q.addField("link", record.link)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("status", String.valueOf(record.status.value()))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("asset_number", record.asset_number)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		record.id = id;
		
		return true;
	}
	
	private boolean updateMrRecord(String tableName, Common.MRRecord_t record) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (!q.addField("date", String.valueOf(Utils.toUnixTime(record.reqDate)))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("number", String.valueOf(record.number))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("equipment", record.equipment)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("supplier", String.valueOf(record.supplier))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("cost", String.valueOf(record.cost))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (record.currency != null) {
			
			if (!q.addField("currency", record.currency.getCurrencyCode())) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.tfmStock != null) {
			
			if (!q.addField("tfm_stock", String.valueOf(record.tfmStock ? 1 : 0))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.tcoWhs != null) {
			
			if (!q.addField("tco_whs", String.valueOf(record.tcoWhs ? 1 : 0))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (!q.addField("whs", String.valueOf(record.whs))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("cc", String.valueOf(record.cc))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("discipline", String.valueOf(record.discipline))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("justification", record.justification)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (record.delivery != null) {
			
			if (!q.addField("delivery", String.valueOf(Utils.toUnixTime(record.delivery)))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.eta != null) {
			
			if (!q.addField("eta", String.valueOf(Utils.toUnixTime(record.eta)))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (!q.addField("link", record.link)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("status", String.valueOf(record.status.value()))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("asset_number", record.asset_number)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(record.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Common.MRRecord_t> findMrRecord(Common.MRFindCondition_t condition, String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("date");
		q.addFieldName("number");
		q.addFieldName("equipment");
		q.addFieldName("supplier");
		q.addFieldName("whs");
		q.addFieldName("cost");
		q.addFieldName("currency");
		q.addFieldName("tfm_stock");
		q.addFieldName("tco_whs");
		q.addFieldName("cc");
		q.addFieldName("discipline");
		q.addFieldName("justification");
		q.addFieldName("delivery");
		q.addFieldName("eta");
		q.addFieldName("link");
		q.addFieldName("status");
		q.addFieldName("asset_number");
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.startDate != null) {
				
				q.addWhereCondition(new SQLiteWhereCondition("date", SQLiteWhereCondition.ConditionType_t.GREATER_OR_EQUAL,
						String.valueOf(Utils.toUnixTime(condition.startDate)), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.endDate != null) {
				
				q.addWhereCondition(new SQLiteWhereCondition("date", SQLiteWhereCondition.ConditionType_t.LESS_OR_EQUAL,
						String.valueOf(Utils.toUnixTime(condition.endDate)), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.number != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("number", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.number), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.whs != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("whs", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.whs), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.location != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("location", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.location), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.cc != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("cc", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.cc), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.discipline != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("discipline", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.discipline), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.status != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("status", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.status), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<Common.MRRecord_t> result = new ArrayList<Common.MRRecord_t>();
		
		while (rs.next()) {
			
			Common.MRRecord_t newRecord = new Common.MRRecord_t();
			newRecord.id = rs.getInt(1);
			newRecord.reqDate = Utils.fromUnixTime(rs.getInt(2));
			newRecord.number = rs.getInt(3);
			newRecord.equipment = rs.getString(4);
			newRecord.supplier = rs.getInt(5);
			newRecord.whs = rs.getInt(6);
			newRecord.cost = rs.getDouble(7);
			
			if (rs.getString(8) != null) {
			
				newRecord.currency = Currency.getInstance(rs.getString(8));
			}
			
			if (rs.getInt(9) != null) {
			
				newRecord.tfmStock = rs.getInt(9) > 0 ? true : false;
			}
			
			if (rs.getInt(10) != null) {
				
				newRecord.tcoWhs = rs.getInt(10) > 0 ? true : false;
			}
			
			newRecord.cc = rs.getInt(11);
			newRecord.discipline = rs.getInt(12);
			newRecord.justification = rs.getString(13);
			
			if (rs.getInt(14) != null) {
			
				newRecord.delivery = Utils.fromUnixTime(rs.getInt(14));
			}
			
			if (rs.getInt(15) != null) {
				
				newRecord.eta = Utils.fromUnixTime(rs.getInt(15));
			}
			
			newRecord.link = rs.getString(16);
			
			newRecord.status = Common.Status_t.fromValue(rs.getInt(17));
			
			newRecord.asset_number = rs.getString(18);
			
			result.add(newRecord);
		}
		
		for (Common.MRRecord_t r : result) {
			
			CrossLink locCondition = new CrossLink(0, r.id, 0);
			
			ArrayList<CrossLink> links = findCrossLink(locCondition,
					DatabaseManager.MR_LOCATION_LINK_TABLE_NAME);
			
			if (links == null) {
				
				System.out.println("Cannot load links for id=" + String.valueOf(r.id));
			}
			
			for (DatabaseManager.CrossLink l : links) {
				
				r.locations.add(l);
			}
			
			/*
			Common.EquipmentRec_t equipCondition = new Common.EquipmentRec_t();
			equipCondition.workId = r.id;
			
			r.equipment = this.findEquipment(equipCondition, EQUIPMENT_TABLE_NAME);
			*/
			
			// Journal
			Common.JournalRec_t journalCondition = new Common.JournalRec_t();
			journalCondition.workid = r.id;
			
			r.journal = findJournal(journalCondition, JOURNAL_TABLE_NAME);
		}
		
		return result;
	}
	
	private boolean createDisciplineTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("name", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.UNIQUE));
		q.addField(new SQLiteTableField("abbreviation", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.UNIQUE));
		q.addField(new SQLiteTableField("counter", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveDisciplineRecord(String tableName, Common.Discipline_t record) {
		
		if (record.id != 0) {
			
			if (!updateDisciplineRecord(tableName, record)) {
				
				return false;
			}
			
			return true;
		}
		
		if (!addDisciplineRecord(tableName, record)) {
			
			return false;
		}
		
		return true;
	}
	
	private boolean addDisciplineRecord(String tableName, Common.Discipline_t record) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("abbreviation", record.abbreviation)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("counter", String.valueOf(record.counter))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		record.id = id;
		
		return true;
	}
	
	private boolean updateDisciplineRecord(String tableName, Common.Discipline_t record) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (record.name != null && !record.name.isEmpty()) {
		
			if (!q.addField("name", record.name)) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.abbreviation != null && !record.abbreviation.isEmpty()) {
		
			if (!q.addField("abbreviation", record.abbreviation)) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		if (record.counter >= 0) {
			
			if (!q.addField("counter", String.valueOf(record.counter))) {
				
				System.out.println("Cannot add field to query");
				return false;
			}
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(record.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Common.Discipline_t> findDiscipline(Common.Discipline_t condition, String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("name");
		q.addFieldName("abbreviation");
		q.addFieldName("counter");
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.name != null) {
				
				q.addWhereCondition(new SQLiteWhereCondition("date", SQLiteWhereCondition.ConditionType_t.GREATER_OR_EQUAL,
						condition.name, SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.abbreviation != null) {
				
				q.addWhereCondition(new SQLiteWhereCondition("date", SQLiteWhereCondition.ConditionType_t.LESS_OR_EQUAL,
						condition.abbreviation, SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<Common.Discipline_t> result = new ArrayList<Common.Discipline_t>();
		
		while (rs.next()) {
			
			Common.Discipline_t newRecord = new Common.Discipline_t(rs.getInt(1), rs.getString(2),
					rs.getString(3), rs.getInt(4));
			
			result.add(newRecord);
		}
		
		return result;
	}
	
	private boolean createJournalTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("work", SQLiteTableField.FieldType_t.INTEGER));
		q.addField(new SQLiteTableField("date", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("position", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("name", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("status", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("comment", SQLiteTableField.FieldType_t.TEXT));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveJournalRecord(String tableName, Common.JournalRec_t record) {
		
		if (record.id != 0) {
			
			if (!updateJournalRecord(tableName, record)) {
				
				return false;
			}
			
			return true;
		}
		
		if (!addJournalRecord(tableName, record)) {
			
			return false;
		}
		
		return true;
	}
	
	private boolean addJournalRecord(String tableName, Common.JournalRec_t record) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("work", String.valueOf(record.workid))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("date", String.valueOf(Utils.toUnixTime(record.date)))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("position", record.position)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("status", String.valueOf(record.status.value()))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("comment", record.comment)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		record.id = id;
		
		return true;
	}
	
	private boolean updateJournalRecord(String tableName, Common.JournalRec_t record) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (!q.addField("work", String.valueOf(record.workid))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("date", String.valueOf(Utils.toUnixTime(record.date)))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("position", record.position)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("status", String.valueOf(record.status.value()))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("comment", record.comment)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(record.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Common.JournalRec_t> findJournal(Common.JournalRec_t condition, String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("work");
		q.addFieldName("date");
		q.addFieldName("position");
		q.addFieldName("name");
		q.addFieldName("status");
		q.addFieldName("comment");
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.workid != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("work", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.workid), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<Common.JournalRec_t> result = new ArrayList<Common.JournalRec_t>();
		
		while (rs.next()) {
			
			Common.JournalRec_t j = new Common.JournalRec_t();
			j.id = rs.getInt(1);
			j.workid = rs.getInt(2);
			j.date = Utils.fromUnixTime(rs.getInt(3));
			j.position = rs.getString(4);
			j.name = rs.getString(5);
			j.status = Common.Status_t.fromValue(rs.getInt(6));
			j.comment = rs.getString(7);
			
			result.add(j);
		}
		
		return result;
	}
	
	public boolean createLocationTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("name", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("parent", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveLocationRecord(String tableName, Common.LocationRec_t record) {
		
		if (record.id != 0) {
			
			if (!updateLocationRecord(tableName, record)) {
				
				System.out.println("Cannot update location record");
				return false;
			}
			
			return true;
		}
		
		if (!addLocationRecord(tableName, record)) {
			
			System.out.println("Cannot add location record");
			return false;
		}
		
		return true;
	}
	
	private boolean addLocationRecord(String tableName, Common.LocationRec_t record) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("parent", String.valueOf(record.parent))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		record.id = id;
		
		return true;
	}
	
	private boolean updateLocationRecord(String tableName, Common.LocationRec_t record) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("parent", String.valueOf(record.parent))) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(record.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Common.LocationRec_t> findLocation(Common.LocationRec_t condition, String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("name");
		q.addFieldName("parent");
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.parent != -1) {
				
				q.addWhereCondition(new SQLiteWhereCondition("parent", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.parent), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<Common.LocationRec_t> result = new ArrayList<Common.LocationRec_t>();
		
		while (rs.next()) {
			
			result.add(new Common.LocationRec_t(rs.getInt(1), rs.getString(2), rs.getInt(3)));
		}
		
		return result;
	}
	
	private boolean createUserTable(String tableName) {
		
		SQLiteCreateTableQuery q = new SQLiteCreateTableQuery(tableName);
		
		q.addField(new SQLiteTableField("id", SQLiteTableField.FieldType_t.INTEGER,
				SQLiteTableField.ConstraintType_t.AUTOINCREMENT));
		q.addField(new SQLiteTableField("login", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.UNIQUE));
		q.addField(new SQLiteTableField("name", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.UNIQUE));
		q.addField(new SQLiteTableField("position", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		q.addField(new SQLiteTableField("email", SQLiteTableField.FieldType_t.TEXT,
				SQLiteTableField.ConstraintType_t.NOT_NULL));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean saveUserRecord(Common.UserData_t record, String tableName) {
		
		if (record.id != 0) {
			
			if (!updateUserRecord(record, tableName)) {
				
				System.out.println("Cannot update location record");
				return false;
			}
			
			return true;
		}
		
		if (!addUserRecord(record, tableName)) {
			
			System.out.println("Cannot add location record");
			return false;
		}
		
		return true;
	}
	
	private boolean addUserRecord(Common.UserData_t record, String tableName) {
		
		SQLiteInsertQuery q = new SQLiteInsertQuery(tableName);
		
		if (!q.addField("login", record.login)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("position", record.position)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("email", record.email)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!_database.transaction()) {
			
			System.out.println("Cannot start transaction");
			return false;
		}
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		int id = getLastRowId(tableName);
		
		if (!_database.commit()) {
			
			System.out.println("Cannot commit transaction");
			return false;
		}
		
		record.id = id;
		
		return true;
	}
	
	private boolean updateUserRecord(Common.UserData_t record, String tableName) {
		
		SQLiteUpdateQuery q = new SQLiteUpdateQuery(tableName);
		
		if (!q.addField("login", record.login)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("name", record.name)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("position", record.position)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		if (!q.addField("email", record.email)) {
			
			System.out.println("Cannot add field to query");
			return false;
		}
		
		q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
				String.valueOf(record.id)));
		
		if (!_database.execute(q)) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		return true;
	}
	
	public boolean deleteUserRecord(Common.UserData_t condition, String tableName) {
		
		SQLiteDeleteQuery q = new SQLiteDeleteQuery(tableName);
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.login != null && !condition.login.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("login", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.login), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.name != null && !condition.name.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("name", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.name), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.position != null && !condition.position.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("position", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.position), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.email != null && !condition.email.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("email", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.email), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		return _database.execute(q);
	}
	
	public ArrayList<Common.UserData_t> findUser(Common.UserData_t condition, String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery(tableName);
		
		q.addFieldName("id");
		q.addFieldName("login");
		q.addFieldName("name");
		q.addFieldName("position");
		q.addFieldName("email");
		
		if (condition != null) {
			
			if (condition.id != 0) {
				
				q.addWhereCondition(new SQLiteWhereCondition("id", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.id), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.login != null && !condition.login.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("login", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.login), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.name != null && !condition.name.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("name", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.name), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.position != null && !condition.position.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("position", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.position), SQLiteWhereCondition.LogicOperation_t.AND));
			}
			
			if (condition.email != null && !condition.email.isEmpty()) {
				
				q.addWhereCondition(new SQLiteWhereCondition("email", SQLiteWhereCondition.ConditionType_t.EQUAL,
						String.valueOf(condition.email), SQLiteWhereCondition.LogicOperation_t.AND));
			}
		}
		
		SQLiteResultSet rs = _database.execute(q);
		
		if (rs == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return null;
		}
		
		ArrayList<Common.UserData_t> result = new ArrayList<Common.UserData_t>();
		
		while (rs.next()) {
			
			Common.UserData_t newUser = new Common.UserData_t();
			
			newUser.id = rs.getInt(1);
			newUser.login = rs.getString(2);
			newUser.name = rs.getString(3);
			newUser.position = rs.getString(4);
			newUser.email = rs.getString(5);
			
			result.add(newUser);
		}
		
		for (Common.UserData_t u : result) {
			
			CrossLink c = new CrossLink(0, u.id, 0);
			
			u.roles = this.findCrossLink(c, USER_ROLE_LINK_TABLE_NAME);
			u.disciplines = this.findCrossLink(c, USER_DISCIPLINE_LINK_TABLE_NAME);
		}
		
		return result;
	}
	
	private boolean isTableExist(String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery("sqlite_master");
		
		q.addWhereCondition(new SQLiteWhereCondition("type", SQLiteWhereCondition.ConditionType_t.EQUAL, "table"));
		q.addWhereCondition(new SQLiteWhereCondition("name", SQLiteWhereCondition.ConditionType_t.EQUAL,
				tableName, SQLiteWhereCondition.LogicOperation_t.AND));
		
		SQLiteResultSet r = _database.execute(q);
		
		if (r == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return false;
		}
		
		if (r.getFetchSize() != 0) {
			
			return true;
		}
		
		return false;
	}
	
	private int getLastRowId(String tableName) {
		
		SQLiteSelectQuery q = new SQLiteSelectQuery("sqlite_sequence");
		
		q.addFieldName("seq");
		
		q.addWhereCondition(new SQLiteWhereCondition("name", SQLiteWhereCondition.ConditionType_t.EQUAL, tableName));
		
		SQLiteResultSet r = _database.execute(q);
		
		if (r == null) {
			
			System.out.println("Cannot execute query");
			System.out.println(q.getQueryString());
			return 0;
		}
		
		if (!r.next())
		{
			return 0;
		}
		
		return r.getInt(1);
	}
}
