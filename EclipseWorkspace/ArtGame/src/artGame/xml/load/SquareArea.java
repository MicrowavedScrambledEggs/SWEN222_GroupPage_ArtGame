package artGame.xml.load;

import artGame.game.Floor;
import artGame.game.Room;
import artGame.xml.XMLHandler;

/**
 * Room segment for defining rooms based on a square made with a top left
 * corner coordinate and a bottom left coordinate tile
 *
 * @author Badi James 300156502
 *
 */
public class SquareArea implements RoomSegment {

	private int fromRow;
	private int fromCol;
	private int toRow;
	private int toCol;
	private int floorLevel;

	public SquareArea(int floorLevel){
		this.floorLevel = floorLevel;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.FROM_COL_ELEMENT)){
			String value = (String) values[0];
			this.fromCol = Integer.parseInt(value);
		} else if(name.equals(XMLHandler.FROM_ROW_ELEMENT)){
			String value = (String) values[0];
			this.fromRow = Integer.parseInt(value);
		} else if(name.equals(XMLHandler.TO_COL_ELEMENT)){
			String value = (String) values[0];
			this.toCol = Integer.parseInt(value);
		} else if(name.equals(XMLHandler.TO_ROW_ELEMENT)){
			String value = (String) values[0];
			this.toRow = Integer.parseInt(value);
		}
	}

	@Override
	public void addToGame() {
		//Does NOTHING!!!
	}

	@Override
	/**
	 * Uses the room's addTile method that does the "all tiles in square"
	 * logic
	 * Thanks Kai for the handy method!
	 */
	public void addTilesToRoom(Room room, Floor floor) {
		room.addTiles(floor, fromRow, fromCol, toRow, toCol, floorLevel);
	}

}
