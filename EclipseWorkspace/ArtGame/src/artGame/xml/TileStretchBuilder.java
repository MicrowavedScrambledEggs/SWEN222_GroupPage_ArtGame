package artGame.xml;

import java.util.ArrayList;

import artGame.game.Coordinate;
import artGame.game.Tile;

public class TileStretchBuilder extends TileBuilder {
	
	private int noTiles;

	public TileStretchBuilder(int noTiles) {
		this.noTiles = noTiles;
	}
	
	@Override
	public Tile buildObject() {
		throw new UnsupportedOperationException("Can't use Tile stretch builder to build tiles");
	}
	
	public ArrayList<TileBuilder> getTileBuilders(){
		ArrayList<TileBuilder> tileBuilders = new ArrayList<TileBuilder>();
		Coordinate start = super.getCoordinate();
		for(int i = 0; i < noTiles; i++){
			TileBuilder toAdd = new TileBuilder();
			toAdd.setArtReference(super.getArtReference());
			toAdd.setDoorReference(super.getDoorReference());
			toAdd.eastWall = super.eastWall;
			toAdd.northWall = super.northWall;
			toAdd.southWall = super.southWall;
			toAdd.westWall = super.westWall;
			Coordinate newCoord = new Coordinate(start.getX() + i, start.getY());
			toAdd.setCoordinate(newCoord);
			tileBuilders.add(toAdd);
		}
		return tileBuilders;
	}
	
	
	
}
