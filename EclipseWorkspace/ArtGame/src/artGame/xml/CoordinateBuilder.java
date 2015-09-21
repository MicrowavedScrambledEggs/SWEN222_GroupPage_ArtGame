package artGame.xml;

import artGame.game.Coordinate;

/**
 * Builds a Coordinate object from data from XML parser
 *
 * @author Badi James
 *
 */
public class CoordinateBuilder implements ObjectBuilder {

	private int x;
	private int y;

	@Override
	/**
	 * Sets the x and y coordinates
	 */
	public void addFeild(String name, String value) {
		if(name.equals(XMLReader.X_COORD_ELEMENT)){
			this.x = Integer.parseInt(value);
		} else if(name.equals(XMLReader.Y_COORD_ELEMENT)){
			this.y = Integer.parseInt(value);
		}

	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		//not needed for this class

	}

	@Override
	public Coordinate buildObject() {
		return new Coordinate(x, y);
	}

}
