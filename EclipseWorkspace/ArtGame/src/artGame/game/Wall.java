package artGame.game;
/**
 * Represents a wall in the game
 * @author Kaishuo
 *
 */
public class Wall{
	private Art art;
	/**
	 * Creates a wall with a piece of art on it
	 */
	public Wall(Art art){
		this.art = art;
		art.addWall(this);
	}
	
	/**
	 * Creates a blank wall with no art on it
	 */
	public Wall(){
		this.art = null;
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
