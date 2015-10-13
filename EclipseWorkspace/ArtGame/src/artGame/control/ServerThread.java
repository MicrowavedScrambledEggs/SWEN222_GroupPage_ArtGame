package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import artGame.control.cmds.Command;
import artGame.game.GameError;
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
	private final int wait;
	private volatile int pid;

	/** Creates a new ServerThread, which will manage the data input/output for 
	 * a single client/server connection. 
	 * @param socket The socket to use for connection. Should be already bound before
	 * the constructor is invoked.
	 * @param game The Game should be the same Game that is given to all other ServerThreads. 
	 * @param wait How long to wait between server refreshes. (If 0, the
	 * server refreshes as fast as it is able.)
	 * @throws IOException 
	 */
	public ServerThread(Socket socket, Game game, int wait) throws IOException {
		super(socket,game);
		this.wait = wait;
		System.err.println("================== SERVER INFO ==================\n"+toString());
	}
	
	/** Testing constructor */
	protected ServerThread(Game g, Socket s, ConcurrentLinkedQueue<Command> q) {
		super(s,g,q);
		wait = SocketThread.wait;
	}

	/** Test constructor */
	protected ServerThread(Socket socket, Game game, ConcurrentLinkedQueue<Command> cl) throws IOException {
		super(socket,game, cl);
		this.wait = Main.WAIT_PERIOD;
	}
	
	/** Constructor for testing */
	protected ServerThread(Socket s) throws IOException {
		super(s,null);
		this.wait = Main.WAIT_PERIOD;
	}

	/** Does the work of setting up the client/server's shared player ID. */
	synchronized void sendGameInfo() {
		try {
			DataInputStream IN =  new DataInputStream(socket().getInputStream());
			DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
			// first, send the game type
			boolean ok = validateGameType(IN, OUT); // kills connection if not true
			
			// TODO server game isn't being updated with player IDs
			// get the next player ID
			pid = getNextPlayerId(PID_START);
			OUT.writeInt(pid);
			// get the client's response-- should be OK for them too
			ok = IN.readBoolean();
			if (ok) {
				game().addPlayer(pid);
				System.out.println("Our player ID is "+pid+" which corresponds to a player "+game().getPlayer(pid));
			} else {
				System.err.println("Fatal desync: client cannot use requested PID.");
				close();
			}
			int numPlayers = game().getPlayers().size();
			System.out.println("Server has "+numPlayers+" players to report on.");
			// now update with the state of the other players:
			for (Player p : game().getPlayers()) {
				
			}
		} catch (IOException e) {
			System.err.println("Connection error: could not send startup info to client!");
			e.printStackTrace();
		}
	}

	private int getNextPlayerId(int start) {
		while (!game().isAvailablePlayerId(start) && start != Integer.MAX_VALUE) {
			start++;
		}
		return start;
	}

	/** part of the method above */
	private boolean validateGameType(DataInputStream IN, DataOutputStream OUT)
			throws IOException {
		System.out.println("Sending game type...");
		OUT.writeShort(TYPE);
		
		// get client response
		boolean ok = IN.readBoolean();
		if (ok) {
			System.out.println("Client is running game map "+TYPE+"; checking ID.");
			return true;
		} else {
			System.err.println("Client is running a different game map; closing connection.");
			close();
			return false;
		}
	}

	public void run() {
		// send our information to the client, establish pid
		sendGameInfo();
		System.out.println("=-=-=-=-=-=-=-=-=- RUNNING SERVER "+pid+" =-=-=-=-=-=-=-=-=-");
		while (!socket().isClosed() && isRunning() &&!isTimedOut()) {
			try {
				long then = System.currentTimeMillis();
				final DataInputStream IN =  new DataInputStream(socket().getInputStream());
				final DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
				// read first
		        Command clientCmd = readSocket(IN); // will end() thread if it times out
		        if (clientCmd == null) break;
		        
				// then update sister threads
				ServerThread[] ss = Main.getKids();
				for (ServerThread s : ss) {
					if (s != null && s != this) {
						s.sendCommand(clientCmd);
					}
				}
				// apply the action to our game
				for (Player x : game().getPlayers()) {
					if (x.getId() == clientCmd.id()) {
						doAction(clientCmd);
						break;
					}
				}
				// do our work!
				long now = System.currentTimeMillis();
				long time = now + wait;
				while (System.currentTimeMillis() < time && hasCommands()) {
					Command serverCmd = pollCommand();
					System.out.println("(In queue: "+queueSize()+")");
					writeCommand(OUT, serverCmd);
				}
			} catch (IOException | InterruptedException | IncompatiblePacketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public InetAddress getInetAddress() {
		return socket().getInetAddress();
	}

	/** Returns the client's port number. */
	@Override
	public int getPort() {
		return socket().getPort();
	}

	/** Returns true only if there is still a usable connection to the client.*/
	@Override
	public boolean isSocketSafe() {
		return socket().isBound() && socket().isConnected() && !socket().isClosed();
	}

	/** Closes the socket and unlinks server and client. 
	 * @return True if socket is successfully closed.
	 */
	@Override
	public synchronized boolean close() {
		if (socket().isClosed()) {
			return true;
		}
		try {
			game().removePlayer(pid);
			socket().shutdownInput();
			socket().shutdownOutput();
			socket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/** Get the ID of the player that belongs to this server's client. */
	@Override
	public int getPlayerId() {
		return pid;
	}
	
	public String toString() {
		return "Server("+pid+") @"+socket().getLocalAddress()+":"+socket().getLocalPort()+" 2 "+socket().getInetAddress() +":"+socket().getPort();
	}
	
	/** -================================================================== */

	
	/** Testing method. Performs the read parameter and writes the send parameter
	 * instead of reading from socket or popping from the queue. 
	 * Cannot time-out as a result of this method. 
	 * Does not propagate action to sister threads.
	 * 
	 * @param send Command to be sent
	 * @return Command to be received from client
	 */
	protected void readAndWriteParameters(Command read, Command send) {
		if (!socket().isClosed()) {
			try {
				DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
				// instead of reading from input, acts on the parameter
				if (read != null) {
					for (Player x : game().getPlayers()) {
						if (x.getId() == read.id()) {
							doAction(read); // now make sure we do the action
							break;
						}
					}
				}
				// then write from our queue as normal
				if (send != null) {
					writeCommand(OUT, send);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** Testing method. Reads the parameter command and writes command from the queue.
	 * 
	 * @param send Command to be send
	 * @return Command received from client
	 */
	protected Command readParameterWriteQueue(Command read) {
		
		if (!socket().isClosed()) {
			try {
				DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
				@SuppressWarnings("unused")
				long then = System.currentTimeMillis();
				// instead of reading from input, acts on the parameter
				if (read != null) {
					for (Player x : game().getPlayers()) {
						if (x.getId() == read.id()) {
							doAction(read); // now make sure we do the action
							break;
						}
					}
				}
				// then do our work!
				if (hasCommands()) {
					Command serverCmd = pollCommand();
					System.out.println("(In queue: "+queueSize()+")");
					writeCommand(OUT, serverCmd);
					return serverCmd;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/** Testing method. Reads from the socket().
	 * Nothing is changed in the queue.
	 * @return Command from queue
	 * @throws IncompatiblePacketException 
	 * @throws InterruptedException 
	 */
	protected Command readFromClient() throws IOException, InterruptedException, IncompatiblePacketException {
		DataInputStream IN = new DataInputStream(socket().getInputStream());
		return readSocket(IN);
	}

	@Override
	public int maxWait() {
		return SocketThread.CONNECTION_TIMEOUT;
	}
}
