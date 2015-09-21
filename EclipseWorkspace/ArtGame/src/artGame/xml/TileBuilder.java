package artGame.xml;

import artGame.game.Coordinate;
import artGame.game.EmptyTile;
import artGame.game.Tile;

/**
 * Builds an EmptyTile object
 * @author Badi James
 *
 */
public class TileBuilder implements ObjectBuilder {

	protected boolean northWall = false;
	protected boolean westWall = false;
	protected boolean southWall = false;
	protected boolean eastWall = false;
	protected Coordinate coord;

	@Override
	/**
	 * Checks name matches wall, then adds wall value
	 */
	public void addFeild(String name, String value) {
		if(name.equals(XMLReader.WALL_ELEMENT)){
			setWall(value);
		}

	}

	/**
	 * Sets the boolean for wall in the direction given by parameter to true
	 * @param value String representation of border direction
	 */
	private void setWall(String value) {
		if(value.equals(XMLReader.NORTH_VALUE)){
			this.northWall = true;
		} else if(value.equals(XMLReader.WEST_VALUE)){
			this.westWall = true;
		} else if(value.equals(XMLReader.SOUTH_VALUE)){
			this.southWall = true;
		} else if(value.equals(XMLReader.EAST_VALUE)){
			this.eastWall = true;
		}

	}

	@Override
	/**
	 * Builds a tile object from booleans for walls
	 */
	public Tile buildObject() {
//		System.out.printf("Building an empty tile:\nPosition: %s\nNorth Wall: %b\nWest Wall %b"
//				+ "\nSouth Wall: %b\nEast Wall %b\n\n",
//				coord.toString(), northWall, westWall, southWall, eastWall);
		return new EmptyTile(northWall, westWall, southWall, eastWall);
	}

	/**
	 * Gets the coordinate that was given to this object builder from the xml Parser
	 * @return
	 */
	public Coordinate getCoordinate(){
		return this.coord;
	}

	@Override
	/**
	 * Checks name and object match coordinate type. Adds the coordinate feild
	 */
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		if(name.equals(XMLReader.POSITION_ELEMENT)){
			if(value instanceof Coordinate){
				this.coord = (Coordinate) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building tile: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(),
						Coordinate.class.getName()));
			}
		}

	}



}
