package org.tco.tfm.mr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.tco.tfm.mr.Common.Discipline_t;
import org.tco.tfm.mr.Common.JournalRec_t;
import org.tco.tfm.mr.Common.LocationRec_t;
import org.tco.tfm.mr.DatabaseManager.CrossLink;

public class Utils {
	
	public static final int toUnixTime(final java.util.Date date)
	{
		long longTime = date.getTime();
		int shortTime = (int)(longTime / 1000);
		return shortTime;
	}
	
	public static final Date fromUnixTime(final int timestamp)
	{
		Long longTime = new Long(timestamp) * 1000;
		Date result = new Date(longTime);
		result.setTime(longTime);
		return result;
	}
	
	public static int getNextDisciplineNumber(int id) {
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		
		Discipline_t discipline = new Discipline_t();
		discipline.id = id;
		
		ArrayList<Discipline_t> list = dbManager.findDiscipline(discipline, DatabaseManager.DISCIPLINE_TABLE_NAME);
		
		if (list.size() != 1) {
			
			return 0;
		}
		
		int result = list.get(0).counter;
		++ result;
		
		return result;
	}
	
	public static int reserveDisciplineNumber(int id) {
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		
		Discipline_t discipline = new Discipline_t();
		discipline.id = id;
		
		ArrayList<Discipline_t> list = dbManager.findDiscipline(discipline, DatabaseManager.DISCIPLINE_TABLE_NAME);
		
		if (list.size() != 1) {
			
			return 0;
		}
		
		int result = list.get(0).counter;
		++ result;
		
		list.get(0).counter = result;
		
		if (!dbManager.saveDisciplineRecord(DatabaseManager.DISCIPLINE_TABLE_NAME, list.get(0))) {
			
			return 0;
		}
		
		return result;
	}
	
	public static String getRequestNumber(Common.MRRecord_t record) {
		
		DisciplineListModel discModel = DisciplineListModel.getInstance();

		for (int i = 0; i < discModel.getSize(); ++i) {
			
			if (discModel.getElementAt(i).id == record.discipline) {
				
				String docNumber;
				
				if (record.id == 0) {
					
					docNumber = String.valueOf(getNextDisciplineNumber(record.discipline));
				}
				else {
					
					docNumber = String.valueOf(record.number);
				}
				
				return Common.MR_DOC_NUMBER_PREFIX + discModel.getElementAt(i).abbreviation + "-" +
						docNumber;
			}
		}
		
		return null;
	}
	
	public static ArrayList<Common.MRRecord_t> findMrRecord(Common.MRFindCondition_t condition) {
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		
		ArrayList<Common.MRRecord_t> result = dbManager.findMrRecord(condition, DatabaseManager.MR_TABLE_NAME);
		
		return result;
	}
	
	public static boolean saveMrRecord(Common.MRRecord_t record) {
		
		SecurityManager secManager = SecurityManager.getInstance();
		
		Common.UserData_t currentUser = secManager.getCurrentContext();
		
		if (currentUser == null) {
			
			System.out.println("Current user context is not set");
			return false;
		}
		
		JournalRec_t j = new Common.JournalRec_t();
		j.date = new Date();
		j.name = currentUser.name;
		j.position = currentUser.position;
		j.comment = record.comment;
		j.status = record.status;
		
		record.journal.add(j);
		
		if (!sendMail(record)) {
			
			System.out.println("Cannot send email");
			return false;
		}
		
		MRTableModel model = MRTableModel.getInstance();
		
		if (!model.save(record)) {
			
			JOptionPane.showMessageDialog(MR.getMainWindow(), "Cannot save MR record in database!");
			return false;
		}
		
		return true;
	}
	
	public static boolean sendMail(Common.MRRecord_t record) {
		
		MSOApplication mso = new MSOApplication();
		EmailGenerator gen = new EmailGenerator(record);
		
		mso.setBody(gen.getHtml());
		
		mso.setSubject(getRequestNumber(record));
		
		ArrayList<String> emails = new ArrayList<String>();
		
		switch (record.status) {
		
		case REQUEST:
			
			emails = getEmailList(findFirstApprovers());
			break;
		
		case REQUESTOR_REVIEW:
			break;
			
		case FIRST_APPROVED:
			
			emails = getEmailList(findSecondApprovers(record));
			break;
			
		case FIRST_REJECTED:
			
			emails = getEmailList(findRequestors(record));
			break;
			
		case FIRST_REVIEW:
			break;
			
		case SECOND_APPROVED:
			
			emails = getEmailList(findThirdApprovers(record));
			break;
			
		case SECOND_REJECTED:
			
			emails = getEmailList(findFirstApprovers());
			
			break;
			
		case SECOND_REVIEW:
			break;
		
		case THIRD_APPROVED:
			
			emails = getEmailList(findRequestors(record));
			break;
		
		case THIRD_REJECTED:
			
			emails = getEmailList(findSecondApprovers(record));
			break;
		
		case DELIVERED:
			
			emails = getEmailList(findSecondApprovers(record));
			break;
			
		default:
			System.out.println("Wrong request status");
			return false;
		}
		
		if (emails.isEmpty()) {
			
			System.out.println("Cannot get recipient list");
			return false;
		}
		
		mso.addToAddress(emails);
		
		if (!mso.send()) {
			
			System.out.println("Cannot send email via MSOApplication");
			return false;
		}
		
		return true;
	}
	
