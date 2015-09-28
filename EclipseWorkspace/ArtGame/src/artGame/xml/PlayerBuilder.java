package artGame.xml;

import artGame.game.Player;

public class PlayerBuilder extends CharacterBuilder {

	public PlayerBuilder(int id) {
		super(id);
	}

	@Override
	/**
	 * Builds the player, then sets its position on the board from its coordinates
	 */
	public Player buildObject() {
//		System.out.printf("Building a player:\nID: %d\nDirection: %s\nPosition: %s\n\n",
//				iD, d.toString(), coord.toString());
		Player toReturn = new Player(super.getDirection(), super.getID());
		toReturn.setRow(super.getCoord().getY());
		toReturn.setCol(super.getCoord().getX());
		return toReturn;
	}

}
