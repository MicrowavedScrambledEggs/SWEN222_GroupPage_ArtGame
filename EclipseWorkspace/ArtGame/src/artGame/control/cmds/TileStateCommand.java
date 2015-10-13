package artGame.control.cmds;

import java.util.Arrays;

import artGame.control.cmds.MoveCommand.Entity;
import artGame.game.Character.Direction;
import artGame.game.EmptyTile;
import artGame.game.Tile;
import artGame.game.Wall;
import artGame.main.Game;

/** TODO
 * 
 * @author Vicki
 *
 */
public final class TileStateCommand implements Command {
	public static final short PLAYER = 0;
	public static final short GUARD = 1;
	public static final short STATUE = 2;
	public static final short EMPTY = 3;
	
	public static enum Occupant { PLAYER, GUARD, STATUE, EMPTY };

	public final TileStateCommand.Occupant occupant; // represented as a short
	public final int id;
	private final short[] wallsArtId = { 0, 0, 0, 0 }; // North, west, south, east
	private final boolean[] wallsHadArt = { false, false, false, false };
	public final int row;
	public final int col;
	public final int floor;
	public final long time;
	
	public static final int bytes = BYTES_SHORT + BYTES_INT + BYTES_LONG + BYTES_SHORT * 4 + BYTES_INT * 4;
	
	private static final String TAG = "TILE";

	/** Creates a new TileState from an array of bytes. */
	public TileStateCommand(byte[] b) { // entity id action x y time
		if (b.length != byteSize()) {
			throw new IllegalArgumentException();
		}
		int i = 0;
		short occ = (short)((b[i++] << 8) + (b[i++]));
		if (occ == PLAYER) {
			occupant = Occupant.PLAYER;
		} else if (occ == GUARD) {
			occupant = Occupant.GUARD;
		} else if (occ == EMPTY) {
			occupant = Occupant.EMPTY;
		} else {
			occupant = Occupant.STATUE;
		}
		id = (b[i++] >>> 24) + (b[i++] >>> 16) + (b[i++] >>> 8) + (b[i++]);
		wallsArtId[0] = (short)((b[i++] >>> 8) + b[i++]);
		wallsArtId[1] = (short)((b[i++] >>> 8) + b[i++]);
	    wallsArtId[2] = (short)((b[i++] >>> 8) + b[i++]);
	    wallsArtId[3] = (short)((b[i++] >>> 8) + b[i++]);
	    wallsHadArt[0] = (b[i++]==(byte)1 ? true : false);
	    wallsHadArt[1] = (b[i++]==(byte)1 ? true : false);
	    wallsHadArt[2] = (b[i++]==(byte)1 ? true : false);
	    wallsHadArt[3] = (b[i++]==(byte)1 ? true : false);
		row = (b[i++] >>> 24) + (b[i++] >>> 16) + (b[i++] >>> 8) + (b[i++]);
		col = (b[i++] >>> 24) + (b[i++] >>> 16) + (b[i++] >>> 8) + (b[i++]);
		floor = (b[i++] >>> 24) + (b[i++] >>> 16) + (b[i++] >>> 8) + (b[i++]);
	    time = (b[i++] << 64) + (b[i++] << 52) + (b[i++] << 48) + (b[i++] << 40)
	    		+ (b[i++] << 32) + (b[i++] << 24) + (b[i++] << 16) + (b[i++] << 8) + (b[i++]);
	}

	/** Creates a new TileState. */
	public TileStateCommand (Tile t, int row, int col, int floor) {
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
		markWall(t,Direction.WEST,1);
		markWall(t,Direction.SOUTH,2);
		markWall(t,Direction.EAST,3);
		this.row = row;
		this.col = col;
		this.floor = floor;
		this.time = System.currentTimeMillis();
	}
	
	private void markWall(Tile t, Direction d, int index) {
		if (t.getWall(d) != null) {
			Wall wall = t.getWall(d);
			if (wall.hadArt()) {
				wallsHadArt[index] = true;
				if (wall.getArt() != null) {
					wallsArtId[index] = (short) wall.getArt().ID;
				}
			}
		}
	}
	
	/** Creates a new TileState. */
	public TileStateCommand (TileStateCommand.Occupant entity, int id, short[] wallArtIds, boolean[] wallHadArt, int row, int col, int floor) throws IllegalArgumentException {
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
		this.row = row;
		this.col = col;
		this.floor = floor;
		this.time = System.currentTimeMillis();
	}

	/** Creates a new TileState at the specified time. */
	public TileStateCommand (TileStateCommand.Occupant entity, int id, short[] wallArtIds, boolean[] wallHadArt, int row, int col, int floor, long time) throws IllegalArgumentException {
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
		this.row = row;
		this.col = col;
		this.floor = floor;
		this.time = time;
	}
	
	public int id() {
		return id;
	}
	
	public Occupant occupant() {
		return occupant;
	}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
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
	
	public boolean[] wallsHadArt() {
		return Arrays.copyOf(wallsHadArt,wallsHadArt.length);
	}
	
	public short[] getWallsArtId() {
		return Arrays.copyOf(wallsArtId,wallsArtId.length);
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
			(byte)(row >>> 24), // int
	        (byte)(row >>> 16),
	        (byte)(row >>> 8),
	        (byte)(row),
			(byte)(col >>> 24), // int
	        (byte)(col >>> 16),
	        (byte)(col >>> 8),
	        (byte)(col),
			(byte)(floor >>> 24), // int
	        (byte)(floor >>> 16),
	        (byte)(floor >>> 8),
	        (byte)(floor),
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

	@Override
	public boolean execute(Game g) {
		Tile t = g.getFloor().getTile(row, col, floor);
		if (t instanceof EmptyTile) {
			EmptyTile e = (EmptyTile)t;
			if (wallsArtId[0] == 0 && e.getWall(Direction.NORTH) != null) {
				e.getWall(Direction.NORTH).setArt(null);
			}
			if (wallsArtId[1] == 0 && e.getWall(Direction.WEST) != null) {
				e.getWall(Direction.WEST).setArt(null);
			}
			if (wallsArtId[2] == 0 && e.getWall(Direction.SOUTH) != null) {
				e.getWall(Direction.SOUTH).setArt(null);
			}
			if (wallsArtId[3] == 0 && e.getWall(Direction.EAST) != null) {
				e.getWall(Direction.EAST).setArt(null);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public long time() {
		return time;
	}
}
