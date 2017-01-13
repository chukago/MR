package org.tco.tfm.mr;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.tco.tfm.mr.DatabaseManager.SimpleListRecord;

public class SecondApproverDialog extends JDialog {
	
	public static class SecondApprData_t {
		
		SimpleListRecord supplier;
		Date eta;
		boolean whseCheck;
		Double cost;
		Currency currency;
		String comment;
	}
	
	private class DateLabelFormatter extends AbstractFormatter {

		private static final long serialVersionUID = 1L;
		private String datePattern = "yyyy-MM-dd";
	    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	    @Override
	    public Object stringToValue(String text) throws ParseException {
	        return dateFormatter.parseObject(text);
	    }

	    @Override
	    public String valueToString(Object value) throws ParseException {
	        if (value != null) {
	            Calendar cal = (Calendar) value;
	            return dateFormatter.format(cal.getTime());
	        }

	        return "";
	    }

	}
	
	private static final long serialVersionUID = 1L;

	private JComboBox<SimpleListRecord> supplierList = new JComboBox<SimpleListRecord>();
	private JDatePickerImpl etaInput;
	private JTextField costInput = new JTextField();
	
	private JRadioButton whseYes = new JRadioButton("Yes");
	private JRadioButton whseNo = new JRadioButton("No");
	
	private JRadioButton currencyUSD = new JRadioButton("USD");
	private JRadioButton currencyKZT = new JRadioButton("KZT");
	
	private JTextArea commentArea = new JTextArea();
	
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
	SecondApprData_t result = null;
	
	private SecondApproverDialog(Window parent) {
		
		super(parent);
		setTitle("Second approver form");
		setModal(true);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(2, 5, 2, 2);
		
		JLabel supplierLabel = new JLabel("Supplier");
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		add(supplierLabel, constraints);
		
		supplierList.setModel(Common.supplierManager);
		supplierList.setPreferredSize(new Dimension(24,24));
		
		JButton addSuppButton = new JButton();
		ImageIcon addIcon = new ImageIcon(getClass().getResource("icons/16x16/add.png"));
		addSuppButton.setIcon(addIcon);
		addSuppButton.setFocusable(false);
		addSuppButton.setPreferredSize(new Dimension(24, 24));
		addSuppButton.setMaximumSize(new Dimension(24, 24));
		
		addSuppButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String name = JOptionPane.showInputDialog(MR.getMainWindow(), "Input supplier name");
				
				if (name == null || name.isEmpty()) {
					
					return;
				}
				
				if (Common.supplierManager.add(name) == null) {
					
					JOptionPane.showMessageDialog(MR.getMainWindow(), "Cannot add the supplier");
				}
			}
			
		});
		
		Container supplierCont = new Container();
		supplierCont.setLayout(new BoxLayout(supplierCont, BoxLayout.X_AXIS));
		
		supplierCont.add(supplierList);
		supplierCont.add(addSuppButton);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		add(supplierCont, constraints);
		
		JLabel etaLabel = new JLabel("ETA");
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(etaLabel, constraints);
		
		UtilDateModel dateModel = new UtilDateModel();
		
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		
		JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
		etaInput = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		add(etaInput, constraints);
		
		JLabel costLabel = new JLabel("Cost");
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(costLabel, constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		add(costInput, constraints);
		
		JPanel whseButtonPanel = new JPanel();
		whseButtonPanel.setBorder(BorderFactory.createTitledBorder("TCO Whse Stock Checked"));
		
		whseButtonPanel.add(whseYes);
		whseButtonPanel.add(whseNo);
		
		ButtonGroup whseGroup = new ButtonGroup();
		whseGroup.add(whseYes);
		whseGroup.add(whseNo);
		
		whseYes.setSelected(true);
		
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(whseButtonPanel, constraints);	
		
		JPanel currencyButtonPanel = new JPanel();
		currencyButtonPanel.setBorder(BorderFactory.createTitledBorder("Currency"));
		
		currencyButtonPanel.add(currencyUSD);
		currencyButtonPanel.add(currencyKZT);
		
		currencyKZT.setSelected(true);
		
		ButtonGroup currencyGroup = new ButtonGroup();
		currencyGroup.add(currencyUSD);
		currencyGroup.add(currencyKZT);
		
		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(currencyButtonPanel, constraints);
		
		
		JLabel commentLabel = new JLabel("Comment");
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(commentLabel, constraints);
		
		JScrollPane commentScroll = new JScrollPane(commentArea);
		commentScroll.setPreferredSize(new Dimension(50, 50));
		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(commentScroll, constraints);
		
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				save();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				setVisible(false);
			}
		});
		
		JPanel buttonPanel = new JPanel();		
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.gridwidth = 2;
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
	
	private void save() {
		
		if (supplierList.getSelectedItem() == null) {
			
			JOptionPane.showMessageDialog(this, "Please select the supplier");
			return;
		}
		
		Date etaDate = (Date)etaInput.getModel().getValue();
		
		if (etaDate == null) {
			
			JOptionPane.showMessageDialog(this, "Please select the ETA");
			return;
		}
		
		if (costInput.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(this, "Please enter the cost");
			return;
		}
		
		if (!currencyUSD.isSelected() && !currencyKZT.isSelected()) {
			
			JOptionPane.showMessageDialog(this, "Please select the currency");
			return;
		}
		
		Double cost = null;
		
		try {
			
			cost = Double.valueOf(costInput.getText());
		}
		catch (NumberFormatException e) {
			
			JOptionPane.showMessageDialog(this, "Wrong cost format");
			return;
		}
		
		SimpleListRecord supplier = (SimpleListRecord)Common.supplierManager.getSelectedItem();
		boolean whseCheck = whseYes.isSelected() ? true : false;
		Currency currency = currencyUSD.isSelected() ? Currency.getInstance("USD") : Currency.getInstance("KZT");
		
		result = new SecondApprData_t();
		
		result.supplier = supplier;
		result.eta = etaDate;
		result.whseCheck = whseCheck;
		result.cost = cost;
		result.currency = currency;
		result.comment = commentArea.getText();
		
		setVisible(false);
	}
	
	public static SecondApprData_t getSecondApproverData(Window parent) {
		
		SecondApproverDialog dialog = new SecondApproverDialog(parent);
		dialog.setVisible(true);
		
		return dialog.result;
	}
}
