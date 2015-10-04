package artGame.xml.load;

import java.util.HashMap;

import artGame.game.Coordinate;
import artGame.game.EmptyTile;
import artGame.game.Character.Direction;
import artGame.game.Tile;
import artGame.xml.XMLHandler;

public class TileBuilder implements BuildStrategy {
	
	private GameMaker gameMaker;
	private boolean northWall = false;
	private boolean westWall = false;
	private boolean southWall = false;
	private boolean eastWall = false;
	private Coordinate coord;
	private HashMap<Direction, Integer> doorReference = new HashMap<Direction, Integer>();
	private HashMap<Direction, Integer> artReference = new HashMap<Direction, Integer>();

	public TileBuilder(GameMaker gameMaker) {
		this.gameMaker = gameMaker;
	}
	
	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.WALL_ELEMENT)){
			setWall(values);
		}
		if(name.equals(XMLHandler.DOOR_ELEMENT)){
			setDoor(values);
		}
		if(name.equals(XMLHandler.POSITION_ELEMENT)){
			if(values[0] instanceof Coordinate){
				this.coord = (Coordinate) values[0];
			} else {
				throw new IllegalArgumentException(String.format("Error when building tile: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						Coordinate.class.getName()));
			}
		}
	}
	
	private void setDoor(Object[] values) {
		Direction dir = setWall((String) values[1]);
		doorReference.put(dir, Integer.parseInt((String) values[0]));
	}

	/**
	 * Sets the boolean for wall in the direction given by parameter to true
	 * @param value String representation of border direction
	 */
	private void setWall(Object... values) {
		Direction dir = setWall((String) values[0]);
		if(values.length > 1){
			artReference.put(dir, Integer.parseInt((String) values[1]));
		}
	}
	
	private Direction setWall(String direction) {
		if(direction.equals(XMLHandler.NORTH_VALUE)){
			this.northWall = true;
			return Direction.NORTH;
		} else if (direction.equals(XMLHandler.SOUTH_VALUE)){
			this.southWall = true;
			return Direction.SOUTH;
		} else if (direction.equals(XMLHandler.WEST_VALUE)){
			this.westWall = true;
			return Direction.WEST;
		} else if (direction.equals(XMLHandler.EAST_VALUE)){
			this.eastWall = true;
			return Direction.EAST;
		} else {
			throw new IllegalArgumentException(String.format("Need a valid direction to set door reference. \"%s\" "
					+ "is not a valid direction.\n", direction));
		}
	}
	
	@Override
	public void addToGame() {
		Tile tile = new EmptyTile(northWall, westWall, southWall, eastWall);
		gameMaker.addTile(coord, tile);
		gameMaker.addDoorMap(coord, doorReference);
		gameMaker.addArtMap(coord, artReference);
	}

	/**
	 * @return the gameMaker
	 */
	public GameMaker getGameMaker() {
		return gameMaker;
	}

	/**
	 * @return the northWall
	 */
	public boolean isNorthWall() {
		return northWall;
	}

	/**
	 * @return the westWall
	 */
	public boolean isWestWall() {
		return westWall;
	}

	/**
	 * @return the southWall
	 */
	public boolean isSouthWall() {
		return southWall;
	}

	/**
	 * @return the eastWall
	 */
	public boolean isEastWall() {
		return eastWall;
	}

	/**
	 * @return the coord
	 */
	public Coordinate getCoord() {
		return coord;
	}

	/**
	 * @return the doorReference
	 */
	public HashMap<Direction, Integer> getDoorReference() {
		return doorReference;
	}

	/**
	 * @return the artReference
	 */
	public HashMap<Direction, Integer> getArtReference() {
		return artReference;
	}
	
	public TileBuilder clone(){
		TileBuilder clone = new TileBuilder(gameMaker);
		clone.artReference = this.artReference;
		clone.coord = this.coord;
		clone.doorReference = this.doorReference;
		clone.eastWall = this.eastWall;
		clone.northWall = this.northWall;
		clone.southWall = this.southWall;
		clone.westWall = this.westWall;
		return clone;
	}
	
	public void setCoord(Coordinate coord){
		this.coord = coord;
	}

}
