package artGame.xml.load;

import artGame.game.Coordinate;

/**
 * Builds a coordinate from data received in xml elements representing coordinates
 * ie: position (for tile and character positions), start and finish (for guard patrol paths),
 * and square (for room defining)
 *
 * @author Badi James 300156502
 *
 */
public class CoordinateBuilder {

	private int x;
	private int y;

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return coordinate built from the received x and y
	 */
	public Coordinate buildCoordinate(){
		return new Coordinate(x, y);
	}

}
