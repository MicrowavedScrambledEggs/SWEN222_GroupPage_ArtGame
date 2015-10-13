package artGame.game;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a piece of art in the players inventory
 * @author Kaishuo
 */
public class Art extends Item{
	public final String name;
	public final int value;
	private Set<Wall> walls;
	public Art(String name,int value,int ID) {
		super(ID);
		this.name = name;
		this.value = value;
		this.description = "A work of art. It is " + name + " worth " + value;
		walls = new HashSet<Wall>();
	}
	
	/**
	 * adds a wall this art exists on
	 */
	public void addWall(Wall w){
		walls.add(w);
	}

	/**
	 * Gets all the walls this art exists on
	 */
	public Set<Wall> getWalls(){
		return walls;
	}
}