package artGame.game;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
	private Set<Item> inventory;
	int ID;
	private boolean caught;
	public Player(Direction dir, int ID) {
		super(dir);
		inventory = new HashSet<Item>();
		this.ID = ID;
		caught = false;
	}

	/**
	 * really should merge this into additem... TODO
	 */
	public void addArt(Art art){
		inventory.add(art);
	}
	
	public void addItem(Item item){
		inventory.add(item);
	}
	
	public String toString(){
		return "P";
	}

	

	public boolean isCaught() {
		return caught;
	}


	public void gotCaught() {
		this.caught = true;
	}


	/**
	 * returns the inventory
	 */
	public Set<Item> getInventory() {
		return inventory;
		
	}
}
