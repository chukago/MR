package org.tco.tfm.mr.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.tco.tfm.mr.Common;
import org.tco.tfm.mr.DatabaseManager;
import org.tco.tfm.mr.DatabaseManager.CrossLink;
import org.tco.tfm.mr.DatabaseManager.SimpleListRecord;

public class ExcelDatabase {
	
	public static String baseLinkPath = "O:\\Operations\\SCM\\Services & Transportation\\"
			+ "Facilities Management Group\\TCO-ESS Shared\\Material Request\\";
	
	private class RawJournalRec_t {
		
		public Date recDate;
		public String position;
		public String name;
		public String status;
		public String comment;
	}
	
	private class RawRecordData_t {
		
		public Date reqDate;
		public int number;
		public String equipment;
		public String whs;
		public String location;
		public String locToBe;
		public int cc;
		public String discipline;
		public String justification;
		public String tfmStockCheck;
		public String tcoWhsCheck;
		public String supplier;
		public Date eta;
		public Date delivery;
		public Double cost;
		public String currency;
		public XSSFHyperlink link;
		public ArrayList<RawJournalRec_t> journal;
	}
	
	private static String databaseFile = "C:\\Users\\rnza\\Desktop\\ESS_MR_Status.xlsm";
	
	//private static String databaseFile = "/home/crtc/Desktop/MR/ESS_MR_Status.xlsm";
	private static int headerHeihgt = 2;
	
	private static int reqDatColPos = 0;
	private static int numberColPos = 1;
	private static int eqDescColPos = 2;
	private static int whsColPos = 3;
	private static int locColPos = 4;
	private static int locToBeColPos = 5;
	private static int ccColPos = 6;
	private static int disciplineColPos = 7;
	private static int justColPos = 8;
	private static int tfmStockColPos = 9;
	private static int tcoWhsCheckColPos = 10;
	private static int suppNameColPos = 11;
	private static int etaColPos = 12;
	private static int deliveryColPos = 13;
	private static int totalCostColPos = 14;
	private static int linkColPos = 15;
	private static int rDateColPos = 16;
	private static int rPositionColPos = 17;
	private static int rNameColPos = 18;
	private static int rStatusColPos = 19;
	private static int rCommentColPos = 20;
	
	private XSSFWorkbook workbook;
	private DatabaseManager dbManager = DatabaseManager.getInstance();
	
	private ArrayList<RawRecordData_t> rawRecords = new ArrayList<RawRecordData_t>();
	private ArrayList<SimpleListRecord> whsList = new ArrayList<SimpleListRecord>();
	private ArrayList<SimpleListRecord> supplierList = new ArrayList<SimpleListRecord>();
	private ArrayList<Common.Discipline_t> disciplineList = new ArrayList<Common.Discipline_t>();
	private ArrayList<Common.UserData_t> userList = new ArrayList<Common.UserData_t>();
	private ArrayList<CrossLink> userToDiscLink = new ArrayList<CrossLink>();
	
	private DefaultMutableTreeNode locationRoot = new DefaultMutableTreeNode();
	
	public ExcelDatabase() {
		
		try {
			
			FileInputStream fs = new FileInputStream(databaseFile);
			
			workbook = new XSSFWorkbook(fs);
			
		}
		catch (IOException e) {
			
			System.out.println("IO Erorr while opening excel database");
			e.printStackTrace();
		}
	}
	
