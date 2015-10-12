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
import artGame.game.GameError;
import artGame.game.Player;
import artGame.main.Game;
import artGame.main.Main;

/** The ClientThread provides a means of sending and receiving commands from the server.
 * Like the ServerThread, it requires a reference to a running game in order to work properly. 
 */
public class ClientThread extends SocketThread {
	private final ConcurrentLinkedQueue<Command> cmdQueue = new ConcurrentLinkedQueue<Command>();
	private static final short TYPE = 120; // TODO in a world with more than one map,
										 // we'd need to get this from the loaded Game
										 // so we can only connect to a server running the same map
	private final Game game;
	private Socket socket;
	private volatile int pid;
	private final int wait;
	
	/** Testing constructor */
	protected ClientThread(Socket s, Game g, ConcurrentLinkedQueue<Command> q) {
		socket = s;
		game = g;
		wait = SocketThread.wait;
		ConcurrentLinkedQueue<Command> dup = new ConcurrentLinkedQueue<>();
		dup.addAll(q);
		cmdQueue.addAll(dup);
	}

	/** Creates a new ClientThread that listens to the given socket.
	 * @param socket A socket connected to a ServerThread
	 * @param game A Game object
	 * @param wait The time to rest between requests
	 * @throws IOException
	 */
	public ClientThread(Socket socket, Game game, int wait) throws IOException {
		this.socket = socket;
		this.game = game;
		this.wait = wait;
		System.err.println("/=/=/=/=/=/=/=/=/= CLIENT INFO /=/=/=/=/=/=/=/=/=\n"+toString());
	}
	
