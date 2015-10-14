package artGame.control.cmds;

import artGame.main.Game;

/** The Command interface is used for creating commands that
 * can be sent and received by the Client and Server Threads, 
 * for the purposes of executing on a Game. 
 * 
 * @author Vicki
 *
 */
public interface CommandInter {	
	public static final int BYTES_BYTE = 1;
	public static final int BYTES_CHAR = 2;
	public static final int BYTES_SHORT = 2;
	public static final int BYTES_INT = 4;
	public static final int BYTES_LONG = 8;
	public static final int BYTES_FLOAT = 4;
	public static final int BYTES_DOUBLE = 8;
	public static final int BYTES_BOOLEAN = 1;
	
	public static final String TAIL = "_CMD";
	
	/** The id of the character (if any) this command modifies */ 
	public int id();
	
	/**<p>The length of this command as a byte array.  (ie, the number of bytes + 1)</p>
	 * <p>REQUIRES: All CommandInter objects of the same class type must have exactly the same
	 * byteSize(). </p>*/
	public int byteSize();
	
	/** Helper method for toString; returns a few characters denoting the command type. */
	public String tag();
	
	/** <p>Returns the command as a stream-writeable array of bytes, of byteSize() size.</p>
	 */ 
	public byte[] bytes();
	
	/** Performs the action on the game. Returns true if the action was successful. */
	public boolean execute(Game g);
	
	/** Returns the time at which the command was created. */
	public long time();
	
	/** Returns a char representing the action type. */
	public char action();
}
