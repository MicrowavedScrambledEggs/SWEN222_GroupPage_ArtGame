package artGame.ui.gamedata;

import java.util.ArrayList;
import java.util.List;

import artGame.game.Art;
import artGame.game.Character.Direction;
import artGame.game.Player;
import artGame.game.Tile;
import artGame.main.Game;

public class GameData {

	private static Game game;

	private static List<ArtItem> arts;
	private static List<artGame.game.Character> chars;

	public static void updateGame(Game game) {
		GameData.game = game;
	}

	public static void setGame(Game game) {
		GameData.game = game;
	}

	public Player getPlayer(){
		return game.getPlayer();
	}

	public static artGame.game.Character[] getCharacters() {

		chars = new ArrayList<>();

		float width = game.getFloor().getWidth();
		float height = game.getFloor().getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				Tile t = game.getFloor().getTile(y, x);

				if(t == null){
					continue;
				}
				if(t.getOccupant() != null){
					chars.add(t.getOccupant());
				}

			}
		}

		// Put it into an array..
		artGame.game.Character[] array = new artGame.game.Character[chars.size()];
		for (int i = 0; i < chars.size(); i++) {
			array[i] = chars.get(i);
		}

		return array;

	}

	/**
	 * Gets all the art in the current game world.
	 *
	 * @return
	 */
	public static ArtItem[] getAllArt() {

		arts = new ArrayList<>();

		float width = game.getFloor().getWidth();
		float height = game.getFloor().getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				Tile t = game.getFloor().getTile(y, x);

				if(t == null){
					continue;
				}

				for (Direction dir : Direction.values()) {
					if (t.getWall(dir) != null) {
						if (t.getWall(dir).getArt() != null) {

							Art a = t.getWall(dir).getArt();

							ArtItem artItem = new ArtItem(a.name, a.value,
									a.ID, t, t.getWall(dir), dir);

							arts.add(artItem);
						}
					}
				}

			}
		}

		// Put it into an array..
		ArtItem[] array = new ArtItem[arts.size()];
		for (int i = 0; i < arts.size(); i++) {
			array[i] = arts.get(i);
		}

		return array;

	}

}
