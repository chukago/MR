package org.tco.tfm.mr;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AccountEditor extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;

	private UserTable uTable = new UserTable();
	private JButton addButton = new JButton();
	private JButton editButton = new JButton();
	private JButton delButton = new JButton();
	
	public AccountEditor(Window owner) {
		
		super(owner);
		
		setTitle("Account editor");
		setModal(true);
		
		uTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() >= 2) {
					
					openRecord();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		Container userTableContainer = new Container();
		userTableContainer.setLayout(new BoxLayout(userTableContainer, BoxLayout.X_AXIS));
		
		JScrollPane userTablePane = new JScrollPane(uTable);
		userTableContainer.add(userTablePane);
		
		Container userButtonPanel = new Container();
		userButtonPanel.setLayout(new BoxLayout(userButtonPanel, BoxLayout.Y_AXIS));
		
		ImageIcon addIcon = new ImageIcon(getClass().getResource("icons/16x16/add.png"));
		addButton.setIcon(addIcon);
		addButton.setFocusable(false);
		addButton.setPreferredSize(new Dimension(24, 24));
		addButton.setMaximumSize(new Dimension(24, 24));
		
		ImageIcon editIcon = new ImageIcon(getClass().getResource("icons/16x16/edit.png"));
		editButton.setIcon(editIcon);
		editButton.setFocusable(false);
		editButton.setPreferredSize(new Dimension(24, 24));
		editButton.setMaximumSize(new Dimension(24, 24));
		
		ImageIcon delIcon = new ImageIcon(getClass().getResource("icons/16x16/delete.png"));
		delButton.setIcon(delIcon);
		delButton.setFocusable(false);
		delButton.setPreferredSize(new Dimension(24, 24));
		delButton.setMaximumSize(new Dimension(24, 24));
		
		addButton.addActionListener(this);
		editButton.addActionListener(this);
		delButton.addActionListener(this);
		
		userButtonPanel.add(addButton);
		//userButtonPanel.add(editButton);
		userButtonPanel.add(delButton);
		userButtonPanel.add(Box.createVerticalGlue());
		
		userTableContainer.add(userButtonPanel);
		
		JPanel dialogButtonPanel = new JPanel();
		dialogButtonPanel.setLayout(new BoxLayout(dialogButtonPanel, BoxLayout.X_AXIS));
		
		JButton closeButton = new JButton("Close");
		
		closeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
			}
		});
		
		dialogButtonPanel.add(closeButton);
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(userTableContainer);
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(dialogButtonPanel);
		
		pack();
	}
	
	@Override
	public void setVisible(boolean visible) {
		
		this.setLocationRelativeTo(getParent());
		super.setVisible(visible);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == addButton) {
			
			add();
		}
		
		if (e.getSource() == editButton) {
			
			edit();
		}
		
		if (e.getSource() == delButton) {
			
			remove();
		}
		
	}
	
	private void add() {
		
		EditUserDialog.getEditUserDialog(this);
	}
	
	private void edit() {
		
		int row = uTable.getSelectedRow();
		
		if (row == -1) {
			
			return;
		}
		
		UserTable.UserTableModel model = UserTable.UserTableModel.getInstance();
		
		EditUserDialog.getEditUserDialog(this, model.getRecord(row));
	}
	
	private void remove() {
		
		int row = uTable.getSelectedRow();
		
		if (row == -1) {
			
			return;
		}
		
		int result = JOptionPane.showConfirmDialog(this,"Are you sure to remove record?",
				"Delete", JOptionPane.YES_NO_OPTION);
		
		if (result != JOptionPane.YES_OPTION) {
			
			return;
		}
		
		UserTable.UserTableModel model = UserTable.UserTableModel.getInstance();
		
		Common.UserData_t d = model.getRecord(row);
		
		if (!model.removeRecord(d)) {
			
			System.out.println("Cannot remove user record");
		}
	}
	
	private void openRecord() {
		
		int row = uTable.getSelectedRow();
		
		if (row == -1) {
			
			return;
		}
		
		
		UserTable.UserTableModel model = UserTable.UserTableModel.getInstance();
		
		EditUserDialog.getEditUserDialog(this, model.getRecord(row));
	}
}
