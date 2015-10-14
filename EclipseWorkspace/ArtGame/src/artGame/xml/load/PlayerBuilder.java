package artGame.xml.load;

import artGame.game.Player;

/**
 * Build strategy for Player objects
 *
 * @author Badi James 300156502
 *
 */
public class PlayerBuilder extends CharacterBuilder {

	public PlayerBuilder(GameMaker gameMaker, int id) {
		super(gameMaker, id);
	}

	@Override
	/**
	 * Constructs Player using stored variables and adds player and inventory
	 * references to gameMaker
	 */
	public void addToGame() {
		Player toAdd = new Player(super.getDirection(), super.getiD());
		toAdd.setRow(super.getCoord().getRow());
		toAdd.setCol(super.getCoord().getCol());
		getGameMaker().addPlayer(toAdd);
		getGameMaker().addCharacterArtRefs(toAdd, getArtRefs());
		getGameMaker().addCharacterKeyRefs(toAdd, getKeyRefs());
	}

}
