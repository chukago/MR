package org.tco.tfm.mr;

import java.util.List;
import java.util.ArrayList;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Variant;


public final class MSOApplication {
	
	static private ActiveXComponent _application = null;
	private ActiveXComponent _item = null;
	private ActiveXComponent _attachments = null;
	
	private List<String> _toAddressList = new ArrayList<String>();
	private List<String> _ccAddressList = new ArrayList<String>();
	private String _subject = new String();
	private String _body = new String();
	private List<String> _attachmentList = new ArrayList<String>();
	
	public MSOApplication() {
		
		try {
			
			synchronized(this) {
				
				if (_application == null) {
					
					_application = new ActiveXComponent("Outlook.Application");
				}	
			}
			
			_item = _application.invokeGetComponent("CreateItem", new Variant(0));
			_attachments = _item.invokeGetComponent("Attachments");
		}
		catch (Exception e) {
			
			e.printStackTrace();
			
			_application = null;
			_item = null;
			_attachments = null;
		}
			
	}
	
	public boolean isValid() {
		
		return _application != null && _item != null && _attachments != null;
	}
	
	public void addToAddress(String address) {
		
		for (String a : _toAddressList) {
			
			if (a.compareToIgnoreCase(address) == 0) {
				
				return;
			}
		}
		
		_toAddressList.add(address);
	}
	
	public void addToAddress(ArrayList<String> addressList) {
		
		boolean found;
		
		for (String toAddition : addressList) {
		
			found = false;
			
			for (String a : _toAddressList) {
				
				if (a.compareToIgnoreCase(toAddition) == 0) {
					
					found = true;
					break;
				}
			}
			
			if (!found) {
				
				_toAddressList.add(toAddition);
			}
		}
	}
	
	public void addCcAddress(String address) {
		
		for (String a : _ccAddressList) {
			
			if (a.toLowerCase() == address.toLowerCase()) {
				
				return;
			}
		}
		
		_ccAddressList.add(address);
	}
	
	public void setSubject(String subject) {
		
		_subject = subject;
	}
	
	public void setBody(String body) {
		
		_body = body;
	}
	
	public void addAttachment(String link) {
	
		for (String a : _attachmentList) {
			
			if (a.toLowerCase() == link.toLowerCase()) {
				
				return;
			}
		}
		
		_attachmentList.add(link);
	}
	
	public boolean send() {
		
		if (_toAddressList.isEmpty()) {
			
			return false;
		}
		
		String _toAddressLine = new String();
		
		for (String a : _toAddressList) {
			
			_toAddressLine += a + ";";
		}
		
		String _ccAddressLine = new String();
		
		for (String a : _ccAddressList) {
			
			_ccAddressLine += a + ";";
		}
		
		try {
			
			_item.setProperty("To", new Variant(_toAddressLine));
			
			if (!_ccAddressLine.isEmpty()) {
				
				_item.setProperty("CC", _ccAddressLine);
			}
			
			if (!_subject.isEmpty()) {
				
				_item.setProperty("Subject", _subject);
			}
			
			if (!_body.isEmpty()) {
				
				_item.setProperty("HTMLBody", _body);
			}
			
			for (String a : _attachmentList) {
				
				_attachments.invoke("Add", new Variant(a));
			}
			
			_item.invoke("Send");
		}
		catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}

}
