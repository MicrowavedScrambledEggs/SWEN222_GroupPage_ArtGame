package artGame.xml.load;

import artGame.game.Floor;
import artGame.game.Room;

public interface RoomSegment extends BuildStrategy {

	public void addTilesToRoom(Room room, Floor floor);

}
