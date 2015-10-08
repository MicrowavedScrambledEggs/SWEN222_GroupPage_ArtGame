package artGame.ui;

import artGame.game.Guard;
import artGame.game.Item;
import artGame.game.Key;
import artGame.game.Player;
import artGame.game.Sculpture;
import artGame.game.Tile;
import artGame.game.Wall;

/**
 * Used by the client to get information about the current gamestate over the network.
 * @author Tim
 *
 */
public class GameData {

	private static Item[] items = {new Key(0, 2), new Key(0, 2)};

	public static Player getCurrentPlayer(){

		return null;
	}

	public static Player[] getPlayers(){

		return null;
	}

	public static Guard[] getGuards(){

		return null;
	}

	public static Tile[] getTiles(){

		return null;
	}

	public static Item[] getCurrentItems(){

		return items;
	}

	public Wall[] getWallData(){

		return null;
	}

	public Sculpture[] getSculptures(){

		return null;
	}

}