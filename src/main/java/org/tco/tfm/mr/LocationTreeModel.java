package org.tco.tfm.mr;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.tco.tfm.mr.Common.LocationRec_t;

public class LocationTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;
	private static LocationTreeModel instance = null;
	
	private DatabaseManager dbManager = DatabaseManager.getInstance();
	
	static public LocationTreeModel getInstance() {
		
		if (instance == null) {
		
			instance = new LocationTreeModel(Utils.loadLocationTree());
		}
		
		return instance;
	}
	
	private LocationTreeModel(DefaultMutableTreeNode root) {
		
		super(root);
	}
	
	public boolean addNode(String name, MutableTreeNode parent) {
		
		for (int i = 0; i < parent.getChildCount(); ++i) {
			
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parent.getChildAt(i);
			Common.LocationRec_t childData = (Common.LocationRec_t)childNode.getUserObject();
			
			if (childData.name.compareToIgnoreCase(name) == 0) {
				
				return false;
			}
		}
		
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parent;
		Common.LocationRec_t parentData = (Common.LocationRec_t)parentNode.getUserObject();
		
		Common.LocationRec_t newLocation = new Common.LocationRec_t(0, name, parentData.id);
		
		if (!dbManager.saveLocationRecord(DatabaseManager.LOCATION_TABLE_NAME, newLocation)) {
			
			System.out.println("Cannot save location in database");
			return false;
		}
		
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
		newNode.setUserObject(newLocation);
		insertNodeInto(newNode, parent, parent.getChildCount());
		
		this.fireTreeNodesInserted(this, parentNode.getPath(),
				new int[]{parent.getChildCount() - 1}, new DefaultMutableTreeNode[]{newNode});
		
		return true;
	}
	
	public DefaultMutableTreeNode findNode(int id) {
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.getRoot();
		Common.LocationRec_t rootData = (Common.LocationRec_t) root.getUserObject();
		
		if (id == rootData.id) {
			
			return root;
		}
		
		for (int i = 0; i < root.getChildCount(); ++i) {
			
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)root.getChildAt(i);
			Common.LocationRec_t childData = (Common.LocationRec_t)childNode.getUserObject();
			
			if (childData.id == id) {
				
				return childNode;
			}
			
			DefaultMutableTreeNode node = findNodeValue(id, childNode);
			
			if (node != null) {
				
				return node;
			}
		}
		
		return null;
	}
	
	private DefaultMutableTreeNode findNodeValue(int id, DefaultMutableTreeNode parentNode) {
		
		for (int i = 0; i < parentNode.getChildCount(); ++i) {
			
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)parentNode.getChildAt(i);
			Common.LocationRec_t childData = (Common.LocationRec_t)childNode.getUserObject();
			
			if (childData.id == id) {
				
				return childNode;
			}
			
			DefaultMutableTreeNode node = findNodeValue(id, childNode);
			
			if (node != null) {
				
				return node;
			}
		}
		
		return null;
	}
	
	public String getNodeStringPath(int id) {
		
		DefaultMutableTreeNode node = findNode(id);
		
		TreeNode[] path = node.getPath();
		
		if (path.length == 1) {
			
			LocationRec_t nodeData = (Common.LocationRec_t) node.getUserObject();
			return nodeData.name;
		}
		
		String result = new String();
		
		for (int i = 1; i < path.length; ++i) {
			
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) path[i];
			LocationRec_t nodeData = (Common.LocationRec_t) n.getUserObject();
			
			result += nodeData.name;
			
			if (i < path.length - 1) {
				
				result += "/";
			}
		}
		
		return result;
	}
}
