package artGame.control.cmds;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

import artGame.main.Game;

/** TODO
 * 
 * @author Vicki
 *
 */
public class GlobalTilesCommand implements CommandInter {
	public static final short PLAYER = 0;
	public static final short GUARD = 1;
	public static final short STATUE = 2;
	public static final short EMPTY = 3;
	
	public static enum Occupant { PLAYER, GUARD, STATUE, EMPTY };

	private TileStateCommand[] cmds;
	private int numCmds;
	public final long time;
	
	//public static final int bytes
	
	private static final String TAG = "GLOBAL";

	/** Creates a new TileState from an array of bytes. 
	 * @throws IOException */
	public GlobalTilesCommand(byte[] b, DataInputStream in, long time) throws IOException { // entity id action x y time
//			if (b.length != byteSize()) {
//				throw new IllegalArgumentException();
//			}
		int i = 0;
		int numCmds = (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + (b[i++]);
		cmds = new TileStateCommand[numCmds];
		for (int n = 0; n < numCmds; n++) {
			int size = in.readInt();
			byte[] newCmd = new byte[size];
			in.read(newCmd,0,size);
			cmds[n] = new TileStateCommand(newCmd);
		}
		this.time = time;
	}

	/** Creates a new TileState. */
	public GlobalTilesCommand (long time, TileStateCommand... cmds) {
		this.cmds = Arrays.copyOf(cmds,cmds.length);
		this.time = time;
	}
	
	public String toString() {
		return tag() +" "+ Command.TAIL;
	}

	public int byteSize() {
		return Command.BYTES_LONG + Command.BYTES_INT;
	}

	@Override
	public String tag() {
		return TAG;
	}
	
	@Override
	public byte[] bytes() {
		byte[] bytes = new byte[TileStateCommand.bytes*numCmds + BYTES_LONG];
        for (int i = 0; i < numCmds; i+=TileStateCommand.bytes) {
        	byte[] tilestate = cmds[i].bytes();
        	for (int y = 0; y < TileStateCommand.bytes; i++) {
        		bytes[i+y] = tilestate[y];
        	}
        }
        int lastIdx = bytes.length - 3;
        bytes[lastIdx++] = (byte)(time >>> 64); // long
        bytes[lastIdx++] = (byte)(time >>> 52);
		bytes[lastIdx++] = (byte)(time >>> 48);
		bytes[lastIdx++] = (byte)(time >>> 40);
		bytes[lastIdx++] = (byte)(time >>> 32);
		bytes[lastIdx++] = (byte)(time >>> 24);
		bytes[lastIdx++] = (byte)(time >>> 16);
		bytes[lastIdx++] = (byte)(time >>> 8);
        return bytes;
	}

	@Override
	public boolean execute(Game g) {
		boolean allSuccess = true;
		for (TileStateCommand t : cmds) {
			boolean y = t.execute(g);
			if (!y) {
				allSuccess = false;
			}
		}
		return allSuccess;			
	}
	
	@Override
	public long time() {
		return time;
	}

	@Override
	public int id() {
		return 0; // FIXME
	}

	@Override
	public char action() {
		return '#';
	}
}
