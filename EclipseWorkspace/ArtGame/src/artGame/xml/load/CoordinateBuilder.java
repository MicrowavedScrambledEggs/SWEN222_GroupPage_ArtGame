package artGame.xml.load;

import artGame.game.Coordinate;

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
	
	public Coordinate buildCoordinate(){
		return new Coordinate(x, y);
	}
	
}
