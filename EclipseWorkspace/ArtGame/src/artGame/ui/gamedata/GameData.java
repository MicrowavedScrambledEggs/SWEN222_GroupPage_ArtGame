package artGame.ui.gamedata;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import artGame.control.IncompatiblePacketException;
import artGame.game.Art;
import artGame.game.Character.Direction;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Item;
import artGame.game.Player;
import artGame.game.Tile;
import artGame.game.Wall;
import artGame.main.Game;

/**
 * Holds the current Game information from the Server
 * @author Tim King 300282037
 *
 */
public class GameData {

	private static Game game;

	private static GamePacketData data;

	private static List<ArtItem> arts;
	private static List<artGame.game.Character> chars;

	/**
	 * Updates the GameData class with an updated set of data from the server
	 * @param data
	 */
	public static void updateGame(GamePacketData data) {
		GameData.data=data;
	}

	/**
	 * Updates the actual game contained by GameData =
	 * @param game
	 */
	public static void updateGame(Game game) {
		GameData.game = game;
	}

	@Deprecated
	public static void setGame(Game game) {
		GameData.game = game;
	}

	/**
	 * Gets the floor of the currently held Game in GameData
	 * @return
	 */
	public static Floor getFloor() {
		return game.getFloor();
	}

	/**
	 * Gets the current player information from server (buffered)
	 * @return
	 */
	public static synchronized Player getPlayer() {
		if(data == null || data.players == null){
			return null;
		}
		for(Player player : data.players){
			if(player.getId() == data.pid){
				return player;
			}
		}
		return null;
	}

	@Deprecated
	public static Tile getPlayerTile() {
		return game.getFloor().getTile(game.getPlayer().getRow(),
				game.getPlayer().getCol());
	}

	/**
	 * Gets all player information from server (buffered)
	 * @return
	 */
	public static synchronized List<Player> getPlayers(){
		return data.players;
	}

	/**
	 * Get's the latest Character information from the server (buffered)
	 * @return
	 */
	public static synchronized artGame.game.Character[] getCharacters() {
		artGame.game.Character[] characters = new artGame.game.Character[data.players.size()+data.guards.size()];
		int index = 0;
		for(int i = 0; i < data.players.size(); i++){
			characters[i] = data.players.get(i);
			index = i;
		}
		for(int i = 0; i < data.guards.size(); i++){
			characters[i+index+1] = data.guards.get(i);
		}
		return characters;

	}

	/**
	 * Gets information about all occupied tiles - Tiles that contain a Character or Art
	 * @return
	 */
	public static synchronized List<TileData> getOccupiedTiles(){
		return data.occupied;
	}

