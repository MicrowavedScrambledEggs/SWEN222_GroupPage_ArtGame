package artGame.game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a collection of tiles into a single room conceptually
 *
 * @author Kaishuo Yang 300335418
 *
 */
public class Room {
	private Set<Tile> contents;

	public Room() {
		contents = new HashSet<Tile>();
	}

	/**
	 * Adds a whole bunch of tiles to this room
	 */
	public void addTiles(Collection<Tile> tiles) {
		for (Tile t : tiles) {
			contents.add(t);
			t.setRoom(this);
		}
	}

	/**
	 * adds all tiles from a given Floor object specified by a starting row,col
	 * and end row,col inclusive
	 */
	public void addTiles(Floor floor, int startRow, int startCol, int endRow,
			int endCol, int floorNumber) {
		for (int i = startRow; i <= endRow; i++) {
			for (int j = startCol; j <= endCol; j++) {
				if (floor.getTile(i, j, floorNumber) != null) {
					contents.add(floor.getTile(i, j, floorNumber));
					floor.getTile(i, j, floorNumber).setRoom(this);
				}
			}
		}
	}

	/**
	 * Returns the set of tiles this room contains
	 */
	public Set<Tile> getTiles() {
		return contents;
	}

	/**
	 * Adds a tile to this room
	 */
	public void addTile(Tile t) {
		contents.add(t);
		t.setRoom(this);
	}

	/**
	 * Hides this room by hiding all its child tiles
	 */
	public void hideRoom() {
		for (Tile t : contents) {
			t.setViewable(false);
		}
	}

	/**
	 * Displays this room by showing all its child tiles
	 */
	public void showRoom() {
		for (Tile t : contents) {
			t.setViewable(true);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contents == null) ? 0 : contents.hashCode());
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
		Room other = (Room) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		return true;
	}


}