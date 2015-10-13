package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import artGame.control.cmds.Command;
import artGame.control.cmds.CommandInter;
import artGame.control.cmds.MoveCommand;
import artGame.control.cmds.TileStateCommand;
import artGame.game.GameError;
import artGame.game.Player;
import artGame.game.Tile;
import artGame.main.Game;
import artGame.main.Main;

/** The ClientThread provides a means of sending and receiving commands from the server.
 * Like the ServerThread, it requires a reference to a running game in order to work properly. 
 */
public class ClientThread extends SocketThread {
	private final ConcurrentLinkedQueue<CommandInter> cmdQueue = new ConcurrentLinkedQueue<CommandInter>();
	private static final short TYPE = 120; // TODO in a world with more than one map,
										 // we'd need to get this from the loaded Game
										 // so we can only connect to a server running the same map
	private volatile int pid;
	private final int wait;
	private long lastUpdateReceived = 0;
	private long avgTimeBetweenUpdates = 0;
	private int totalUpdates = 0;
	
	/** Testing constructor */
	protected ClientThread(Socket s, Game g, ConcurrentLinkedQueue<CommandInter> q) {
		super(s,g);
		wait = SocketThread.wait;
		ConcurrentLinkedQueue<CommandInter> dup = new ConcurrentLinkedQueue<>();
		dup.addAll(q);
		cmdQueue.addAll(dup);
	}

	/** Creates a new ClientThread that listens to the given socket().
	 * @param socket A socket connected to a ServerThread
	 * @param game A Game object
	 * @param wait The time to rest between requests
	 * @throws IOException
	 */
	public ClientThread(Socket socket, Game game, int wait) throws IOException {
		super(socket,game);
		this.wait = wait;
		System.err.println("/=/=/=/=/=/=/=/=/= CLIENT INFO /=/=/=/=/=/=/=/=/=\n"+toString());
	}
	
	/** Does the work of setting up the client/server's shared player ID. */
	synchronized void receiveGameInfo() {
		System.out.println("Sending game info...");
		try {
			DataInputStream IN =  new DataInputStream(socket().getInputStream());
			DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
			long then = System.currentTimeMillis();
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
			System.out.println("Client received pid "+pid);
			boolean validId = game().isAvailablePlayerId(pid);
			// tell the server that this is OK (or not)
			OUT.writeBoolean(validId);
			OUT.flush();
			// then close ourselves if it wasn't valid
			if (validId) {
				game().addPlayer(pid);
				System.out.println("Our player ID is "+pid+" which corresponds to a player "+game().getPlayer(pid));
			} else {
				System.err.println("Fatal desync: cannot use requested PID.");
				close();
			}
			// sleep
			long now = System.currentTimeMillis();
			if (now < then + Main.BROADCAST_PERIOD
					&& 0 > then + Main.BROADCAST_PERIOD - now) {
				sleep (then + Main.BROADCAST_PERIOD - now);
			}
			OUT.flush();
		} catch (IOException e) {
			System.err.println("Connection error: could not get startup info from server!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	public void run() {
		receiveGameInfo();
		
		System.out.println("=-=-=-=-=-=-=-=-=- RUNNING CLIENT "+ pid +" =-=-=-=-=-=-=-=-=-");
		int runcount = 0;
		while (!socket().isClosed() && isRunning() && !isTimedOut()) {
			try {
				DataInputStream IN =  new DataInputStream(socket().getInputStream());
				long then = System.currentTimeMillis();
				// first, write to server
				while (System.currentTimeMillis() < then + wait && hasCommands()) {
					writeQueue();
				}
				then = System.currentTimeMillis();
				// then, read server's command
				while (then + wait > System.currentTimeMillis() && IN.available() == 0) {}
				if (IN.available() > 0) {
					CommandInter c = readSocket(IN);
					lastUpdateReceived = System.currentTimeMillis();
					avgTimeBetweenUpdates = ((avgTimeBetweenUpdates * totalUpdates) + (lastUpdateReceived-then)) / (totalUpdates + 1);
					totalUpdates++;
					System.out.print(c.toString());
					// TODO process server command
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncompatiblePacketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			runcount++;
		}
	}
	
	@Override
	public InetAddress getInetAddress() {
		return socket().getInetAddress();
	}

	@Override
	public int getPort() {
		return socket().getPort();
	}

	@Override
	public boolean isSocketSafe() {
		return socket().isBound() && socket().isConnected() && !socket().isClosed();
	}

	@Override
	public boolean close() {
		try {
			socket().close();
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
		return "Client("+pid+") @"+socket().getLocalAddress()+":"+socket().getLocalPort()+" 2 "+socket().getInetAddress() +":"+socket().getPort();
	}
	
	
	/** Returns the number of elements in the queue, waiting to be processed. */
	synchronized protected int queueSize() {
		return cmdQueue.size();
	}
	
	/** Checks the first item in the queue. */
	synchronized public CommandInter peek() {
		return cmdQueue.peek();
	}
	
	/** Testing method. Gets the queue of commands to be sent. */
	synchronized protected Collection<CommandInter> getQueue() {
		return new LinkedList<>(cmdQueue);
	}
	
	/**===========================================================================
	 * ===========================================================================
	 * ===========================================================================
	 * ===========================================================================
	 * 							   testing methods below!
	 * ===========================================================================
	 * ===========================================================================
	 * ===========================================================================
	 * ===========================================================================
	 */
	
	/** Testing method. Reads and writes the parameter Commands.
	 * The queue is untouched. 
	 * 
	 * @param send Command to be send
	 * @return Command received from client
	 */
	protected void writeAndReadParameters(CommandInter send, CommandInter read) {
		
		if (!socket().isClosed()) {
			try {
				DataInputStream IN =  new DataInputStream(socket().getInputStream());
				DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
				long then = System.currentTimeMillis();
				writeQueue();
				while (then + wait > System.currentTimeMillis() && IN.available() == 0) {}
				// instead of reading from input, acts on the parameter
				for (Player x : game().getPlayers()) {
					if (x.getId() == read.id()) {
						doAction(read);
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

	/** Testing method. Writes the parameter command and reads command from the queue.
	 * Nothing is read from the socket.
	 * 
	 * @param send Command to be sent
	 * @return Command received from server
	 */
	@SuppressWarnings("unused")
	protected CommandInter writeParameterReadQueue(CommandInter send) {
		if (!socket().isClosed()) {
			try {
				DataInputStream IN =  new DataInputStream(socket().getInputStream());
				DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
				long then = System.currentTimeMillis();
				// first, write our parameter to server
				if (send != null) {
					writeCommand(OUT, send);
				}
				// then, read server's command
				if (hasCommands()) {
					return pollCommand();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/** Testing method. Writes the parameter command and nothing else. 
	 * Queue is not changed; nothing is read from socket.
	 * 
	 * @param toServer Command to be sent
	 * @return Command received from socket
	 */
	protected CommandInter writeParameter(CommandInter toServer) {
		if (!socket().isClosed()) {
			try {
				DataOutputStream OUT = new DataOutputStream(socket().getOutputStream());
				if (toServer != null) {
					writeCommand(OUT, toServer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
