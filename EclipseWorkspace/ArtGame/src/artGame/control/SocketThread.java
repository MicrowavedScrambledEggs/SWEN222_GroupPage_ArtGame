package artGame.control;

import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import artGame.control.cmds.Action;

/** TODO
 * 
 * @author Vicki
 *
 */
public abstract class SocketThread extends Thread {
	private final ConcurrentLinkedQueue<Action> queue = new ConcurrentLinkedQueue<Action>();
	static final int CONNECTION_TIMEOUT = 10000;
	static final int LARGE_PACKET_SIZE = 1024; // used for testing 

	/** Returns the address of the machine this SocketThread is connected to. */
	public abstract InetAddress getInetAddress();
	
	/** Returns the port on the machine the SocketThread is connected to. */
	public abstract int getPort();

	/** Helper method that makes sure the socket is set up correctly before use. 
	 * If it returns false, no attempt should be made to use the socket until it is true. */
	public abstract boolean isSocketSafe();
	
	/** Returns whether the socket has timed out. */
	public abstract boolean isTimedOut();

	/** Closes the thread's socket. Should only be called before discarding the thread. */
	public abstract boolean close();
	
	/** Returns the ID of the player connected to this socket.*/
	public abstract int getPlayerId();

	/** Readies an Action to be sent along the connection. */
	public boolean sendAction(Action a) {
		// TODO some checks here 
		return queue.add(a);
	}
}
