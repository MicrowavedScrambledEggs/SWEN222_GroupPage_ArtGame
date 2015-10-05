package artGame.game;

import java.util.ArrayList;
import java.util.List; 

public class Guard extends Character {

	private List<Coordinate> path;

	// for static guard
	public Guard(Direction dir, int ID) {
		super(dir, ID);
	}

	// for pathing guard
	public Guard(Direction dir, int ID, List<Coordinate> path) {
		super(dir, ID);
		this.path = path;
	}

	public String toString() {
		return "G";
	}

	public Coordinate nextCoord() {
		if (path != null) {
			Coordinate current = new Coordinate(this.col, this.row);
			int step = path.indexOf(current);
			step = step+1;
			if(step == path.size()) step = 0;
			return path.get(step);
		}
		else return new Coordinate(this.col,this.row);
	}
	
	/**
	 * offsets a guards path by a given amount. must be used when adding guards
	 * to any floor except ground floor
	 */
	public void offsetPath(int offset){
		if(path==null) throw new GameError("attempting to offset a guard without path. wtf happened?");
		List<Coordinate> newPath = new ArrayList<Coordinate>();
		for(Coordinate c:path){
			newPath.add(new Coordinate(c.getX(),c.getY()+offset));
		}
		this.path = newPath;// //
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		Guard other = (Guard) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (ID!=other.ID)
			return false;
		return true;
	}
	
	
}
