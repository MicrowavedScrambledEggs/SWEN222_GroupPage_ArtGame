package artGame.control.cmds;

import artGame.game.Player;
import artGame.main.Game;

/** TODO
 * 
 * @author Vicki
 *
 */
public final class Command implements CommandInter {
	public static final int bytes = BYTES_SHORT + 3*(BYTES_INT) + BYTES_CHAR + BYTES_LONG;
	public final int id;
	public final char action;
	public final long time;
	
	private static final String TAG = "MOVE";
	
	/** Creates a new MoveCommand. */
	public Command (char action, int id) {
		this.id = id;
		this.action = action;
		this.time = System.currentTimeMillis();
	}

	/** Creates a new MoveCommand at the specified time. */
	public Command (char action, int id, long time) {
		this.id = id;
		this.action = action;
		this.time = time;
	}
	
	/** Creates a new MovePacket from bytes */
	public Command(byte[] b) { // entity id action x y time
		//byte[] bytes = new byte[byteSize()];
		int i = 0;
		id = (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + b[i++];
        action = (char)((b[i++] << 8) + (b[i++]));
		time = (b[i++] << 64) + (b[i++] << 52) + (b[i++] << 48) + (b[i++] << 40)
				+ (b[i++] << 32) + (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + (b[i++]);
	}
	
	public int id() {
		return id;
	}
	
	public int key() {
		return action;
	}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o instanceof MoveCommand) {
			MoveCommand m = (MoveCommand)o;
			return m.id == id && m.action == action;
		}
		return false;
	}
	
	public String toString() {
		return tag() +" "+TAIL;
	}

	@Override
	public int byteSize() {
		return bytes + 1;
	}

	@Override
	public String tag() {
		return TAG;
	}

	@Override
	public byte[] bytes() { // entity id action x y time
		byte[] bytes = new byte[byteSize()];
		int i = 0;
		bytes[i++] = (byte)(id >>> 24); // int
        bytes[i++] = (byte)(id >>> 16);
        bytes[i++] = (byte)(id >>> 8);
        bytes[i++] = (byte)(id);
        bytes[i++] = (byte)(action >>> 8); // char
        bytes[i++] = (byte)(action);
		bytes[i++] = (byte)(time >>> 64); // long
		bytes[i++] = (byte)(time >>> 52);
		bytes[i++] = (byte)(time >>> 48);
		bytes[i++] = (byte)(time >>> 40);
        bytes[i++] = (byte)(time >>> 32);
		bytes[i++] = (byte)(time >>> 24);
        bytes[i++] = (byte)(time >>> 16);
        bytes[i++] = (byte)(time >>> 8);
        bytes[i++] = (byte)(time);
        
        return bytes;
	}

	/** Takes the action specified by this command on the Game object. */
	@Override
	public boolean execute(Game g) {
		Player p = g.getPlayer(id);
		g.doAction(p, action);
		return true;
	}
	
	@Override
	public long time() {
		return time;
	}

	@Override
	public char action() {
		// TODO Auto-generated method stub
		return 0;
	}
}
