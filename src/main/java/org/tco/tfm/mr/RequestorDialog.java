package org.tco.tfm.mr;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.tco.tfm.mr.Common.LocationRec_t;
import org.tco.tfm.mr.Common.MRRecord_t;
import org.tco.tfm.mr.DatabaseManager.CrossLink;
import org.tco.tfm.mr.DatabaseManager.SimpleListRecord;
import org.tco.tfm.mr.LocationList.LocationListModel;

public class RequestorDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private JTextArea equipmentText = new JTextArea();
	private JTree locationTree = new JTree(LocationTreeModel.getInstance());
	private JComboBox<Common.Discipline_t> discCombo = new JComboBox<Common.Discipline_t>();
	private JComboBox<SimpleListRecord> whsCombo = new JComboBox<SimpleListRecord>();
	private JButton newLocButton = new JButton();
	private JButton addLocButton = new JButton();
	private JButton delLocButton = new JButton();
	private LocationList locationList = new LocationList();
	private JTextArea justText = new JTextArea();
	private JButton linkButton = new JButton();
	private JTextField linkText = new JTextField();
	private JTextArea commentText = new JTextArea();
	private JTextField assetText = new JTextField();
	
	private JRadioButton tfmStock = new JRadioButton("Stock");
	private JRadioButton tfmNonStock = new JRadioButton("Non Stock");
	
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
	private Common.MRRecord_t data;

	public RequestorDialog(Window parent) {
		
		super(parent);
		
		setTitle("Add request");
		setModal(true);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 5, 2, 2);
		
		
		JScrollPane equipmentScroll = new JScrollPane(equipmentText);
		equipmentScroll.setPreferredSize(new Dimension(240, 100));
		equipmentScroll.setBorder(BorderFactory.createTitledBorder("Equipment"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(equipmentScroll, constraints);
		
		locationTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				
				if (e.getPath() != null) {
					
					newLocButton.setEnabled(true);
					return;
				}
			}
		});
		
		JScrollPane locationScroll = new JScrollPane(locationTree);
		locationScroll.setPreferredSize(new Dimension(250, 200));
		locationScroll.setBorder(BorderFactory.createTitledBorder("Location"));
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(locationScroll, constraints);
		
		JPanel locOprPanel = new JPanel();
		locOprPanel.setLayout(new BoxLayout(locOprPanel, BoxLayout.Y_AXIS));
		
		ImageIcon newLocIcon = new ImageIcon(getClass().getResource("icons/16x16/add.png"));
		newLocButton.setIcon(newLocIcon);
		newLocButton.setFocusable(false);
		newLocButton.setPreferredSize(new Dimension(24, 24));
		newLocButton.setMaximumSize(new Dimension(24, 24));
		newLocButton.setEnabled(false);
		
		ImageIcon addLocIcon = new ImageIcon(getClass().getResource("icons/16x16/arrowright.png"));
		addLocButton.setIcon(addLocIcon);
		addLocButton.setFocusable(false);
		addLocButton.setPreferredSize(new Dimension(24, 24));
		addLocButton.setMaximumSize(new Dimension(24, 24));
		
		ImageIcon delLocIcon = new ImageIcon(getClass().getResource("icons/16x16/arrowleft.png"));
		delLocButton.setIcon(delLocIcon);
		delLocButton.setFocusable(false);
		delLocButton.setPreferredSize(new Dimension(24, 24));
		delLocButton.setMaximumSize(new Dimension(24, 24));
		
		newLocButton.addActionListener(this);
		addLocButton.addActionListener(this);
		delLocButton.addActionListener(this);
		
		locOprPanel.add(Box.createGlue());
		locOprPanel.add(newLocButton);
		locOprPanel.add(addLocButton);
		locOprPanel.add(delLocButton);
		locOprPanel.add(Box.createGlue());
		
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		add(locOprPanel, constraints);
		
		JScrollPane locListScroll = new JScrollPane(locationList);
		locListScroll.setBorder(BorderFactory.createTitledBorder("Selected locations"));
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(locListScroll, constraints);
		
		JLabel discLabel = new JLabel("Discipline");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(discLabel, constraints);
		
		discCombo.setModel(DisciplineListModel.getInstance());
		
		discCombo.setPreferredSize(new Dimension(150, 25));
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(discCombo, constraints);
		
		JLabel assetLabel = new JLabel("Asset number");
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(assetLabel, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(assetText, constraints);
		
		JLabel whsLabel = new JLabel("Whs");
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(whsLabel, constraints);
		
		whsCombo.setModel(new WhsListModel());
		
		whsCombo.setPreferredSize(new Dimension(100, 25));
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(whsCombo, constraints);
		
		JPanel tfmStockPanel = new JPanel();
		tfmStockPanel.setLayout(new BoxLayout(tfmStockPanel, BoxLayout.Y_AXIS));
		
		tfmStockPanel.add(tfmStock);
		tfmStockPanel.add(tfmNonStock);
		
		ButtonGroup tfmButtonGroup = new ButtonGroup();
		tfmButtonGroup.add(tfmStock);
		tfmButtonGroup.add(tfmNonStock);
		
		tfmStock.setSelected(true);
		
		tfmStockPanel.setBorder(BorderFactory.createTitledBorder("TFM Stock"));
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(tfmStockPanel, constraints);
		
		JLabel justLabel = new JLabel("Justification");
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(justLabel, constraints);
		
		justText.setLineWrap(true);
		justText.setWrapStyleWord(true);
		
		JScrollPane justScroll = new JScrollPane(justText);
		justScroll.setPreferredSize(new Dimension(1, 50));
		
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.gridheight = 6;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(justScroll, constraints);
		
		JLabel linkLabel = new JLabel("Link");
		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(linkLabel, constraints);
		
		linkText.setEditable(false);
		
		Container linkCont = new Container();
		linkCont.setLayout(new BoxLayout(linkCont, BoxLayout.X_AXIS));
		
		linkCont.add(linkText);
		ImageIcon linkIcon = new ImageIcon(getClass().getResource("icons/16x16/link.png"));
		linkButton.setIcon(linkIcon);
		linkButton.setFocusable(false);
		linkButton.setPreferredSize(new Dimension(24, 24));
		linkButton.setMaximumSize(new Dimension(24, 24));
		linkButton.addActionListener(this);
		linkCont.add(linkButton);
		
		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		add(linkCont, constraints);
		
		JScrollPane commentScroll = new JScrollPane(commentText);
		commentScroll.setPreferredSize(new Dimension(240, 100));
		commentScroll.setBorder(BorderFactory.createTitledBorder("Comment"));
		constraints.gridx = 0;
		constraints.gridy = 11;
		constraints.gridwidth = 3;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(commentScroll, constraints);
		
		saveButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();		
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		constraints.gridx = 0;
		constraints.gridy = 12;
		constraints.gridwidth = 3;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == addLocButton) {
			
			addLocation();
		}
		
		if (e.getSource() == delLocButton) {
			
			removeLocation();
		}
		
		if (e.getSource() == linkButton) {
			
			selectLink();
		}
		
		if (e.getSource() == cancelButton) {
			
			setVisible(false);
		}
		
		if (e.getSource() == saveButton) {
			
			save();
		}
		
		if (e.getSource() == newLocButton) {
			
			addNewLocation();
		}
	}
	
	private void addLocation() {
		
		TreePath path = locationTree.getSelectionPath();
		
		if (path == null) {
			
			return;
		}
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		
		DefaultListModel<DefaultMutableTreeNode> model = (DefaultListModel<DefaultMutableTreeNode>) locationList.getModel();
		model.addElement(node);
	}
	
	private void removeLocation() {
		
		int i = locationList.getSelectedIndex();
		
		if (i == -1) {
			
			return;
		}
		
		DefaultListModel<DefaultMutableTreeNode> model = (DefaultListModel<DefaultMutableTreeNode>) locationList.getModel();
		
		model.remove(i);
	}
	
	private void selectLink() {
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		
		int retVal = chooser.showDialog(this, "Select the folder");
		
		if (retVal != JFileChooser.APPROVE_OPTION) {
			
			return;
		}
		
		System.out.println();
		linkText.setText(chooser.getSelectedFile().getAbsolutePath());
	}
	
	private void save() {
		
		if (equipmentText.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Equipment enter the equipment description");
			return;
		}
		
		LocationListModel locationModel = (LocationListModel) locationList.getModel();
		
		if (locationModel.getSize() == 0) {
			
			JOptionPane.showMessageDialog(this, "Location list is empty");
			return;
		}
		
		Common.Discipline_t discipline = (Common.Discipline_t) discCombo.getSelectedItem();
		
		if (discipline == null) {
			
			JOptionPane.showMessageDialog(this, "Please select the discipline");
			return;
		}
		
		SimpleListRecord whs = (SimpleListRecord) whsCombo.getSelectedItem();
		
		if (whs == null) {
			
			JOptionPane.showMessageDialog(this, "Please select the whs");
			return;
		}
		
		if (justText.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Please enter the justification");
			return;
		}
		
		if (linkText.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Please select the link");
			return;
		}
		
		if (assetText.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Please enter the asset number");
			return;
		}
		
		
		MRRecord_t newRecord = new MRRecord_t();
		
		newRecord.reqDate = new Date();
		newRecord.equipment = equipmentText.getText();
		newRecord.discipline = discipline.id;
		newRecord.whs = whs.id;
		newRecord.justification = justText.getText();
		newRecord.link = linkText.getText();
		newRecord.status = Common.Status_t.REQUEST;
		newRecord.tfmStock = tfmStock.isSelected() ? true : false;
		newRecord.comment = commentText.getText();
		newRecord.asset_number = assetText.getText();
		
		for (int i = 0; i < locationModel.getSize(); ++i) {
			
			LocationRec_t nodeData = (LocationRec_t) locationModel.get(i).getUserObject();
			
			newRecord.locations.add(new CrossLink(0, 0, nodeData.id));
		}
		
		if (!Utils.saveMrRecord(newRecord)) {
			
			JOptionPane.showMessageDialog(this, "Cannot save record");
			return;
		}
		
		setVisible(false);
	}
	
	public void setRecord(Common.MRRecord_t newData) {
		
		data = newData;
		
		equipmentText.setText(newData.equipment);
		
		LocationListModel locationModel = (LocationListModel) locationList.getModel();
		LocationTreeModel locTreeModel = LocationTreeModel.getInstance();
		for (int i = 0; i < newData.locations.size(); ++i) {
			
			DefaultMutableTreeNode node = locTreeModel.findNode(newData.locations.get(i).second);
			locationModel.addElement(node);
		}
		
		DisciplineListModel discModel = DisciplineListModel.getInstance();
		
		for (int i = 0; i < discModel.getSize(); ++i) {
			
			if (discModel.getElementAt(i).id == newData.discipline) {
				
				discCombo.setSelectedIndex(i);
				break;
			}
		}
		
		WhsListModel whsModel = (WhsListModel) whsCombo.getModel();
		
		for (int i = 0; i < whsModel.getSize(); ++i) {
			
			if (whsModel.getElementAt(i).id == newData.whs) {
				
				whsCombo.setSelectedIndex(i);
				break;
			}
		}
		
		justText.setText(newData.justification);
		
		linkText.setText(newData.link);
	}
	
	private void addNewLocation() {
		
		TreePath path = locationTree.getSelectionPath();
		
		if (path == null) {
			
			return;
		}
		
		LocationTreeModel model = (LocationTreeModel) locationTree.getModel();
		
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		
		String name = JOptionPane.showInputDialog(this, "Enter new location name");
		
		if (name == null || name.isEmpty()) {
			
			return;
		}
		
		model.addNode(name, parentNode);
	}
}
