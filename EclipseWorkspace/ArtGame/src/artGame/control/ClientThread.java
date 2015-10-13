package artGame.control;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.Sys;

import artGame.control.cmds.Action;
import artGame.control.cmds.BasicPacketParser;
import artGame.control.cmds.Command;
import artGame.control.cmds.GetItemAction;
import artGame.control.cmds.MovePlayerAction;
import artGame.control.cmds.Packet;
import artGame.game.Art;
import artGame.game.Item;
import artGame.game.Player;
import artGame.game.Character.Direction;
import artGame.main.Game;
import artGame.main.Main;
import artGame.ui.gamedata.GameData;
import artGame.ui.gamedata.GamePacketData;

/**
 * The ClientThread provides a means of sending and receiving commands from the
 * server. Like the ServerThread, it requires a reference to a running game in
 * order to work properly.
 */
public class ClientThread extends SocketThread {
	private static final short TYPE = 120; // TODO in a world with more than one
											// map,
											// we'd need to get this from the
											// loaded Game
											// so we can only connect to a
											// server running the same map
	private final Game game;
	private Socket socket;
	private boolean isPlaying = true;
	private int pid = 404;
	private final int wait;
	private final DataInputStream IN;
	private final DataOutputStream OUT;
	private GamePacketData gameData;

	private long lastPing;
	private static int PING_DELAY = 50;
	
	/**
	 * @deprecated Use {@link #ClientThread(Socket,Game,int)} instead
	 */
	public ClientThread(Socket s, Game g) throws IOException {
		this(s, g, Main.BROADCAST_PERIOD);
	}

	/**
	 * Creates a new ClientThread that listens to the given socket.
	 *
	 * @param s
	 *            A socket connected to a ServerThread
	 * @param g
	 *            A Game object
	 * @param wait
	 *            The time to rest between requests
	 * @throws IOException
	 */
	public ClientThread(Socket s, Game g, int wait) throws IOException {
		this.socket = s;
		game = g;
		this.wait = wait;
		IN = new DataInputStream(socket.getInputStream());
		OUT = new DataOutputStream(socket.getOutputStream());
		System.err
				.println("/=/=/=/=/=/=/=/=/= CLIENT INFO /=/=/=/=/=/=/=/=/=\n"
						+ toString());
	}

	/**
	 * Does the work of setting up the client/server's shared player ID.
	 */
	private void receiveGameInfo() {
		System.out.println("Sending game info...");
		try {
			long then = System.currentTimeMillis();
			// first: read the server game type
			System.out.println("Getting server game type...");
			short gameType = IN.readShort();

			// write response
			OUT.writeBoolean(gameType == TYPE);
			if (gameType == TYPE) {
				System.out.println("Server is running game map " + TYPE
						+ "; checking ID.");
			} else {
				System.err
						.println("Server isn't running the requested game map.");
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
				System.out.println("Success! Using id " + pid + ".");
			} else {
				System.err.println("Fatal desync: cannot use requested PID.");
				close();
			}
			// sleep
			// long now = System.currentTimeMillis();
			// if (now < then + Main.BROADCAST_PERIOD
			// && 0 > then + Main.BROADCAST_PERIOD - now) {
			// sleep (then + Main.BROADCAST_PERIOD - now);
			// }
		} catch (IOException e) {
			System.err
					.println("Connection error: could not get startup info from server!");
			e.printStackTrace();
		}
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void run() {
		receiveGameInfo();

		System.out.println("=-=-=-=-=-=-=-=-=- RUNNING CLIENT " + pid
				+ " =-=-=-=-=-=-=-=-=-");
		int runcount = 0;
		while (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				// first, write to server
				
				if(then - lastPing >= PING_DELAY){
					super.sendCommand(new Command('x', pid));
					lastPing = then;
				}	
				
				//flush any commands to the server
				write();
				
				//super.waitFor(IN);
				// System.out.println(IN.available());
				if (IN.available() > 0) {
					byte[] bytes = new byte[IN.available()];
					IN.readFully(bytes);
					try {
						gameData = GameData.read(bytes);
						if (gameData != null) {
							GameData.updateGame(gameData);
						}
					} catch (IncompatiblePacketException e) {
						e.printStackTrace();
					}
					// Command c = super.readCommand(IN);
					// System.out.print("command: " + c.toString());
					// TODO read game data input and any other commands ########
	
				}
				long now = System.currentTimeMillis();
				if (now < then + Main.BROADCAST_PERIOD
						&& 0 > then + Main.BROADCAST_PERIOD - now) {
					sleep(then + Main.BROADCAST_PERIOD - now);
				}
				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// try {
			// sleep(20);
			// } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			runcount++;
		}
	}

	public synchronized GamePacketData getGameData() {
		return gameData;
	}

	private void write() throws IOException {
		while (super.hasCommands()) {
			Command c = super.pollCommand();
			super.writeCommand(OUT, c);
		}
	}

	private void getPlayerCmd() {
		System.out.print("\n> ");
		Scanner sc = new Scanner(System.in);
		sc.useDelimiter("\\s");
		sendCommand(new Command(sc.next().charAt(0), game.getPlayer().getId()));
		sc.close();
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
			IN.close();
			OUT.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int getPlayerId() {
		return pid;
	}

	public String toString() {
		return "Client(" + pid + ") @" + socket.getLocalAddress() + ":"
				+ socket.getLocalPort() + " 2 " + socket.getInetAddress() + ":"
				+ socket.getPort();
	}

	/**
	 * ========================================================================
	 * ===
	 */

	/**
	 * Testing method. Reads/writes the parameter Commands instead of the queue
	 *
	 * @param send
	 *            Command to be send
	 * @return Command received from client
	 */
	protected void writeAndReadParameters(Command send, Command read) {

		if (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				write();
				long now = System.currentTimeMillis();
				// then, read server's command
				while (then + wait > now && IN.available() == 0) {
				}
				if (IN.available() > 0) {
					Command c = super.readCommand(IN);
					System.out.print(c.toString());
					// TODO process server command
				}
				// instead of reading from input, acts on the parameter
				for (Player x : game.getPlayers()) {
					if (x.getId() == read.id) {
						game.doAction(x, read.action); // now make sure we do
														// the action
						break;
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
	 * Testing method. Writes the parameter command and reads command from the
	 * queue.
	 *
	 * @param send
	 *            Command to be sent
	 * @return Command received from server
	 */
	protected Command writeParameterReadQueue(Command send) {
		if (!socket.isClosed()) {
			try {
				long then = System.currentTimeMillis();
				// first, write our parameter to server
				super.writeCommand(OUT, send);
				// then, read server's command
				while (then + wait > System.currentTimeMillis()
						&& IN.available() == 0) {
				}
				if (IN.available() > 0) {
					Command c = super.readCommand(IN);
					System.out.print(c.toString());
					for (Player p : game.getPlayers()) {
						game.doAction(p, c.action);
					}
					return c;
				}
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
