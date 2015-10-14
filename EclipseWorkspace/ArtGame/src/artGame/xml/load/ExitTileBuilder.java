package artGame.xml.load;

import artGame.game.ExitTile;
import artGame.game.Tile;

/**
 * Build strategy for building exit tiles. Exactly like super class TileBuilder
 * except adds a ExitTile to game instead of an EmptyTile
 *
 * @author Badi James 300156502
 *
 */
public class ExitTileBuilder extends TileBuilder {

	public ExitTileBuilder(int level, GameMaker gameMaker) {
		super(level, gameMaker);
	}

	@Override
	/**
	 * Creates a new tile from the wall booleans. Adds the tile with it's position
	 * info to the game maker, and adds the art references and door references to
	 * game maker so that game maker can match the artwork and doors to the tile's
	 * walls when building a new game
	 */
	public void addToGame() {
		Tile tile = new ExitTile(isNorthWall(), isWestWall(), isSouthWall(), isEastWall());
		getGameMaker().addTile(getLevel(), getCoord(), tile);
		getGameMaker().addDoorMap(getLevel(), getCoord(), getDoorReference());
		getGameMaker().addArtMap(getLevel(), getCoord(), getArtReference());
	}

}
