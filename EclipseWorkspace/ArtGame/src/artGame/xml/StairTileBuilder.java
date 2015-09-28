package artGame.xml;

import artGame.game.Coordinate;
import artGame.game.StairTile;
import artGame.game.Tile;

public class StairTileBuilder extends TileBuilder {
	
	private int linkedLevel;
	private Coordinate linkedCoord;
	
	public int getLinkedLevel() {
		return linkedLevel;
	}
	public void setLinkedLevel(int linkedLevel) {
		this.linkedLevel = linkedLevel;
	}
	public Coordinate getLinkedCoord() {
		return linkedCoord;
	}
	public void setLinkedCoord(Coordinate linkedCoord) {
		this.linkedCoord = linkedCoord;
	}
	
	@Override
	/**
	 * Builds an StairTile from wall booleans given to it from XML Parser
	 */
	public Tile buildObject() {
		return new StairTile(super.northWall, super.westWall, super.southWall, super.eastWall);
	}
	
}
