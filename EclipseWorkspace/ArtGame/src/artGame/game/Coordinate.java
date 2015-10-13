package artGame.game;

/**
 * Basic co-ordinate class. Stores an x value and a y value
 * @author Badi James
 *
 */
public class Coordinate implements Comparable<Coordinate>{
	
	private int col;
	private int row;
	
	public Coordinate(int col, int row) {
		this.col = col;
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	@Override
	public String toString() {
		return "(" + col + ", " + row + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
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
		Coordinate other = (Coordinate) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	@Override
	public int compareTo(Coordinate o) {
		return (int) (Math.hypot(o.getCol(), o.getRow()) - Math.hypot(this.getCol(), this.getRow()) + 0.5);
	}
	
	
}