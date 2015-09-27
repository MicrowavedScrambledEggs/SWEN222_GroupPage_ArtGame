package artGame.xml;

import artGame.game.Character.Direction;
import artGame.game.Coordinate;
import artGame.game.Sculpture;

public class SculptureBuilder implements ObjectBuilder {
	
	private String artName;
	private Direction d;
	private Coordinate position;
	private int value;
	private int artID;
	
	public SculptureBuilder(int artID){
		this.artID = artID;
	}
	
	@Override
	public void addFeild(String name, String value) {
		if(name.equals(XMLReader.NAME_ELEMENT)){
			this.artName = value;
		} else if (name.equals(XMLReader.VALUE_ELEMENT)){
			this.value = Integer.parseInt(value);
		} else if(name.equals(XMLReader.DIRECTION_ELEMENT)){
			addDirection(value);
		} else if(name.equals(XMLReader.ART_ID_ATTRIBUTE)){
			artID = Integer.parseInt(value);
		}
	}
	
	/**
	 * Sets the direction field to the value represented by the argument string
	 * @param value String representation of direction value
	 */
	private void addDirection(String value) {
		if(value.equals(XMLReader.NORTH_VALUE)){
			d = Direction.NORTH;
		} else if(value.equals(XMLReader.WEST_VALUE)){
			d = Direction.WEST;
		} else if(value.equals(XMLReader.SOUTH_VALUE)){
			d = Direction.SOUTH;
		} else if(value.equals(XMLReader.EAST_VALUE)){
			d = Direction.EAST;
		}
	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		if(name.equals(XMLReader.POSITION_ELEMENT)){
			if(value instanceof Coordinate){
				this.position = (Coordinate) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building player: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(),
						Coordinate.class.getName()));
			}
		}
	}

	@Override
	public Sculpture buildObject() {
		Sculpture sculpture = new Sculpture(d, artID, value, artName);
		sculpture.setCol(position.getX());
		sculpture.setRow(position.getY());
		return sculpture;
	}

}