	/** Does the work of setting up the client/server's shared player ID. */
	synchronized void receiveGameInfo() {
		System.out.println("Sending game info...");
		try {
			DataInputStream IN =  new DataInputStream(socket.getInputStream());
			DataOutputStream OUT = new DataOutputStream(socket.getOutputStream());
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
			boolean validId = game.isAvailablePlayerId(pid);
			// tell the server that this is OK (or not)
			OUT.writeBoolean(validId);
			OUT.flush();
			// then close ourselves if it wasn't valid
			if (validId) {
				game.addPlayer(pid);
				System.out.println("Our player ID is "+pid+" which corresponds to a player "+game.getPlayer(pid));
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
		while (!socket.isClosed() && isRunning() && !isTimedOut()) {
			try {
				DataInputStream IN =  new DataInputStream(socket.getInputStream());
				long then = System.currentTimeMillis();
				// first, write to server
				while (System.currentTimeMillis() < then + wait && hasCommands()) {
					writeQueue();
				}
				long now = System.currentTimeMillis();
				// then, read server's command
				while (then + wait > System.currentTimeMillis() && IN.available() == 0) {}
				if (IN.available() > 0) {
					Command c = readCommand(IN);
					System.out.print(c.toString());
					// TODO process server command
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			runcount++;
		}
	}

	/** If the queue has at least one command to send, writes it to the server. */
	synchronized protected void writeQueue() throws IOException {
		DataOutputStream OUT = new DataOutputStream(socket.getOutputStream());
		if (hasCommands()) {
			Command c = pollCommand();
			writeCommand(OUT, c);
		}
	}
	
	/** Testing method. Performs action on Game */
	synchronized protected boolean doAction(Command c) {
		try {
			System.out.println("Do action on "+ game.getName());
			game.doAction(game.getPlayer(c.id), c.action);
			return true;
		} catch (GameError e) { }
		System.err.println("Player "+c.id+" was not found to perform "+ c.action +"!");
		return false;
	}
	
	/** this should never be used. */
	@SuppressWarnings("unused")
	@Deprecated
	private char getUserCmd() {
		RunnableFuture<java.lang.Character> t = new RunnableFuture<java.lang.Character>() {
			char result = ' ';
			boolean done = false;
			boolean cancelled = false;
			@Override
			public boolean cancel(boolean arg0) {
				if (!done) {
					cancelled = true;
					return false;
				} else if (cancelled) {
					return false;
				}
				cancelled = true;
				return true;
			}

			@Override
			public java.lang.Character get() throws InterruptedException,
					ExecutionException {
				while (!done) {
					sleep(10);
				}
				return new java.lang.Character(result);
			}

			@Override
			public java.lang.Character get(long arg0, TimeUnit arg1)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				long then = System.currentTimeMillis() + arg1.toMillis(arg0);
				if (cancelled) {
					throw new CancellationException();
				}
				while (then > System.currentTimeMillis() && !done) {
					if (then - System.currentTimeMillis() < 10) {
						sleep(then - System.currentTimeMillis());
					} else {
						sleep(10);
					}
				}
				if (!done) {
					throw new TimeoutException();
				}
				return new java.lang.Character(result);
			}

			@Override
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public boolean isDone() {
				return done;
			}

			@Override
			public void run() {
				System.out.print("\n> ");
				Scanner sc = new Scanner(System.in);
				result = sc.next().charAt(0);
				sc.close();
			}
		};
		try {
			return t.get().charValue();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return '`'; // TODO we can do better
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
	public int getPlayerId() {
		return pid;
	}
	
	public String toString() {
		return "Client("+pid+") @"+socket.getLocalAddress()+":"+socket.getLocalPort()+" 2 "+socket.getInetAddress() +":"+socket.getPort();
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
	protected void writeAndReadParameters(Command send, Command read) {
		
		if (!socket.isClosed()) {
			try {
				DataInputStream IN =  new DataInputStream(socket.getInputStream());
				DataOutputStream OUT = new DataOutputStream(socket.getOutputStream());
				long then = System.currentTimeMillis();
				writeQueue();
				while (then + wait > System.currentTimeMillis() && IN.available() == 0) {}
				if (IN.available() > 0) {
					Command c = readCommand(IN);
					System.out.print(c.toString());
					// TODO process server command
				}
				// instead of reading from input, acts on the parameter
				for (Player x : game.getPlayers()) {
					if (x.getId() == read.id) {
						game.doAction(x, read.action); // now make sure we do the action
						break;
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
	protected Command writeParameterReadQueue(Command send) {
		if (!socket.isClosed()) {
			try {
				DataInputStream IN =  new DataInputStream(socket.getInputStream());
				DataOutputStream OUT = new DataOutputStream(socket.getOutputStream());
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
	 * @param send Command to be sent
	 * @return Command received from socket
	 */
	protected Command writeParameter(Command send) {
		if (!socket.isClosed()) {
			try {
				DataOutputStream OUT = new DataOutputStream(socket.getOutputStream());
				if (send != null) {
					writeCommand(OUT, send);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/** Testing method, reads command from socket and returns it
	 * Queue is not changed; nothing is written.  */
	protected Command readSocket(final DataInputStream IN) throws InterruptedException {
		Command serverCmd = null;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Command> read = executor.submit(new Callable<Command>() {
			@Override
			public Command call() throws Exception {
				return readCommand(IN);
			}
		});
		try {
			serverCmd = read.get(SocketThread.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			setTimedOut(true);
			System.err.println("Timed out while reading client.");
			close();
		}
		return serverCmd;
	}

	@Override
	public int maxWait() {
		return SocketThread.CONNECTION_TIMEOUT;
	}
	
	/** Queues a command to be sent. */
	synchronized public boolean sendCommand(Command c) {
		if (c != null) {
			return cmdQueue.add(c);
		} 
		return false;
	}
	
	/** Polls the next command to be sent. */
	synchronized protected Command pollCommand() {
		return cmdQueue.poll();
	}
	
	/** Returns true if there are commands to be sent. */
	synchronized protected boolean hasCommands() {
		return (cmdQueue.size() > 0);
	}
	
	/** Writes the given command to the data stream. */
	synchronized protected void writeCommand(DataOutputStream out, Command c) throws IOException {
		out.writeChar(c.key());
		out.writeInt(c.id());
	}
	
	/** Returns the number of elements in the queue, waiting to be processed. */
	synchronized protected int queueSize() {
		return cmdQueue.size();
	}
	
	/** Checks the first item in the queue. */
	synchronized public Command peek() {
		return cmdQueue.peek();
	}

	synchronized protected Game getGame() {
		return game;
	}
	synchronized protected Collection<Command> getQueue() {
		return new LinkedList<>(cmdQueue);
	}
}
