package artGame.game;

import java.util.HashSet;
import java.util.Set;

public class Room {
	private Set<Tile> contents;
	
	public Room(){
		contents = new HashSet<Tile>();
	}
	
	public void hideRoom(){
		for(Tile t:contents){
			t.setViewable(false);
		}
	}
	
	public void showRoom(){
		for(Tile t:contents){
			t.setViewable(true);
		}
	}
}