package artGame.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a character in the game represented by a id, position and face
 * direction
 *
 * @author Kaishuo
 *
 */
public class Character {
	public enum Direction {
		NORTH, SOUTH, EAST, WEST
	};

	int ID;
	int row;
	int col;
	protected Direction dir; // face direction
	protected Set<Item> inventory;

	public Character(Direction dir, int ID) {
		this.dir = dir;
		inventory = new HashSet<Item>();
		this.ID = ID;
	}

	/**
	 * Adds the Item item to inventory
	 */
	public void addItem(Item item) {
		inventory.add(item);
	}

	/**
	 * Removes the Item item from inventory
	 */
	public void takeItem(Item item) {
		inventory.remove(item);
	}

	/**
	 * Returns the inventory
	 */
	public Set<Item> getInventory() {
		return inventory;

	}

	/**
	 * Removes all items from the inventory
	 */
	public void clearInventory() {
		inventory = new HashSet<Item>();

	}

	/**
	 * Gets current row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets current row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Gets current column
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Sets current column
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * Gets current facing direction
	 */
	public Direction getDir() {
		return dir;
	}

	/**
	 * Sets current facing direction
	 */
	public void setDir(Direction dir) {
		this.dir = dir;
	}

	/**
	 *  Returns the unique ID of the character.
	 */
	public int getId() {
		return ID;
	}

	public void setId(int pid){
		this.ID=pid;
	}

}
