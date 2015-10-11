package artGame.xml.load;

import artGame.game.Player;

public class PlayerBuilder extends CharacterBuilder {

	public PlayerBuilder(GameMaker gameMaker, int id) {
		super(gameMaker, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addToGame() {
		Player toAdd = new Player(super.getDirection(), super.getiD());
		toAdd.setRow(super.getCoord().getRow());
		toAdd.setCol(super.getCoord().getCol());
		getGameMaker().addPlayer(toAdd);
		getGameMaker().addCharacterArtRefs(toAdd, getArtRefs());
		getGameMaker().addCharacterKeyRefs(toAdd, getKeyRefs());
	}

}
