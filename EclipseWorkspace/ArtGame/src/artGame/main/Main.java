package artGame.main;

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

public class Main {
	public static final String GAME_NAME = "Wherefore Art Thou";
	public static final int WAIT_PERIOD = 20;
	public static final int BROADCAST_PERIOD = 2000; // cranked up to obscene levels to make output easier to read
	public static final int CONNECTION_TIMEOUT = 60000;

	public static final int LARGE_PACKET_SIZE = 1024;

	public static void main (String[] args) {
		// mostly stolen from Dave's PacMan code.
		String filename = null;
		String serverURL = null;
		boolean server = true;
		int maxClients = 1;
		int gameClock = WAIT_PERIOD;
		int broadcastClock = BROADCAST_PERIOD;
		int publicServerPort = 32768; // default

		for (int i = 0; i != args.length; ++i) {
			if (args[i].startsWith("-")) {
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
					publicServerPort = Integer.parseInt(args[++i]);
				} else if (arg.equals("-clients")) {
					maxClients = Integer.parseInt(args[++i]);
				}
			} else {
				filename = args[i];
			}
		}


		// Sanity checks, also stolen directly from Dave's PacMan code
		if(serverURL != null && server) {
			System.out.println("Cannot be a server and connect to another server!");
			System.exit(1);
		} else if(serverURL != null && gameClock != WAIT_PERIOD) {
			System.out.println("Cannot overide clock period when connecting to server.");
			System.exit(1);
		} else if (server && maxClients <= 0) {
			System.out.println("Server must be able to connect at least one client.");
		} else if (!server && maxClients > 1) {
			System.out.println("Cannot override number of clients when connecting to server.");
		}

		try {
			if(server) {
				// Run in Server mode
				runPublicSocket(publicServerPort,gameClock,broadcastClock,maxClients);
			} else if(serverURL != null) {
				// Run in client mode
				System.out.println("Hi?");
				runClient(serverURL,publicServerPort);
			} else {
				// single user game
				// Board board = createBoardFromFile(filename,nHomerGhosts,nRandomGhosts);
				// singleUserGame(gameClock, board);
			}
		} catch(IOException ioe) {
			System.out.println("I/O error: " + ioe.getMessage());
			ioe.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
//		we'll need this later but not right now
//		else if(serverURL == null && filename == null) {
//			System.out.println("Board file must be provided for single user, or server mode.");
//			System.exit(1);
//		}

		// public connection stuff begins
//
//		if (server) {
//			runPublicSocket(publicServerPort); // TODO will need parameters for game file
//		} else {
//			DatagramSocket sock = null;
//			try {
//				sock = new DatagramSocket(CLIENT_DEF_PUBLIC_PORT);
//			} catch (SocketException e) {
//				e.printStackTrace();
//			}
//			Thread publicClient = new SingleClientThread(sock);
//			publicClient.start();
//			while (!sock.isClosed()) {
//
//			}
		}
//

	private static void runClient(String addr, int port) throws IOException {
		Socket s = new Socket(addr,port);
		System.out.println("The client has connected to " + s.getInetAddress() +":"+s.getPort());
		new ClientThread(s).run();
	}


	@SuppressWarnings("resource") // (otherwise it complains that the publicSocket is never used.)
	private static void runPublicSocket(int port, int gameClock, int broadcastClock, int maxClients) {
		if (maxClients <= 0) throw new IllegalArgumentException("The server must be capable of accepting at least one client request.");
		// now we've passed the sanity checks, enter the main listen loop
		ServerSocket publicSocket = null;
		try {
			publicSocket = new ServerSocket(port, 6, InetAddress.getLocalHost());
			SocketThread[] children = new SocketThread[maxClients];
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
