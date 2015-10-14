package artGame.xml.load;

import artGame.game.Floor;
import artGame.game.Room;
import artGame.game.Tile;
import artGame.xml.XMLHandler;

/**
 * Room segment for a single tile to add to a room
 *
 * @author Badi James 300156502
 *
 */
public class SingleTile implements RoomSegment {

	private int floorLevel;
	private int row;
	private int col;

	public SingleTile(int floorLevel) {
		super();
		this.floorLevel = floorLevel;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.COL_ELEMENT)){
			String value = (String) values[0];
			this.col = Integer.parseInt(value);
		} else if(name.equals(XMLHandler.ROW_ELEMENT)){
			String value = (String) values[0];
			this.row = Integer.parseInt(value);
		}
	}

	@Override
	public void addToGame() {
		//Does Nothing
	}

	@Override
	/**
	 * Gets the tile from the given floor using the stored row and col value
	 * and add it to the given room
	 */
	public void addTilesToRoom(Room room, Floor floor) {
		Tile toAdd = floor.getTile(row, col, floorLevel);
		room.addTile(toAdd);
	}

}
