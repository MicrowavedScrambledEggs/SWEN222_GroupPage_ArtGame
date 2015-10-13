package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import artGame.control.cmds.Action;
import artGame.control.cmds.Command;
import artGame.control.cmds.CommandInter;
import artGame.control.cmds.MoveCommand;
import artGame.control.cmds.TileStateCommand;
import artGame.game.GameError;
import artGame.game.Player;
import artGame.main.Game;

/** TODO
 * 
 * @author Vicki
 *
 */
public abstract class SocketThread extends Thread {
	private final ConcurrentLinkedQueue<CommandInter> cmdQueue = new ConcurrentLinkedQueue<CommandInter>();
	static final int wait = 1;
	static final int CONNECTION_TIMEOUT = 10000;
	static final int LARGE_PACKET_SIZE = 1024; // used for testing
	boolean isRunning = true;
	private long timeOutAt = 0;
	private final Socket socket;
	private Game game;

	private boolean isTimedOut = false;
	
	public SocketThread(Socket s, Game g) {
		socket = s;
		game = g;
	}
	
	public SocketThread(Socket s, Game g, ConcurrentLinkedQueue<CommandInter> cmdQ) {
		socket = s;
		game = g;
		ConcurrentLinkedQueue<CommandInter> dup = new ConcurrentLinkedQueue<>();
		dup.addAll(cmdQ);
		cmdQueue.addAll(dup);
	}
	
	protected Socket socket() {
		return socket;
	}
	
	protected Game game() {
		return game;
	}
	
	/** Returns the address of the machine this SocketThread is connected to. */
	public abstract InetAddress getInetAddress();
	
	/** Returns the port on the machine the SocketThread is connected to. */
	public abstract int getPort();

	/** Helper method that makes sure the socket is set up correctly. 
	 * If it returns false, no attempt should be made to use the socket. */
	public abstract boolean isSocketSafe();
	
	/** Returns whether the socket has timed out. */
	public boolean isTimedOut() {
		return isTimedOut;
	}
	
	/** Returns the maximum length of time the SocketThread will wait when waitFor() is called. */
	public int maxWait() {
		return SocketThread.CONNECTION_TIMEOUT;
	}
	
	/** Writes the given command to the data stream. */
	synchronized protected void writeCommand(DataOutputStream out, CommandInter toServer) throws IOException {
		out.write(toServer.byteSize());
		out.write(toServer.bytes(), 0, toServer.byteSize());
		out.flush();
	}
	
	/** Updates the time at which the client connection is considered timed out and should be killed. */
	public long updateTimeOut() {
		timeOutAt = System.currentTimeMillis() + CONNECTION_TIMEOUT;
		return timeOutAt;
	}
	
	/** If the child class has timed out while performing some other process, 
	 * this method is called to force isTimedOut()'s state.
	 * @return Whether the thread was timed out before the method was called.   
	 */
	protected boolean setTimedOut(boolean b) {
		boolean wasTimedOut = isTimedOut;
		isTimedOut = b;
		return wasTimedOut;
	}

	/** Closes the thread's socket. Should only be called before discarding the thread. */
	public abstract boolean close();
	
	/** Returns the ID of the player connected to this socket.*/
	public abstract int getPlayerId();

	/** Reads next command from socket and returns it.
	 * @throws IncompatiblePacketException */
	protected CommandInter readSocket(final DataInputStream IN) throws InterruptedException, IncompatiblePacketException {
		byte[] arr = readByteCommand(IN);
		if (arr.length == MoveCommand.bytes) {
			return new MoveCommand(arr);
		} else if (arr.length == TileStateCommand.bytes) {
			return new TileStateCommand(arr);
		}
		throw new IncompatiblePacketException("Cannot parse this packet size!");
	}
	
	/** Reads the next array of bytes from the input stream. */
	protected byte[] readByteCommand(final DataInputStream in) throws IncompatiblePacketException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<byte[]> read = executor.submit(new Callable<byte[]>() {
			@Override
			public byte[] call() throws Exception { 
				while (in.available() <= 0) {}
				updateTimeOut();
				int size = in.readInt();
				if (size <= Integer.MAX_VALUE) {
					byte[] b = new byte[size];
					System.out.println(size+",\n\t"+Arrays.toString(b));
					in.read(b, 0, size);
					return b;
				} else {
					return new byte[0];
				}
			}
		});
		
		try {
			return read.get(SocketThread.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			System.err.println("ExecutionException occurred; returning a null command.");
			e.printStackTrace();
		} catch (TimeoutException e) {
			setTimedOut(true);
			System.err.println("Timed out while reading client.");
		} catch (InterruptedException e) {
			System.err.println("Reading was interrupted; returning a null command.");
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Cannot parse a packet of this size!");
	}
	
	/** Gets the queue of commands to be written. */
	protected Collection<CommandInter> getQueue() {
		return new LinkedList<>(cmdQueue);
	}
	
	/** Readies a command to be sent */
	public synchronized boolean sendCommand(CommandInter c) {
		if (c != null) {
			return cmdQueue.add(c);
		} 
		return false;
	}
	
	/** Polls the next command to be sent */
	protected synchronized CommandInter pollCommand() {
		return cmdQueue.poll();
	}
	
	/** Returns true if there are commands to be sent. */
	protected synchronized boolean hasCommands() {
		return (cmdQueue.size() > 0);
	}
	
	/** Returns the number of elements in the queue, waiting to be processed. */
	protected int queueSize() {
		return cmdQueue.size();
	}
	
	/** Checks the first item in the queue. */
	public synchronized CommandInter peek() {
		return cmdQueue.peek();
	}
	
	/** Prevents the thread from running without closing the socket. */
	protected void end() {
		isRunning = false;
	}

	/** Checks whether the thread is running. */
	protected boolean isRunning() {
		return isRunning;
	}
	
	/** If the queue has at least one command to send, writes it to the server. */
	synchronized protected void writeQueue() throws IOException {
		DataOutputStream OUT = new DataOutputStream(socket.getOutputStream());
		if (hasCommands()) {
			CommandInter c = pollCommand();
			
			writeCommand(OUT, c);
		}
	}
	
	/** Testing method. 
	 * Performs action on Game */
	synchronized protected boolean doAction(CommandInter clientCmd) {
		try {
			System.out.println("Do action on "+ game.getName());
			clientCmd.execute(game);
		} catch (GameError e) { }
		return false;
	}
}
