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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
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
		Item other = (Item) obj;
		if (ID != other.ID)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

}
