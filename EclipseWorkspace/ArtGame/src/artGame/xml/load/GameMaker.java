package artGame.xml.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import artGame.game.Character;
import artGame.game.Character.Direction;
import artGame.game.Art;
import artGame.game.Chest;
import artGame.game.Coordinate;
import artGame.game.Door;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.Key;
import artGame.game.Player;
import artGame.game.Room;
import artGame.game.StairTile;
import artGame.game.Tile;
import artGame.game.Wall;
import artGame.main.Game;

/**
 * Stores all the objects and data from the build strategies
 *
 * When makeGame() gets called, uses all these objects and data to construct
 * a game
 *
 * @author Badi James 300156502
 *
 */
public class GameMaker {

	private ArrayList<ExitTile> exits = new ArrayList<ExitTile>();
	private ArrayList<FloorBuilder> floors = new ArrayList<FloorBuilder>();
	private Tile[][][] tileArrays;
	private ArrayList<LinkedTileReference> stairLinks = new ArrayList<LinkedTileReference>();
	private HashMap<Integer, Door> doors = new HashMap<Integer, Door>();
	private HashMap<Integer, Art> paintings = new HashMap<Integer, Art>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private HashMap<Integer, ArrayList<Character>> nPCs = new HashMap<Integer, ArrayList<Character>>();
	private HashMap<Integer, Key> keys = new HashMap<Integer, Key>();
	private HashMap<Chest, HashSet<Integer>> chestKeyRefs
		= new HashMap<Chest, HashSet<Integer>>();
	private HashMap<Chest, HashSet<Integer>> chestArtRefs
		= new HashMap<Chest, HashSet<Integer>>();
	private HashMap<Character, HashSet<Integer>> characterKeyRefs
		= new HashMap<Character, HashSet<Integer>>();
	private HashMap<Character, HashSet<Integer>> characterArtRefs
		= new HashMap<Character, HashSet<Integer>>();
	private int maxCol = 0;
	private int maxRow = 0;
	private ArrayList<RoomBuilder> roomDefiners = new ArrayList<RoomBuilder>();

	/**
	 * Adds the given tile to the given level at the rows and
	 * coords in the given coordinate
	 *
	 * Updates the max row and col of the game if the given coordinate
	 * has a higher row and or col than any previous coordinate
	 *
	 * @param level Level number tile is on
	 * @param coord row and col of tile
	 * @param tile tile to be added
	 */
	public void addTile(int level, Coordinate coord, Tile tile) {
		//Creates a floor builder for the level if none exists
		if(level +1 > floors.size() || floors.get(level) == null){
			floors.add(level, new FloorBuilder(this));
		}
		//Adds the tile to the floor builder for that level
		floors.get(level).addTile(coord, tile);

		//Updates the max row and col of game
		if(coord.getCol() > maxCol) {maxCol = coord.getCol();}
		if(coord.getRow() > maxRow) {maxRow = coord.getRow();}

		if(tile instanceof ExitTile){//updates colection of exit tiles
			exits.add((ExitTile) tile);
		}
	}

	/**
	 * Adds a map matching up wall directions with door ids for a tile at the given
	 * coordinate on the given level. Ensures the right door gets put on the correct
	 * tile's correct wall
	 *
	 * @param level Level number tile is on
	 * @param coord Coordinate of tile the door references are for
	 * @param doorReference Directions of walls to door ids to put a door in that direction
	 */
	public void addDoorMap(int level, Coordinate coord,
			HashMap<Direction, Integer> doorReference) {
		floors.get(level).addDoorMap(coord, doorReference);
	}

	/**
	 * Adds a map matching up wall directions with art ids for a tile at the given
	 * coordinate on the given level. Ensures the right painting gets put on the correct
	 * tile's correct wall
	 *
	 * @param level Level number tile is on
	 * @param coord Coordinate of tile the door references are for
	 * @param doorReference Directions of walls to art ids to put a painting in that direction
	 */
	public void addArtMap(int level, Coordinate coord,
			HashMap<Direction, Integer> artReference) {
		floors.get(level).addArtMap(coord, artReference);
	}

	/**
	 * Constructs a door from the given array of door info and stores it in the doors collection
	 * for matching up to tile's walls when making game
	 * @param doorInfo Array of information about door
	 */
	public void addDoor(String[] doorInfo){
		int keyId = -1;//key id for doors that don't have locks
		boolean locked = Boolean.parseBoolean(doorInfo[2]);
		if(doorInfo.length > 3){
			//If door has a key associated with it, gets the given key id
			keyId = Integer.parseInt(doorInfo[3]);
		}
		int doorId = Integer.parseInt(doorInfo[0]);
		doors.put(doorId, new Door(locked, keyId));
	}


	/**
	 * Stores the given info, used to link two stair tiles up with each other
	 * @param tileLevel level of the first stair tile
	 * @param linkedLevel level of the second stair tile
	 * @param stairCoord row and col of the fist stair tile
	 * @param linkedCoord row and col of the second stair tile
	 */
	public void addLinkedTileReference(int tileLevel, int linkedLevel,
			Coordinate stairCoord, Coordinate linkedCoord) {
		stairLinks.add(new LinkedTileReference(linkedLevel, tileLevel, stairCoord, linkedCoord));

	}

	/**
	 * 'Tuple' for storing a pair of positions to be used
	 * to link two stair tiles together
	 *
	 * @author Badi James
	 *
	 */
	private class LinkedTileReference{
		private int linkedLevel;
		private int stairLevel;
		private Coordinate stairCoordinate;
		private Coordinate linkedCoordinate;

