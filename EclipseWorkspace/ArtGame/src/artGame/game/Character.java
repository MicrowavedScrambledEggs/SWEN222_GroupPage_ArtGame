package artGame.game;

import java.util.HashSet;
import java.util.Set;

/**
 * a character in the game represented by a id, position and face direction
 * @author Kaishuo
 *
 */
public class Character {
	public enum Direction{NORTH,SOUTH,EAST,WEST};

	int ID;
	int row;
	int col;
	protected Direction dir; //face direction
	protected Set<Item> inventory;
	
	
	public Character(Direction dir,int ID) {
		this.dir = dir;
		inventory = new HashSet<Item>();
		this.ID = ID;
	}
	
	
	/**
	 * adds the Item item to inventory
	 */
	public void addItem(Item item){
		inventory.add(item);
	}
	
	/**
	 * removes the Item item from inventory
	 */
	public void takeItem(Item item){
		inventory.remove(item);
	}


	/**
	 * returns the inventory
	 */
	public Set<Item> getInventory() {
		return inventory;
		
	}

	/**
	 * gets current row
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * sets current row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * gets current column
	 */
	public int getCol() {
		return col;
	}
	
	/**
	 * sets current column
	 */
	public void setCol(int col) {
		this.col = col;
	}

	/**
	 * gets current facing direction
	 */
	public Direction getDir() {
		return dir;
	}
	
	/**
	 * sets current facing direction
	 */
	public void setDir(Direction dir) {
		this.dir = dir;
	}
	

	/** Returns the unique ID of the character. */
	public int getId() {
		return ID;
	}
	
}
