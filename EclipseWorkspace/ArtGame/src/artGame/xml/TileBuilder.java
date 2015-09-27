package artGame.xml;

import java.util.HashMap;

import artGame.game.Character.Direction;
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
	private Coordinate coord;
	
	private HashMap<Direction, Integer> doorReference = new HashMap<Direction, Integer>();
	private HashMap<Direction, Integer> artReference = new HashMap<Direction, Integer>();
	
	public void addDoorReference(String direction, int id){
		Direction dir = stringToDirection(direction);
		doorReference.put(dir, id);
	}
	
	public void addArtReference(String direction, int id){
		Direction dir = stringToDirection(direction);
		artReference.put(dir, id);
	}
	
	public HashMap<Direction, Integer> getDoorReference() {
		return doorReference;
	}

	public HashMap<Direction, Integer> getArtReference() {
		return artReference;
	}

	public void setDoorReference(HashMap<Direction, Integer> doorReference) {
		this.doorReference = doorReference;
	}

	public void setArtReference(HashMap<Direction, Integer> artReference) {
		this.artReference = artReference;
	}

	private Direction stringToDirection(String direction) {
		if(direction.equals(XMLReader.NORTH_VALUE)){
			return Direction.NORTH;
		} else if (direction.equals(XMLReader.SOUTH_VALUE)){
			return Direction.SOUTH;
		} else if (direction.equals(XMLReader.WEST_VALUE)){
			return Direction.WEST;
		} else if (direction.equals(XMLReader.EAST_VALUE)){
			return Direction.EAST;
		} else {
			throw new IllegalArgumentException(String.format("Need a valid direction to set door reference. \"%s\" "
					+ "is not a valid direction.\n", direction));
		}
	}

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
	

	public void setCoordinate(Coordinate coord) {
		this.coord = coord;
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
