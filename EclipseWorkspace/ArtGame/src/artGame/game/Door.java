package artGame.game;

/**
 * Represents a (possibly) locked door in the game world. Once a player unlocks
 * the door, it remains unlocked for the rest of the game
 *
 * @author Kaishuo
 *
 */
public class Door extends Wall {
	private boolean locked;
	private int keyID;

	public Door(boolean locked, int keyID) {
		this.locked = locked;
		this.keyID = keyID;
	}

	/**
	 * Attempts to unlock the door with the given keyID. Returns true and unlocks
	 * if it is the correct key otherwise returns false and door remains locked
	 */
	public boolean unlock(int key) {
		if (key == this.keyID) {
			this.locked = false;
			return true;
		} else
			return false;
	}

	/**
	 * Given Player P will attempt to unlock the door
	 */
	public void unlock(Player p) {
		for (Item i : p.getInventory()) {
			if (i instanceof Key) {
				if (((Key) i).ID == this.keyID) {
					this.locked = false;
				}
			}
		}
	}

	@Override
	public boolean passable() {
		return !locked; //can pass through if door unlocked
	}

	/**
	 * Returns a short description of the door(used in inspect)
	 */
	public String getDescription() {
		if (locked)
			return "Door number " + keyID + ". It is locked.";
		else
			return "A unlocked door";
	}

	/**
	 * @return the id of the key for this door
	 */
	public int getKeyID(){
		return this.keyID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + keyID;
		result = prime * result + (locked ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Door other = (Door) obj;
		if (keyID != other.keyID)
			return false;
		if (locked != other.locked)
			return false;
		return true;
	}


}
