package artGame.ui.launcher;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import artGame.control.ClientThread;
import artGame.ui.RenderWindowTest;
import artGame.ui.Screen;
import artGame.ui.UIRenderer;
/**
 * 
 * @author Tim King
 * Launches the actual game client.  Should ask for an IP and port to connect to, then run the game screen, passing to it 
 * the ClientThread to access.
 *
 */
public class GameLauncher extends JPanel {

	private Screen screen; 
	private UIRenderer ui;
	
	private JFrame frame;
	
	private JTextField ipField;
	private JTextField portField;
	
	private JLabel ipLabel;
	private JLabel portLabel;
	
	private JButton connectButton;
	
	public GameLauncher(){
		/* Open up a window to set server IP/Port to connect to */
		
		frame = new JFrame("Connect to server");
		
		ipField = new JTextField(20);
		portField = new JTextField(6);
		
		ipLabel = new JLabel("ip address");
		portLabel = new JLabel("port");
		
		connectButton = new JButton("connect");
		
		JPanel topLeft = new JPanel(new BorderLayout());
		JPanel topRight = new JPanel(new BorderLayout());
		
		JPanel bottom = new JPanel(new FlowLayout());
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		topLeft.add(ipLabel, BorderLayout.NORTH);
		topLeft.add(ipField, BorderLayout.SOUTH);
		
		topRight.add(portLabel, BorderLayout.NORTH);
		topRight.add(portField, BorderLayout.SOUTH);
		
		portField.setText("32768");
		
		connectButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				String ip = ipField.getText();
				int port = Integer.parseInt(portField.getText());
				try {
					start(GameLauncher.runClient(ipField.getText(), port));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "ERROR: Can't connect to " + ip + " on port " + port);
					e1.printStackTrace();
				}
			}
			
		});
		
		bottom.add(connectButton);
		
		this.add(topLeft);
		this.add(topRight);
		this.add(bottom);
		
		frame.add(this);
		
		EntryGUI.centreWindow(frame);
		
		frame.setEnabled(true);
		frame.setVisible(true);
		frame.pack();
		
	}
	
	public void start(ClientThread client){
		
		screen = new RenderWindowTest(client);
		screen.initialize();
	
		while(glfwWindowShouldClose(screen.getWindow()) == GL_FALSE){
			update();
		}
	}
	
	public void update(){
		screen.render();
	}
	
	private static ClientThread runClient(String addr, int port) throws IOException {
		Socket s = new Socket(addr,port);
		System.out.println("The client has connected to " + s.getInetAddress() +":"+s.getPort());
		ClientThread th = new ClientThread(s);
		th.start();
		return th;
	}
	
	public static void main(String[] args){
		new GameLauncher();
	}
	
}