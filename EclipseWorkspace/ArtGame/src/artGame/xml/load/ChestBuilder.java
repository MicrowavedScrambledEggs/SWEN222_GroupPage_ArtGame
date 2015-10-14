package artGame.xml.load;

import java.util.HashSet;

import artGame.game.Chest;
import artGame.game.Coordinate;
import artGame.game.ExitTile;
import artGame.game.Item;
import artGame.game.Tile;
import artGame.xml.XMLHandler;

/**
 * Build strategy for building "Chests" (pretty much furniture that can store items)
 *
 * Like super class tile builder, but also stores the chest's id and art and key
 * references for the chest's item
 *
 * @author Badi James 300156502
 *
 */
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
			//Adding inventory references
			//Fist value is type of item
			//second value is Id for the item
			if(values[0].equals(XMLHandler.ART_VALUE)){
				artRefs.add(Integer.parseInt((String) values[1]));
			} else if(values[0].equals(XMLHandler.KEY_VALUE)){
				keyRefs.add(Integer.parseInt((String) values[1]));
			}
		}
	}

	@Override
	/**
	 * Creates a new tile from the wall booleans. Adds the tile with it's position
	 * info to the game maker, and adds the art references and door references to
	 * game maker so that game maker can match the artwork and doors to the tile's
	 * walls when building a new game
	 *
	 * Also adds inventory references to game to allow game maker to put the right
	 * items in the right chests when building game
	 */
	public void addToGame() {
		Chest chest = new Chest(id, isNorthWall(), isWestWall(), isSouthWall(), isEastWall());
		getGameMaker().addTile(getLevel(), getCoord(), chest);
		getGameMaker().addDoorMap(getLevel(), getCoord(), getDoorReference());
		getGameMaker().addArtMap(getLevel(), getCoord(), getArtReference());
		getGameMaker().addChestKeyRefs(chest, keyRefs);
		getGameMaker().addChestArtRefs(chest, artRefs);
	}

}