	public static ArrayList<String> getEmailList(ArrayList<Common.UserData_t> usersList) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		boolean found;
		
		for (Common.UserData_t u : usersList) {
			
			found = false;
			
			for (String email : result) {
				
				if (email.compareToIgnoreCase(u.email) == 0) {
					
					found = true;
					break;
				}
			}
			
			if (!found) {
				
				result.add(u.email);
			}
		}
		
		return result;
	
	}
	
	public static ArrayList<Common.UserData_t> findFirstApprovers() {
	
		ArrayList<Common.UserData_t> result = new ArrayList<Common.UserData_t>();
		
		ArrayList<Common.UserData_t> userList = getUsersList();
		
		for (Common.UserData_t u : userList) {
			
			for (CrossLink l : u.roles) {
				
				if (l.second == Common.Role_t.FIRST_APPROVER.value()) {
					
					result.add(u);
					break;
				}
			}
		}
		
		return result;
	}
	
	public static ArrayList<Common.UserData_t> findSecondApprovers(Common.MRRecord_t record) {
		
		boolean foundRole;
		boolean foundDiscipline;
		
		ArrayList<Common.UserData_t> result = new ArrayList<Common.UserData_t>();
		
		ArrayList<Common.UserData_t> userList = getUsersList();
		
		for (Common.UserData_t u : userList) {
			
			foundRole = false;
			foundDiscipline = false;
			
			for (CrossLink l : u.roles) {
				
				if (l.second == Common.Role_t.SECOND_APPROVER.value()) {
					
					foundRole = true;
					break;
				}
			}
			
			for (CrossLink l : u.disciplines) {
				
				if (l.second == record.discipline) {
					
					foundDiscipline = true;
					break;
				}
			}
			
			if (foundRole && foundDiscipline) {
				
				result.add(u);
			}
		}
		
		return result;
	}
	
	public static ArrayList<Common.UserData_t> findThirdApprovers(Common.MRRecord_t record) {
		
		boolean foundRole;
		boolean foundDiscipline;
		
		ArrayList<Common.UserData_t> result = new ArrayList<Common.UserData_t>();
		
		ArrayList<Common.UserData_t> userList = getUsersList();
		
		for (Common.UserData_t u : userList) {
			
			foundRole = false;
			foundDiscipline = false;
			
			for (CrossLink l : u.roles) {
				
				if (l.second == Common.Role_t.THIRD_APPROVER.value()) {
					
					foundRole = true;
					break;
				}
			}
			
			for (CrossLink l : u.disciplines) {
				
				if (l.second == record.discipline) {
					
					foundDiscipline = true;
					break;
				}
			}
			
			if (foundRole && foundDiscipline) {
				
				result.add(u);
			}
		}
		
		return result;
	}
	
	public static ArrayList<Common.UserData_t> findRequestors(Common.MRRecord_t record) {
		
		if (record.journal.size() == 0) {
			
			return null;
		}
		
		String position = record.journal.get(0).position;
		
		ArrayList<Common.UserData_t> result = new ArrayList<Common.UserData_t>();
		
		ArrayList<Common.UserData_t> userList = getUsersList();
		
		for (Common.UserData_t u : userList) {
			
			if (u.position.compareToIgnoreCase(position) == 0) {
				
				result.add(u);
			}
		}
		
		return result;
	}
	
	private static ArrayList<Common.UserData_t> getUsersList() {
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		return dbManager.findUser(null, DatabaseManager.USER_TABLE_NAME);
	}
	
	// Load root node
	public static DefaultMutableTreeNode loadLocationTree() {
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		
		Common.LocationRec_t rootData = new Common.LocationRec_t(0, "All locations", 0);
		root.setUserObject(rootData);
		
		Common.LocationRec_t condition = new Common.LocationRec_t(0, null, 0);
		
		ArrayList<Common.LocationRec_t> children = dbManager.findLocation(condition, DatabaseManager.LOCATION_TABLE_NAME);
		
		Collections.sort(children, new Comparator<Common.LocationRec_t>() {

			@Override
			public int compare(LocationRec_t first, LocationRec_t second) {
				
				return (first.name.compareToIgnoreCase(second.name));
			}
			
		});
		
		for (Common.LocationRec_t child : children) {
			
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
			childNode.setUserObject(child);
			root.add(childNode);
			
			loadLocationTree(childNode);
		}
		
		return root;
	}
	
	// Load children for node
	private static void loadLocationTree(DefaultMutableTreeNode node) {
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		Common.LocationRec_t nodeData = (Common.LocationRec_t)node.getUserObject();
		
		Common.LocationRec_t condition = new Common.LocationRec_t(0, null, nodeData.id);
		
		ArrayList<Common.LocationRec_t> children = dbManager.findLocation(condition, DatabaseManager.LOCATION_TABLE_NAME);
		
		Collections.sort(children, new Comparator<Common.LocationRec_t>() {

			@Override
			public int compare(LocationRec_t first, LocationRec_t second) {
				
				return (first.name.compareToIgnoreCase(second.name));
			}
			
		});
		
		for (Common.LocationRec_t child : children) {
			
			DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
			childNode.setUserObject(child);
			node.add(childNode);
			
			loadLocationTree(childNode);
		}
	}
}
