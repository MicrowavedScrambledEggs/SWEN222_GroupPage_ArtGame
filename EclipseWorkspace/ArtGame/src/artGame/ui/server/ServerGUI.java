<<<<<<< HEAD
package artGame.ui.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
	
	private JTextField fileURL; 
	private JButton loadFile;
	private JLabel fileLabel;
	
	private JTextField maxClient;
	private JLabel maxClientLabel;
	
	private JTextField gameClock;
	private JLabel gameClockLabel;
	
	private JTextField port;
	private JLabel portLabel;
	
	private JButton startServer;
	
	final JFileChooser files = new JFileChooser();
	
	private Thread serverThread;
	
	private boolean serverRunning = false;
	private boolean running = true;

	public ServerGUI(int width, int height) {
		
		 try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		frame = new JFrame("ArtGame Server");

		setPreferredSize(new Dimension(width, height));

		//Initialize main components
		menu = new ServerMenuBar();
		log = new ServerLog(width, 300);
		infoBar = new ServerInfoBar(width, 20);

		//Init Text fields/buttons
		fileURL = new JTextField(20);
		loadFile = new JButton("load");
		fileLabel = new JLabel("Map file");
		//fileURL.setHorizontalAlignment(JTextField.CENTER);
		
		loadFile.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int returnVal = files.showOpenDialog(null);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = files.getSelectedFile();
		            //This is where a real application would open the file.
		            log("Opening: " + file.getName() + ".\n");
		            fileURL.setText(file.getAbsolutePath());
		        } else {
		            log("Open command cancelled by user.\n");
		        }
				
			}
			
		});
		
		maxClient = new JTextField(8);
		maxClientLabel = new JLabel("Max clients");
		//maxClient.setHorizontalAlignment(JTextField.CENTER);
		
		gameClock = new JTextField(10);
		gameClockLabel = new JLabel("Game clock (MS)");
		//gameClock.setHorizontalAlignment(JTextField.CENTER);
		
		port = new JTextField(10);
		portLabel = new JLabel("Port");
		//port.setHorizontalAlignment(JTextField.CENTER);
		
		startServer = new JButton("Run server");
		startServer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(serverRunning){
					serverRunning = false;
					serverThread.interrupt();
					serverThread = null;
					setInputEnabled(true);
					infoBar.setRunningStatus(false);
					log.log("server killed");
				} else {
					final String[] args = {"-server", maxClient.getText()};
					if(maxClient.getText().length() != 1 || !Character.isDigit(maxClient.getText().charAt(0))){
						log.log("invalid max clients: please use 0-6");
						return;
					}
					if(Character.isDigit(maxClient.getText().charAt(0))){
						int digit = Integer.parseInt(""+maxClient.getText().charAt(0));
						if(digit < 0 || digit > 6){
							log.log("invalid max clients: please use 0-6");
							return;
						}
					}
					setInputEnabled(false);
					serverRunning = true;
					serverThread = new Thread(new Runnable(){

						@Override
						public void run() {
							artGame.main.Main.main(args);
						}
						
					});
					serverThread.start();
					infoBar.setRunningStatus(true);
					try {
						log.log("server started at " + InetAddress.getLocalHost().getHostAddress());
					} catch (UnknownHostException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						infoBar.setServerIP(InetAddress.getLocalHost().getHostAddress());
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					startServer.setText("Kill server");
				}
				frame.getContentPane().repaint();
			}
			
		});
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel header = new JPanel();
		JPanel topLeft = new JPanel(new BorderLayout());
		JPanel topRight = new JPanel(new BorderLayout());
		JPanel botLeft = new JPanel(new BorderLayout());
		JPanel botRight = new JPanel(new BorderLayout());
		
		//Add GUI components to the main gui panel (this)
		header.add(fileLabel);
		header.add(fileURL);
		header.add(loadFile);
		
		topLeft.add(maxClientLabel, BorderLayout.NORTH);
		topLeft.add(maxClient, BorderLayout.SOUTH);
		
		topRight.add(gameClockLabel, BorderLayout.NORTH);
		topRight.add(gameClock, BorderLayout.SOUTH);
		
		botLeft.add(portLabel, BorderLayout.NORTH);
		botLeft.add(port, BorderLayout.SOUTH);
		
		botRight.add(startServer);
		
		this.add(header);
		this.add(topLeft);
		this.add(topRight);
		this.add(botLeft);
		this.add(botRight);
		
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		frame.setJMenuBar(menu);
		frame.add(this);
		frame.add(log);
		frame.add(infoBar);
		frame.setVisible(true);
		frame.pack();
		
		run();
		
	}

	public void run(){
		
		Thread th = new Thread(new Runnable(){
			public void run(){
				while(1 == 1){
					frame.repaint();
				}
			}
		});
		th.start();
	}
	
	private void setInputEnabled(boolean enabled){
		this.fileURL.setEditable(enabled);
		this.loadFile.setEnabled(enabled);
		this.gameClock.setEditable(enabled);
		this.maxClient.setEditable(enabled);
		this.port.setEditable(enabled);
	}
	
	public void log(String text){
		System.out.println(text);
	}
	
	public static void main(String[] args) {
		ServerGUI gui = new ServerGUI(400, 200);
	}

}
=======
package artGame.ui.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
	
	private JTextField fileURL; 
	private JButton loadFile;
	private JLabel fileLabel;
	
	private JTextField maxClient;
	private JLabel maxClientLabel;
	
	private JTextField gameClock;
	private JLabel gameClockLabel;
	
	private JTextField port;
	private JLabel portLabel;
	
	private JButton startServer;
	
	final JFileChooser files = new JFileChooser();
	
	private Thread serverThread;
	
	private boolean serverRunning = false;
	private boolean running = true;

	public ServerGUI(int width, int height) {
		
		 try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		frame = new JFrame("ArtGame Server");

		setPreferredSize(new Dimension(width, height));

		//Initialize main components
		menu = new ServerMenuBar();
		log = new ServerLog(width, 300);
		infoBar = new ServerInfoBar(width, 20);

		//Init Text fields/buttons
		fileURL = new JTextField(20);
		loadFile = new JButton("load");
		fileLabel = new JLabel("Map file");
		//fileURL.setHorizontalAlignment(JTextField.CENTER);
		
		loadFile.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				int returnVal = files.showOpenDialog(null);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = files.getSelectedFile();
		            //This is where a real application would open the file.
		            log("Opening: " + file.getName() + ".\n");
		            fileURL.setText(file.getAbsolutePath());
		        } else {
		            log("Open command cancelled by user.\n");
		        }
				
			}
			
		});
		
		maxClient = new JTextField(8);
		maxClientLabel = new JLabel("Max clients");
		//maxClient.setHorizontalAlignment(JTextField.CENTER);
		
		gameClock = new JTextField(10);
		gameClockLabel = new JLabel("Game clock (MS)");
		//gameClock.setHorizontalAlignment(JTextField.CENTER);
		
		port = new JTextField(10);
		portLabel = new JLabel("Port");
		//port.setHorizontalAlignment(JTextField.CENTER);
		
		startServer = new JButton("Run server");
		startServer.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(serverRunning){
					serverRunning = false;
					serverThread.interrupt();
					serverThread = null;
					setInputEnabled(true);
					infoBar.setRunningStatus(false);
					log.log("server killed");
				} else {
					final String[] args = {"-server", maxClient.getText()};
					if(maxClient.getText().length() != 1 || !Character.isDigit(maxClient.getText().charAt(0))){
						log.log("invalid max clients: please use 0-6");
						return;
					}
					if(Character.isDigit(maxClient.getText().charAt(0))){
						int digit = Integer.parseInt(""+maxClient.getText().charAt(0));
						if(digit < 0 || digit > 6){
							log.log("invalid max clients: please use 0-6");
							return;
						}
					}
					setInputEnabled(false);
					serverRunning = true;
					serverThread = new Thread(new Runnable(){

						@Override
						public void run() {
							artGame.main.Main.main(args);
						}
						
					});
					serverThread.start();
					infoBar.setRunningStatus(true);
					try {
						log.log("server started at " + InetAddress.getLocalHost().getHostAddress());
					} catch (UnknownHostException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						infoBar.setServerIP(InetAddress.getLocalHost().getHostAddress());
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					startServer.setText("Kill server");
				}
				frame.getContentPane().repaint();
			}
			
		});
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel header = new JPanel();
		JPanel topLeft = new JPanel(new BorderLayout());
		JPanel topRight = new JPanel(new BorderLayout());
		JPanel botLeft = new JPanel(new BorderLayout());
		JPanel botRight = new JPanel(new BorderLayout());
		
		//Add GUI components to the main gui panel (this)
		header.add(fileLabel);
		header.add(fileURL);
		header.add(loadFile);
		
		topLeft.add(maxClientLabel, BorderLayout.NORTH);
		topLeft.add(maxClient, BorderLayout.SOUTH);
		
		topRight.add(gameClockLabel, BorderLayout.NORTH);
		topRight.add(gameClock, BorderLayout.SOUTH);
		
		botLeft.add(portLabel, BorderLayout.NORTH);
		botLeft.add(port, BorderLayout.SOUTH);
		
		botRight.add(startServer);
		
		this.add(header);
		this.add(topLeft);
		this.add(topRight);
		this.add(botLeft);
		this.add(botRight);
		
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		frame.setJMenuBar(menu);
		frame.add(this);
		frame.add(log);
		frame.add(infoBar);
		frame.setVisible(true);
		frame.pack();
		
		run();
		
	}

	public void run(){
		
		Thread th = new Thread(new Runnable(){
			public void run(){
				while(1 == 1){
					frame.repaint();
				}
			}
		});
		th.start();
	}
	
	private void setInputEnabled(boolean enabled){
		this.fileURL.setEditable(enabled);
		this.loadFile.setEnabled(enabled);
		this.gameClock.setEditable(enabled);
		this.maxClient.setEditable(enabled);
		this.port.setEditable(enabled);
	}
	
	public void log(String text){
		System.out.println(text);
	}
	
	public static void main(String[] args) {
		ServerGUI gui = new ServerGUI(400, 200);
	}

}
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
