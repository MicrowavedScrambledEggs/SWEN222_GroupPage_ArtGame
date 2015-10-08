package artGame.xml.load;

import java.util.HashMap;

import artGame.game.Coordinate;
import artGame.game.Tile;
import artGame.game.Character.Direction;

public class FloorBuilder {

	private GameMaker gameMaker;
	private HashMap<Coordinate, Tile> tiles = new HashMap<Coordinate, Tile>();
	private Tile[][] tileArray;
	private int maxCol;
	private int maxRow;
	private HashMap<Coordinate, HashMap<Direction, Integer>> doorMap
		= new HashMap<Coordinate, HashMap<Direction, Integer>>();
	private HashMap<Coordinate, HashMap<Direction, Integer>> artMap
		= new HashMap<Coordinate, HashMap<Direction, Integer>>();

	public FloorBuilder(GameMaker gameMaker){
		this.gameMaker = gameMaker;
	}

	public void addTile(Coordinate coord, Tile tile) {
		tiles.put(coord, tile);
		if(coord.getX() > maxCol) {maxCol = coord.getX();}
		if(coord.getY() > maxRow) {maxRow = coord.getY();}
	}

	public void addDoorMap(Coordinate coord,
			HashMap<Direction, Integer> doorReference) {
		doorMap.put(coord, doorReference);

	}

	public void addArtMap(Coordinate coord,
			HashMap<Direction, Integer> artReference) {
		artMap.put(coord, artReference);
	}

	private void addDoorsToTiles() {
		for(Coordinate coord : doorMap.keySet()){
			HashMap<Direction, Integer> tileDoorMap = doorMap.get(coord);
			Tile tile = tiles.get(coord);
			for(Direction dir : tileDoorMap.keySet()){
				tile.setWall(dir, gameMaker.getDoor(tileDoorMap.get(dir)));
			}
		}
	}

	private void addArtToTiles() {
		for(Coordinate coord : artMap.keySet()){
			HashMap<Direction, Integer> tileArtMap = artMap.get(coord);
			Tile tile = tiles.get(coord);
			for(Direction dir : tileArtMap.keySet()){
				tile.getWall(dir).setArt(gameMaker.getArt(tileArtMap.get(dir)));
			}
		}
	}

	/**
	 * Creates a 2d array of tiles from the given height and width and populates it from
	 * the map of coordinates to tiles, using the coordinate keys to find the array positions
	 * for each of the tiles.
	 */
	public Tile[][] buildTileArray() {
		addDoorsToTiles();
		addArtToTiles();
		tileArray = new Tile[maxRow+1][maxCol+1];
		for(Coordinate coord : tiles.keySet()){
			Tile toAdd = tiles.get(coord);
			tileArray[coord.getY()][coord.getX()] = tiles.get(coord);
		}
		return tileArray;
	}



}
