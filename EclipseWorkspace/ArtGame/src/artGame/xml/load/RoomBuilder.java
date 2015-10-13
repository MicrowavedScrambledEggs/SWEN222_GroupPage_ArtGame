package artGame.xml.load;

import java.util.ArrayList;

import artGame.game.Floor;
import artGame.game.Room;
import artGame.xml.XMLHandler;

public class RoomBuilder implements BuildStrategy {

	private GameMaker gameMaker;
	private ArrayList<RoomSegment> roomSegments = new ArrayList<RoomSegment>();

	public RoomBuilder(GameMaker gameMaker){
		this.gameMaker = gameMaker;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.SEGMENT_ELEMENT) || name.equals(XMLHandler.SQUARE_ELEMENT)){
			roomSegments.add((RoomSegment) values[0]);
		}
	}

	@Override
	public void addToGame() {
		gameMaker.addRoomDefiner(this);
	}

	public void defineRoom(Floor floor) {
		Room room = new Room();
		for(RoomSegment segment : roomSegments){
			segment.addTilesToRoom(room, floor);
		}
	}

}
