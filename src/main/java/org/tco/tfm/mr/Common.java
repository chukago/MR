package org.tco.tfm.mr;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

import org.tco.tfm.mr.DatabaseManager.CrossLink;
import org.tco.tfm.mr.DatabaseManager.SimpleListRecord;

public class Common {

	public static SimpleListRecordModel supplierManager = 
			new SimpleListRecordModel(DatabaseManager.SUPPLIER_TABLE_NAME);
	
	public static SimpleListRecordModel whseManager = new  
			SimpleListRecordModel(DatabaseManager.WHS_TABLE_NAME);
	
	public static String MR_DOC_NUMBER_PREFIX = "TEN-TFM-";
	
	public static enum Role_t {
		
		REQUESTOR(1, "Requestor"),
		FIRST_APPROVER(2, "First Approver"),
		SECOND_APPROVER(3, "Second Approver"),
		THIRD_APPROVER(4, "Third Approver"),
		ADMINISTRATOR(Integer.MAX_VALUE, "Administrator");
		
		private int value;
		private String stringRep;
		
		private Role_t(int value, String stringRep) {
			
			this.value = value;
			this.stringRep = stringRep;
		}
		
		public int value() {
			
			return value;
		}
		
		@Override
		public String toString() {
			
			return stringRep;
		}
		
		static Role_t fromValue(int value) {
			
			for (Role_t v : Role_t.values()) {
				
				if (v.value == value) {
					
					return v;
				}
			}
			
			return null;
		}
	}
	
	public static enum Status_t {
		
		NONE(0, "No status"),
		REQUEST(1, "Request"),
		REQUESTOR_REVIEW(2, "Requestor review"),
		FIRST_APPROVED(3, "First approved"),
		FIRST_REJECTED(4, "Rejected by first approver"),
		FIRST_REVIEW(5, "First approver review"),
		SECOND_APPROVED(6, "Second approved"),
		SECOND_REJECTED(7, "Rejected by second approver"),
		SECOND_REVIEW(8, "Second approver review"),
		THIRD_APPROVED(9, "Wait delivery"),
		THIRD_REJECTED(10, "Rejected by third approver"),
		DELIVERED(11, "Delivered");
		
		private int value;
		private String stringRep;
		
		private Status_t(int value, String stringRep) {
			
			this.value = value;
			this.stringRep = stringRep;
		}
		
		public int value() {
			
			return value;
		}
		
		public String getStringRep() {
			
			return stringRep;
		}
		
		@Override
		public String toString() {
			
			return stringRep;
		}
		
		static Status_t fromValue(int value) {
			
			for (Status_t v : Status_t.values()) {
				
				if (v.value == value) {
					
					return v;
				}
			}
			
			return null;
		}
	}
	
	public static class LocationRec_t implements Cloneable {
		
		public int id;
		public String name;
		public int parent = -1; // -1 - because parent can be 0 (root node)
		
		public LocationRec_t() {
			
		}
		
		public LocationRec_t(int id, String name, int parent) {
			
			this.id = id;
			this.name = name;
			this.parent = parent;
		}
		
		@Override
		public String toString() {
			
			return name;
		}
		
		@Override
		public LocationRec_t clone() {
			
			LocationRec_t newObject = new LocationRec_t();
			newObject.id = id;
			newObject.name = name == null ? null : new String(name);
			newObject.parent = parent;
			
			return newObject;
		}
	}
	
	public static class JournalRec_t implements Cloneable {
		
		public int id;
		public int workid;
		public Date date;
		public String position;
		public String name;
		public Status_t status;
		public String comment;
		
		@Override
		public JournalRec_t clone() {
			
			JournalRec_t newObject = new JournalRec_t();
			newObject.id = id;
			newObject.workid = workid;
			newObject.date = date == null ? null : (Date) date.clone();
			newObject.position = position == null ? null : new String(position);
			newObject.name = name == null ? null : new String(name);
			newObject.status = status;
			newObject.comment = comment == null ? null : new String(comment);
			
			return newObject;
		}
	}
	
	
	public static class MRRecord_t implements Cloneable{
		
		public int id;
		public Date reqDate;
		public int number;
		public String equipment;
		public int whs;
		public int cc;
		public int discipline;
		public int supplier;
		public double cost;
		public Currency currency;
		public Boolean tfmStock;
		public Boolean tcoWhs;
		public String justification;
		public String link;
		public Status_t status;
		public Date eta;
		public Date delivery;
		public String comment;
		public String asset_number;
		public ArrayList<CrossLink> locations = new ArrayList<CrossLink>();
		//public ArrayList<EquipmentRec_t> equipment = new ArrayList<EquipmentRec_t>();
		public ArrayList<JournalRec_t> journal = new ArrayList<JournalRec_t>();
		
