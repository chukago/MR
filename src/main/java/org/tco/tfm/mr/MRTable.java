package org.tco.tfm.mr;

import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.tco.tfm.mr.Common.MRRecord_t;

public class MRTable extends JTable implements ActionListener{

	private class TableMouseListener extends MouseAdapter {
		
		private JTable table;
		
		public TableMouseListener(JTable table) {
			
			this.table = table;
		}
		
		@Override
		public void mousePressed(MouseEvent event) {
			
			Point point = event.getPoint();
			int currentRow = table.rowAtPoint(point);
			table.setRowSelectionInterval(currentRow, currentRow);
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private SecurityManager secManager = SecurityManager.getInstance();
	private MRTableModel model = MRTableModel.getInstance();
	
	private JPopupMenu contextMenu = new JPopupMenu();
	private JMenuItem addItem = new JMenuItem("Add");
	private JMenuItem approveItem = new JMenuItem("Approve");
	private JMenuItem rejectItem = new JMenuItem("Reject");
	private JMenuItem sendBackItem = new JMenuItem("Send back");
	private JMenuItem reviewItem = new JMenuItem("Review");
	private JMenuItem deliveryItem = new JMenuItem("Delivery");
	private JMenuItem openLinkItem = new JMenuItem("Open folder");
	
	public MRTable() {
		
		super();
		
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setModel(model);
		setDefaultRenderer(Object.class, new MRTableRenderer());
		
		setRowSorter(MRTableSorter.getInstance());
		
		addMouseListener(new TableMouseListener(this));
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				/*
				if (e.getButton() == MouseEvent.BUTTON1 &&
						e.getClickCount() >= 2) {
					
					showSummary();
				}
				*/
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
		
		initContextMenu(contextMenu);
		
		contextMenu.addPopupMenuListener(new PopupMenuListener(){

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				
				updateContextMenu();
			}
			
		});
		
		this.setComponentPopupMenu(contextMenu);
		
		SettingsManager settManager = SettingsManager.getInstance();
		
		ArrayList<Integer> widths =  settManager.getColumnWidhts();
		
		if (widths.size() <= this.getColumnCount()) {
			
			for (int i = 0; i < widths.size(); ++i) {
				
				this.getColumnModel().getColumn(i).setMinWidth(widths.get(i));
			}
		}
	}
	
	private void initContextMenu(JPopupMenu menu) {
		
		menu.add(addItem);
		menu.add(approveItem);
		menu.add(rejectItem);
		//menu.add(sendBackItem);
		//menu.add(reviewItem);
		menu.add(deliveryItem);
		menu.add(openLinkItem);
		
		addItem.addActionListener(this);
		approveItem.addActionListener(this);
		rejectItem.addActionListener(this);
		sendBackItem.addActionListener(this);
		reviewItem.addActionListener(this);
		deliveryItem.addActionListener(this);
		openLinkItem.addActionListener(this);
	}
	
	private void updateContextMenu() {
		
		addItem.setEnabled(false);
		approveItem.setEnabled(false);
		rejectItem.setEnabled(false);
		sendBackItem.setEnabled(false);
		reviewItem.setEnabled(false);
		deliveryItem.setEnabled(false);
		
		Common.UserData_t userContext = secManager.getCurrentContext();
		
		if (userContext == null) {
			
			return;
		}
		
		if (secManager.currentHasRole(Common.Role_t.REQUESTOR)) {
			
			addItem.setEnabled(true);
		}
		
		
		
		MRRecord_t selected = model.getDataAt(getRowSorter().convertRowIndexToModel(getSelectedRow()));
		
		if (selected == null) {
			
			return;
		}
		
		switch(selected.status) {
		
		case REQUESTOR_REVIEW:
			
			if (selected.journal == null || selected.journal.size() == 0) {
				
				break;
			}
			
			if (selected.journal.get(0).position.compareToIgnoreCase(userContext.position) == 0) {
				
				reviewItem.setEnabled(true);
			}
			
			break;
			
		case REQUEST:
			
			if (secManager.currentHasRole(Common.Role_t.FIRST_APPROVER)) {
				
				approveItem.setEnabled(true);
				rejectItem.setEnabled(true);
				sendBackItem.setEnabled(true);
			}
			
			break;
			
		case FIRST_APPROVED:
			
			if (secManager.currentHasRole(Common.Role_t.SECOND_APPROVER) &&
					secManager.currentHasDiscipline(selected.discipline)) {
				
				approveItem.setEnabled(true);
				rejectItem.setEnabled(true);
				sendBackItem.setEnabled(true);
			}
			
			break;
			
		case SECOND_APPROVED:
			
			if (secManager.currentHasRole(Common.Role_t.THIRD_APPROVER) &&
					secManager.currentHasDiscipline(selected.discipline)) {
				
				approveItem.setEnabled(true);
				rejectItem.setEnabled(true);
				sendBackItem.setEnabled(true);
			}
			
			break;
			
		case THIRD_APPROVED:
			
			if (selected.journal == null || selected.journal.size() == 0) {
				
				break;
			}
			
			
			if (selected.journal.get(0).position.compareToIgnoreCase(userContext.position) == 0) {
				
				deliveryItem.setEnabled(true);
			}
			
			break;
			
		case FIRST_REVIEW:
			
			if (secManager.currentHasRole(Common.Role_t.FIRST_APPROVER)) {
				
				approveItem.setEnabled(true);
				rejectItem.setEnabled(true);
				sendBackItem.setEnabled(true);
			}
			
			break;
			
		case SECOND_REVIEW:
			
			if (secManager.currentHasRole(Common.Role_t.SECOND_APPROVER) &&
					secManager.currentHasDiscipline(selected.discipline)) {
				
				approveItem.setEnabled(true);
				rejectItem.setEnabled(true);
				sendBackItem.setEnabled(true);
			}
			
			break;
		
		case FIRST_REJECTED:
			break;
		case SECOND_REJECTED:
			break;
		case THIRD_REJECTED:
			break;
		case NONE:
			break;
		case DELIVERED:
			break;
		default:
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == addItem) {
			
			addRequest();
		}
		
		if (e.getSource() == approveItem) {
			
			approveRequest();
		}
		
		if (e.getSource() == rejectItem) {
			
			rejectRequest();
		}
		
		if (e.getSource() == deliveryItem) {
			
			deliveryRequest();
		}
		
		if (e.getSource() == openLinkItem) {
			
			openLink();
		}
	}
	
