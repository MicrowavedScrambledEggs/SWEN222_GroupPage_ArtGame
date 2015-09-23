package artGame.xml;

import artGame.game.Character.Direction;
import artGame.game.Coordinate;
import artGame.game.Player;

public class PlayerBuilder implements ObjectBuilder {

	private Direction d;
	private Coordinate coord;
	private int iD;

	@Override
	public void addFeild(String name, String value) {
		//TODO:Inventory handling
		if(name.equals(XMLReader.DIRECTION_ELEMENT)){
			addDirection(value);
		} else if(name.equals(XMLReader.ID_ATTRIBUTE)){
			iD = Integer.parseInt(value);
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
				this.coord = (Coordinate) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building player: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(),
						Coordinate.class.getName()));
			}
		}

	}

	@Override
	/**
	 * Builds the player, then sets its position on the board from its coordinates
	 */
	public Player buildObject() {
		System.out.printf("Building a player:\nID: %d\nDirection: %s\nPosition: %s\n\n",
				iD, d.toString(), coord.toString());
		Player toReturn = new Player(d, iD);
		toReturn.setRow(coord.getX());
		toReturn.setCol(coord.getY());
		return toReturn;
	}

	/**
	 * @return Coordinate given to it from data from XML parser
	 */
	public Coordinate getCoordinate(){
		return this.coord;
	}
}
