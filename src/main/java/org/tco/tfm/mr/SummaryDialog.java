package org.tco.tfm.mr;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import org.tco.tfm.mr.Common.MRRecord_t;

public class SummaryDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JLabel reqNumLabel = new JLabel();
	private JTextPane equipmentText = new JTextPane();

	private SummaryDialog(Window owner) {
		
		super(owner);
		
		setTitle("Request summary");
		setModal(true);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		reqNumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		add(reqNumLabel, constraints);
		
		equipmentText.setPreferredSize(new Dimension(250, 50));
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(equipmentText, constraints);
	}
	
	@Override
	public void setVisible(boolean visible) {
		
		this.setLocationRelativeTo(getParent());
		super.setVisible(visible);
	}
	
	private void setData(MRRecord_t data) {
		
		reqNumLabel.setText(Utils.getRequestNumber(data));
		equipmentText.setText(data.equipment);
		pack();
	}
	
	public static void showSummaryDialog(Window owner, MRRecord_t data) {
		
		SummaryDialog dialog = new SummaryDialog(owner);
		dialog.setData(data);
		dialog.setVisible(true);
	}
}

