package artGame.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a collection of tiles into a single room conceptually
 * @author Kaishuo
 *
 */
public class Room {
	private Set<Tile> contents;
	
	public Room(){
		contents = new HashSet<Tile>();
	}
	
	/**
	 * Hides this room by hiding all its child tiles
	 */
	public void hideRoom(){
		for(Tile t:contents){
			t.setViewable(false);
		}
	}
	
	/**
	 * Displays this room by showing all its child tiles
	 */
	public void showRoom(){
		for(Tile t:contents){
			t.setViewable(true);
		}
	}
}