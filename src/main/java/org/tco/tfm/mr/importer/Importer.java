package org.tco.tfm.mr.importer;

import org.tco.tfm.mr.DatabaseManager;

public class Importer {

	public static void main(String[] args) {

		if (!DatabaseManager.getInstance().checkDbConsistency()) {
			
			System.out.println("Database is corrupted!");
			return;
		}
		
		ExcelDatabase db = new ExcelDatabase();
		
		db.parse();
	}

}
