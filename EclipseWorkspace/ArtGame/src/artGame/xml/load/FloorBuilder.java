package artGame.xml.load;

import java.util.HashMap;

import artGame.game.Coordinate;
import artGame.game.Tile;
import artGame.game.Character.Direction;

/**
 * Class used by game maker to make tile arrays for each level of the game world
 * (level as in building level). Stores a coordinate-to-tile map and references
 * to art and door ids for each tile, so that when buildTileArray() is called,
 * can fully make an array of tiles with the correct doors and art on walls for
 * that level
 *
 * @author Badi James 300156502
 *
 */
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

	/**
	 * Adds the given tile under the given coordinate key to the coordinate-to-tile
	 * map. Updates the stored maxCol and maxRow if the coordinate has a higher row
	 * and or col value than any coordinate keys currently stored
	 * @param coord
	 * @param tile
	 */
	public void addTile(Coordinate coord, Tile tile) {
		tiles.put(coord, tile);
		if(coord.getCol() > maxCol) {maxCol = coord.getCol();}
		if(coord.getRow() > maxRow) {maxRow = coord.getRow();}
	}

	/**
	 * Adds a map matching up wall directions with door ids for a tile at the given
	 * coordinate. Ensures the right door gets put on the correct tile's correct wall
	 * @param coord Coordinate of tile the door references are for
	 * @param doorReference Directions of walls to door ids to put a door in that direction
	 */
	public void addDoorMap(Coordinate coord,
			HashMap<Direction, Integer> doorReference) {
		doorMap.put(coord, doorReference);

	}

	/**
	 * Adds a map matching up wall directions with art ids for a tile at the given
	 * coordinate. Ensures the right painting gets put on the correct tile's correct wall
	 * @param coord Coordinate of tile the art references are for
	 * @param doorReference Directions of walls to art ids to put a painting in that direction
	 */
	public void addArtMap(Coordinate coord,
			HashMap<Direction, Integer> artReference) {
		artMap.put(coord, artReference);
	}

	/**
	 * Matches doors to tiles' walls
	 */
	private void addDoorsToTiles() {
		for(Coordinate coord : doorMap.keySet()){
			HashMap<Direction, Integer> tileDoorMap = doorMap.get(coord);
			Tile tile = tiles.get(coord);
			for(Direction dir : tileDoorMap.keySet()){
				tile.setWall(dir, gameMaker.getDoor(tileDoorMap.get(dir)));
			}
		}
	}

	/**
	 * Matches art to tile's walls
	 */
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
	 * Matches up doors and artworks stored in the gameMaker to the tiles stored here
	 *
	 * Then creates a 2d array of tiles from the max row and max col of the stored tiles and
	 * populates it from the map of coordinates to tiles.
	 *
	 * @return the 2D array of tiles for this floor, complete with doors and art on walls
	 */
	public Tile[][] buildTileArray() {
		addDoorsToTiles();
		addArtToTiles();
		tileArray = new Tile[maxRow+1][maxCol+1];
		for(Coordinate coord : tiles.keySet()){
			Tile toAdd = tiles.get(coord);
			//gets array position from coordinate key
			tileArray[coord.getRow()][coord.getCol()] = tiles.get(coord);
		}
		return tileArray;
	}



}
