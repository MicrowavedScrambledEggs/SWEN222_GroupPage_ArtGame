package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.swing.SwingUtilities;

import artGame.control.cmds.Command;
import artGame.game.Player;
import artGame.main.Game;
import artGame.main.Main;
import artGame.ui.gamedata.GameData;
import artGame.ui.gamedata.GamePacketData;

/**
 * TODO
 *
 * @author Vicki
 *
 */
public class ServerThread extends SocketThread {
	private static final int PID_START = 1; // the id of the first player
	private static final short TYPE = 120; // TODO in a world with more than one
											// map,
											// we'd need to get this from the
											// loaded Game
											// so only clients running the same
											// level can connect.
	private Game game;
	private Socket socket;
	private final int wait;
	private int pid;
	private long timesOutAt = 0;
	private final DataInputStream IN;
	private final DataOutputStream OUT;

	/**
	 * Creates a new ServerThread, which will manage the data input/output for a
	 * single client/server connection.
	 *
	 * @param game
	 *            The Game should be the same Game that is given to all other
	 *            ServerThreads.
	 * @param socket
	 *            The socket to use for connection. Should be already bound
	 *            before the constructor is invoked.
	 * @param wait
	 *            How long to wait between server refreshes. (If 0, the server
	 *            refreshes as fast as it is able.)
	 * @throws IOException
	 */
	public ServerThread(Game game, Socket socket, int wait) throws IOException {
		this.game = game;
		this.socket = socket;
		this.wait = wait;
		IN = new DataInputStream(socket.getInputStream());
		OUT = new DataOutputStream(socket.getOutputStream());
		System.err
				.println("================== SERVER INFO ==================\n"
						+ toString());
		// OK, so the ServerThread's constructor will also responsible for
		// sending the
		// information a client needs to set up the game

		System.out.println("=-=-=-=-=-=-=-=-=- RUNNING SERVER " + pid
				+ " =-=-=-=-=-=-=-=-=-");

		sendGameInfo();
	}

	/** Constructor for testing */
	protected ServerThread(Socket s) throws IOException {
		this.game = null;
		this.socket = s;
		this.wait = Main.WAIT_PERIOD;
		IN = new DataInputStream(socket.getInputStream());
		OUT = new DataOutputStream(socket.getOutputStream());
	}

	public void updateGame(Game game) {
		this.game = game;
	}

