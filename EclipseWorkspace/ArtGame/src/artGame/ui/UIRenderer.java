package artGame.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class UIRenderer extends JPanel {
	
	private JFrame frame;
	
	public UIRenderer(int width, int height){
		
		this.setPreferredSize(new Dimension(width, height));
		
		frame = new JFrame();
		//frame.setUndecorated(true);
		
		//frame.setAlwaysOnTop(true);
		frame.add(this);
		frame.setEnabled(true);
		frame.setVisible(true);
		frame.pack();
		//frame.setOpacity(0);
		//frame.setAlwaysOnTop(true);
		frame.requestFocus();
		//frame.setAlwaysOnTop(false);
	}
	
	public void dispose(){
		frame.dispose();
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.setColor(new Color(0.5f, 0.5f, 0.5f, 0.2f));
		g.fillRect(0,  0,  getWidth(),  getHeight());
	}
	
}

