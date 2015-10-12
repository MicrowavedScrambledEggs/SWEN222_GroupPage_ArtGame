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
import java.time.Clock;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import artGame.control.ClientThread;
import artGame.control.GameClock;
import artGame.control.SocketThread;
import artGame.control.ServerThread;
import artGame.xml.XMLHandler;

public class Main {
	public static final String GAME_NAME = "Wherefore Art Thou";
	public static final int WAIT_PERIOD = 20;
	public static final int BROADCAST_PERIOD = 2000; // cranked up to obscene levels to make output easier to read
	public static final int CONNECTION_TIMEOUT = 60000;
	public static final int LARGE_PACKET_SIZE = 1024;
	
	public static GameClock clock;
	
	private static volatile ServerThread[] children = new ServerThread[0];
	private static Game GAME;
	
	/** For testing only */
	@Deprecated
	public static void main (String[] args) {
		// logic mostly stolen from Dave's PacMan code.
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
					System.out.println("Commands:");
					System.out.println("-server <numclients>	| starts a server that can accept n clients");
					System.out.println("-connect <IP>			| starts a client that connects to the IP");
					System.out.println("-clock <time>			| if server, sets min delay between updates [NOT IMPLEMENTED]");
					System.out.println("-port <num>				| port to use");
					System.out.println("-loadworld <path>		| gametype to run");
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
			XMLHandler xmlh = new XMLHandler();
			GAME = xmlh.loadGame(f);
			System.out.println(GAME.toString());
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
				runPublicSocket(port,gameClock,maxClients);
			} else if(serverURL != null) {
				// Run as client
				runClient(serverURL, port);
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

	private static void runClient(String addr, int port) throws IOException {
		Socket s = new Socket(addr,port);
		children = null;
		System.out.println("The client has connected to " + s.getInetAddress() +":"+s.getPort());
		new ClientThread(s, GAME, Main.BROADCAST_PERIOD).run();
	}


	/** This method runs a public socket out of the given port that
	 * clients can connect to in order to join the game. 
	 * 
	 * @param port Port to use
	 * @param gameClock Start time of the game clock
	 * @param maxClients
	 */
	@SuppressWarnings("resource") // (otherwise it complains that the publicSocket is never used.)
	private static void runPublicSocket(int port, int gameClock, int maxClients) {
		clock = new GameClock(GAME);
		clock.start();
		if (maxClients <= 0) {
			throw new IllegalArgumentException("The server must be capable of accepting at least one client request.");
		} else if (children == null) { 
			System.err.println("Cannot run a public server when the server has been closed.");
			return; 
		} else if (children.length == 0) {
			children = new ServerThread[maxClients];
		}
		// now we've passed the sanity checks, enter the main listen loop
		ServerSocket publicSocket = null;
		long starttime = System.currentTimeMillis();
		try {
			publicSocket = new ServerSocket(port, maxClients+1, InetAddress.getLocalHost());
			System.out.println("The server has set up shop at "+publicSocket.getLocalSocketAddress());
			long[] timeout = new long[maxClients];
			int nextServerIdx = 0;
			boolean firstPlayer = true;
			// this is the while loop that manages the public socket
			while (1 == 1) {
				try {
					Socket s = publicSocket.accept();
					children[nextServerIdx] = new ServerThread(s, GAME, WAIT_PERIOD);
					children[nextServerIdx].start();
					if (firstPlayer) {
						startMultiplayer();
						firstPlayer = false;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// this loop checks if we should close any of our child sockets
				// we don't expect to have more than six players, so this should be OK.
				for (int i = 0; i < children.length; i++) {
					if (children[i] != null && children[i].isTimedOut()) {
						if (timeout[i] >= System.currentTimeMillis()) {
							children[i].close();
							children[i] = null;
							timeout[i] = 0;
							if (i < nextServerIdx) {
								nextServerIdx = i;
							}
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
	
	/** IMPORTANT: STARTS THE SERVER'S LOCAL GAME CLOCK.
	 * @throws IOException
	 */
	private static void startMultiplayer() throws IOException {	
		clock = new GameClock(GAME);
		clock.start();
		// now, wait for the game to finish
		while(GAME.hasPlayers() && children != null) {
			Thread.yield();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		System.out.println();
	}
	
	public static ServerThread[] getKids() {
		return Arrays.copyOf(children,children.length);
	}

	public static void stop() {
		if (children == null) {
			System.err.println("The main socket has already been closed.");
			return;
		}
		for (SocketThread s : children) {
			if (s != null) {
				System.err.println("Closing socket on port "+s.getPort()+" to "+s.getPlayerId());
				s.close();
			}
		}
		children = null;
	}
}