	public void parse() {
		
		if (!importDiscipline()) {
			
			System.out.println("Cannot import disciplines!");
			return;
		}
		
		if (!importUsers("Requestor")) {
			
			System.out.println("Cannot import requestors");
			return;
		}
		
		if (!importUsers("1st_Appr")) {
			
			System.out.println("Cannot import first approvers");
			return;
		}
		
		if (!importUsers("2nd_Appr")) {
			
			System.out.println("Cannot import second approvers");
			return;
		}

		if (!importUsers("3rd_Appr")) {
			
			System.out.println("Cannot import third approvers");
			return;
		}
		
		if (!bindUsersRoles("Requestor", Common.Role_t.REQUESTOR)) {
			
			System.out.println("Cannot bind requestor role");
			return;
		}
		
		if (!bindUsersRoles("1st_Appr", Common.Role_t.FIRST_APPROVER)) {
			
			System.out.println("Cannot bind first approver role");
			return;
		}
		
		if (!bindUsersRoles("2nd_Appr", Common.Role_t.SECOND_APPROVER)) {
			
			System.out.println("Cannot bind second approver role");
			return;
		}
		
		if (!bindUsersRoles("3rd_Appr", Common.Role_t.THIRD_APPROVER)) {
			
			System.out.println("Cannot bind third approver role");
			return;
		}
		
		if (!bindUsersDiscipline()) {
			
			System.out.println("Cannot bind discipline");
			return;
		}
		
		if (!loadRawData()) {
			
			System.out.println("Cannot load raw data");
			return;
		}
		
		if (!saveInDatabase()) {
			
			System.out.println("Cannot save in database");
			return;
		}
		
	}
	
	public boolean loadRawData() {
		
		rawRecords.clear();
		
		int rowCounter = 0;
		int rowPos = 0;
		
		XSSFSheet sheet = workbook.getSheet("ESS_MR_Status");
		
		while(true) {
			
			RawRecordData_t newRecord = new RawRecordData_t();
			
			rowPos = headerHeihgt + rowCounter * 4;
			
			XSSFRow row = sheet.getRow(rowPos);
			
			if (row == null) {
				
				break;
			}
			
			XSSFCell cell;
			
			// Request date
			cell = row.getCell(reqDatColPos);
			newRecord.reqDate = cell.getDateCellValue();
			
			// MR Number
			newRecord.number = decodeMRNumber(row.getCell(numberColPos).getStringCellValue());
			
			// Equipment
			cell = row.getCell(eqDescColPos);
			newRecord.equipment = cell.getStringCellValue();
			
			// Whs
			cell = row.getCell(whsColPos);
			newRecord.whs = cell.getStringCellValue();
			
			// Location
			cell = row.getCell(locColPos);
			newRecord.location = cell.getStringCellValue();
			
			// LocToBe
			cell = row.getCell(locToBeColPos);
			newRecord.locToBe = cell.getStringCellValue();
			
			// CC
			cell = row.getCell(ccColPos);
			newRecord.cc = (int)cell.getNumericCellValue();
			
			// Discipline
			cell = row.getCell(disciplineColPos);
			newRecord.discipline = cell.getStringCellValue();
			
			// Justification
			cell = row.getCell(justColPos);
			newRecord.justification = cell.getStringCellValue();
			
			// TFM Stock
			cell = row.getCell(tfmStockColPos);
			newRecord.tfmStockCheck = cell.getStringCellValue();
			
			// TCO Whse Check
			cell = row.getCell(tcoWhsCheckColPos);
			newRecord.tcoWhsCheck = cell.getStringCellValue();
			
			// Supplier name
			cell = row.getCell(suppNameColPos);
			newRecord.supplier = cell.getStringCellValue();
			
			// ETA
			cell = row.getCell(etaColPos);
			newRecord.eta = cell.getDateCellValue();
			
			// Delivery
			cell = row.getCell(deliveryColPos);
			newRecord.delivery = cell.getDateCellValue();
			
			// Total Cost
			cell = row.getCell(totalCostColPos);
			newRecord.cost = cell.getNumericCellValue();
			
			if (cell.getCellStyle().getDataFormatString().matches("^.+KZT.+$")) {
				
				newRecord.currency = "KZT";
			}
			else {
				
				newRecord.currency = "USD";
			}
			
			// Link
			cell = row.getCell(linkColPos);
			newRecord.link = cell.getHyperlink();
			
			if (newRecord.link == null) {
				
				System.out.println("Null link");
			}
			
			// Status
			newRecord.journal = loadJournal(sheet, rowPos);
			
			++rowCounter;
			
			rawRecords.add(newRecord);
		}
		
		return true;
	}
	
