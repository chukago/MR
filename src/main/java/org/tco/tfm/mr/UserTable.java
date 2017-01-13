package org.tco.tfm.mr;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.tco.tfm.mr.Common.UserData_t;
import org.tco.tfm.mr.DatabaseManager.CrossLink;

public class UserTable extends JTable{

	public static class UserTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		private static UserTableModel instance = null;
		
		public static final int LOGIN_COLUMN_POS = 0;
		public static final int NAME_COLUMN_POS = 1;
		public static final int POSITION_COLUMN_POS = 2;
		public static final int EMAIL_COLUMN_POS = 3;
		
		DatabaseManager dbManager = DatabaseManager.getInstance();
		ArrayList<UserData_t> data = new ArrayList<UserData_t>();
		
		public static UserTableModel getInstance() {
			
			if (instance == null) {
				
				instance = new UserTableModel();
			}
			
			return instance;
		}
		
		private UserTableModel() {
			
			data = dbManager.findUser(null, DatabaseManager.USER_TABLE_NAME);
		}
		
		@Override
		public int getColumnCount() {
			
			return 4;
		}

		@Override
		public int getRowCount() {
			
			return data.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			
			if (rowIndex < 0 || rowIndex >= data.size()) {
			
				return null;
			}
			
			switch(columnIndex) {
			
			case LOGIN_COLUMN_POS:
				
				return data.get(rowIndex).login;
				
			case NAME_COLUMN_POS:
				
				return data.get(rowIndex).name;
				
			case POSITION_COLUMN_POS:
				
				return data.get(rowIndex).position;
				
			case EMAIL_COLUMN_POS:
				
				return data.get(rowIndex).email;
			}
			
			return null;
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			
			switch(columnIndex) {
			
			case LOGIN_COLUMN_POS:
				
				return "Login";
				
			case NAME_COLUMN_POS:
				
				return "Name";
				
			case POSITION_COLUMN_POS:
				
				return "Position";
				
			case EMAIL_COLUMN_POS:
				
				return "E-mail";
			}
			
			return null;
		}
		
		public Common.UserData_t getRecord(int row) {
			
			if (data.size() == 0 || row >= data.size()) {
				
				return null;
			}
			
			return data.get(row);
		}
		
		public boolean addRecord(Common.UserData_t newData) {
			
			for (UserData_t d : data) {
				
				if (d.login.compareToIgnoreCase(newData.login) == 0) {
					
					return false;
				}
			}
			
			if (!dbManager.saveUserRecord(newData, DatabaseManager.USER_TABLE_NAME)) {
				
				System.out.println("Cannot save user record in database");
				return false;
			}
			
			for (CrossLink r : newData.roles) {
				
				r.first = newData.id;
				
				if (!dbManager.saveCrossLink(r, DatabaseManager.USER_ROLE_LINK_TABLE_NAME)) {
					
					System.out.println("Cannot save user<->role crosslink in database");
				}
			}
			
			for (CrossLink d : newData.disciplines) {
				
				d.first = newData.id;
				
				if (!dbManager.saveCrossLink(d, DatabaseManager.USER_DISCIPLINE_LINK_TABLE_NAME)) {
					
					System.out.println("Cannot save user<->discipline crosslink in database");
				}
			}
			
			data.add(newData);
			fireTableRowsInserted(data.size() - 1, data.size() - 1);
			
			return true;
			
		}
		
		public boolean removeRecord(Common.UserData_t newData) {
			
			if (!dbManager.deleteUserRecord(newData, DatabaseManager.USER_TABLE_NAME)) {
				
				System.out.println("Cannot delete user record in database");
				return false;
			}
			
			for (int i = 0; i < data.size(); ++i) {
				
				if (data.get(i).id == newData.id) {
					
					data.remove(i);
					
					this.fireTableRowsDeleted(i, i);
					return true;
				}
			}
			
			return false;
		}
	}
	
	public class UserTableRenderer extends JTextArea implements TableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		public UserTableRenderer() {
			
			super();
			
			setLineWrap(true);
			setWrapStyleWord(true);
			//setOpaque(true);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			
			if (isSelected) {
				
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			}
			else {
				
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			
			if (column == 0) {
				
				table.setRowHeight(row, 10);
			}
			
			this.setText(value.toString());
			
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			
			if (table.getRowHeight(row) < getPreferredSize().height) {
				
				table.setRowHeight(row, getPreferredSize().height);
			}
			
			return this;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	public UserTable() {
		
		super();
		
		setModel(UserTableModel.getInstance());
		this.setDefaultRenderer(Object.class, new UserTableRenderer());
	}

}
