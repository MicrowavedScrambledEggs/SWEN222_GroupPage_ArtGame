package artGame.xml;

import artGame.game.ExitTile;
import artGame.game.Tile;

/**
 * Creates an ExitTile from data given from XML Parser. Same as TileBuilder except buildObject()
 * buids an ExitTile instead
 * @author jamesbadi
 *
 */
public class ExitTileBuilder extends TileBuilder {

	public ExitTileBuilder(){
		super();
	}

	@Override
	/**
	 * Builds an ExitTile from wall booleans given to it from XML Parser
	 */
	public Tile buildObject() {
		System.out.printf("Building an exit tile:\nPosition: %s\nNorth Wall: %b\nWest Wall %b"
				+ "\nSouth Wall: %b\nEast Wall %b\n\n",
				super.coord.toString(), northWall, westWall, southWall, eastWall);
		return new ExitTile(super.northWall, super.westWall, super.southWall, super.eastWall);
	}

}
