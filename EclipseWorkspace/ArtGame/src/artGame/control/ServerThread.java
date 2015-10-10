package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import artGame.control.cmds.Command;
import artGame.game.Player;
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
	private final Game game; 
	private final GameClock gameRunner;
	private Socket socket;
	private final int wait;
	private int pid;
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
		gameRunner = new GameClock(game);
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
				long then = System.currentTimeMillis();
				// read first
				waitFor(IN);
				// FIXME terrible implementation
				Command clientCmd = super.readCommand(IN);
				ServerThread[] ss = Main.getKids();
				for (ServerThread s : ss) {
					if (s != this) {
						s.sendCommand(clientCmd);
					}
				}
				for (Player x : game.getPlayers()) {
					if (x.getId() == clientCmd.id) {
						game.doAction(x, clientCmd.action);
						break;
					}
				}
				// then do our work!
				long now = System.currentTimeMillis();
				while (then + wait > now) {
					if (super.hasCommands()) {
						Command serverCmd = super.pollCommand();
						super.writeCommand(OUT, serverCmd);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	@Override
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/** Returns the client's port number. */
	@Override
	public int getPort() {
		return socket.getPort();
	}

	/** Returns true only if there is still a useable connection to the client.*/
	@Override
	public boolean isSocketSafe() {
		return socket.isBound() && socket.isConnected() && !socket.isClosed();
	}

	/** Closes the socket and unlinks server and client. 
	 * @return True if socket is successfully closed.
	 */
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

	/** Returns whether the connection has timed out, and therefore should be closed. */
	@Override
	public boolean isTimedOut() {
		return timedout;
	}
	
	@Override
	public int getPlayerId() {
		return pid;
	}
}
