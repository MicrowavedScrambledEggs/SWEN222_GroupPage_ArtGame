package artGame.ui.server;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author Tim King
 *
 */
public class ServerGUI extends JPanel {

	private JFrame frame;
	
	private ServerMenuBar menu;
	private ServerLog log;
	private ServerInfoBar infoBar;
	
	public ServerGUI(int width, int height){
		frame = new JFrame();
		
		setPreferredSize(new Dimension(width, height));
		
		frame.add(this);
		frame.setVisible(true);
		frame.pack();
	}
	
	public static void main(String[] args){
		ServerGUI gui = new ServerGUI(400, 200);
	}
	
}