	private void addRequest() {
		
		RequestorDialog dialog = new RequestorDialog(MR.getMainWindow());
		dialog.setVisible(true);
	}
	
	private void approveRequest() {
		
		MRRecord_t selected = model.getDataAt(getRowSorter().convertRowIndexToModel(getSelectedRow()));
		
		if (selected == null) {
			
			return;
		}
		
		switch (selected.status) {
		
		case REQUEST:
			
			selected.status = Common.Status_t.FIRST_APPROVED;
			Utils.saveMrRecord(selected);
			break;
			
		case FIRST_APPROVED:
			
			SecondApproverDialog.SecondApprData_t data = SecondApproverDialog.getSecondApproverData(MR.getMainWindow());
			
			if (data != null) {
				
				selected.supplier = data.supplier.id;
				selected.eta = data.eta;
				selected.tcoWhs = data.whseCheck;
				selected.cost = data.cost;
				selected.currency = data.currency;
				selected.comment = data.comment;
				selected.status = Common.Status_t.SECOND_APPROVED;
				
				Utils.saveMrRecord(selected);
			}
			
			break;
			
		case SECOND_APPROVED:
			
			String ccString = JOptionPane.showInputDialog(MR.getMainWindow(), "Enter the CC");
			
			Integer cc = null;
			
			try {
				
				cc = Integer.parseInt(ccString);
			}
			catch (NumberFormatException e) {
				
				JOptionPane.showMessageDialog(MR.getMainWindow(), "Wrong number format");
				return;
			}
			
			selected.cc = cc;
			selected.status = Common.Status_t.THIRD_APPROVED;
			Utils.saveMrRecord(selected);
			
			break;
		
		default:
			break;
		}
	}
	
	private void rejectRequest() {
		
		MRRecord_t selected = model.getDataAt(getRowSorter().convertRowIndexToModel(getSelectedRow()));
		
		if (selected == null) {
			
			return;
		}
		
		String comment = JOptionPane.showInputDialog(MR.getMainWindow(), "Enter comment");
		
		if (comment == null || comment.isEmpty()) {
			
			JOptionPane.showMessageDialog(MR.getMainWindow(), "Please enter comment");
			return;
		}
		
		switch (selected.status) {

		case REQUEST:
			
			selected.status = Common.Status_t.FIRST_REJECTED;
			break;
		
		case FIRST_APPROVED:
			
			selected.status = Common.Status_t.SECOND_REJECTED;
			break;

		case SECOND_APPROVED:
			
			selected.status = Common.Status_t.THIRD_REJECTED;
			break;

		default:
			break;
		
		}
		
		selected.comment = comment;
		
		Utils.saveMrRecord(selected);
	}
	
	private void deliveryRequest() {
		
		MRRecord_t selected = model.getDataAt(getRowSorter().convertRowIndexToModel(getSelectedRow()));
		
		if (selected == null) {
			
			return;
		}
		
		if (selected.status != Common.Status_t.THIRD_APPROVED) {
			
			return;
		}
		
		selected.delivery = new Date();
		selected.status = Common.Status_t.DELIVERED;
		
		Utils.saveMrRecord(selected);
	}
	
	private void openLink() {
		
		MRRecord_t selected = model.getDataAt(getRowSorter().convertRowIndexToModel(getSelectedRow()));
		
		if (selected == null) {
			
			return;
		}
		
		try {
			
			Desktop.getDesktop().browse(Paths.get(selected.link).toUri());
		} 
		catch (IOException e) {
			
			JOptionPane.showMessageDialog(MR.getMainWindow(), "Cannot open link");
			e.printStackTrace();
		}
	
	}
	
	/*
	private void showSummary() {
		
		MRRecord_t selected = model.getDataAt(getRowSorter().convertRowIndexToModel(getSelectedRow()));
		
		if (selected == null) {
			
			return;
		}

		SummaryDialog.showSummaryDialog(MR.getMainWindow(), selected);
	}
	*/
}
