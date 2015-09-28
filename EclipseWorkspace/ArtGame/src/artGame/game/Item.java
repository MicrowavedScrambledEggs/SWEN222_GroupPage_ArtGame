package artGame.game;

/**
 * A miscelaneous item in the game, also the base
 * class for art and keys
 * @author Kaishuo
 *
 */
public class Item {
	public final int ID; 
	private String description; //short description of item
								//perhaps use in mouse over
								//in inventory?
	public Item(int ID){
		this.ID = ID;
	}
	
	/**
	 * gets the item description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * sets the item description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