	private ArrayList<RawJournalRec_t> loadJournal(XSSFSheet sheet, int rowPos) {
		
		ArrayList<RawJournalRec_t> result = new ArrayList<RawJournalRec_t>();
		
		for (int i = 0; i <= 3; ++i) {
			
			XSSFRow row = sheet.getRow(rowPos + i);
			XSSFCell cell = row.getCell(rStatusColPos);
			
			if (cell.getStringCellValue().isEmpty()) {
				
				break;
			}
			
			 RawJournalRec_t record = new RawJournalRec_t();
			
			//Date
			cell = row.getCell(rDateColPos);
			record.recDate = cell.getDateCellValue();
			
			//Position
			cell = row.getCell(rPositionColPos);
			record.position = cell.getStringCellValue();
			
			//Name
			cell = row.getCell(rNameColPos);
			record.name = cell.getStringCellValue();
			
			//Status
			cell = row.getCell(rStatusColPos);
			record.status = cell.getStringCellValue();
			
			//Comment
			cell = row.getCell(rCommentColPos);
			
			switch(cell.getCellType()) {
			
			case XSSFCell.CELL_TYPE_NUMERIC:
				
				record.comment = String.valueOf(cell.getNumericCellValue());
				break;
				
			default:
				record.comment = cell.getStringCellValue();	
			}
			
			result.add(record);
		}
		
		return result;
	}
	
	private int decodeMRNumber(String numberLine) {
		
		Pattern p = Pattern.compile("^TEN-TFM-\\w+-(\\d+$)");
		Matcher m = p.matcher(numberLine);
		
		if (!m.matches()) {
			
			return 0;
		}
		
		return Integer.valueOf(m.group(1));
	}
	
	private boolean saveInDatabase() {
		
		for (RawRecordData_t r : rawRecords) {
			
			//System.out.println(r.discipline + "-" + String.valueOf(r.number));
			
			Common.MRRecord_t newMrRecord = new Common.MRRecord_t();
			
			Common.Status_t status;
			
			//Status
			try {
				
				status = decodeStatus(r.journal);
			}
			catch (Exception e) {
				
				System.out.println("Cannot decode status");
				System.out.println(r.discipline + "-" + r.number);
				e.printStackTrace();
				return false;
			}
			
			newMrRecord.status = status;
			
			switch(newMrRecord.status) {
			
			case REQUEST:
			case REQUESTOR_REVIEW:
			case FIRST_APPROVED:
			case FIRST_REJECTED:
			case FIRST_REVIEW:
			case SECOND_REJECTED:
				
				if (!addRequestorInfo(newMrRecord, r)) {
					
					System.out.println("Cannot get requestor data");
					return false;
				}
				
				break;
			
			case SECOND_APPROVED:
			case SECOND_REVIEW:
			case THIRD_REJECTED:
				
				if (!addRequestorInfo(newMrRecord, r)) {
					
					System.out.println("Cannot get requestor data");
					return false;
				}
				
				if (!addSecondInfo(newMrRecord, r)) {
					
					System.out.println("Cannot get second approver data");
					return false;
				}
				
				break;
				
			case THIRD_APPROVED:
				
				if (!addRequestorInfo(newMrRecord, r)) {
					
					System.out.println("Cannot get requestor data");
					return false;
				}
				
				if (!addSecondInfo(newMrRecord, r)) {
					
					System.out.println("Cannot get second approver data");
					return false;
				}
				
				//CC
				if (r.cc <= 0) {
					
					System.out.println("Wrong cc number");
					return false;
				}
				
				newMrRecord.cc = r.cc;
				
				//Delivery
				if (r.delivery != null) {
					
					newMrRecord.delivery = r.delivery;
					newMrRecord.status = Common.Status_t.DELIVERED;
				}
				
				break;
				
			default:
				System.out.println("Wrong MR Status");	
				return false;
			}
			
			// Save MR record
			if (!dbManager.saveMrRecord(DatabaseManager.MR_TABLE_NAME, newMrRecord)) {
				
				System.out.println("Cannot save MR record");
				return false;
			}
			
			// Save mr<->location link
			if (r.location == null || r.location.isEmpty()) {
				
				System.out.println("Location is null or empty");
				return false;
			}
			
			if (r.locToBe == null || r.locToBe.isEmpty()) {
				
				System.out.println("LocToBe is null or empty");
				return false;
			}
			
			int locId = getLocationId(r.location, r.locToBe);
			
			if (locId == 0) {
				
				System.out.println("Cannot get location id");
				return false;
			}
			
			DatabaseManager.CrossLink link = new DatabaseManager.CrossLink(0, newMrRecord.id, locId);
			
			if (!dbManager.saveCrossLink(link, DatabaseManager.MR_LOCATION_LINK_TABLE_NAME)) {
				
				System.out.println("Cannot save mr<->location record");
				return false;
			}
			
			// Save journal
			ArrayList<Common.JournalRec_t> journal = decodeJournal(r.journal);
			
			for (Common.JournalRec_t jr : journal) {
				
				jr.workid = newMrRecord.id;
				
				if (!dbManager.saveJournalRecord(DatabaseManager.JOURNAL_TABLE_NAME, jr)) {
					
					System.out.println("Cannot save journal record");
					return false;
				}
			}
		}
		
		// Save discipline to update counter
		for (Common.Discipline_t d : disciplineList) {
			
			if (!dbManager.saveDisciplineRecord(DatabaseManager.DISCIPLINE_TABLE_NAME, d)) {
				
				System.out.println("Cannot save discipline with new counter");
				return false;
			}
		}
		
		return true;
	}
	
