package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import artGame.control.cmds.Action;
import artGame.control.cmds.Command;

/** TODO
 * 
 * @author Vicki
 *
 */
public abstract class SocketThread extends Thread {
	private final ConcurrentLinkedQueue<Action> queue = new ConcurrentLinkedQueue<Action>(); // FIXME TO BE DELETED
	private final ConcurrentLinkedQueue<Command> cmdQueue = new ConcurrentLinkedQueue<Command>();
	static final int wait = 20;
	static final int CONNECTION_TIMEOUT = 10000;
	static final int LARGE_PACKET_SIZE = 1024; // used for testing 
	
	private volatile long timesOutAt = System.currentTimeMillis() + maxWait();
	private Socket socket;
	
	/** Returns the maximum length of time the SocketThread will wait when waitFor() is called. */
	public abstract int maxWait();

	/** Returns the address of the machine this SocketThread is connected to. */
	public abstract InetAddress getInetAddress();
	
	/** Returns the port on the machine the SocketThread is connected to. */
	public abstract int getPort();

	/** Helper method that makes sure the socket is set up correctly before use. 
	 * If it returns false, no attempt should be made to use the socket until it is true. */
	public abstract boolean isSocketSafe();
	
	/** Returns whether the socket has timed out. */
	public boolean isTimedOut() {
		return timesOutAt < System.currentTimeMillis();
	}

	/** Closes the thread's socket. Should only be called before discarding the thread. */
	public abstract boolean close();
	
	/** Returns the ID of the player connected to this socket.*/
	public abstract int getPlayerId();

	/** Readies an Action to be sent along the connection. */
	@Deprecated
	public boolean sendAction(Action a) {
		// TODO some checks here 
		return queue.add(a);
	}
	
	/** Polls an action to send from the queue */
	@Deprecated
	protected Action poll() {
		return queue.poll();
	}
	
	/** Returns true if there are queued commands to be processed. */
	@Deprecated
	/** Returns true if there are queued actions to process. */
	protected boolean hasActions() {
		return queue.size() > 0;
	}
	
	/** Readies a command to be sent */
	public boolean sendCommand(Command c) {
		if (c != null) {
			return cmdQueue.add(c);
		} 
		return false;
	}
	
	/** Polls the next command to be sent */
	protected Command pollCommand() {
		return cmdQueue.poll();
	}
	
	/** Returns true if there are commands to be sent. */
	protected boolean hasCommands() {
		return (cmdQueue.size() > 0);
	}
	
	/** Attempts to read a command from the data stream. */
	protected Command readCommand(DataInputStream in) throws IOException {
		return new Command(in.readChar(), in.readInt());
	}
	
	/** Writes the given command to the data stream. */
	protected void writeCommand(DataOutputStream out, Command c) throws IOException {
		out.writeChar(c.key());
		out.writeInt(c.id());
	}
	
	/** Returns the number of elements in the queue, waiting to be processed. */
	protected int queueSize() {
		return cmdQueue.size();
	}
	
	/** Checks the first item in the queue. */
	public Command peek() {
		return cmdQueue.peek();
	}

	/** In between data segments, waits and checks to ensure the connection hasn't
	 * timed out.
	 * @param s
	 */
	protected void waitFor(InputStream s) {
		timesOutAt = System.currentTimeMillis() + maxWait();
		try {
			while (s.available() <= 0 && timesOutAt > System.currentTimeMillis()) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// we've been interrupted! let's get back to work.
					timesOutAt = 0;
					System.err.println("Waiting was interrupted.");
				}
			}
			if (s.available() > 0) { // great, there's input, let's get back to business
				return;
			}
			timesOutAt = 0;
			System.err.println("Connection timed out! Closing.");
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("uh... so there's been a terrible accident...");
		}
	}
}