		public LinkedTileReference(int linkedLevel,
				int stairLevel, Coordinate stairCoordinate,
				Coordinate linkedCoordinate) {
			super();
			this.linkedLevel = linkedLevel;
			this.stairLevel = stairLevel;
			this.stairCoordinate = stairCoordinate;
			this.linkedCoordinate = linkedCoordinate;
		}
	}

	public void addPlayer(Player toAdd) {
		players.add(toAdd);
	}

	public void addPainting(Art art) {
		paintings.put(art.ID, art);
	}

	/**
	 * Adds a character (sculpture or a guard) to map of level number to NPC collection
	 * @param level level NPC is on
	 * @param nPC Character to add
	 */
	public void addNPC(int level, Character nPC){
		if(nPCs.get(level) == null){
			nPCs.put(level, new ArrayList<Character>());
		}
		nPCs.get(level).add(nPC);
	}

	/**
	 * Using all the collected objects and data, builds a floor object complete
	 * with NPCs, art, doors and linked stairs and defined rooms
	 *
	 * Builds a game with the created floor and adds players to it
	 * @return Game built from all the recieved objects and data
	 */
	public Game makeGame() {
		buildTileArrays();
		fillChests();
		fillInventories();
		Floor floor = new Floor(exits, tileArrays);
		defineRooms(floor);
		addNPCsToFloor(floor);
		linkStairs(floor);
		return new Game(floor, players);
	}

	/**
	 * Defines which tiles are in which rooms using the collection of roombuilders
	 * @param floor Floor tiles are in
	 */
	private void defineRooms(Floor floor) {
		for(RoomBuilder roomBuilder : roomDefiners ){
			roomBuilder.defineRoom(floor);
		}
	}

	/**
	 * Links up stair tiles using the collection of linkedTileReferences
	 * @param floor Floor stair tiles are in
	 */
	private void linkStairs(Floor floor) {
		for(LinkedTileReference lt : this.stairLinks){
			Coordinate stairCoord = lt.stairCoordinate;
			Coordinate linkedCoord = lt.linkedCoordinate;
			floor.linkStairs(stairCoord.getRow(), stairCoord.getCol(), lt.stairLevel,
					linkedCoord.getRow(), linkedCoord.getCol(), lt.linkedLevel);
		}
	}

	/**
	 * builds a 3d tile array from all the floor builders to pass to a Floor constructor
	 */
	private void buildTileArrays() {
		//Uses the dimensions of the game world to define array size
		tileArrays = new Tile[floors.size()][maxRow+1][maxCol+1];
		for(int i = 0; i < floors.size(); i++){
			//iterates through the floor builders for each level
			//gets their tile array and adds each tile to the 3d Floor constructor
			//tile array at their equivalent positions
			Tile[][] floor = floors.get(i).buildTileArray();
			for(int j = 0; j < floor.length; j++){
				for(int k = 0; k < floor[j].length; k++){
					tileArrays[i][j][k] = floor[j][k];
				}
			}
		}
	}

	/**
	 * Puts art and keys in character's inventories with matching art and key
	 * id references
	 */
	private void fillInventories() {
		for(Character ch : characterKeyRefs.keySet()){
			for(int keyId : characterKeyRefs.get(ch)){
				ch.addItem(keys.get(keyId));
			}
		}
		for(Character ch : characterArtRefs.keySet()){
			for(int artId : characterArtRefs.get(ch)){
				ch.addItem(paintings.get(artId));
			}
		}
	}

	/**
	 * Puts art and keys in chest's inventories with matching art and key
	 * id references
	 */
	private void fillChests() {
		for(Chest ch : chestKeyRefs.keySet()){
			for(int keyId : chestKeyRefs.get(ch)){
				ch.setContent(keys.get(keyId));
			}
		}
		for(Chest ch : chestArtRefs.keySet()){
			for(int artId : chestArtRefs.get(ch)){
				ch.setContent(paintings.get(artId));
			}
		}
	}

	/**
	 * Iterates through the NPC collection and adds them to the given floor
	 * @param floor Floor to add NPCs to
	 */
	private void addNPCsToFloor(Floor floor) {
		for(Integer i : nPCs.keySet()){
			for(Character nPC : nPCs.get(i)){
				floor.setCharacter(nPC, nPC.getRow(), nPC.getCol(), i);
			}
		}
	}

	/**
	 * Adds id reference of keys stored in chests. For use in matching keys
	 * to chest inventories
	 * @param chest Chest storing keys
	 * @param keyRefs Ids of keys being stored
	 */
	public void addChestKeyRefs(Chest chest, HashSet<Integer> keyRefs) {
		addKeys(keyRefs);
		chestKeyRefs.put(chest, keyRefs);
	}

	/**
	 * Builds keys from the given collection of integer key IDs and adds them
	 * to the key ids
	 * @param keyRefs
	 */
	private void addKeys(HashSet<Integer> keyRefs) {
		for(int keyID : keyRefs){
			keys.put(keyID, new Key(keyID, keyID));
		}
	}

	public void addChestArtRefs(Chest chest, HashSet<Integer> artRefs) {
		chestArtRefs.put(chest, artRefs);
	}

	public void addCharacterKeyRefs(Character ch, HashSet<Integer> keyRefs) {
		addKeys(keyRefs);
		characterKeyRefs.put(ch, keyRefs);
	}

	public void addCharacterArtRefs(Character ch, HashSet<Integer> artRefs) {
		characterArtRefs.put(ch, artRefs);
	}

	public Art getArt(Integer integer) {
		return paintings.get(integer);
	}

	public Wall getDoor(Integer integer) {
		return doors.get(integer);
	}

	public void addRoomDefiner(RoomBuilder roomBuilder) {
		roomDefiners.add(roomBuilder);

	}
}