	private boolean addRequestorInfo(Common.MRRecord_t newMrRecord, RawRecordData_t rawData) {
		
		//Date
		if (rawData.reqDate == null) {
			
			System.out.println("Wrong date value");
			return false;
		}
		
		newMrRecord.reqDate = rawData.reqDate;
		
		//Number
		if (rawData.number <= 0) {
			
			System.out.println("Wrong request number");
			return false;
		}
		
		newMrRecord.number = rawData.number;
		
		//Equipment
		if (rawData.equipment == null || rawData.equipment.isEmpty()) {
			
			System.out.println("Wrong equipment description");
			return false;
		}
		
		newMrRecord.equipment = rawData.equipment;
		
		//TfmStock
		if (rawData.tfmStockCheck.compareToIgnoreCase("Stock") == 0) {
			
			newMrRecord.tfmStock = true;
		}
		else {
			
			newMrRecord.tfmStock = false;
		}
		
		//Justification
		if (rawData.justification == null || rawData.justification.isEmpty()) {
			
			System.out.println("Wrong request justification");
			return false;
		}
		
		newMrRecord.justification = rawData.justification;
		
		//Whs
		int whsId = getWhsId(rawData.whs);
		
		if (whsId == 0) {
			
			System.out.println("Wrong whs id");
			return false;
		}
		
		newMrRecord.whs = whsId;
		
		//Discipline
		Common.Discipline_t discipline = getDiscipline(rawData.discipline);
		
		if (discipline == null) {
			
			System.out.println("Cannot get discipline id");
			return false;
		}
		
		newMrRecord.discipline = discipline.id;
		
		if (discipline.id < newMrRecord.number) {
			
			discipline.counter = newMrRecord.number;
		}
		
		//Link
		if (rawData.link == null || rawData.link.getAddress().isEmpty()) {
			
			System.out.println("Wrong link address");
			System.out.println("Discipline - " + rawData.discipline);
			System.out.println("Number - " + String.valueOf(rawData.number));
		
			newMrRecord.link = baseLinkPath;
		}
		
		else {
		
			String linkAddress = rawData.link.getAddress();
			linkAddress = linkAddress.replace("%20", " ");
			linkAddress = linkAddress.replace("/", "\\");
			linkAddress = baseLinkPath + linkAddress;
			
			newMrRecord.link = linkAddress;
		}
		
		return true;
	}
	
