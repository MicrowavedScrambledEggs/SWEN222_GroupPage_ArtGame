package artGame.control.cmds;

import artGame.control.cmds.MoveCommand.Entity;
import artGame.game.Character.Direction;
import artGame.game.EmptyTile;
import artGame.game.Tile;
import artGame.game.Wall;

/** TODO
 * 
 * @author Vicki
 *
 */
public class TileStateCommand implements Command {
	public static final short PLAYER = 0;
	public static final short GUARD = 1;
	public static final short STATUE = 2;
	public static final short EMPTY = 3;
	
	public static enum Occupant { PLAYER, GUARD, STATUE, EMPTY };

	private final TileStateCommand.Occupant occupant; // represented as a short
	private final int id;
	private final short[] wallsArtId = { 0, 0, 0, 0 }; // North, south, east, west
	private final boolean[] wallsHadArt = { false, false, false, false };
	private final long time;
	
	private final int bytes = BYTES_SHORT + BYTES_INT + BYTES_LONG + BYTES_SHORT * 4 + BYTES_BOOLEAN * 4;
	
	private static final String TAG = "TILE";

	/** Creates a new TileState. */
	public TileStateCommand (Tile t) {
		if (t.getOccupant() instanceof artGame.game.Guard) {
			occupant = Occupant.GUARD;
		} else if (t.getOccupant() instanceof artGame.game.Player) {
			occupant = Occupant.PLAYER;
		} else if (t.getOccupant() != null) {
			occupant = Occupant.STATUE;
		} else {
			occupant = Occupant.EMPTY;
		}
		if (t.getOccupant() != null) {
			this.id = t.getOccupant().getId();
		} else {
			id = 0;
		}
		markWall(t,Direction.NORTH,0);
		markWall(t,Direction.EAST,1);
		markWall(t,Direction.SOUTH,2);
		markWall(t,Direction.WEST,3);
		this.time = System.currentTimeMillis();
	}
	
	private void markWall(Tile t, Direction d, int index) {
		if (t.getWall(d) != null) {
			Wall wall = t.getWall(d);
			if (wall.hadArt) {
				wallsHadArt[index] = true;
				if (wall.getArt() != null) {
					wallsArtId[index] = (short) wall.getArt().ID;
				}
			}
		}
	}
	
	/** Creates a new TileState. */
	public TileStateCommand (TileStateCommand.Occupant entity, int id, short[] wallArtIds, boolean[] wallHadArt) throws IllegalArgumentException {
		if (wallArtIds.length != 4 && wallArtIds.length != wallsHadArt.length) {
			throw new IllegalArgumentException();
		}
		this.occupant = entity;
		this.id = id;
		for (int i = 0; i < wallsArtId.length; i++) {
			this.wallsArtId[i] = wallArtIds[i];
		}
		for (int i = 0; i < wallsArtId.length; i++) {
			this.wallsHadArt[i] = wallHadArt[i];
		}
		this.time = System.currentTimeMillis();
	}

	/** Creates a new TileState at the specified time. */
	public TileStateCommand (TileStateCommand.Occupant entity, int id, short[] wallArtIds, boolean[] wallHadArt, long time) throws IllegalArgumentException {
		if (wallArtIds.length != 4 && wallArtIds.length != wallsHadArt.length) {
			throw new IllegalArgumentException();
		}
		this.occupant = entity;
		this.id = id;
		for (int i = 0; i < wallsArtId.length; i++) {
			this.wallsArtId[i] = wallArtIds[i];
		}
		for (int i = 0; i < wallsArtId.length; i++) {
			this.wallsHadArt[i] = wallHadArt[i];
		}
		this.time = time;
	}
	
	public int id() {
		return id;
	}
	
	public Occupant occupant() {
		return occupant;
	}
	
	public boolean equals(Object o) {
		if (o instanceof TileStateCommand) {
			TileStateCommand m = (TileStateCommand)o;
			if (m.id == id && m.occupant == occupant) {
				for (int i = 0; i < wallsArtId.length; i++) {
					if (m.wallsArtId[i] != wallsArtId[i]
							|| m.wallsHadArt[i] != wallsHadArt[i]) {
						return false;
					}
				}
				return true;
			}
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
	
	public short indexOf(TileStateCommand.Occupant o) {
		for (int i = 0; i < Occupant.values().length; i++) {
			if (Occupant.values()[i] == o) {
				return (short)i;
			}
		}
		return -1;
	}

	@Override
	public byte[] bytes() { // entity id action x y time
		short occupant = indexOf(this.occupant);
		byte[] bytes = {
			(byte)(occupant >>> 8), // short
			(byte)(occupant),
			(byte)(id >>> 24), // int
	        (byte)(id >>> 16),
	        (byte)(id >>> 8),
	        (byte)(id),
	        (byte)(wallsArtId[0] >>> 8), // short
			(byte)(wallsArtId[0]),
	        (byte)(wallsArtId[1] >>> 8), // short
			(byte)(wallsArtId[1]),
	        (byte)(wallsArtId[2] >>> 8), // short
			(byte)(wallsArtId[2]),
	        (byte)(wallsArtId[3] >>> 8), // short
			(byte)(wallsArtId[3]),
			(byte)(wallsHadArt[0] ? 1 : 0), // boolean
			(byte)(wallsHadArt[1] ? 1 : 0), // boolean
			(byte)(wallsHadArt[2] ? 1 : 0), // boolean
			(byte)(wallsHadArt[3] ? 1 : 0), // boolean
			(byte)(time >>> 64), // long
			(byte)(time >>> 52),
			(byte)(time >>> 48),
			(byte)(time >>> 40),
        	(byte)(time >>> 32),
			(byte)(time >>> 24),
        	(byte)(time >>> 16),
        	(byte)(time >>> 8),
        	(byte)(time)
		};
        
        return bytes;
	}
}
