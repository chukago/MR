package org.tco.tfm.mr;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MR {

	private static MainWindow mainWindow;
	
	public static void main(String[] args) {
		
		SplashWindow splash = new SplashWindow();
		splash.setLocationRelativeTo(null);
		splash.setVisible(true);
		
		try {
			
			switch(System.getProperty("sun.arch.data.model")) {
			
			case "32":
				
				System.loadLibrary("jacob-1.18-x32");
				break;
			case "64":
				
				System.loadLibrary("jacob-1.18-x64");
				break;
				
			default:
				
				splash.setVisible(false);
				JOptionPane.showMessageDialog(null, "Неверная архитектура системы");
				System.out.println("Wrong system architecture");
				return;
			}
		}
		catch(UnsatisfiedLinkError e) {
			
			splash.setVisible(false);
			JOptionPane.showMessageDialog(null, "Не могу загрузить необходимые библиотеки");
			System.out.println("Cannot load necessary libraries");
			return;
		}
		
		//Load Data From Database - long time operation
		MRTableModel.getInstance();
		
		splash.setVisible(false);
		SecurityManager.getInstance().setCurrentUser(System.getProperty("user.name"));
		
		mainWindow = new MainWindow();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
	
	public static MainWindow getMainWindow() {
		
		return mainWindow;
	}
}