	private boolean addSecondInfo(Common.MRRecord_t newMrRecord, RawRecordData_t rawData) {
		
		//Supplier
		if (rawData.supplier == null || rawData.supplier.isEmpty()) {
			
			System.out.println("Wrong supplier");
			return false;
		}
		
		int suppId = getSupplierId(rawData.supplier);
		
		if (suppId == 0) {
			
			System.out.println("Cannot get supplier id");
		}
		
		newMrRecord.supplier = suppId;
		
		//ETA
		if (rawData.eta == null) {
			
			System.out.println("Wrong eta date");
			return false;
		}
		
		//newMrRecord.equipment.get(0).eta = rawData.eta;
		
		newMrRecord.eta  = rawData.eta;
		
		//Cost
		if (rawData.cost <= 0) {
			
			System.out.println("Wrong cost");
			return false;
		}
		
		newMrRecord.cost = rawData.cost;
		
		//Currency
		Currency currency;
		
		try {
			
			currency = Currency.getInstance(rawData.currency);
			
			if (currency == null) {
				
				System.out.println("Wrong currency - " + rawData.currency);
				return false;
			}
		}
		catch (IllegalArgumentException e) {
			
			System.out.println("Wrong currency");
			e.printStackTrace();
			return false;
		}
		
		newMrRecord.currency = currency;
		
		//TcoWhs
		if (rawData.tcoWhsCheck != null && rawData.tcoWhsCheck.compareToIgnoreCase("Yes") == 0) {
			
			newMrRecord.tcoWhs = true;	
		}
		else {
			
			newMrRecord.tcoWhs = false;
		}
		
		return true;
	}
	
	private Common.Status_t decodeStatus(ArrayList<RawJournalRec_t> rawJournal) throws Exception {
		
		final String requestString = "Request";
		final String approvedString = "Approved";
		final String rejectedString = "Rejected";
		final String reviewString = "Review";
		
		switch(rawJournal.size()) {
		
		case 1:
			
			if (rawJournal.get(0).status.compareToIgnoreCase(requestString) == 0) {
				
				return Common.Status_t.REQUEST;
			}
			else if (rawJournal.get(0).status.compareToIgnoreCase(reviewString) == 0) {
				
				return Common.Status_t.REQUESTOR_REVIEW;
			}
			
			break;
			
		case 2:
			
			if (rawJournal.get(1).status.compareToIgnoreCase(approvedString) == 0) {
				
				return Common.Status_t.FIRST_APPROVED;
			}
			else if (rawJournal.get(1).status.compareToIgnoreCase(reviewString) == 0) {
				
				return Common.Status_t.FIRST_REVIEW;
			}
			else if (rawJournal.get(1).status.compareToIgnoreCase(rejectedString) == 0) {
				
				return Common.Status_t.FIRST_REJECTED;
			}
			
			break;
			
		case 3:
		
			if (rawJournal.get(2).status.compareToIgnoreCase(approvedString) == 0) {
				
				return Common.Status_t.SECOND_APPROVED;
			}
			else if (rawJournal.get(2).status.compareToIgnoreCase(reviewString) == 0) {
				
				return Common.Status_t.SECOND_REVIEW;
			}
			else if (rawJournal.get(2).status.compareToIgnoreCase(rejectedString) == 0) {
				
				return Common.Status_t.SECOND_REJECTED;
			}
			
			break;
			
		case 4:
			
			if (rawJournal.get(3).status.compareToIgnoreCase(approvedString) == 0) {
				
				return Common.Status_t.THIRD_APPROVED;
			}
			
			else if (rawJournal.get(3).status.compareToIgnoreCase(rejectedString) == 0) {
				
				return Common.Status_t.THIRD_REJECTED;
			}
			
			break;
		}
		
		throw new Exception("Wrong data format");
	}
	
