package artGame.game;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
	private Set<Art> stolenArt;
	private Set<Item> inventory;
	int ID;
	
	public Player(Direction dir, int ID) {
		super(dir);
		inventory = new HashSet<Item>();
		stolenArt = new HashSet<Art>();
		this.ID = ID;
	}

	
	public void addArt(Art art){
		stolenArt.add(art);
	}
	
	public void addItem(Item item){
		inventory.add(item);
	}
	public String toString(){
		return "P";
	}


	/**
	 * returns the inventory
	 */
	public Set<Item> getInventory() {
		return inventory;
		
	}
}
