package org.tco.tfm.mr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class SettingsManager {

	private static SettingsManager instance = null;
	
	private String databasePath;
	private ArrayList<Integer> mrTableColumnsWidths = new ArrayList<Integer>();
	
	public static SettingsManager getInstance() {
		
		if (instance == null) {
			
			instance = new SettingsManager();
		}
		
		return instance;
	}
	
	private SettingsManager() {
		
		Properties properties = new Properties();
		
		try {
			
			properties.load(getClass().getResourceAsStream("settings/settings.prop"));
		} 
		catch (IOException e) {
			
			System.out.println("Cannot load properties");
			e.printStackTrace();
		}
		
		databasePath = properties.getProperty("database.location");
		
		String columnWidth = properties.getProperty("table.columns.width");
		
		decodeColumnsWidth(columnWidth);
		
	}
	
	public String getDatabasePath() {
		
		return databasePath;
	}
	
	public ArrayList<Integer> getColumnWidhts() {
		
		return mrTableColumnsWidths;
	}
	
	private void decodeColumnsWidth(String stringRep) {
		
		mrTableColumnsWidths.clear();
		
		if (stringRep == null) {
			
			return;
		}
		
		String[] chunks = stringRep.split(";");
		
		for (String s : chunks) {
			
			try {
				
				Integer width = Integer.decode(s);
				
				mrTableColumnsWidths.add(width);
			}
			catch (NumberFormatException e) {
				
				System.out.println("Wrong column width number format");
				e.printStackTrace();
				return;
			}
		}
	}
}
