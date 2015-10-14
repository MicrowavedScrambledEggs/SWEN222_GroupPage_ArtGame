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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + value;
		result = prime * result + ((walls == null) ? 0 : walls.hashCode());
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
		Art other = (Art) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value != other.value)
			return false;
		if (walls == null) {
			if (other.walls != null)
				return false;
		} else if (!walls.equals(other.walls))
			return false;
		return true;
	}
}