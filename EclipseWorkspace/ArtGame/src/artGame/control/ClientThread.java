package artGame.control;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import artGame.control.cmds.Action;
import artGame.control.cmds.BasicPacketParser;
import artGame.control.cmds.Command;
import artGame.control.cmds.GetItemAction;
import artGame.control.cmds.MovePlayerAction;
import artGame.control.cmds.Packet;
import artGame.game.Character.Direction;
import artGame.main.Game;
import artGame.main.Main;

/**
 */
public class ClientThread extends SocketThread {
	private static final short TYPE = 120; // TODO in a world with more than one map,
										 // we'd need to get this from the loaded Game
										 // so we can only connect to a server running the same map
	private final Game game;
	private Socket socket;
	private boolean isPlaying = true;
	private int pid = 404;
	private final int wait;
	private volatile long timeout;
	private final DataInputStream IN;
	private final DataOutputStream OUT;
	
	/**
	 * @deprecated Use {@link #ClientThread(Socket,Game,int)} instead
	 */
	public ClientThread(Socket s, Game g) throws IOException {
		this(s, g, Main.BROADCAST_PERIOD);
	}

	public ClientThread(Socket s, Game g, int wait) throws IOException {
		this.socket = s;
		game = g;
		this.wait = wait;
		timeout = System.currentTimeMillis() + SocketThread.CONNECTION_TIMEOUT;
		IN = new DataInputStream(socket.getInputStream());
		OUT = new DataOutputStream(socket.getOutputStream());
		System.err.println("/=/=/=/=/=/=/=/=/= CLIENT INFO /=/=/=/=/=/=/=/=/=\nPID: "+pid
				+ "\nSOCKETADDR: "+socket.getLocalAddress()+" PORT: "+socket.getLocalPort()
				+ "\nCONNECT TO: "+socket.getInetAddress() +" PORT: "+socket.getPort());
	}
	
	/** Does the work of setting up the client/server's shared player ID.
	 */
	private void receiveGameInfo() {
		System.out.println("Sending game info...");
		try {			
			// first: read the server game type
			System.out.println("Getting server game type...");
			short gameType = IN.readShort();
			
			// write response
			OUT.writeBoolean(gameType == TYPE);
			if (gameType == TYPE) {
				System.out.println("Server is running game map "+TYPE+"; checking ID.");
			} else {
				System.err.println("Server isn't running the requested game map.");
				close();
				return;
			}
			
			// get the server's id
			pid = IN.readInt();
			boolean validId = game.isAvailablePlayerId(pid);
			// tell the server that this is OK (or not)
			OUT.writeBoolean(validId);
			// then close ourselves if it wasn't valid
			if (validId) {
				System.out.println("Success! Using id "+pid+".");
			} else {
				System.err.println("Fatal desync: cannot use requested PID.");
				close();
			}
		} catch (IOException e) {
			System.err.println("Connection error: could not get startup info from server!");
			e.printStackTrace();
		}
	}
	
	public void run() {
		receiveGameInfo();
		
		System.out.println("=-=-=-=-=-=-=-=-=- RUNNING CLIENT "+ pid +" =-=-=-=-=-=-=-=-=-");
		int runcount = 0;
		while (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				// first, write to server
				if (super.hasCommands()) {
					Command c = super.pollCommand();
					super.writeCommand(OUT, c);
				}
				long now = System.currentTimeMillis();
				while (then + wait > now && IN.available() > 0) {
					Command c = super.readCommand(IN);
					System.out.print(c.toString()+" | ");
					// TODO actually... do stuff here. 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			runcount++;
		}
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
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean isTimedOut() {
		return System.currentTimeMillis() < timeout;
	}

	@Override
	public int getPlayerId() {
		return pid;
	}
}
