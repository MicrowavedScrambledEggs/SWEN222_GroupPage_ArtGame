package artGame.control.cmds;

/** TODO
 * 
 * @author Vicki
 *
 */
public interface Command {	
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
	
	/** The length of this command as a byte array.  (ie, the number of bytes + 1)*/
	public int byteSize();
	
	/** Helper method for toString; returns a few characters denoting the command type. */
	public String tag();
	
	/** Returns the command as a stream-writeable array of bytes, of byte() size. */ 
	public byte[] bytes();
}
