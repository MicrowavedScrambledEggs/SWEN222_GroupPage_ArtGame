package artGame.xml.load;

import artGame.game.Character.Direction;
import artGame.game.Coordinate;
import artGame.game.ExitTile;
import artGame.game.StairTile;
import artGame.game.Tile;
import artGame.xml.XMLHandler;

public class StairTileBuilder extends TileBuilder {

	private int linkedLevel;
	private Coordinate linkedCoord;
	private boolean up;
	private Direction dir;

	public StairTileBuilder(int level, Direction dir, boolean up, GameMaker gameMaker) {
		super(level, gameMaker);
		this.dir = dir;
		this.up = up;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		super.addField(name, values);
		if(name.equals(XMLHandler.LEVEL_ATTRIBUTE)){
			this.linkedLevel = Integer.parseInt((String) values[0]);
		}
		if(name.equals(XMLHandler.LINKED_TILE_ELEMENT)){
			if(values[0] instanceof Coordinate){
				this.linkedCoord = (Coordinate) values[0];
			} else {
				throw new IllegalArgumentException(String.format("Error when building stair tile: "
						+ "Tried to add %s when %s was needed for linked tile reference",
						values[0].getClass().getName(), Coordinate.class.getName()));
			}
		}
	}

	@Override
	public void addToGame() {
		StairTile tile = new StairTile(isNorthWall(), isWestWall(), isSouthWall(), isEastWall(), dir, up);
		getGameMaker().addTile(getLevel(), getCoord(), tile);
		getGameMaker().addDoorMap(getLevel(), getCoord(), getDoorReference());
		getGameMaker().addArtMap(getLevel(), getCoord(), getArtReference());
		getGameMaker().addLinkedTileReference(tile, getLevel(), linkedLevel, getCoord(), linkedCoord);
	}

}
