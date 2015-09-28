package artGame.game;

import java.util.HashSet;
import java.util.Set;

public class Character {
	public enum Direction{NORTH,SOUTH,EAST,WEST};

	int ID;
	int row;
	int col;
	protected Direction dir;
	protected Set<Item> inventory;
	
	
	public Character(Direction dir,int ID) {
		this.dir = dir;
		inventory = new HashSet<Item>();
		this.ID = ID;
	}
	
	public void addItem(Item item){
		inventory.add(item);
	}
	


	/**
	 * returns the inventory
	 */
	public Set<Item> getInventory() {
		return inventory;
		
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}
	
	
	
}
