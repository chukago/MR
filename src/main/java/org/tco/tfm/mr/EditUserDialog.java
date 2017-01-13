package org.tco.tfm.mr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.tco.tfm.mr.Common.Discipline_t;
import org.tco.tfm.mr.Common.Role_t;
import org.tco.tfm.mr.DatabaseManager.CrossLink;

public class EditUserDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	
	private JTextField loginField = new JTextField();
	private JTextField nameField = new JTextField();
	private JTextField positionField = new JTextField();
	private JTextField emailField = new JTextField();
	private JTable roleTable = new JTable();
	private CheckListModel<Role_t> roleModel = new CheckListModel<Role_t>(Role_t.values());
	private JTable disciplineTable = new JTable();
	private CheckListModel<Discipline_t> discModel = new CheckListModel<Discipline_t>(
			DatabaseManager.getInstance().findDiscipline(null, DatabaseManager.DISCIPLINE_TABLE_NAME));
	
	JButton saveButton = new JButton("Save");
	JButton cancelButton = new JButton("Cancel");
	
	private Common.UserData_t data;

	private EditUserDialog(Window owner) {
		
		super(owner);
		
		setTitle("User settings");
		
		setModal(true);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		
		JLabel loginLabel = new JLabel("Login");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		add(loginLabel, constraints);
		
		loginField.setPreferredSize(new Dimension(250, 25));
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(loginField, constraints);
		
		JLabel nameLabel = new JLabel("Name");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		add(nameLabel, constraints);
		
		nameField.setPreferredSize(new Dimension(250, 25));
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;;
		add(nameField, constraints);
		
		JLabel positionLabel = new JLabel("Position");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;;
		add(positionLabel, constraints);
		
		positionField.setPreferredSize(new Dimension(250, 25));
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(positionField, constraints);
		
		JLabel emailLabel = new JLabel("E-mail");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.VERTICAL;
		add(emailLabel, constraints);
		
		emailField.setPreferredSize(new Dimension(250, 25));
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		add(emailField, constraints);
		
		roleTable.setModel(roleModel);
		JScrollPane roleScroll = new JScrollPane(roleTable);
		roleScroll.setPreferredSize(new Dimension(250, 150));
		roleScroll.setBorder(BorderFactory.createTitledBorder("Roles"));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		add(roleScroll, constraints);
		
		disciplineTable.setModel(discModel);
		JScrollPane disciplineScroll = new JScrollPane(disciplineTable);
		disciplineScroll.setPreferredSize(new Dimension(250, 150));
		disciplineScroll.setBorder(BorderFactory.createTitledBorder("Discipline"));
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.BOTH;
		add(disciplineScroll, constraints);
		
		JPanel dialogButtonPanel = new JPanel();
		
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				save();
			}
			
		});
		
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				data = null;
				setVisible(false);
			}
			
		});
		
		dialogButtonPanel.add(saveButton);
		dialogButtonPanel.add(cancelButton);
		
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		
		add(dialogButtonPanel, constraints);
		
		pack();
	}
	
	@Override
	public void setVisible(boolean visible) {
		
		this.setLocationRelativeTo(getParent());
		super.setVisible(visible);
	}
	
	public void setData(Common.UserData_t data) {
		
		this.data = data.clone();
		
		loginField.setText(this.data.login);
		nameField.setText(this.data.name);
		positionField.setText(this.data.position);
		emailField.setText(this.data.email);
		
		for (CrossLink l : this.data.roles) {
			
			roleModel.select(Role_t.fromValue(l.second));
		}
		
		DisciplineListModel dlModel = DisciplineListModel.getInstance();
		
		for (CrossLink d : this.data.disciplines) {
			
			discModel.select(dlModel.getItem(d.second));
		}
	}
	
	private void save() {
		
		if (loginField.getText() == null || loginField.getText().isEmpty()) {
			
			return;
		}
		
		if (nameField.getText() == null || nameField.getText().isEmpty()) {
			
			return;
		}
		
		if (positionField.getText() == null || positionField.getText().isEmpty()) {
			
			return;
		}
		
		if (emailField.getText() == null || emailField.getText().isEmpty()) {
			
			return;
		}
		
		if (data == null) {
			
			data = new Common.UserData_t();
		}
		
		data.login = loginField.getText();
		data.name = nameField.getText();
		data.position = positionField.getText();
		data.email = emailField.getText();
		
		data.roles = new ArrayList<CrossLink>();
		data.disciplines = new ArrayList<CrossLink>();
		
		ArrayList<Role_t> selectedRoles = roleModel.getSelected();
		
		for (Role_t r : selectedRoles) {
			
			data.roles.add(new CrossLink(0, 0, r.value()));
		}
		
		ArrayList<Discipline_t> selectedDisciplines = discModel.getSelected();
		
		for (Discipline_t d : selectedDisciplines) {
			
			data.disciplines.add(new CrossLink(0, 0, d.id));
		}
		
		UserTable.UserTableModel userModel = UserTable.UserTableModel.getInstance();
		
		if (!userModel.addRecord(data)) {
			
			JOptionPane.showMessageDialog(this, "Cannot save record");
			return;
		}
		
		setVisible(false);
		
	}
	
	public void setEditable(boolean editable) {
		
		loginField.setEditable(editable);
		nameField.setEditable(editable);
		positionField.setEditable(editable);
		emailField.setEditable(editable);
		
		roleTable.setEnabled(editable);
		disciplineTable.setEnabled(editable);
		
		saveButton.setEnabled(false);
		cancelButton.setEnabled(false);
	}
	
	public static void getEditUserDialog(Window owner) {
		
		EditUserDialog dialog = new EditUserDialog(owner);
		dialog.setVisible(true);
	}
	
	public static void getEditUserDialog(Window owner, Common.UserData_t data) {
		
		EditUserDialog dialog = new EditUserDialog(owner);
		dialog.setData(data);
	
		dialog.setEditable(false);
		dialog.setVisible(true);
	}
}
