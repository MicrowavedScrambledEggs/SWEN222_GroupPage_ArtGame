package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
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
import artGame.game.Player;
import artGame.main.Game;

/** TODO
 * 
 * @author Vicki
 *
 */
public abstract class SocketThread extends Thread {
	static final int wait = 1;
	static final int CONNECTION_TIMEOUT = 10000;
	static final int LARGE_PACKET_SIZE = 1024; // used for testing
	boolean isRunning = true;
	private long timeOutAt = 0;

	private boolean isTimedOut = false;
	
	public SocketThread() {}
	
	/** Returns the maximum length of time the SocketThread will wait when waitFor() is called. */
	public abstract int maxWait();

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


	/** TODO documentation
	 * 
	 * @param in Data stream to read from
	 * @return Next command to be read from socket
	 * @throws InterruptedException 
	 */
	protected Command readCommand(final DataInputStream in) {
		Command clientCmd = null;
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Command> read = executor.submit(new Callable<Command>() {
			@Override
			public Command call() throws Exception { 
				updateTimeOut();
				return new Command(in.readChar(), in.readInt());
			}
		});
		
		try {
			clientCmd = read.get(SocketThread.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
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
		return clientCmd;
	}
	
	/** Readies a command to be sent */
	public abstract boolean sendCommand(Command c);
	
	/** Polls the next command to be sent */
	protected abstract Command pollCommand();
	
	/** Returns true if there are commands to be sent. */
	protected abstract boolean hasCommands();
		
	/** Writes the given command to the data stream. */
	protected abstract void writeCommand(DataOutputStream out, Command c) throws IOException;
	
	/** Returns the number of elements in the queue, waiting to be processed. */
	protected abstract int queueSize();
	
	/** Returns the first item in the queue. */
	public abstract Command peek();
	
	protected abstract Collection<Command> getQueue();
	
	/** Prevents the thread from running without closing the socket. */
	protected void end() {
		isRunning = false;
	}

	/** Checks whether the thread is running. */
	protected boolean isRunning() {
		return isRunning;
	}
}