	private ArrayList<Common.JournalRec_t> decodeJournal(ArrayList<RawJournalRec_t> rawJournal) {
		
		final String requestString = "Request";
		final String approvedString = "Approved";
		final String rejectedString = "Rejected";
		final String reviewString = "Review";
		
		ArrayList<Common.JournalRec_t> result = new ArrayList<Common.JournalRec_t>();
		
		for (int i = 0; i < rawJournal.size(); ++i) {
			
			Common.JournalRec_t newRecord = new Common.JournalRec_t();
			
			// Date
			if (rawJournal.get(i).recDate == null) {
				
				return null;
			}
			
			// Position
			if (rawJournal.get(i).position == null || rawJournal.get(i).position.isEmpty()) {
			
				return null;
			}
			
			// Name
			if (rawJournal.get(i).name == null || rawJournal.get(i).name.isEmpty()) {
				
				return null;
			}
			
			newRecord.date = rawJournal.get(i).recDate;
			newRecord.position = rawJournal.get(i).position;
			newRecord.name = rawJournal.get(i).name;
			newRecord.comment = rawJournal.get(i).comment;
			
			switch(i) {
			
			case 0:
				
				if (rawJournal.get(0).status.compareToIgnoreCase(requestString) == 0) {
					
					newRecord.status = Common.Status_t.REQUEST;
				}
				else if (rawJournal.get(0).status.compareToIgnoreCase(reviewString) == 0) {
					
					newRecord.status = Common.Status_t.REQUESTOR_REVIEW;
				}
				
				break;
				
			case 1:
				
				if (rawJournal.get(1).status.compareToIgnoreCase(approvedString) == 0) {
					
					newRecord.status = Common.Status_t.FIRST_APPROVED;
				}
				else if (rawJournal.get(1).status.compareToIgnoreCase(reviewString) == 0) {
					
					newRecord.status = Common.Status_t.FIRST_REVIEW;
				}
				else if (rawJournal.get(1).status.compareToIgnoreCase(rejectedString) == 0) {
					
					newRecord.status = Common.Status_t.FIRST_REJECTED;
				}
				
				break;
				
			case 2:
			
				if (rawJournal.get(2).status.compareToIgnoreCase(approvedString) == 0) {
					
					newRecord.status = Common.Status_t.SECOND_APPROVED;
				}
				else if (rawJournal.get(2).status.compareToIgnoreCase(reviewString) == 0) {
					
					newRecord.status = Common.Status_t.SECOND_REVIEW;
				}
				else if (rawJournal.get(2).status.compareToIgnoreCase(rejectedString) == 0) {
					
					newRecord.status = Common.Status_t.SECOND_REJECTED;
				}
				
				break;
				
			case 3:
				
				if (rawJournal.get(3).status.compareToIgnoreCase(approvedString) == 0) {
					
					newRecord.status = Common.Status_t.THIRD_APPROVED;
				}
				
				else if (rawJournal.get(3).status.compareToIgnoreCase(rejectedString) == 0) {
					
					newRecord.status = Common.Status_t.THIRD_REJECTED;
				}
				
				break;
				
			default:
				
				return null;
			}
			
			result.add(newRecord);
		}
		
		return result;
	}
	
	private int getWhsId(String whsName) {
		
		for(SimpleListRecord r : whsList) {
			
			if (r.value.compareToIgnoreCase(whsName) == 0) {
			
				return r.id;
			}
		}
		
		SimpleListRecord newRecord = new SimpleListRecord(0, whsName);
		
		if (!dbManager.saveSimpleListRecord(DatabaseManager.WHS_TABLE_NAME, newRecord)) {
			
			System.out.println("Cannot save whs");
			return 0;
		}
		
		whsList.add(newRecord);
		
		return newRecord.id;
	}
	
	private int getSupplierId(String supplier) {
		
		for(SimpleListRecord r : supplierList) {
			
			if (r.value.replaceAll("\\s+", "").compareToIgnoreCase(supplier.replaceAll("\\s+", "")) == 0) {
			
				return r.id;
			}
		}
		
		SimpleListRecord newRecord = new SimpleListRecord(0, supplier);
		
		if (!dbManager.saveSimpleListRecord(DatabaseManager.SUPPLIER_TABLE_NAME, newRecord)) {
			
			System.out.println("Cannot save supplier");
			return 0;
		}
		
		supplierList.add(newRecord);
		
		return newRecord.id;
	}
	
