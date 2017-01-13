package org.tco.tfm.mr;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.tco.tfm.mr.Common.LocationRec_t;

public class LocationList extends JList<DefaultMutableTreeNode> {
	
	public static class LocationListRenderer extends JLabel implements ListCellRenderer<DefaultMutableTreeNode> {

		private static final long serialVersionUID = 1L;

		public LocationListRenderer() {
			
			super();
			
			this.setOpaque(true);
		}
		
		@Override
		public Component getListCellRendererComponent(JList<? extends DefaultMutableTreeNode> list,
				DefaultMutableTreeNode value, int index, boolean isSelected, boolean cellHasFocus) {
			
			if (isSelected) {
				
				setForeground(list.getSelectionForeground());
				setBackground(list.getSelectionBackground());
			}
			else {
				
				setForeground(list.getForeground());
				setBackground(list.getBackground());
			}
			
			this.setText(getStringRep(value));
			
			return this;
		}
		
		private String getStringRep(DefaultMutableTreeNode node) {
			
			if (node == null) {
				
				return null;
			}
			
			TreeNode[] path = node.getPath();
			
			String result = new String();
			
			for (TreeNode n : path) {
				
				DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode)n;
				LocationRec_t nodeData = (LocationRec_t)pathNode.getUserObject();
				
				result += nodeData.name;
				
				if (n != path[path.length - 1]) {
					
					result += " > ";
				}
			}
			
			return result;
		}
	}
	
	public static class LocationListModel extends DefaultListModel<DefaultMutableTreeNode> {

		private static final long serialVersionUID = 1L;
		
		public LocationListModel() {
			
			super();
		}
		
		@Override
		public void addElement(DefaultMutableTreeNode element) {
			
			for (int i = 0; i < getSize(); ++i) {
				
				if (isIntersection(element, getElementAt(i))) {
					
					return;
				}
			}
			
			super.addElement(element);
		}
		
		// Check if one path is the subpath of second
		private boolean isIntersection(DefaultMutableTreeNode first, DefaultMutableTreeNode second) {
			
			TreeNode[] firstPath = first.getPath();
			TreeNode[] secondPath = second.getPath();
			
			int steps = Math.min(firstPath.length, secondPath.length);
			
			for (int i = 0; i < steps; ++i) {
				
				if (firstPath[i] != secondPath[i]) {
					
					return false;
				}
			}
			
			return true;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	public LocationList() {
		
		super();
		
		this.setModel(new LocationListModel());
		this.setCellRenderer(new LocationListRenderer());
	}

}
