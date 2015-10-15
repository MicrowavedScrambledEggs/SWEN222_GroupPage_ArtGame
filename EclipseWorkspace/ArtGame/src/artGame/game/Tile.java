package artGame.game;

import java.util.Arrays;

import artGame.game.Character.Direction;
/**
 * The base class for all other tiles
 * @author Kaishuo
 *
 */
public abstract class Tile {
	protected Wall[] walls;
	protected Character occupant;
	private boolean viewable; // whether or not the tile is visible
	public abstract boolean walkable();
	private Room room;

	public Tile(boolean nwall, boolean wwall, boolean swall, boolean ewall) {
		walls = new Wall[4];
		if (nwall) {
			this.walls[0] = new Wall();
		}
		if (wwall) {
			this.walls[1] = new Wall();
		}
		if (swall) {
			this.walls[2] = new Wall();
		}
		if (ewall) {
			this.walls[3] = new Wall();
		}
	}

	/**
	 * Gets the room this tile belongs to
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * Sets the room this tile belongs to
	 */
	public void setRoom(Room room) {
		this.room = room;
	}


	/**
	 * Returns whether or not we can see this tile
	 */
	public boolean isViewable() {
		return viewable;
	}

	/**
	 * Sets this tile as visible(or not)
	 */
	public void setViewable(boolean viewable) {
		this.viewable = viewable;
	}

	/**
	 * Returns the character currently occupying this tile
	 */
	public Character getOccupant() {
		return occupant;
	}

	/**
	 * Sets a character to occupy this tile
	 * @param occupant
	 */
	public void setOccupant(Character occupant) {
		this.occupant = occupant;
	}

	/**
	 * Sets one of this tile's walls to be the specified wall
	 */
	public void setWall(Direction dir, Wall wall) {
		if (dir == Direction.NORTH) {
			walls[0] = wall;
		} else if (dir == Direction.WEST) {
			walls[1] = wall;
		} else if (dir == Direction.SOUTH) {
			walls[2] = wall;
		} else if (dir == Direction.EAST) {
			walls[3] = wall;
		}
	}

	/**
	 * Returns the wall in this tiles given direction
	 */
	public Wall getWall(Direction dir) {
		if (dir == Direction.NORTH) {
			return walls[0];
		} else if (dir == Direction.WEST) {
			return walls[1];
		} else if (dir == Direction.SOUTH) {
			return walls[2];
		} else if (dir == Direction.EAST) {
			return walls[3];
		} else
			return null;
	}

	public String toPrintString() {
		return "N: "+ (wallArt(0))
				+" W: "+ (wallArt(1))
				+" S: "+ (wallArt(2))
				+" E: "+ (wallArt(3));
	}

	private String wallArt(int i) {
		if (walls[i] != null) {
			if (walls[i].getArt() != null) {
				return "\'"+walls[i].getArt().name+"\'";
			}
			return "W";
		}
		return " ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (viewable ? 1231 : 1237);
		int[] wallPrimes = {211,223,227, 229};
		for(int i = 0; i < walls.length; i++){
			if(walls[i] != null){
				if(walls[i] instanceof Door){
					result = prime * result + ((Door) walls[i]).hashCode();
				} else {
					result = prime * result + wallPrimes[i];
				}
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (viewable != other.viewable)
			return false;
		for(int i = 0; i < walls.length; i++){
			if(walls[i] == null && other.walls[i] == null){
				continue;
			} else if(walls[i] != null && other.walls[i] != null){
				if(walls[i] instanceof Door){
					if(!walls[i].equals(other.walls[i])){
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