	private boolean importUsers(String tableName) {
		
		boolean userHasInDb = false;
		
		XSSFSheet sheet = workbook.getSheet(tableName);
		
		int rowPos = 1;
		
		while(true) {
			
			XSSFRow row = sheet.getRow(rowPos);
			
			if (row == null) {
				
				break;
			}
			
			XSSFCell cell;
			
			// Login
			cell = row.getCell(0);
			String login = cell.getStringCellValue();
			 
			// Name
			cell = row.getCell(1);
			String name = cell.getStringCellValue();
			
			// Position
			cell = row.getCell(2);
			String position = cell.getStringCellValue();
			
			// Email
			cell = row.getCell(3);
			String email = cell.getStringCellValue();
			
			++rowPos;
			
			userHasInDb = false;
			
			for (Common.UserData_t d : userList) {
				
				if (d.login.compareToIgnoreCase(login) == 0) {
				
					userHasInDb = true;
				}
			}
			
			if (!userHasInDb) {
				
				Common.UserData_t d = new Common.UserData_t();
				
				d.login = login;
				d.name = name;
				d.position = position;
				d.email = email;
			
				if (!dbManager.saveUserRecord(d, DatabaseManager.USER_TABLE_NAME)) {
					
					System.out.println("Cannot save user in database");
					return false;
				}
				
				userList.add(d);
			}
		}
		
		return true;
	}
	
