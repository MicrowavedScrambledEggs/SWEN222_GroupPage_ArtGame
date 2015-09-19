package artGame.xml;

import artGame.game.ExitTile;
import artGame.game.Tile;

public class ExitTileBuilder extends TileBuilder {
	
	public ExitTileBuilder(){
		super();
	}
	
	@Override
	public Tile buildObject() {
		System.out.printf("Building an exit tile:\nPosition: %s\nNorth Wall: %b\nWest Wall %b"
				+ "\nSouth Wall: %b\nEast Wall %b\n\n",
				super.coord.toString(), northWall, westWall, southWall, eastWall);
		return new ExitTile(super.northWall, super.westWall, super.southWall, super.eastWall);
	}

}
