package artGame.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import artGame.control.ClientThread;
import artGame.control.SocketThread;
import artGame.control.ServerThread;
import artGame.xml.XMLReader;

public class Main {
	public static final String GAME_NAME = "Wherefore Art Thou";
	public static final int WAIT_PERIOD = 20;
	public static final int BROADCAST_PERIOD = 2000; // cranked up to obscene levels to make output easier to read
	public static final int CONNECTION_TIMEOUT = 60000;
	public static final int LARGE_PACKET_SIZE = 1024;
	
	private static volatile SocketThread[] children = new SocketThread[0];
	private static Game GAME;

	/* TODO:	 * 
	 * stop/interrupt server
	 */
	
	public static void main (String[] args) {
		// mostly stolen from Dave's PacMan code.
		String filename = null;
		String serverURL = null;
		boolean server = false;
		int maxClients = 1;
		int gameClock = WAIT_PERIOD;
		int broadcastClock = BROADCAST_PERIOD;
		int port = 32768; // default
		boolean readFileName = false;
		
		for (int i = 0; i != args.length; ++i) {
			if (args[i].startsWith("-")) {
				readFileName = false;
				String arg = args[i];
				if(arg.equals("-help")) {
					System.out.println("help text pending");
					System.exit(0);
				} else if(arg.equals("-server")) {
					server = true;
					maxClients = Integer.parseInt(args[++i]);
				} else if(arg.equals("-connect")) {
					serverURL = args[++i];
				} else if(arg.equals("-clock")) {
					gameClock = Integer.parseInt(args[++i]);
				} else if(arg.equals("-port")) {
					port = Integer.parseInt(args[++i]);
				} else if (arg.equals("-clients")) {
					maxClients = Integer.parseInt(args[++i]);
				} else if (arg.equals("-loadworld")) {
					filename = args[++i];
					readFileName = true;
				}
			} else if (readFileName) {
				filename = filename + " "+ args[i];
			}
		}

		if (filename != null) {
			File f = new File(filename);
			XMLReader xmlreader = new XMLReader(f);
			GAME = xmlreader.getGame();
		}

		// Sanity checks, also stolen directly from Dave's PacMan code
		if(serverURL != null && server) {
			System.out.println("Cannot be a server and connect to another server!");
			System.exit(-1);
		} else if(serverURL != null && gameClock != WAIT_PERIOD) {
			System.out.println("Cannot overide clock period when connecting to server.");
			System.exit(-1);
		} else if (server && maxClients <= 0) {
			System.out.println("Server must be able to connect at least one client.");
			System.exit(-1);
		} else if (!server && maxClients > 1) {
			System.out.println("Cannot override number of clients when connecting to server.");
			System.exit(-1);
		}

		try {
			if(server) {
				// Run as server
				runPublicSocket(port,gameClock,broadcastClock,maxClients);
			} else if(serverURL != null) {
<<<<<<< HEAD
				// Run as client
				runClient(GAME, serverURL,port);
=======
				// Run in client mode
				runClient(serverURL,publicServerPort);
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
			} else {
				// single user game
			}
		} catch(IOException ioe) {
			System.out.println("I/O error: " + ioe.getMessage());
			ioe.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}

	private static void runClient(Game game, String addr, int port) throws IOException {
		Socket s = new Socket(addr,port);
		children = null;
		System.out.println("The client has connected to " + s.getInetAddress() +":"+s.getPort());
		new ClientThread(s, game).run();
	}


	@SuppressWarnings("resource") // (otherwise it complains that the publicSocket is never used.)
	private static void runPublicSocket(int port, int gameClock, int broadcastClock, int maxClients) {
		if (maxClients <= 0) {
			throw new IllegalArgumentException("The server must be capable of accepting at least one client request.");
		} else if (children == null) { 
			System.err.println("Cannot run a public server when the server has been closed.");
			return; 
		} else if (children.length == 0) {
			children = new SocketThread[maxClients];
		}
		// now we've passed the sanity checks, enter the main listen loop
		ServerSocket publicSocket = null;
		long starttime = System.currentTimeMillis();
		try {
			publicSocket = new ServerSocket(port, maxClients+1, InetAddress.getLocalHost());
			System.out.println("The server has set up shop at "+publicSocket.getLocalSocketAddress());
			long[] timeout = new long[maxClients];
			int numConnected = 0;
			// this is the while loop that manages the public socket
			while (1 == 1) {
				try {
					Socket s = publicSocket.accept();
					children[numConnected] = new ServerThread(null, s, WAIT_PERIOD);
					children[numConnected].start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				numConnected ++;
				// this loop checks if we should close any of our child sockets
				// we don't expect to have more than six players, so this should be OK.
				for (int i = 0; i < children.length; i++) {
					if (children[i] != null && children[i].isTimedOut()) {
						if (timeout[i] >= System.currentTimeMillis()) {
							children[i].close();
							children[i] = null;
							timeout[i] = 0;
						} else if (children[i] != null && timeout[i] == 0){
							timeout[i] = System.currentTimeMillis() + (long)CONNECTION_TIMEOUT;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void stop() {
		if (children == null) {
			System.err.println("The socket has already been closed.");
			return;
		}
		for (SocketThread s : children) {
			System.err.println("Closing socket on port "+s.getPort()+" to "+s.getPlayerId());
			s.close();
		}
	}

//	private static void runServer(int port, int nclients, int gameClock, int broadcastClock, Board game) {
//	GameClock clk = new GameClock(gameClock,game,null);
//
//	// Listen for connections
//	System.out.println("PACMAN SERVER LISTENING ON PORT " + port);
//	System.out.println("PACMAN SERVER AWAITING " + nclients + " CLIENTS");
//	try {
//		Master[] connections = new Master[nclients];
//		// Now, we await connections.
//		ServerSocket ss = new ServerSocket(port);
//		while (1 == 1) {
//			// 	Wait for a socket
//			Socket s = ss.accept();
//			System.out.println("ACCEPTED CONNECTION FROM: " + s.getInetAddress());
//			int uid = game.registerPacman();
//			connections[--nclients] = new Master(s,uid,broadcastClock,game);
//			connections[nclients].start();
//			if(nclients == 0) {
//				System.out.println("ALL CLIENTS ACCEPTED --- GAME BEGINS");
//				multiUserGame(clk,game,connections);
//				System.out.println("ALL CLIENTS DISCONNECTED --- GAME OVER");
//				return; // done
//			}
//		}
//	} catch(IOException e) {
//		System.err.println("I/O error: " + e.getMessage());
//	}
//}
}
