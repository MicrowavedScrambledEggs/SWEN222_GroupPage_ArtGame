package artGame.game;

/**
 * Represents a item in the game
 * @author Kaishuo
 *
 */
public class Item {
	public final int ID; 
	protected String description; //optional
	public Item(int ID){
		this.ID = ID;
		this.description = "An item... what could it be?";
	}
	
	/**
	 * Gets the item description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the item description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
