package artGame.game;
/**
 * Represents a chest which can contain a single item
 * @author Kaishuo
 *
 */
public class Chest extends Tile {
	public final int id;
	private Item content;

	public Chest(int id,boolean nwall, boolean wwall, boolean swall, boolean ewall) {
		super(nwall, wwall, swall, ewall);
		this.id = id;
	}

	/**
	 * Makes the Player p to take the item from the 
	 * chest if it is not empty
	 */
	public void takeItem(Player p) {
		if (content != null) {
			p.addItem(content);
			this.content = null;
		}
	}

	/**
	 * Returns the item currently in the chest
	 */
	public Item getContent() {
		return content;
	}

	/**
	 * Sets the chest's item
	 * @param content
	 */
	public void setContent(Item content) {
		this.content = content;
	}

	@Override
	public boolean walkable() {
		return false;
	}

	public String toString() {
		return "C";
	}
	
	/**
	 * String returned when a player inspects the chest
	 */
	public String getDescription(){
		return "A chest. What could be inside?";
	}
}