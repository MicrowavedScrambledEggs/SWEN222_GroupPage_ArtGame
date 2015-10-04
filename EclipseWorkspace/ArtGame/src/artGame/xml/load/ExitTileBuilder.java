package artGame.xml.load;

import artGame.game.ExitTile;
import artGame.game.Tile;

public class ExitTileBuilder extends TileBuilder {
	
	public ExitTileBuilder(GameMaker gameMaker) {
		super(gameMaker);
	}

	@Override
	public void addToGame() {
		Tile tile = new ExitTile(isNorthWall(), isWestWall(), isSouthWall(), isEastWall());
		getGameMaker().addTile(getCoord(), tile);
		getGameMaker().addDoorMap(getCoord(), getDoorReference());
		getGameMaker().addArtMap(getCoord(), getArtReference());
	}

}
