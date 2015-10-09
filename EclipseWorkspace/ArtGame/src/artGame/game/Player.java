package artGame.game;

/**
 * Represents a player character in the game
 * @author Kaishuo
 *
 */
public class Player extends Character{
	private boolean caught;
	public Player(Direction dir, int ID) {
		super(dir,ID);
		caught = false;
	}

	@Override
	public String toString(){
		return "P";
	}

	/**
	 * Returns whether or not this player has been caught
	 */
	public boolean isCaught() {
		return caught;
	}

	/**
	 * The player has been caught
	 */
	public void gotCaught() {
		this.caught = true;
	}

}
