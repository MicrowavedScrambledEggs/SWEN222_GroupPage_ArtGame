package artGame.control;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import artGame.control.cmds.Action;
import artGame.control.cmds.BasicPacketParser;
import artGame.control.cmds.Packet;
import artGame.main.Game;
import artGame.main.Main;

/** TODO
 * 
 * @author Vicki
 *
 */
public class ServerThread extends SocketThread {
	private static final int PID_START = 1; // the id of the first player
	private static final short TYPE = 120; // TODO in a world with more than one map,
										 // we'd need to get this from the loaded Game
										 // so only clients running the same level can connect. 
	private Socket socket;
	private final Game game;
	private final int wait;
	private int pid; // FIXME
	private boolean timedout = false;
	private final DataInputStream IN;
	private final DataOutputStream OUT;

	/** Creates a new ServerThread, which will manage the data input/output for 
	 * a single client/server connection. 
	 * 
	 * @param game The Game should be the same Game that is given to all other ServerThreads. 
	 * @param socket The socket to use for connection. Should be already bound before
	 * the constructor is invoked.
	 * @param wait How long to wait between server refreshes. (If 0, the
	 * server refreshes as fast as it is able.)
	 * @throws IOException 
	 */
	public ServerThread(Game game, Socket socket, int wait) throws IOException {
		this.game = game;
		this.socket = socket;
		this.wait = wait;
		IN = new DataInputStream(socket.getInputStream());
		OUT = new DataOutputStream(socket.getOutputStream());
		System.err.println("================== SERVER INFO ==================\nPID: "+pid
				+ "\nSOCKETADDR: "+socket.getLocalAddress()+" PORT: "+socket.getLocalPort()
				+ "\nCONNECT TO: "+socket.getInetAddress() +" PORT: "+socket.getPort());
		// OK, so the ServerThread's constructor will also responsible for sending the
		// information a client needs to set up the game
	}

	/** Does the work of setting up the client/server's shared player ID.
	 */
	private void sendGameInfo() {
		try {
			// first, send the game type
			System.out.println("Sending game type...");
			OUT.writeShort(TYPE);
			
			// get client response
			boolean ok = IN.readBoolean();
			if (ok) {
				System.out.println("Client is running game map "+TYPE+"; checking ID.");
			} else {
				System.err.println("Client is running a different game map; closing connection.");
				close();
				return;
			}
			
			// get the next player ID
			pid = PID_START;
			while (!game.isAvailablePlayerId(pid)) {
				pid++;
			}
			System.out.println("Assigning client the shared ID "+ pid);
			OUT.writeInt(pid);
			
			// get the client's response-- should be OK for them too
			ok = IN.readBoolean();
			if (ok) {
				
			} else {
				System.err.println("Fatal desync: client cannot use requested PID.");
				close();
			}
			System.out.println();
		} catch (IOException e) {
			System.err.println("Connection error: could not send startup info to client!");
			e.printStackTrace();
		}
	}

	public void run() {
		sendGameInfo();
		System.out.println("=-=-=-=-=-=-=-=-=- RUNNING SERVER "+pid+" =-=-=-=-=-=-=-=-=-");
		int runcount = 0; // debugging
		while (!socket.isClosed()) {
			try {
				// read first
//				IN.readInt(); //FIXME
				waitFor(IN);
				Action a = BasicPacketParser.getActionFromStream(IN);
				System.out.println(a.toString());
				runcount++;
			} catch (IncompatiblePacketException e) {  }
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
					System.err.println("Waiting was interrupted!");
				}
			}
			if (s.available() > 0) { // great, there's input, let's get back to business
				return;
			}
			timedout = true;
			System.err.println("Connection timed out! Closing.");
			socket.close();
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
//	private void processDataStream(DataInputStream input, int action, int gotId) throws IOException {
//		if (gotId == 0) { // client-sent packets should never be 0!
//			
//		} else if (gotId == pid) { // this packet is about an action taken by our client player
//			waitFor(input);
//			if (action == Packet.MOVE) {
//				readMove(input,gotId);
//			} else if (action == Packet.ITEM_LOSE) {
//				readTakeItem(input,gotId);
//			} else if (action == Packet.ESCAPE) {
//				readEscape(input,gotId);
//			} else if (action == Packet.ITEM_GAIN) {
//				readGiveItem(input,gotId);
//			} else if (action == Packet.LOSE) {
//				readCaught(input,gotId);
//			} else if (action == Packet.INVENTORY) {
//				readInventory(input,gotId);
//			} else {
//				throw new IllegalArgumentException();
//			}
//		} else { // this packet is an action taken on something in the world
//			System.out.println("ACTION "+action+" FOR ACTOR "+gotId+" IS INVALID");
//		}
//		// wait for terminating character
//		waitFor(input);
//		if (input.readInt() == Integer.MAX_VALUE) {
//			System.out.println("DONE");
//		} else {
//			System.err.println("ERR");
//		}
//	}

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
	
	@Override
	public int getPlayerId() {
		return pid;
	}
}