	/** Does the work of setting up the client/server's shared player ID. */
	private void sendGameInfo() {
		try {
			// first, send the game type
			System.out.println("Sending game type...");
			OUT.writeShort(TYPE);

			// get client response
			boolean ok = IN.readBoolean();
			if (ok) {
				System.out.println("Client is running game map " + TYPE
						+ "; checking ID.");
			} else {
				System.err
						.println("Client is running a different game map; closing connection.");
				close();
				return;
			}
			// TODO server game isn't being updated with player IDs
			// get the next player ID
			pid = PID_START;

			while (!Main.getGame().isAvailablePlayerId(pid)) {
				pid++;
			}

			System.out.println("Assigning client the shared ID " + pid);
			OUT.writeInt(pid);

			// get the client's response-- should be OK for them too
			ok = IN.readBoolean();
			if (ok) {

			} else {
				System.err
						.println("Fatal desync: client cannot use requested PID.");
				close();
			}
			System.out.println();
		} catch (IOException e) {
			System.err
					.println("Connection error: could not send startup info to client!");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!socket.isClosed() && socket.isConnected()) {
			try {
				game = Main.getGame();

				long then = System.currentTimeMillis();
				//super.waitFor(IN);

				// read first
				timesOutAt = System.currentTimeMillis()
						+ SocketThread.CONNECTION_TIMEOUT;

				// FIXME terrible implementation
				Command clientCmd = super.readCommand(IN);

				if (clientCmd.action == 'x' && clientCmd.id == pid) {

				}

				if (clientCmd.action == '!') {
					System.out
							.println("Stop-moving packet for " + clientCmd.id);
				}
				ServerThread[] ss = Main.getKids();
				//System.out.println("cmd: " + clientCmd.action);
				for (ServerThread s : ss) {
					if (s != null && s != this) {
						s.sendCommand(clientCmd); // make sure everyone else
													// gets this command
					}
				}
				for (Player x : game.getPlayers()) {
					if (x.getId() == clientCmd.id) {
						game.doAction(x, clientCmd.action); // now make sure we
															// do the action
						break;
					}
				}
				// then do our work!
				if (super.hasCommands()) {
					Command serverCmd = super.pollCommand();
					System.out.println("(In queue: " + queueSize() + ")");
					super.writeCommand(OUT, serverCmd);
				}

				try {
					byte[] bytes = GameData
							.toByteArray(new GamePacketData(pid, game));
					if(bytes != null){
						OUT.write(bytes);
					}
				} catch (IncompatiblePacketException e) {

				}

				OUT.flush();
				long now = System.currentTimeMillis();
			
				if (now < then + Main.BROADCAST_PERIOD
						&& 0 > then + Main.BROADCAST_PERIOD - now) {
					sleep(then + Main.BROADCAST_PERIOD - now);
				}
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("socket closed");

	}

	public synchronized boolean isClosed(){
		return socket.isClosed();
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

	/** Returns true only if there is still a useable connection to the client. */
	@Override
	public boolean isSocketSafe() {
		return socket.isBound() && socket.isConnected() && !socket.isClosed();
	}

	/**
	 * Closes the socket and unlinks server and client.
	 *
	 * @return True if socket is successfully closed.
	 */
	@Override
	public boolean close() {
		if (socket.isClosed()) {
			return true;
		}
		try {
			game.removePlayer(pid);
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Returns whether the connection has timed out, and therefore should be
	 * closed.
	 */
	@Override
	public boolean isTimedOut() {
		return timesOutAt < System.currentTimeMillis();
	}

	@Override
	public int getPlayerId() {
		return pid;
	}

	public String toString() {
		return "Server(" + pid + ") @" + socket.getLocalAddress() + ":"
				+ socket.getLocalPort() + " 2 " + socket.getInetAddress() + ":"
				+ socket.getPort();
	}

	/** -================================================================== */

	/**
	 * Testing method. Reads/writes the parameter Commands instead of the queue
	 *
	 * @param send
	 *            Command to be send
	 * @return Command received from client
	 */
	protected void readAndWriteParameters(Command read, Command send) {
		if (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				// read first
				timesOutAt = System.currentTimeMillis()
						+ SocketThread.CONNECTION_TIMEOUT;
				// instead of reading from input, acts on the parameter
				if (read != null) {
					for (Player x : game.getPlayers()) {
						if (x.getId() == read.id) {
							game.doAction(x, read.action); // now make sure we
															// do the action
							break;
						}
					}
				}
				// then write from our queue as normal
				if (send != null) {
					super.writeCommand(OUT, send);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Testing method. Reads the parameter command and writes command from the
	 * queue.
	 *
	 * @param send
	 *            Command to be send
	 * @return Command received from client
	 */
	protected Command readParameterWriteQueue(Command read) {

		if (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				// read first
				timesOutAt = System.currentTimeMillis()
						+ SocketThread.CONNECTION_TIMEOUT;
				// instead of reading from input, acts on the parameter
				if (read != null) {
					for (Player x : game.getPlayers()) {
						if (x.getId() == read.id) {
							game.doAction(x, read.action); // now make sure we
															// do the action
							break;
						}
					}
				}
				// then do our work!
				if (super.hasCommands()) {
					Command serverCmd = super.pollCommand();
					System.out.println("(In queue: " + queueSize() + ")");
					super.writeCommand(OUT, serverCmd);
					return serverCmd;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Testing method. Reads the parameter command and writes command from the
	 * queue.
	 *
	 * @param send
	 *            Command to be send
	 * @return Command received from client
	 */
	protected Command readQueueWriteParameter(Command send) {

		if (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				// read first
				timesOutAt = System.currentTimeMillis()
						+ SocketThread.CONNECTION_TIMEOUT;
				waitFor(IN);
				// reading queue...
				Command clientCmd = super.readCommand(IN);
				if (clientCmd.action == '!') {
					System.out
							.println("Stop-moving packet for " + clientCmd.id);
				}
				for (Player x : game.getPlayers()) {
					if (x.getId() == clientCmd.id) {
						game.doAction(x, clientCmd.action); // now make sure we
															// do the action
						break;
					}
				}
				// then do our work!
				if (send != null)
					super.writeCommand(OUT, send);
				return clientCmd;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public int maxWait() {
		return SocketThread.CONNECTION_TIMEOUT;
	}
}
