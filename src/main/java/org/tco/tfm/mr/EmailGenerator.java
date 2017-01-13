package org.tco.tfm.mr;

import java.text.SimpleDateFormat;

import org.tco.tfm.mr.Common.JournalRec_t;
import org.tco.tfm.mr.DatabaseManager.CrossLink;

public class EmailGenerator {

	private Common.MRRecord_t record;
	private SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	
	public EmailGenerator(Common.MRRecord_t record) {
		
		this.record = record;
	}
	
	public String getHtml() {
		
		String result = new String();
		
		result += "<html>\n<body>\n";
		result += "<table width='840' border='0'>\n";
		
		//Header
		result += "<tr><td><h2 align=center>";
		result += getHeader();
		result += "</h2></td></tr>";
		
		result += "<tr><td>" + getInformationTable() + "</td></tr>";
		
		result += "<tr><td>" + getLocationTable() + "</td></tr>";
		
		result += "<tr><td>" + getJournalTable() + "</td></tr>";
		
		result += "</table>\n";
		result += "</body>\n</html>";
		
		
		return result;
	}
	
	private String getInformationTable() {
		
		String result = new String();
		
		result += "<table align='center' width='75%' border='1'>\n";
		result += "<tr><td align='center' bgcolor='silver' colspan='2'>Summary</td></tr>";
		result += "<tr><td>Date</td><td>" + defaultDateFormat.format(record.reqDate) + "</tr>\n";
		result += "<tr><td>Number</td><td>" + Utils.getRequestNumber(record) + "</tr>\n";
		result += "<tr><td>Equipment</td><td>" + record.equipment + "</tr>\n";
		result += "<tr><td>Asset number</td><td>" + record.asset_number + "</tr>\n";
		result += "<tr><td>Justification</td><td>" + record.justification + "</tr>\n";
		result += "<tr><td>Link</td><td><a href='" + record.link + "'>"+ record.link +"</a></tr>\n";
		
		if (record.whs != 0) {
			
			WhsListModel whsModel = new WhsListModel();
			result += "<tr><td>Whse</td><td>" + whsModel.getValue(record.whs) + "</tr>\n";
			
		}
		
		if (record.cc != 0) {
			
			result += "<tr><td>CC</td><td>" + String.valueOf(record.cc) + "</tr>\n";	
		}
		
		if (record.eta != null) {
			
			result += "<tr><td>ETA</td><td>" + defaultDateFormat.format(record.eta) + "</tr>\n";
		}
		
		if (record.delivery != null) {
			
			result += "<tr><td>Delivery</td><td>" + defaultDateFormat.format(record.delivery) + "</tr>\n";
		}
		
		if (record.tfmStock != null) {
			
			result += "<tr><td>TFM Stock</td><td>" + (record.tfmStock ? "Stock" : "Non Stock") + "</tr>\n";
		}
		
		if (record.tcoWhs != null) {
			
			result += "<tr><td>TFM Stock</td><td>" + (record.tcoWhs ? "Yes" : "No") + "</tr>\n";
		}
		
		if (record.cost > 0) {
			
			String costString = new String();
			
			if (record.currency != null) {
				
				costString += record.currency.getCurrencyCode() + " ";
			}
			
			costString += String.valueOf(record.cost);
			
			result += "<tr><td>Cost</td><td>" + costString + "</tr>\n";
		}
		
		if (record.supplier != 0) {
			
			SimpleListRecordModel supplierModel = new SimpleListRecordModel(
					DatabaseManager.SUPPLIER_TABLE_NAME);
			
			for (int i = 0; i < supplierModel.getSize(); ++i) {
				
				if (supplierModel.getElementAt(i).id == record.supplier) {
					
					result += "<tr><td>Supplier</td><td>" + supplierModel.getElementAt(i).value + "</tr>\n";
					break;
				}
			}
		}
		
		result += "</table>\n";
		
		return result;
	}
	
	private String getLocationTable() {
		
		String result = new String();
		LocationTreeModel model = LocationTreeModel.getInstance();
		
		result += "<table align='center' width='75%' border='1'>\n";
		result += "<tr><td align='center' bgcolor='silver'>Locations</td></tr>\n";
		
		for (CrossLink l : record.locations) {
			
			result += "<tr><td>" + model.getNodeStringPath(l.second) + "</td></tr>\n";
		}
		
		result += "</table>\n";
		
		return result;
	}
	
	private String getJournalTable() {
		
		String result = new String();
		
		result += "<table align='center' width='75%' border='1'>\n";
		result += "<tr><td align='center' bgcolor='silver' colspan='5'>Journal</td></tr>";
		
		result += "<tr>";
		result += "<td>Date</td>";
		result += "<td>Name</td>";
		result += "<td>Position</td>";
		result += "<td>Status</td>";
		result += "<td>Comment</td>";
		result += "</tr>\n";
		
		for (JournalRec_t j : record.journal) {
			
			result += "<tr>";
			result += "<td>" + defaultDateFormat.format(j.date) + "</td>";
			result += "<td>" + j.name + "</td>";
			result += "<td>" + j.position + "</td>";
			result += "<td>" + j.status.getStringRep() + "</td>";
			
			if (j.comment != null) {
			
				result += "<td>" + j.comment + "</td>";
			}
			else {
				
				result += "<td></td>";
			}
			
			result += "</tr>\n";
			
		}
		
		result += "</table>\n";
		
		return result;
	}
	
	public String getHeader() {
		
		switch (record.status) {
		
		case DELIVERED:
			
			return "Equipment has been delivered";
		
		case FIRST_APPROVED:
			
			return "Request has been approved by first approver";
		
		case FIRST_REJECTED:
			
			return "Request has been rejected by first approver";
		
		case FIRST_REVIEW:
			
			return "Request has been sent for review to first approver";
		
		case REQUEST:
			
			return "Request has been sent to first approver";
		
		case REQUESTOR_REVIEW:
			
			return "Request has been sent for review to requestor";
		
		case SECOND_APPROVED:
			
			return "Request has been approved by second approver";
		
		case SECOND_REJECTED:
			
			return "Request has been rejected by second approver";
		
		case SECOND_REVIEW:
			
			return "Request has been sent for review to second approver";
		
		case THIRD_APPROVED:
			
			return "Request has been approved by third approver";
		
		case THIRD_REJECTED:
			
			return "Request has been rejected by third approver";
			
		default:
				
			return null;
		}
	}
}