		@Override
		public MRRecord_t clone() {
			
			MRRecord_t newObject = new MRRecord_t();
			newObject.id = id;
			newObject.reqDate = reqDate == null ? null : (Date) reqDate.clone();
			newObject.number = number;
			newObject.equipment = equipment == null ? null : new String(equipment);
			newObject.whs = whs;
			newObject.cc = cc;
			newObject.discipline = discipline;
			newObject.supplier = supplier;
			newObject.cost = cost;
			newObject.currency = currency == null ? null : Currency.getInstance(currency.getCurrencyCode());
			newObject.tfmStock = tfmStock == null ? null : tfmStock.booleanValue();
			newObject.tcoWhs = tcoWhs == null ? null : tcoWhs.booleanValue();
			newObject.justification = justification == null ? null : new String(justification);
			newObject.link = link == null ? null : new String(link);
			newObject.status = status;
			newObject.eta = eta == null ? null : (Date) eta.clone();
			newObject.delivery = delivery == null ? null : (Date) delivery.clone();
			newObject.comment = comment == null ? null : new String(comment);
			newObject.asset_number = asset_number == null ? null : new String(asset_number);
			
			newObject.locations = new ArrayList<CrossLink>();
			
			for (CrossLink l : locations) {
			
				newObject.locations.add(l.clone());
			}
			
			newObject.journal = new ArrayList<JournalRec_t>();
			
			for (JournalRec_t j : journal) {
			
				newObject.journal.add(j.clone());
			}
			
			return newObject;
		}
	}
	
	public static class MRFindCondition_t {
		
		public int id;
		public Date startDate;
		public Date endDate;
		public int number;
		public int whs;
		public int location;
		public int cc;
		public int discipline;
		public int status;
	}
	
	public static class Discipline_t {
		
		public int id;
		public String name;
		public String abbreviation;
		public int counter = -1;
		
		public Discipline_t() {
			
		}
		
		public Discipline_t(int id, String name, String abbreviation) {
			
			this.id = id;
			this.name = name;
			this.abbreviation = abbreviation;
			this.counter = 0;
		}
		
		public Discipline_t(int id, String name, String abbreviation, int counter) {
			
			this.id = id;
			this.name = name;
			this.abbreviation = abbreviation;
			this.counter = counter;
		}
		
		@Override
		public String toString() {
			
			return name;
		}
	}
	
	public static class UserData_t implements Cloneable, Comparable<UserData_t> {
		
		public int id = 0;
		public String login;
		public String name;
		public String position;
		public String email;
		
		ArrayList<CrossLink> roles = new ArrayList<CrossLink>();
		ArrayList<CrossLink> disciplines = new ArrayList<CrossLink>();
		
		@Override
		public UserData_t clone() {
			
			UserData_t newObject = new UserData_t();
			newObject.id = id;
			newObject.login = login;
			newObject.name = name;
			newObject.position = position;
			newObject.email = email;
			
			newObject.roles = new ArrayList<CrossLink>();
			newObject.disciplines = new ArrayList<CrossLink>();
			
			for(CrossLink l : roles) {
				
				newObject.roles.add(l.clone());
			}
			
			for(CrossLink d : disciplines) {
				
				newObject.disciplines.add(d.clone());
			}
			
			return newObject;
		}

		@Override
		public int compareTo(UserData_t other) {
			
			if (login.compareTo(other.login) != 0) {
				
				return login.compareTo(other.login);
			}
			
			if (name.compareTo(other.name) != 0) {
				
				return name.compareTo(other.name);
			}
			
			if (position.compareTo(other.position) != 0) {
				
				return position.compareTo(other.position);
			}
			
			if (email.compareTo(other.email) != 0) {
				
				return email.compareTo(other.email);
			}
			
			return 0;
		}
	}
	
	public static class Supplier_t {
		
		int id;
		String name;
		
		public Supplier_t (int id, String name) {
			
			this.id = id;
			this.name = name;
		}
		
		public Supplier_t (SimpleListRecord record) {
			
			id = record.id;
			name = record.value;
		}
		
		@Override
		public String toString() {
			
			return name;
		}
	}
}
