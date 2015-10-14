package artGame.xml.load;

import artGame.game.Floor;
import artGame.game.Room;

/**
 * Interface for Room segments that help define which tiles are in which rooms
 *
 * @author Badi James 300156502
 *
 */
public interface RoomSegment extends BuildStrategy {

	/**
	 * Add tiles from the given floor to the given room. Tiles added depend on
	 * concrete implementation of sub class
	 * @param room Room to add tiles to
	 * @param floor Floor to get tiles from
	 */
	public void addTilesToRoom(Room room, Floor floor);

}
