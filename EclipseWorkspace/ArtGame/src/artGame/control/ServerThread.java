package artGame.control;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

//import artGame.control.GamePacket.MovePacket;
import artGame.main.Game;
import artGame.main.Main;

public class ServerThread extends SocketThread {
	private Socket socket;
	private final Game game;
	private final int wait;
	private final int pid = 404;
	private boolean timedout = false;
	
	public ServerThread(Game game, Socket socket, int wait) {
		this.game = game;
		this.socket = socket;
		this.wait = wait;
		System.err.println("SERVER INFO:\nPID: "+pid
				+ "\nSOCKETADDR  "+socket.getLocalAddress()+" PORT: "+socket.getLocalPort()
				+ "\nCONNECT TO: "+socket.getInetAddress() +" PORT: "+socket.getPort());
		// OK, so the ServerThread's constructor will also responsible for sending the
		// information a client needs to set up the game
		System.out.println("Sending our ID ("+pid+") to client...");
		sendGameInfo();
	}
	
	/** Sends the packet that will initialise the game for the client. */
	private void sendGameInfo() {
		// things the game needs: (feel free to add/remove!)
		// [ ] layout of game 'board'
		// [ ] number of players (if any)
		// [ ] number of max players?
		// [ ] guard patrols? 
	}
	
	public void run() {
		int runcount = 0; // debugging
		while (!socket.isClosed()) {
			long then = System.currentTimeMillis();
			try {
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				// read first
				// great! there's data in the stream! is it from the client player or the client game?
				waitFor(input);
				
				int source = input.readInt(); // 0 if player, 1 if gameworld
				System.out.println("SRC = "+source);
				// now we know that, what's being affected by this data?
				waitFor(input);
				int gotId = input.readInt();
				System.out.println("ID  = "+gotId);
				// and now we know that, what kind of data are we getting?
				waitFor(input);
				int action = input.readInt();
				System.out.println("ACT = "+action);
				
				// now we know who it's for, we can start parsing it.
				processDataStream(input,action,gotId);
				
				// and now to make things print a little slower we'll just sleep for a bit. 
				Thread.sleep(4000);
				runcount++;
			} catch (IOException e) { e.printStackTrace(); }
			catch (InterruptedException e) { e.printStackTrace(); }
			runcount++;
		}
	}
	
	/** In between data segments, waits and checks to ensure the connection hasn't
	 * timed out. 
	 * @param s
	 */
	private void waitFor(InputStream s) {
		long TIMEOUT = System.currentTimeMillis() + Main.CONNECTION_TIMEOUT;
		try {
			while (s.available() <= 0 && TIMEOUT > System.currentTimeMillis()) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// we've been interrupted! let's get back to work.
					TIMEOUT = 0;
					System.out.println("hmm?");
				}
			}
			if (s.available() > 0) { // great, there's input, let's get back to business
				return;
			}
			timedout = true;
			System.err.println("Connection timed out! Waiting to close.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("uh... so there's been a terrible accident...");
		}
	}

	/* The process of translating the stream data is gloriously repetitive.
	 * Let's have methods for dealing with that!
	 * 
	 * Working on getting the PacketParsers and an Action class to do this job. 
	 */
	private void processDataStream(DataInputStream input, int action, int gotId) throws IOException {
		if (gotId == 0) { // client-sent packets should never be 0!
			
		} else if (gotId == pid) { // this packet is about an action taken by our client player
			waitFor(input);
			if (action == BasicPacketParser.MOVE) {
				readMove(input,gotId);
			} else if (action == BasicPacketParser.ITEM_TAKE) {
				readTakeItem(input,gotId);
			} else if (action == BasicPacketParser.ESCAPE) {
				readEscape(input,gotId);
			} else if (action == BasicPacketParser.ITEM_GIVE) {
				readGiveItem(input,gotId);
			} else if (action == BasicPacketParser.LOSE) {
				readCaught(input,gotId);
			} else if (action == BasicPacketParser.INVENTORY) {
				readInventory(input,gotId);
			} else {
				throw new IllegalArgumentException();
			}
		} else { // this packet is an action taken on something in the world
			System.out.println("ACTION "+action+" FOR ACTOR "+gotId+" IS INVALID");
		}
		// wait for terminating character
		waitFor(input);
		if (input.readInt() == Integer.MAX_VALUE) {
			System.out.println("DONE");
		} else {
			System.err.println("ERR");
		}
	}

	/**
	 * 
	 */
	private void readMove(DataInputStream input, int gotId) throws IOException {
		System.out.println("Reading a MOVE packet");
			int[] readPos = new int[4];
			readPos[0] = input.readInt();
			readPos[1] = input.readInt();
			readPos[2] = input.readInt();
			readPos[3] = input.readInt();
			Point playerPos = new Point(readPos[0],readPos[1]);
			Point playerDes = new Point(readPos[2],readPos[3]);
			System.out.println(pid+" ("+playerPos.getX()+","+playerPos.getY()+") -> ("+playerDes.getX()+","+playerDes.getY()+")");
	}

	/**
	 * @param input
	 * @throws IOException
	 */
	private void readTakeItem(DataInputStream input, int gotId) throws IOException {
		System.out.println("Reading a TAKE packet");
		waitFor(input);
		int playerGet = input.readInt();
		int objectGet = input.readInt();
		int itemId = input.readInt();
		System.out.println((playerGet > 0 ? "Player "+playerGet : "Item "+objectGet) +" wants item "+itemId);
	}

	/**
	 * 
	 */
	private void readInventory(DataInputStream input, int gotId) {
		System.out.println("Reading an INVENTORY packet");
		// get inventory of the requested agent
		// (providing they're in range and other such concerns)
		// (the network cares not for your precious game logic)
	}
	
	/** In real code, there will be a collection of item IDs passed in here,
	 * as obtained by the game. 
	 * @throws IOException */
	private void sendInventory(DataOutputStream output, int pid) throws IOException {
		output.writeInt(0);
		output.writeInt(pid);
		output.writeInt(BasicPacketParser.INVENTORY);
		// this is a pretend inventory!
		int[] inventory = new int[(int)(Math.random()*10)];
		for (int i = 0; i < inventory.length; i++) {
			inventory[i] = (int)(Math.random()*100)+100;
		}
		
		for (int i = 0; i < inventory.length; i++) {
			output.writeInt(inventory[i]);
		}
		output.writeInt(Integer.MAX_VALUE);
	}

	/**
	 * 
	 */
	private void readCaught(DataInputStream input, int gotId) {
		System.out.println("Reading a CAUGHT packet");
	}

	/**
	 * 
	 */
	private void readGiveItem(DataInputStream input, int gotId) {
		System.out.println("Reading a GIVE packet");
	}

	/**
	 * 
	 */
	private void readEscape(DataInputStream input, int gotId) {
		System.out.println("Reading an ESCAPE packet");
	}

	@Override
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	@Override
	public int getPort() {
		return socket.getPort();
	}

	@Override
	public boolean isSocketSafe() {
		return socket.isBound() && socket.isConnected() && !socket.isClosed();
	}
	
	@Override
	public boolean close() {
		if (socket.isClosed()) {
			return true;
		} 
		try { 
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean isTimedOut() {
		return timedout;
	}
}
