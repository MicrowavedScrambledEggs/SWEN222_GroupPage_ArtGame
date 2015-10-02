package artGame.ui.launcher;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import artGame.ui.server.ServerGUI;

public class EntryGUI extends JPanel {

	private JFrame frame;
	
	public EntryGUI(){
		frame = new JFrame();
		
		JButton server = new JButton("server");
		server.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new ServerGUI(400, 200);
				frame.dispose();
			}
			
		});
		JButton client = new JButton("client");
		client.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable(){
					public void run(){
						new GameLauncher();
					}
				}).start();
				frame.dispose();
			}
			
		});
		
		add(server);
		add(client);
		
		frame.add(this);
		frame.setEnabled(true);
		
		centreWindow(frame);
		
		frame.setVisible(true);
		frame.pack();
	}

	public static void centreWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
	public static void main(String[] args){
		new EntryGUI();
	}
	
}
