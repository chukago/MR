package org.tco.tfm.mr;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class SplashWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private int width = 371;
	private int height = 336;
	private BufferedImage image = null;
	
	public SplashWindow() {
		
		setSize(width, height);
		setUndecorated(true);
		
		try {
			
			image = ImageIO.read(getClass().getResourceAsStream("imgs/SplashBackground.png"));
		} 
		catch (IOException e) {
			
			System.out.println("Cannot load image");
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		
		if (image != null) {
			
			g.drawImage(image, 0, 0, width, height, null);
			g.drawString("MR Status v." + ProgramVersion.getVersionString(), 10, 330);
		}
	}
}
