package artGame.game;

/**
 * Represents a sculpture on the game floor(as opposed to one in a players
 * inventory)
 * 
 * @author Kaishuo
 *
 */
public class Sculpture extends Character {
	private int value;
	private String name;
	private String description;
	private boolean taken;

	public Sculpture(Direction dir, int ID, int value, String name) {
		super(dir, ID);
		this.value = value;
		this.name = name;
		this.description = "A sculpture. It is " + name +".";
		this.taken = false;
	}

	/**
	 * Converts this sculpture to a Art item
	 */
	public Art toItem(Floor f) {
		Art artItem = new Art(name, value, f.nextItemID());
		artItem.setDescription("A sculpture. It is " + name +" worth " + value);
		this.taken = true;
		return artItem;
	}

	/**
	 * Gets this sculptures description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets this sculptures description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isTaken(){
		return taken;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o instanceof Sculpture) {
			Sculpture s = (Sculpture)o;
			return value == s.value
					&& name.equals(s.name)
					&& description.equals(s.description)
					&& (taken == s.taken);
		} else if (o instanceof Art) {
			Art a = (Art)o;
			return name.equals(a.name)
				&& value == a.value
				&& description.equals(a.description);
		}
		return false;
	}
	
}
