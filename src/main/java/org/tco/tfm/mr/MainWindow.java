package org.tco.tfm.mr;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

public class MainWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private MRTable table = new MRTable();
	
	private JButton filterButton = new JButton();
	private JButton accountButton = new JButton();
	private MRTableFilterDialog filterDialog = new MRTableFilterDialog(this);
	
	public MainWindow() {
		
		super();
		
		setTitle("MR Status v." + ProgramVersion.getVersionString());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JToolBar mainToolBar = new JToolBar();
		mainToolBar.setFloatable(false);
		
		add(mainToolBar, BorderLayout.NORTH);
		
		ImageIcon addIcon = new ImageIcon(getClass().getResource("icons/48x48/filter.png"));
		filterButton.setIcon(addIcon);
		filterButton.setFocusable(false);
		filterButton.addActionListener(this);
		
		ImageIcon peopleIcon = new ImageIcon(getClass().getResource("icons/48x48/people.png"));
		accountButton.setIcon(peopleIcon);
		accountButton.setFocusable(false);
		accountButton.setToolTipText("Accounts");
		accountButton.addActionListener(this);
		
		mainToolBar.add(filterButton);
		mainToolBar.add(accountButton);
		
		JScrollPane tableScroll = new JScrollPane(table);
		
		add(tableScroll, BorderLayout.CENTER);
		
		setSecurityContext();
		
		pack();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == accountButton) {
			
			AccountEditor ed = new AccountEditor(this);
			ed.setVisible(true);
		}
		
		if (e.getSource() == filterButton) {
			
			filterDialog.setVisible(true);
		}
	}
	
	private void setSecurityContext() {
		
		SecurityManager secManager = SecurityManager.getInstance();
		
		//filterButton.setEnabled(secManager.currentHasRole(Common.Role_t.REQUESTOR));
		
		if (secManager.hasSuperUser()) {
		
			accountButton.setEnabled(secManager.currentHasRole(Common.Role_t.ADMINISTRATOR));
		}
	}
}
