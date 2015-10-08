package artGame.xml.load;

import java.util.HashSet;

import artGame.game.Chest;
import artGame.game.Coordinate;
import artGame.game.ExitTile;
import artGame.game.Item;
import artGame.game.Tile;
import artGame.xml.XMLHandler;

public class ChestBuilder extends TileBuilder {

	private int id;
	private HashSet<Integer> artRefs = new HashSet<Integer>();
	private HashSet<Integer> keyRefs = new HashSet<Integer>();

	public ChestBuilder(int level, GameMaker gameMaker, int id) {
		super(level, gameMaker);
		this.id = id;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		super.addField(name, values);
		if(name.equals(XMLHandler.ITEM_ELEMENT)){
			if(values[0].equals(XMLHandler.ART_VALUE)){
				artRefs.add(Integer.parseInt((String) values[1]));
			} else if(values[0].equals(XMLHandler.KEY_VALUE)){
				keyRefs.add(Integer.parseInt((String) values[1]));
			}
		}
	}

	@Override
	public void addToGame() {
		Chest chest = new Chest(id, isNorthWall(), isWestWall(), isSouthWall(), isEastWall());
		getGameMaker().addTile(getLevel(), getCoord(), chest);
		getGameMaker().addDoorMap(getLevel(), getCoord(), getDoorReference());
		getGameMaker().addArtMap(getLevel(), getCoord(), getArtReference());
		getGameMaker().addChestKeyRefs(chest, keyRefs);
		getGameMaker().addChestArtRefs(chest, artRefs);
	}

}
