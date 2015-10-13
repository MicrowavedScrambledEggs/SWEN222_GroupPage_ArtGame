package artGame.game;
/**
 * Represents a wall in the game
 * @author Kaishuo
 *
 */
public class Wall{
	private Art art;
	private boolean hadArt; // Vicki: so new player clients can know which walls should be empty
							// must be true if the wall has ever had art on it.
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
		if (this.art != null) {
			this.art.getWalls().remove(this); // FIXME replace this with Art removeWall() method if we want one
		}
		this.art = art;
		if(art!=null) {
			art.addWall(this);
			hadArt = true;
		}
	}
	
	public boolean hadArt() {
		return hadArt;
	}
	
	/**
	 * Returns whether or not characters can walk through this wall
	 */
	public boolean passable() {
		return false; //no you cant walk through solid walls
	}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o instanceof Wall) {
			Wall w = (Wall)o;
			if (hadArt && getArt() != null) {
				return getArt().equals(w.getArt());
			} else {
				return hadArt == w.hadArt;
			}
		}
		return false;
	}
	

}
