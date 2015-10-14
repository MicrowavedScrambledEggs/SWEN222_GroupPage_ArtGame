package artGame.control.cmds;

import artGame.game.Player;
import artGame.game.Tile;
import artGame.main.Game;

/** MoveCommands are shared between clients and servers to instruct
 * movable entities to change their positions.
 * 
 * The given x and y are the position of the character at the time
 * of the movement request, and the action designates the 
 * direction in which the player should move. 
 * 
 * @author Vicki
 */
public final class MoveCommand implements CommandInter {
	public static final short PLAYER = 0;
	public static final short GUARD = 1;
	public static enum Entity { PLAYER, GUARD };
	public static final int bytes = BYTES_SHORT + 3*(BYTES_INT) + BYTES_CHAR + BYTES_LONG;
	public static final char DO_NOTHING = '-';

	public final MoveCommand.Entity entity; // represented as a short
	public final int id;
	public final char action;
	public final int x;
	public final int y;
	public final long time;
	
	private static final String TAG = "MOVE";
	
	/** Creates a new MoveCommand. */
	public MoveCommand (MoveCommand.Entity entity, int id, char action, int x, int y) {
		this.entity = entity;
		this.id = id;
		this.action = action;
		this.x = x;
		this.y = y;
		this.time = System.currentTimeMillis();
	}

	/** Creates a new MoveCommand at the specified time. */
	public MoveCommand (MoveCommand.Entity entity, int id, char action, int x, int y, long time) {
		this.entity = entity;
		this.id = id;
		this.action = action;
		this.x = x;
		this.y = y;
		this.time = time;
	}
	
	/** Creates a new MovePacket from bytes */
	public MoveCommand(byte[] b) { // entity id action x y time
		int i = 0;
		short entity = (short)((b[i++] << 8) + b[i++]);
		if (entity == PLAYER) {
			this.entity = Entity.PLAYER;
		} else {
			this.entity = Entity.GUARD;
		}		
		id = (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + b[i++];
        action = (char)((b[i++] << 8) + (b[i++]));
		x = (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + (b[i++]);
		y = (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + (b[i++]);
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
			return m.id == id && m.action == action
					&& m.entity == entity
					&& m.x == x
					&& m.y == y;
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
		short entity = valueOf(this.entity);
		byte[] bytes = new byte[byteSize()];
		bytes[0] = (byte)(entity >>> 8); // short
		bytes[1] = (byte)(entity);
		bytes[2] = (byte)(id >>> 24); // int
        bytes[3] = (byte)(id >>> 16);
        bytes[4] = (byte)(id >>> 8);
        bytes[5] = (byte)(id);
        bytes[6] = (byte)(action >>> 8); // char
        bytes[7] = (byte)(action);
		bytes[8] = (byte)(x >>> 24); // int
        bytes[9] = (byte)(x >>> 16);
        bytes[10] = (byte)(x >>> 8);
        bytes[11] = (byte)(x);
		bytes[12] = (byte)(y >>> 24); // int
        bytes[13] = (byte)(y >>> 16);
        bytes[14] = (byte)(y >>> 8);
        bytes[15] = (byte)(y);
		bytes[16] = (byte)(time >>> 64); // long
		bytes[17] = (byte)(time >>> 52);
		bytes[18] = (byte)(time >>> 48);
		bytes[19] = (byte)(time >>> 40);
        bytes[20] = (byte)(time >>> 32);
		bytes[21] = (byte)(time >>> 24);
        bytes[22] = (byte)(time >>> 16);
        bytes[23] = (byte)(time >>> 8);
        bytes[24] = (byte)(time);
        
        return bytes;
	}
	
	/** Returns the value of an entity as an integer.  
	 * Returns -1 if the entity cannot be found. */ 
	public short valueOf(MoveCommand.Entity e) {
		for (int i = 0; i < Entity.values().length; i++) {
			if (Entity.values()[i] == e) {
				return (short)i;
			}
		}
		return -1;
	}

	/** Takes the action specified by this command on the Game object. */
	@Override
	public boolean execute(Game g) {
		Player p = g.getPlayer(id);
		Tile t = g.getFloor().getTile(x, y);
		if (g.getFloor().getTile(x, y).getOccupant() == null
				|| !g.getFloor().getTile(x, y).getOccupant().equals(p)) {
			System.err.println("This command moves Player "+id+" from ("+x+","+y+"), but they aren't there!");
		}
		g.doAction(p, action);
		return true;
	}
	
	@Override
	public long time() {
		return time;
	}

	@Override
	public char action() {
		return action;
	}
}
