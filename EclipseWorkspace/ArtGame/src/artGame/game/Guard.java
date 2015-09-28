package artGame.game;

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
			if(step == path.size()) step = 0;
			return path.get(step + 1);
		}
		else return new Coordinate(this.col,this.row);
	}
}
