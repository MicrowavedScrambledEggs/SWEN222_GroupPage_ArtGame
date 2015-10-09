package artGame.xml.load;

import artGame.game.ExitTile;
import artGame.game.Tile;

public class ExitTileBuilder extends TileBuilder {

	public ExitTileBuilder(int level, GameMaker gameMaker) {
		super(level, gameMaker);
	}

	@Override
	public void addToGame() {
		Tile tile = new ExitTile(isNorthWall(), isWestWall(), isSouthWall(), isEastWall());
		getGameMaker().addTile(getLevel(), getCoord(), tile);
		getGameMaker().addDoorMap(getLevel(), getCoord(), getDoorReference());
		getGameMaker().addArtMap(getLevel(), getCoord(), getArtReference());
	}

}
