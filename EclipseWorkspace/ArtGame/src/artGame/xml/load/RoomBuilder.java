package artGame.xml.load;

import java.util.ArrayList;

import artGame.game.Floor;
import artGame.game.Room;
import artGame.xml.XMLHandler;

/**
 * Defines which tiles of a floor are in a room. Stores collection of room segments
 * that define which specific areas of a floor have the tiles for the room
 *
 * @author Badi James 300156502
 *
 */
public class RoomBuilder implements BuildStrategy {

	private GameMaker gameMaker;
	private ArrayList<RoomSegment> roomSegments = new ArrayList<RoomSegment>();

	public RoomBuilder(GameMaker gameMaker){
		this.gameMaker = gameMaker;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		//Collected room segments for defining which tiles are to be in the room
		if(name.equals(XMLHandler.SEGMENT_ELEMENT) || name.equals(XMLHandler.SQUARE_ELEMENT)){
			roomSegments.add((RoomSegment) values[0]);
		}
	}

	@Override
	public void addToGame() {
		gameMaker.addRoomDefiner(this);
	}

	/**
	 * Creates a Room then,
	 * Iterates through the collection of room segments, getting the room segments to
	 * add tiles from the given Floor to the Room
	 * @param floor
	 */
	public void defineRoom(Floor floor) {
		Room room = new Room();
		for(RoomSegment segment : roomSegments){
			segment.addTilesToRoom(room, floor);
		}
	}

}