	@Deprecated
	public static ArtItem[] getAllArt() {


		arts = new ArrayList<>();

		float width = game.getFloor().getWidth();
		float height = game.getFloor().getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				Tile t = game.getFloor().getTile(y, x);

				if (t == null) {
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

	/**
	 * Reads a formatted byte array produced from {@link #toByteArray(GamePacketData)}
	 * @param packet
	 * @return
	 * @throws IncompatiblePacketException
	 */
	public synchronized static GamePacketData read(byte[] packet)
			throws IncompatiblePacketException {

		GamePacketData data = new GamePacketData();

		List<Player> players = new ArrayList<>();
		List<Guard> guards = new ArrayList<>();

		int pid = -1;

		String text = new String(packet, Charset.forName("UTF-8"));
		
		Scanner sc = new Scanner(text);

		try {

		while(sc.hasNext()){

			if(sc.hasNext("<")){
				sc.next("<");
				pid = sc.nextInt();
			}
			if(sc.hasNext(">")){
				sc.next(">");
				continue;
			}

			if(sc.hasNext("player:")){

				List<Integer> items = new ArrayList<>();

				sc.next("player:");
				int id = sc.nextInt();

				sc.next(",");
				int row = sc.nextInt();
				sc.next(",");
				int col = sc.nextInt();
				sc.next(",");
				int dir = sc.nextInt();

				while(!sc.hasNext(";")){

					if(sc.hasNext(",")){
						sc.next(",");
					}
					if(sc.hasNextInt()){
						items.add(sc.nextInt());
					}
				}

				sc.next(";");

				Player p = new Player(Direction.values()[dir], id);
				p.setRow(row);
				p.setCol(col);
				for(int i : items){
					p.addItem(new Item(i));
				}

				players.add(p);

			}


			if(sc.hasNext("guard:")){

				List<Integer> items = new ArrayList<>();

				sc.next("guard:");
				int id = sc.nextInt();
				sc.next(",");
				int row = sc.nextInt();
				sc.next(",");
				int col = sc.nextInt();
				sc.next(",");
				int dir = sc.nextInt();

				while(!sc.hasNext(";")){

					if(sc.hasNext(",")){
						sc.next(",");
					}
					if(sc.hasNextInt()){
						items.add(sc.nextInt());
					}

				}

				sc.next(";");

				Guard g = new Guard(Direction.values()[dir], id);
				g.setRow(row);
				g.setCol(col);
				for(int i : items){
					g.addItem(new Item(i));
				}

				guards.add(g);

			}

			//Now read any occupied tiles in..

			/* format
			for(TileData tileData : data.occupied){
				build.append(" tile: ");
				build.append(tileData.row + " ");
				build.append(tileData.col + " ");
				build.append(tileData.itemId + " ");
				build.append(tileData.itemDir + " ");

				for(int i : tileData.artIds){
					build.append(i + " ");
				}

				build.append(" ; ");
			}
			*/

			List<TileData> tiles = new ArrayList<>();

			if(sc.hasNext("tile:")){
				sc.next("tile:");
				int row = sc.nextInt();
				int col = sc.nextInt();
				int itemId = sc.nextInt();
				int itemDir = sc.nextInt();

				int[] artIds = new int[4];

				for(int i = 0; i < 4; i++){
					artIds[i] = sc.nextInt();
				}
				TileData tileData = new TileData(row, col, itemId, itemDir, artIds);
				tiles.add(tileData);

				if(sc.hasNext(";")){
					sc.next(";");
				}
			}

		}

		} catch (NoSuchElementException e){
			System.out.println("invalid game data packet");
			sc.close();
			return null;
		}

		sc.close();
		data.pid = pid;
		data.guards = guards;
		data.players = players;

		return data;
	}

	/**
	 * Writes a GamePacketData object to a byte array for server transmission
	 * @param data
	 * @return
	 * @throws IncompatiblePacketException
	 */
	public static synchronized byte[] toByteArray(GamePacketData data)
			throws IncompatiblePacketException {

		StringBuilder build = new StringBuilder();
		build.append("< ");
		build.append(" " + data.pid + " ");
		for (Player player : data.players) {
			int id = player.getId();

			build.append("player: " + id + " , ");

			int row = player.getRow();
			int col = player.getCol();
			int dir = player.getDir().ordinal();

			build.append("" + Integer.toString(row) + " , "
					+ Integer.toString(col) + " , " + Integer.toString(dir) + " , ");

			Set<Item> inventory = player.getInventory();
			for (Item item : inventory) {
				int itemID = item.ID;
				build.append("" + itemID + " , ");
			}
			build.deleteCharAt(build.length() - 1);
			build.append(" ; ");
		}

		for (Guard guard : data.guards) {
			int id = guard.getId();

			build.append(" guard: " + id + " , ");

			int row = guard.getRow();
			int col = guard.getCol();
			int dir = guard.getDir().ordinal();

			build.append("" + Integer.toString(row) + " , "
					+ Integer.toString(col) + " , " + Integer.toString(dir) + " , ");

			Set<Item> inventory = guard.getInventory();
			for (Item item : inventory) {
				int itemID = item.ID;
				build.append("" + itemID + " , ");
			}
			build.deleteCharAt(build.length() - 1);
			build.append(" ; ");
		}

		//Now append occupied tile data..
		for(TileData tileData : data.occupied){
			build.append(" tile: ");
			build.append(tileData.row + " ");
			build.append(tileData.col + " ");
			build.append(tileData.itemId + " ");
			build.append(tileData.itemDir + " ");

			for(int i : tileData.artIds){
				build.append(i + " ");
			}

			build.append(" ; ");
		}

		build.append(" > ");

		return build.toString().getBytes(Charset.forName("UTF-8"));
	}

	public static void addAll(List<Byte> list, byte[] bytes) {
		for (Byte b : bytes) {
			list.add(b);
		}
	}
}
