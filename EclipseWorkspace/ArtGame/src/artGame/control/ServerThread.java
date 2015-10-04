package artGame.control;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import artGame.main.Game;
import artGame.main.Main;

public class ServerThread extends SocketThread {
	private Socket socket;
	private final Game game;
	private final int wait;
	private final int pid = 404; // FIXME This isn't actually getting sent to the client!
	private boolean timedout = false;

	/** Creates a new ServerThread, which will manage the data input/output for 
	 * a single client/server connection. 
	 * 
	 * @param game The Game should be the same Game that is given to all other ServerThreads. 
	 * @param socket The socket to use for connection. Should be already bound before
	 * the constructor is invoked.
	 * @param wait How long to wait between server refreshes. (If 0, the
	 * server refreshes as fast as it is able.)
	 */
	public ServerThread(Game game, Socket socket, int wait) {
		this.game = game;
		this.socket = socket;
		this.wait = wait;
		System.out.println("SERVER INFO:\nPID: "+pid
				+ "\nSOCKETADDR  "+socket.getLocalAddress()+" PORT: "+socket.getLocalPort()
				+ "\nCONNECT TO: "+socket.getInetAddress() +" PORT: "+socket.getPort());
		// OK, so the ServerThread's constructor will also responsible for sending the
		// information a client needs to set up the game
		// sendGameInfo();
	}

	/** Sends the packet that will initialise the game for the client. */
	private void sendGameInfo() {
		System.out.println("Sending our ID ("+pid+") to client...");
		// things the game needs: (feel free to add/remove!)
		// [ ] layout of game 'board'
		// [ ] number of players (if any)
		// [ ] number of max players?
		// [ ] guard patrols?
	}

	public void run() {
		System.out.println("run");
		int runcount = 0; // debugging
		while (!socket.isClosed()) {
			try {
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream()); // TODO implement our backtalk
				// read first
				// great! there's data in the stream! is it from the client player or the client game?
				waitFor(input);
				byte[] data = new byte[Main.LARGE_PACKET_SIZE];
				byte curVal = input.readByte();
				int i = 0;
				// if the integer we got wasn't a terminal, let's read the rest of the data. 
				while (curVal != (byte)Packet.TERMINAL) {
					data[i] = (byte)curVal;
					waitFor(input);
					curVal = input.readByte();
					i++;
				}
				// knowing we got some data, let's try to process it as a packet. 
				if (i > 1) { 
					data = Arrays.copyOf(data,i);
					try {
						Action a = BasicPacketParser.getActionFromBytes(data, true);
					} catch (IncompletePacketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				runcount++;
			} catch (IOException e) { e.printStackTrace(); }
//			catch (InterruptedException e) { e.printStackTrace(); }
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
