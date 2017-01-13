package org.tco.tfm.mr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.tco.tfm.mr.Common.JournalRec_t;
import org.tco.tfm.mr.DatabaseManager.CrossLink;

public class MRTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private static MRTableModel instance = null;
	
	public ArrayList<CrossLink> locations = new ArrayList<CrossLink>();
	//public ArrayList<EquipmentRec_t> equipment = new ArrayList<EquipmentRec_t>();
	public ArrayList<JournalRec_t> journal = new ArrayList<JournalRec_t>();
	
	public static final int ID_COLUMN_POS = 0;
	public static final int DATE_COLUMN_POS = 1;
	public static final int NUMBER_COLUMN_POS = 2;
	public static final int EQUIPMENT_COLUMN_POS = 3;
	public static final int ETA_COLUMN_POS = 4;
	public static final int DELIVERY_COLUMN_POS = 5;
	public static final int WHS_COLUMN_POS = 6;
	public static final int ASSET_COLUMN_POS = 7;
	public static final int LOCATION_COLUMN_POS = 8;
	public static final int CC_COLUMN_POS = 9;
	public static final int DISCIPLINE_COLUMN_POS = 10;
	public static final int SUPPLIER_COLUMN_POS = 11;
	public static final int COST_COLUMN_POS = 12;
	public static final int TFM_STOCK_COLUMN_POS = 13;
	public static final int TCO_WHS_COLUMN_POS = 14;
	public static final int JUSTIFICATION_COLUMN_POS = 15;
	public static final int LINK_COLUMN_POS = 16;
	public static final int STATUS_COLUMN_POS = 17;

	private LocationTreeModel locModel = LocationTreeModel.getInstance();
	
	private ArrayList<Common.MRRecord_t> data = new ArrayList<Common.MRRecord_t>();
	private SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private DisciplineListModel discModel = DisciplineListModel.getInstance();
	private DatabaseManager dbManager = DatabaseManager.getInstance();
	private SimpleListRecordModel suppList = new SimpleListRecordModel(DatabaseManager.SUPPLIER_TABLE_NAME);
	
	public static MRTableModel getInstance() {
		
		if (instance == null) {
			
			instance = new MRTableModel();
		}
		
		return instance;
	}
	
	private MRTableModel() {
		
		super();
		
		data = Utils.findMrRecord(null);
	}
	
	@Override
	public int getColumnCount() {
		
		return 18;
	}

	@Override
	public int getRowCount() {
		
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if (rowIndex < 0 || rowIndex >= data.size()) {
		
			return null;
		}
		
		switch(columnIndex) {
		
		case ID_COLUMN_POS:
			
			return data.get(rowIndex).id;
		
		case DATE_COLUMN_POS:
			
			return defaultDateFormat.format(data.get(rowIndex).reqDate);
			
		case NUMBER_COLUMN_POS:
			
			for (int i = 0; i < discModel.getSize(); ++i) {
				
				if (discModel.getElementAt(i).id == data.get(rowIndex).discipline) {
					
					return Common.MR_DOC_NUMBER_PREFIX + 
							discModel.getElementAt(i).abbreviation + "-" + 
							String.valueOf(data.get(rowIndex).number);
				}
			}
			
			return data.get(rowIndex).number;
			
		case EQUIPMENT_COLUMN_POS:
			
			return data.get(rowIndex).equipment;
			
		case ETA_COLUMN_POS:
			
			if (data.get(rowIndex).eta == null) {
				
				return null;
			}
			
			return defaultDateFormat.format(data.get(rowIndex).eta);
			
		case DELIVERY_COLUMN_POS:
			
			if (data.get(rowIndex).delivery == null) {
				
				return null;
			}
			
			return defaultDateFormat.format(data.get(rowIndex).delivery);
			
		case WHS_COLUMN_POS:
			
			return Common.whseManager.getValue(data.get(rowIndex).whs);
			
		case ASSET_COLUMN_POS:
			
			return data.get(rowIndex).asset_number;
			
		case LOCATION_COLUMN_POS:
			
			String stringPath = new String();
			
			for (CrossLink l : data.get(rowIndex).locations) {
				
				DefaultMutableTreeNode node = locModel.findNode(l.second);
				
				if (node == null) {
					
					return "Unknown location";
				}
				
				TreeNode[] path = node.getPath();
				
				if (path.length == 1) {
					
					DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode)path[0];
					Common.LocationRec_t pathData = (Common.LocationRec_t)pathNode.getUserObject();
					
					return pathData.name;
				}
				
				for (int i = 1; i < path.length; ++i) {
					
					DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode)path[i];
					Common.LocationRec_t pathData = (Common.LocationRec_t)pathNode.getUserObject();
					
					stringPath += pathData.name;
					
					if (i != path.length - 1) {
					
						stringPath += " > ";
					}
				}
				
				stringPath += "\n";
			}
			
			return stringPath;
			
		case CC_COLUMN_POS:
			
			if (data.get(rowIndex).cc <= 0) {
				
				return "";
			}
			
			return data.get(rowIndex).cc;
			
		case DISCIPLINE_COLUMN_POS:
			
			for (int i = 0; i < discModel.getSize(); ++i) {
				
				if (discModel.getElementAt(i).id == data.get(rowIndex).discipline) {
					
					return discModel.getElementAt(i).name;
				}
			}
			
			return data.get(rowIndex).discipline;
			
		case SUPPLIER_COLUMN_POS:
			
			for (int i = 0; i < suppList.getSize(); ++i) {
				
				if (suppList.getElementAt(i).id == data.get(rowIndex).supplier) {
					
					return suppList.getElementAt(i).value;
				}
			}
			
			return null;
			
		case COST_COLUMN_POS:
			
			String result = new String();
			
			if (data.get(rowIndex).cost > 0) {
				
				if (data.get(rowIndex).currency != null) {
					
					result += data.get(rowIndex).currency.getCurrencyCode() + " ";
				}
				
				//result += String.format(String.valueOf(data.get(rowIndex).cost), "#");
				//result += (new BigDecimal(data.get(rowIndex).cost)).toPlainString();
				//result += String.format(String.valueOf(data.get(rowIndex).cost), "%.2f\n");
				result += String.valueOf(data.get(rowIndex).cost);
				
				return result;
			}
			
			return null;
		
		case TFM_STOCK_COLUMN_POS:
			
			if (data.get(rowIndex).tfmStock != null) {
				
				return data.get(rowIndex).tfmStock ? "Stock" : "Non stock";
			}
			
			return null;
			
		case TCO_WHS_COLUMN_POS:
			
			if (data.get(rowIndex).tcoWhs != null) {
				
				return data.get(rowIndex).tcoWhs ? "YES" : "NO";
			}
			
			return null;
			
		case JUSTIFICATION_COLUMN_POS:
			
			return data.get(rowIndex).justification;
		
		case LINK_COLUMN_POS:
			
			return data.get(rowIndex).link;
			
		case STATUS_COLUMN_POS:
			
			return data.get(rowIndex).status.getStringRep();
		}

		return null;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		
		switch(columnIndex) {
		
		case ID_COLUMN_POS:
			
			return "ID";
			
		case DATE_COLUMN_POS:
			
			return "Date";
			
		case NUMBER_COLUMN_POS:
			
			return "Number";
			
		case EQUIPMENT_COLUMN_POS:
			
			return "Equipment";
			
		case ETA_COLUMN_POS:
			
			return "ETA";
			
		case DELIVERY_COLUMN_POS:
			
			return "Delivery";
			
		case WHS_COLUMN_POS:
			
			return "Whs";
			
		case ASSET_COLUMN_POS:
			
			return "Asset number";
			
		case LOCATION_COLUMN_POS:
			
			return "Location";
			
		case CC_COLUMN_POS:
			
			return "CC";
			
		case DISCIPLINE_COLUMN_POS:
			
			return "Discipline";
			
		case SUPPLIER_COLUMN_POS:
			
			return "Supplier";
			
		case COST_COLUMN_POS:
			
			return "Cost";
			
		case TFM_STOCK_COLUMN_POS:
			
			return "TFM Stock";
			
		case TCO_WHS_COLUMN_POS:
			
			return "TCO Whse";
			
		case JUSTIFICATION_COLUMN_POS:
			
			return "Justification";
			
		case LINK_COLUMN_POS:
			
			return "Link";
			
		case STATUS_COLUMN_POS:
			
			return "Status";
		}

		return null;
	}
	
	public boolean save(Common.MRRecord_t record) {
		
		if (record == null) {
			
			return false;
		}
		
		if (record.id != 0) {
			
			return updateRecord(record);
		}
		
		return addRecord(record);
	}
	
	private boolean updateRecord(Common.MRRecord_t record) {
		
		int foundPos = -1;
		
		for (int i = 0; i < data.size(); ++i) {
			
			if (record.id == data.get(i).id) {
				
				foundPos = i;
				break;
			}
		}
		
		if (foundPos == -1) {
			
			return false;
		}
		
		for (JournalRec_t j : record.journal) {
			
			if (j.id != 0) {
				
				continue;
			}
			
			j.workid = data.get(foundPos).id;
			
			if (!dbManager.saveJournalRecord(DatabaseManager.JOURNAL_TABLE_NAME, j)) {
				
				System.out.println("Cannot save MR record");
				return false;
			}
		}
		
		if (!dbManager.saveMrRecord(DatabaseManager.MR_TABLE_NAME, record)) {
			
			System.out.println("Cannot save MR record");
			return false;
		}
		
		// Comment is writing only in journal
		// Must be clean after saving a record
		record.comment = null;
		
		data.set(foundPos, record.clone());
		
		this.fireTableRowsUpdated(foundPos, foundPos);
		
		return true;
	}
	
	private boolean addRecord(Common.MRRecord_t record) {
		
		int number = Utils.reserveDisciplineNumber(record.discipline);
		
		if (number == 0) {
			
			System.out.println("Cannot get document number");
			return false;
		}
		
		record.number = number;
		
		record.status = Common.Status_t.REQUEST;
		
		if (!dbManager.saveMrRecord(DatabaseManager.MR_TABLE_NAME, record)) {
			
			System.out.println("Cannot save MR record");
			return false;
		}
		
		for (CrossLink l : record.locations) {
			
			if (l.id != 0) {
				
				continue;
			}
			
			l.first = record.id;
			
			if (!dbManager.saveCrossLink(l, DatabaseManager.MR_LOCATION_LINK_TABLE_NAME)) {
				
				System.out.println("Cannot save MR<->location link");
				return false;
			}
		}
		
		for (JournalRec_t j : record.journal) {
			
			if (j.id != 0) {
				
				continue;
			}
			 
			j.workid = record.id;
			
			if (!dbManager.saveJournalRecord(DatabaseManager.JOURNAL_TABLE_NAME, j)) {
				
				System.out.println("Cannot save journal record");
				return false;
			}
		}
		
		record.comment = null;
		
		data.add(record.clone());
		this.fireTableRowsInserted(data.size() - 1, data.size() - 1);
		
		return true;
	}

	public Common.MRRecord_t getDataAt(int pos) {
		
		if (data.size() == 0 || pos < 0 || pos >= data.size()) {
			
			return null;
		}
		
		return data.get(pos);
	}
}
