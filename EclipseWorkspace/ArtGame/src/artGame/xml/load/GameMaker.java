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

	public void addTile(int level, Coordinate coord, Tile tile) {
		if(level +1 > floors.size() || floors.get(level) == null){
			floors.add(level, new FloorBuilder(this));
		}
		floors.get(level).addTile(coord, tile);
		if(coord.getCol() > maxCol) {maxCol = coord.getCol();}
		if(coord.getRow() > maxRow) {maxRow = coord.getRow();}
		if(tile instanceof ExitTile){
			exits.add((ExitTile) tile);
		}
	}

	public void addDoorMap(int level, Coordinate coord,
			HashMap<Direction, Integer> doorReference) {
		floors.get(level).addDoorMap(coord, doorReference);

	}

	public void addArtMap(int level, Coordinate coord,
			HashMap<Direction, Integer> artReference) {
		floors.get(level).addArtMap(coord, artReference);
	}

	public void addDoor(String[] doorInfo){
		int keyId = -1;
		boolean locked = Boolean.parseBoolean(doorInfo[2]);
		if(doorInfo.length > 3){
			keyId = Integer.parseInt(doorInfo[3]);
		}
		int doorId = Integer.parseInt(doorInfo[0]);
		doors.put(doorId, new Door(locked, keyId));
	}

	public void addLinkedTileReference(StairTile tile, int tileLevel, int linkedLevel,
			Coordinate stairCoord, Coordinate linkedCoord) {
		stairLinks.add(new LinkedTileReference(tile, linkedLevel, tileLevel, stairCoord, linkedCoord));

	}

	private class LinkedTileReference{
		private StairTile stairTile;
		private int linkedLevel;
		private int stairLevel;
		private Coordinate stairCoordinate;
		private Coordinate linkedCoordinate;

		public LinkedTileReference(StairTile stairTile, int linkedLevel,
				int stairLevel, Coordinate stairCoordinate,
				Coordinate linkedCoordinate) {
			super();
			this.stairTile = stairTile;
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

	public void addNPC(int level, Character nPC){
		if(nPCs.get(level) == null){
			nPCs.put(level, new ArrayList<Character>());
		}
		nPCs.get(level).add(nPC);
	}

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

	private void defineRooms(Floor floor) {
		for(RoomBuilder roomBuilder : roomDefiners ){
			roomBuilder.defineRoom(floor);
		}
	}

	private void linkStairs(Floor floor) {
		for(LinkedTileReference lt : this.stairLinks){
			Coordinate stairCoord = lt.stairCoordinate;
			Coordinate linkedCoord = lt.linkedCoordinate;
			StairTile linkedTile =
					(StairTile) tileArrays[lt.linkedLevel][linkedCoord.getRow()][linkedCoord.getCol()];
			floor.linkStairs(stairCoord.getRow(), stairCoord.getCol(), lt.stairLevel,
					linkedCoord.getRow(), linkedCoord.getCol(), lt.linkedLevel);
		}
	}

	private void buildTileArrays() {
		tileArrays = new Tile[floors.size()][maxRow+1][maxCol+1];
		for(int i = 0; i < floors.size(); i++){
			Tile[][] floor = floors.get(i).buildTileArray();
			for(int j = 0; j < floor.length; j++){
				for(int k = 0; k < floor[j].length; k++){
					tileArrays[i][j][k] = floor[j][k];
				}
			}
		}
	}

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

	private void addNPCsToFloor(Floor floor) {
		for(Integer i : nPCs.keySet()){
			for(Character nPC : nPCs.get(i)){
				floor.setCharacter(nPC, nPC.getRow(), nPC.getCol(), i);
			}
		}
	}

	public void addChestKeyRefs(Chest chest, HashSet<Integer> keyRefs) {
		addKeys(keyRefs);
		chestKeyRefs.put(chest, keyRefs);
	}

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
