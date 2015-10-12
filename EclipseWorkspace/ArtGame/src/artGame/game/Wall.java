package artGame.game;
/**
 * Represents a wall in the game
 * @author Kaishuo
 *
 */
public class Wall{
	private Art art;
	public final boolean hadArt; // Vicki: so new player clients can know which walls should be empty
	/**
	 * Creates a wall with a piece of art on it
	 */
	public Wall(Art art){
		this.art = art;
		art.addWall(this);
		if (art != null) { hadArt = true; }
		else { hadArt = false; }
	}
	
	/**
	 * Creates a blank wall with no art on it
	 */
	public Wall(){
		this.art = null;
		hadArt = false;
	}
	
	/**
	 * Gets the piece of art on the wall
	 */
	public Art getArt() {
		return art;
	}

	/**
	 * Sets a piece of art on this wall
	 */
	public void setArt(Art art) {
		this.art = art;
		if(art!=null) art.addWall(this);
	}
	
	/**
	 * Returns whether or not characters can walk through this wall
	 */
	public boolean passable() {
		return false; //no you cant walk through solid walls
	}
	

}
