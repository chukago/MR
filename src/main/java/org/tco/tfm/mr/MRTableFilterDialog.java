package org.tco.tfm.mr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.tco.tfm.mr.Common.Discipline_t;
import org.tco.tfm.mr.Common.Status_t;

public class MRTableFilterDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private CheckListModel<Status_t> statusListModel;
	private CheckListModel<Discipline_t> discListModel;
	
	private JButton filterButton = new JButton("Filter");
	private JButton clearButton = new JButton("Clear");
	private JButton cancelButton = new JButton("Cancel");
	
	public MRTableFilterDialog(Window parent) {
		
		super(parent);
		setTitle("Table filter");
		setModal(true);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		
		JLabel statusLabel = new JLabel("Status");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(statusLabel, constraints);
		
		JTable statusTable = new JTable();
		JScrollPane statusPane = new JScrollPane(statusTable);
		statusPane.setPreferredSize(new Dimension(275, 150));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(statusPane, constraints);
		
		statusListModel = new CheckListModel<Status_t>(getStatusList());
		statusTable.setModel(statusListModel);
		
		JLabel discLabel = new JLabel("Discipline");
		discLabel.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(discLabel, constraints);
		
		JTable discTable = new JTable();
		JScrollPane discPane = new JScrollPane(discTable);
		discPane.setPreferredSize(new Dimension(275, 150));
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(discPane, constraints);
		
		discListModel = new CheckListModel<Discipline_t>(getDisciplineList());
		discTable.setModel(discListModel);
		
		filterButton.addActionListener(this);
		clearButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();		
		buttonPanel.add(filterButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(cancelButton);
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(buttonPanel, constraints);
		
		pack();
	}
	
	@Override
	public void setVisible(boolean visible) {
		
		this.setLocationRelativeTo(getParent());
		super.setVisible(visible);
	}
	
	private ArrayList<Status_t> getStatusList() {
		
		 ArrayList<Status_t> statusList = new  ArrayList<Status_t>();
		 
		 statusList.add(Status_t.REQUEST);
		 statusList.add(Status_t.FIRST_APPROVED);
		 statusList.add(Status_t.SECOND_APPROVED);
		 statusList.add(Status_t.THIRD_APPROVED);
		 statusList.add(Status_t.FIRST_REJECTED);
		 statusList.add(Status_t.SECOND_REJECTED);
		 statusList.add(Status_t.THIRD_REJECTED);
		 statusList.add(Status_t.DELIVERED);
		 
		 return statusList;
	}
	
	private ArrayList<Discipline_t> getDisciplineList() {
		
		ArrayList<Discipline_t> discList = new  ArrayList<Discipline_t>();
		
		DisciplineListModel model = DisciplineListModel.getInstance();
		
		for (int i = 0; i < model.getSize(); ++i) {
			
			discList.add(model.getElementAt(i));
		}
		
		return discList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == filterButton) {
			
			MRTableFilter.FilterData_t filterData = new MRTableFilter.FilterData_t();
			filterData.statusList = statusListModel.getSelected();
			filterData.disciplineList = discListModel.getSelected();
			
			MRTableFilter.getInstance().setFilterData(filterData);
			setVisible(false);
		}
		
		if (e.getSource() == clearButton) {
			
			clear();
		}
		
		if (e.getSource() == cancelButton) {
			
			setVisible(false);
		}
		
	}

	private void clear() {
	
		statusListModel.clear();
		discListModel.clear();
		MRTableFilter.getInstance().setFilterData(null);
		setVisible(false);
	}
}
