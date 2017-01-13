package org.tco.tfm.mr;

import java.util.ArrayList;

import org.tco.tfm.mr.DatabaseManager.CrossLink;

public class SecurityManager {
	
	private static SecurityManager instance = null;
	private String currentUser = null;
	private Common.UserData_t currentContext = null;
	
	private DatabaseManager dbManager = DatabaseManager.getInstance();
	
	public static SecurityManager getInstance() {
		
		if (instance == null) {
			
			instance = new SecurityManager();
		}
		
		return instance;
	}

	public void setCurrentUser(String login) {
		
		currentUser = login;
		currentContext = getUserContext(currentUser);
	}
	
	public String getCurrentUser() {
		
		return currentUser;
	}
	
	public boolean hasSuperUser() {
		
		ArrayList<Common.UserData_t> list = dbManager.findUser(null, DatabaseManager.USER_TABLE_NAME);
		
		for (Common.UserData_t u : list) {
			
			for (CrossLink l : u.roles) {
				
				if (l.second == Common.Role_t.ADMINISTRATOR.value()) {
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean currentHasRole (Common.Role_t role) {
		
		if (currentContext == null) {
			
			return false;
		}
		
		for (DatabaseManager.CrossLink l :currentContext.roles) {
			
			if (role.value() == l.second) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean currentHasDiscipline (int id) {
		
		if (currentContext == null) {
			
			return false;
		}
		
		for (DatabaseManager.CrossLink l :currentContext.disciplines) {
			
			if (id == l.second) {
				
				return true;
			}
		}
		
		return false;
	}
	
	public Common.UserData_t getCurrentContext() {
		
		return currentContext == null ? null : currentContext.clone();

	}
	
	private Common.UserData_t getUserContext(String login) {
		
		Common.UserData_t condition = new Common.UserData_t();
		condition.login = login;
		
		ArrayList<Common.UserData_t> list = dbManager.findUser(condition, DatabaseManager.USER_TABLE_NAME);
		
		if (list.size() != 1) {
			
			System.out.println("Cannot load user data");
			return null;
		}
		
		return list.get(0);
	}
}
