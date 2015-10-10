package artGame.game;

import artGame.game.Character.Direction;

/**
 * Represents a set of stairs connecting different floors in the game
 * 
 * @author Kaishuo
 *
 */
public class StairTile extends Tile {
	private int col;
	private int row;
	private StairTile linkedTile;
	private boolean goingUp;
	private Direction dir;

	public StairTile(boolean nwall, boolean wwall, boolean swall,
			boolean ewall, Direction dir, boolean goingUp) {
		super(nwall, wwall, swall, ewall);
		this.dir = dir;
		this.goingUp = goingUp;
	}

	/**
	 * Returns whether this stairs is going up(or going down if false)
	 */
	public boolean isGoingUp() {
		return goingUp;
	}

	/**
	 * Sets whether or not this stairs is going up(shouldn't be called under
	 * normal circumstances...)
	 */
	public void setGoingUp(boolean goingUp) {
		this.goingUp = goingUp;
	}

	/**
	 * Returns the direction this stairs is pointing(Direction of bottom stairs)
	 */
	public Direction getDir() {
		return dir;
	}

	/**
	 * Sets direction of these stairs is going up(shouldn't be called under
	 * normal circumstances...)
	 */
	public void setDir(Direction dir) {
		this.dir = dir;
	}

	/**
	 * Only walkable if both this and its linked stairs is empty
	 */
	@Override
	public boolean walkable() {
		return this.occupant == null && this.linkedTile.occupant == null;
	}

	/**
	 * Returns the tile this one is linked to
	 */
	public StairTile getLinkedTile() {
		return linkedTile;
	}

	/**
	 * Links this tile to another stairs
	 */
	public void setLinkedTile(StairTile linkedTile) {
		this.linkedTile = linkedTile;
	}

	/**
	 * When we try to normally move a character on to this tile, we should
	 * instead move it to the linked tile
	 */
	@Override
	public void setOccupant(Character occupant) {
		if (occupant != null)
			this.linkedTile.setOccupantHelper(occupant);
	}

	/**
	 * Helper method to actually set the occupant
	 */
	public void setOccupantHelper(Character occupant) {
		this.occupant = occupant;
		occupant.setCol(col);
		occupant.setRow(row);
	}

	@Override
	public String toString() {
		return "S";
	}

	public void setLoc(int row, int col) {
		this.row = row;
		this.col = col;
	}

}
