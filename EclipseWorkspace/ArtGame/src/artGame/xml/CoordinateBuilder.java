package artGame.xml;

import artGame.game.Coordinate;

public class CoordinateBuilder implements ObjectBuilder {
	
	private int x;
	private int y;
	
	@Override
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
		// TODO Auto-generated method stub

	}

	@Override
	public Coordinate buildObject() {
		return new Coordinate(x, y);
	}

}
