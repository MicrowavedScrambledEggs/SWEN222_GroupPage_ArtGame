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
import artGame.game.Sculpture;
import artGame.game.Tile;
import artGame.game.Wall;
import artGame.main.Game;

import static artGame.game.Character.Direction.*;

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

	private static boolean out;
	
	private static Direction[] directionValues = Direction.values();
	
	
	
	/**
	 * Updates the GameData class with an updated set of data from the server
	 * @param data
	 */
	public static void updateGame(GamePacketData data) {
		GameData.data=data;
		if(data.out){
			out = true;
		}
		updateGameObjects();
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
		
		List<artGame.game.Character> chars = new ArrayList<>(); 

		if(data == null || data.players == null){
			return new artGame.game.Character[]{};
		}
		
		for(Player p : data.players){
			chars.add(p);
		}
		
		for(Guard g : data.guards){
			chars.add(g);
		}
		
		for(Sculpture s : getSculptures()){
			chars.add(s);
		}
		
		artGame.game.Character[] characters = new artGame.game.Character[chars.size()];
		
		for(int i = 0; i < chars.size(); i++){
			characters[i] = chars.get(i);
		}
		
		return characters;

	}
	
	/**
	 * Gets all current sculptures in game
	 * @return
	 */
	public static synchronized List<Sculpture> getSculptures(){
		
		List<Sculpture> sculps = new ArrayList<>();
		
		for(TileData t : data.occupied){
			if(t.itemId != -1){
				artGame.game.Character c = game.getFloor().getTile(t.row, t.col).getOccupant();
				if(c != null && c instanceof Sculpture){
					sculps.add((Sculpture)c);
				}
			}
		}
		
		return sculps;
	}
	
	/**
	 * Checks whether our client side game objects are out of date and alters them if they are.
	 */
	public static synchronized void updateGameObjects(){
				
		//Check artworks..	
		
		ArtItem[] art = getAllArt();
		
		for(ArtItem i : art){

			boolean contains = false;
			
			for(TileData t : data.occupied){
				if(t.itemDir == -1){
					continue;
				}
				Direction artDir = directionValues[t.itemDir];
		
				if(t.row == i.getRow() && t.col == i.getCol() && i.getDirection() == artDir){
					boolean isId = false;
					for(int artId : t.artIds){
						if(artId == i.ID){
							isId = true;
						}
					}
					if(isId){
						contains = true;
					}
				}
				
			}
			
			if(!contains){
				game.getFloor().getTile(i.getRow(), i.getCol()).getWall(i.getDirection()).setArt(null);
			}
		}
		
		for(int x = 0; x < game.getFloor().getHeight(); x++){
			for(int y = 0; y < game.getFloor().getWidth(); y++){
				Tile tile = game.getFloor().getTile(x, y);
				
				if(tile == null){
					continue;
				}					
				
				artGame.game.Character ch = tile.getOccupant();
				if(ch != null){
					
					if(ch instanceof Sculpture){
						Sculpture chS = (Sculpture)ch;
						if(chS.isTaken()){
							
							continue;
						}
					}
					
					boolean contains = false;
					for(TileData t : data.occupied){
						if(t.row == x && t.col == y){
							contains = true;
						}
					}
					if(!contains){
						artGame.game.Character occu = game.getFloor().getTile(x, y).getOccupant();
						if(occu instanceof Sculpture){
							((Sculpture)occu).setTaken(true);
						}
					}
				}
			}
		}
		
	}

	/**
	 * Gets information about all occupied tiles - Tiles that contain a Character or Art
	 * @return
	 */
	public static synchronized List<TileData> getOccupiedTiles(){
		if(data.occupied == null){
			return new ArrayList<TileData>();
		}
		return data.occupied;
	}

	/**
	 * Gets all the art from the starting game map.
	 * @return
	 */
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
							
							artItem.setWorldLocation(y, x);

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
	 * Gets all sculptures from the base game map.
	 * @return
	 */
	public static Sculpture[] getStartingSculptures(){
		
		List<Sculpture> sculps = new ArrayList<>();

		float width = game.getFloor().getWidth();
		float height = game.getFloor().getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				Tile t = game.getFloor().getTile(y, x);

				if (t == null) {
					continue;
				}

				if(t.getOccupant() != null){
					if(t.getOccupant() instanceof Sculpture){
						sculps.add((Sculpture)t.getOccupant());
					}
				}

			}
		}

		// Put it into an array..
		Sculpture[] array = new Sculpture[sculps.size()];
		for (int i = 0; i < sculps.size(); i++) {
			array[i] = sculps.get(i);
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
		List<TileData> tiles = new ArrayList<>();

		int pid = -1;

		String text = new String(packet, Charset.forName("UTF-8"));
		
		Scanner sc = new Scanner(text);

		try {

		while(sc.hasNext()){

			if(sc.hasNextInt()){
				int byteCount = sc.nextInt();
				
				if(Math.abs(packet.length-byteCount) > 10){
					sc.close();
					return null;
				}
			}
			
			if(sc.hasNext("<")){
				sc.next("<");
				pid = sc.nextInt();
			}
			
			if(sc.hasNext("out")){
				sc.next("out");
				data.out = true;
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

			//read sculptures.. format:
			/*for (Sculpture s : data.sculptures) {
				int id = s.getId();

				build.append(" guard: " + id + " , ");

				int row = s.getRow();
				int col = s.getCol();
				int dir = s.getDir().ordinal();

				build.append("" + Integer.toString(row) + " , "
						+ Integer.toString(col) + " , " + Integer.toString(dir) + " , ");

				Set<Item> inventory = s.getInventory();
				for (Item item : inventory) {
					int itemID = item.ID;
					build.append("" + itemID + " , ");
				}
				build.deleteCharAt(build.length() - 1);
				build.append(" ; ");
			}*/
			
			List<Sculpture> sculptures = new ArrayList<>();
			
			if(sc.hasNext("sculpture:")){

				List<Integer> items = new ArrayList<>();

				sc.next("sculpture:");
				int id = sc.nextInt();
				sc.next(",");
				String name = sc.next();
				sc.next(",");
				int value = sc.nextInt();
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

				Sculpture s = new Sculpture(Direction.values()[dir], id, value, name);
				s.setRow(row);
				s.setCol(col);
				for(int i : items){
					s.addItem(new Item(i));
				}

				sculptures.add(s);

			}

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
		data.occupied = tiles;
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
		build.append(" < ");
		
		build.append(" " + data.pid + " ");
		
		if(data.out){
			build.append("out ");
		}
		
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
		
		for (Sculpture s : data.sculptures) {
			int id = s.getId();

			build.append(" sculpture: " + id + " , ");

			int row = s.getRow();
			int col = s.getCol();
			int dir = s.getDir().ordinal();
			
			//value and name
			String name = s.getName();
			int value = s.getValue();
			
			build.append(name + " , " + value + " , ");

			build.append("" + Integer.toString(row) + " , "
					+ Integer.toString(col) + " , " + Integer.toString(dir) + " , ");

			Set<Item> inventory = s.getInventory();
			for (Item item : inventory) {
				int itemID = item.ID;
				build.append("" + itemID + " , ");
			}
			build.deleteCharAt(build.length() - 1);
			build.append(" ; ");
		}
		
		//append sculpture data

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

		byte[] text = build.toString().getBytes(Charset.forName("UTF-8"));
		build.insert(0, text.length);
		byte[] bytes = build.toString().getBytes(Charset.forName("UTF-8"));
		return bytes;
	}

	public static void addAll(List<Byte> list, byte[] bytes) {
		for (Byte b : bytes) {
			list.add(b);
		}
	}

	public static boolean isOut() {
		return out;
	}
}