	private boolean bindUsersRoles(String tableName, Common.Role_t role) {
		
		XSSFSheet sheet = workbook.getSheet(tableName);
		
		int rowPos = 1;
		
		while(true) {
			
			XSSFRow row = sheet.getRow(rowPos);
			
			if (row == null) {
				
				break;
			}
			
			XSSFCell cell;
			
			// Login
			cell = row.getCell(0);
			String login = cell.getStringCellValue();
			
			++rowPos;
			
			for (Common.UserData_t d : userList) {
				
				if (d.login.compareToIgnoreCase(login) == 0) {
				
					CrossLink link = new CrossLink(0, d.id, role.value());
					
					if (!dbManager.saveCrossLink(link, DatabaseManager.USER_ROLE_LINK_TABLE_NAME)) {
						
						System.out.println("Cannot bind user role");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean bindUsersDiscipline() {
		
		XSSFSheet sheet = workbook.getSheet("2nd_Appr");
		
		int rowPos = 1;
		
		while(true) {
			
			XSSFRow row = sheet.getRow(rowPos);
			
			if (row == null) {
				
				break;
			}
			
			XSSFCell cell;
			
			// Login
			cell = row.getCell(0);
			String login = cell.getStringCellValue();
			
			// Discipline
			cell = row.getCell(4);
			String discipline = cell.getStringCellValue();			
			
			// 3rd approvers
			cell = row.getCell(5);
			String thirdAppr = cell.getStringCellValue();
			
			++rowPos;
			
			for (Common.UserData_t u : userList) {
				
				if (u.login.compareToIgnoreCase(login) == 0) {
					
					Common.Discipline_t d = getDiscipline(discipline);
					
					if (!bindDiscipline(u, d)) {
						
						System.out.print("Cannot bind discipline to 2nd approver");
						return false;
					}
				}
			}
			
			String[] thirdList = thirdAppr.split(";");
			
			for (int i = 0; i < thirdList.length; ++i) {
				
				for (Common.UserData_t u : userList) {
					
					if (u.login.compareToIgnoreCase(thirdList[i]) == 0) {
						
						Common.Discipline_t d = getDiscipline(discipline);
						
						if (!bindDiscipline(u, d)) {
							
							System.out.print("Cannot bind discipline to 3rd approver");
							return false;
						}
					}
				}
			}
			
		}
		
		return true;
	}
	
	private boolean bindDiscipline(Common.UserData_t userData, Common.Discipline_t discipline) {
		
		for (CrossLink l : userToDiscLink) {
			
			if (l.first == userData.id && l.second == discipline.id) {
				
				return true;
			}
		}
		
		CrossLink newLink = new CrossLink(0, userData.id, discipline.id);
		
		if (!dbManager.saveCrossLink(newLink, DatabaseManager.USER_DISCIPLINE_LINK_TABLE_NAME)) {
			
			System.out.println("Cannot save crosslink in database");
			return false;
		}
		
		userToDiscLink.add(newLink);
		
		return true;
	}
	
	private boolean importDiscipline() {
		
		XSSFSheet sheet = workbook.getSheet("Abbr");
		
		int rowPos = 1;
		
		while(true) {
			
			XSSFRow row = sheet.getRow(rowPos);
			
			if (row == null) {
				
				break;
			}
			
			XSSFCell cell;
			
			// Discipline
			cell = row.getCell(0);
			String discName = cell.getStringCellValue();
			 
			// Abbreviation
			cell = row.getCell(1);
			String abbreviation = cell.getStringCellValue();
			
			++rowPos;
			
			Common.Discipline_t d = new Common.Discipline_t(0, discName, abbreviation);
		
			if (!dbManager.saveDisciplineRecord(DatabaseManager.DISCIPLINE_TABLE_NAME, d)) {
				
				System.out.println("Cannot save discipline in database");
				return false;
			}
			
			disciplineList.add(d);
		}
		
		return true;
	}
	
	private Common.Discipline_t getDiscipline(String discipline) {
	
		if (discipline == null || discipline.isEmpty()) {
			
			System.out.println("Discipline name is null or empty");
			return null;
		}
		
		for (Common.Discipline_t d : disciplineList) {
			
			if (d.name.compareToIgnoreCase(discipline) == 0) {
				
				return d;
			}
		}
		
		return null;
		
		/*
		Common.Discipline_t newDiscipline = new Common.Discipline_t(0, discipline, discipline);
		
		if (!dbManager.saveDisciplineRecord(DatabaseManager.DISCIPLINE_TABLE_NAME, newDiscipline)) {
			
			System.out.println("Cannot save discipline in database");
			return null;
		}
		
		disciplineList.add(newDiscipline);
		
		return newDiscipline;
		*/
	}
	
	private int getLocationId(String location, String subLocation) {
		
		for (int i = 0; i < locationRoot.getChildCount(); ++i) {
			
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) locationRoot.getChildAt(i);
			Common.LocationRec_t childData = (Common.LocationRec_t)childNode.getUserObject();
			
			if (childData.name.replaceAll("\\s+", "").compareToIgnoreCase(location.replaceAll("\\s+", "")) == 0) {
				
				return getSubLocationId(childNode, subLocation);
			}
		}
		
		Common.LocationRec_t newLocation = new Common.LocationRec_t(0, location, 0);
		
		if (!dbManager.saveLocationRecord(DatabaseManager.LOCATION_TABLE_NAME, newLocation)) {
			
			System.out.println("Cannot save location in database");
			return 0;
		}
		
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
		newNode.setUserObject(newLocation);
		
		locationRoot.add(newNode);
		
		return getSubLocationId(newNode, subLocation);
		
	}
	
	private int getSubLocationId(DefaultMutableTreeNode node, String location) {
	
		for (int i = 0; i < node.getChildCount(); ++i) {
			
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
			Common.LocationRec_t childData = (Common.LocationRec_t)childNode.getUserObject();
			
			if (childData.name.replaceAll("\\s+", "").compareToIgnoreCase(location.replaceAll("\\s+", "")) == 0) {
				
				return childData.id;
			}
		}
		
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node;
		Common.LocationRec_t parentData = (Common.LocationRec_t)parentNode.getUserObject();
		
		Common.LocationRec_t newLocation = new Common.LocationRec_t(0, location, parentData.id);
		
		if (!dbManager.saveLocationRecord(DatabaseManager.LOCATION_TABLE_NAME, newLocation)) {
			
			System.out.println("Cannot save location in database");
			return 0;
		}
		
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
		newNode.setUserObject(newLocation);
		
		node.add(newNode);
		
		return newLocation.id;
	}
	
	@Override
	public void finalize() {
		
		try {
			
			workbook.close();
		}
		catch (IOException e) {

			System.out.println("Cannot close workbook");
			e.printStackTrace();
		}
	}
}
