package artGame.xml.load;

import artGame.game.Floor;
import artGame.game.Room;
import artGame.game.Tile;
import artGame.xml.XMLHandler;

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
	public void addTilesToRoom(Room room, Floor floor) {
		Tile toAdd = floor.getTile(row, col, floorLevel);
		room.addTile(toAdd);
	}

}
