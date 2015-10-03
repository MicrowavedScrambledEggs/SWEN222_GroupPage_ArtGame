package artGame.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.game.Art;
import artGame.game.Character.Direction;
import artGame.game.Coordinate;
import artGame.game.Door;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Player;
import artGame.game.Sculpture;
import artGame.game.Tile;
import artGame.main.Game;

/**
 * SAX handler extended to specifically handle artGame xml files
 *
 * Creates artGame.game objects while parsing xml file. Uses those objects to
 * create a artGame.main.Game when buildGame() called
 *
 * @author Badi James
 *
 */
public class ArtGameLoadHandler extends DefaultHandler {

	private HashMap<Integer, Door> doors = new HashMap<Integer, Door>();
	private HashMap<Integer, Art> paintings = new HashMap<Integer, Art>();
	private HashMap<Coordinate, Tile> floorTiles = new HashMap<Coordinate, Tile>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Guard> guards = new ArrayList<Guard>();
	private ArrayList<Sculpture> sculptures = new ArrayList<Sculpture>();
	private ArrayList<ExitTile> exits = new ArrayList<ExitTile>();
	private ArrayList<PlayerBuilder> playerBuilders = new ArrayList<PlayerBuilder>();
	private ArrayList<TileBuilder> tileBuilders = new ArrayList<TileBuilder>();
	private ArrayList<ArtBuilder> artBuilders = new ArrayList<ArtBuilder>();
	private ArrayList<SculptureBuilder> sculptureBuilders = new ArrayList<SculptureBuilder>();
	private ArrayList<GuardBuilder> guardBuilders = new ArrayList<GuardBuilder>();
	//Keeps track of which object it is currently building
	private Stack<ObjectBuilder> buildStack = new Stack<ObjectBuilder>();
	//For elements with string data in between tags. So that characters() knows what element
	//the data is for
	private String currentElement;

	public ArtGameLoadHandler(){
		super();
	}

	/**
	 * Called when parser comes across a start tag for an element
	 *
	 * If the element is an element relating to an artGame.game object, creates an object builder
	 * related to that object and pushes it to the stack.
	 * If the element is an element relating to a primitive variable for an artGame.game object,
	 * add that variable to the object builder at the top of the stack
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		//TODO: Add a lot more cases once artGame.game is more complete
		//TODO: Add handling for Guards
		//TODO: Add handling for Art
		//TODO: Add handling for inventory
		if(qName.equals(XMLReader.EMPTY_TILE_ELEMENT)){
			//decides the type of tile builder based on if it's for an exit tile or a regular empty tile
			if(attributes.getLength() != 0 &&
					attributes.getValue(XMLReader.EXIT_ATTRIBUTE).equals(XMLReader.TRUE_VALUE)){
				buildStack.push(new ExitTileBuilder());
			} else {
				buildStack.push(new TileBuilder());
			}
		} else if(qName.equals(XMLReader.TILE_STRETCH_ELEMENT)){
			buildStack.push(new TileStretchBuilder(Integer.parseInt(attributes.getValue(0))));
		} else if(qName.equals(XMLReader.STAIR_TILE_ELEMENT)){
			buildStack.push(new StairTileBuilder());
		} else if(qName.equals(XMLReader.POSITION_ELEMENT) || qName.equals(XMLReader.START_ELEMENT)
				|| qName.equals(XMLReader.FINISH_ELEMENT)){
			buildStack.push(new CoordinateBuilder());
		} else if(qName.equals(XMLReader.LINKED_TILE_ELEMENT)){
			StairTileBuilder stairBuilder = (StairTileBuilder) buildStack.peek();
			stairBuilder.setLinkedLevel(Integer.parseInt(attributes.getValue(0)));
			buildStack.push(new CoordinateBuilder());
		} else if(qName.equals(XMLReader.X_COORD_ELEMENT) || qName.equals(XMLReader.Y_COORD_ELEMENT)){
			//Variables for coordinate
			//if xml file is correctly written, object builder on top of stack should be a coordinate builder
			addFieldToCurrentBuilder(qName, attributes.getValue(XMLReader.VALUE_ATTRIBUTE));
		} else if(qName.equals(XMLReader.WALL_ELEMENT)){
			//Wall variables for tiles
			//if xml file is correctly written, object builder on top of stack should be a tile builder
			addFieldToCurrentBuilder(qName, attributes.getValue(XMLReader.DIRECTION_ATTRIBUTE));
			if(attributes.getLength() > 1){
				setWallArtReference(attributes);
			}
		} else if(qName.equals(XMLReader.DOOR_ELEMENT)){
			buildDoor(attributes);
		} else if(qName.equals(XMLReader.PLAYER_ELEMENT)){
			buildStack.push(new PlayerBuilder(Integer.parseInt(attributes.getValue(XMLReader.ID_ATTRIBUTE))));
		} else if(qName.equals(XMLReader.DIRECTION_ELEMENT) || qName.equals(XMLReader.NAME_ELEMENT)
				|| qName.equals(XMLReader.VALUE_ELEMENT)){
			currentElement = qName;
		} else if(qName.equals(XMLReader.PAINTING_ELEMENT)){
			buildStack.push(new ArtBuilder(Integer.parseInt(attributes.getValue(0))));
		} else if(qName.equals(XMLReader.SCULPTURE_ELEMENT)){
			buildStack.push(new SculptureBuilder(Integer.parseInt(attributes.getValue(0))));
		} else if(qName.equals(XMLReader.GUARD_ELEMENT)){
			buildStack.push(new GuardBuilder(Integer.parseInt(attributes.getValue(0))));
		} else if(qName.equals(XMLReader.PATROL_ELEMENT)){
			buildStack.push(new Patrol());
		} else if(qName.equals(XMLReader.X_PATH_ELEMENT)){
			buildStack.push(new WestEastStretch());
		} else if(qName.equals(XMLReader.Y_PATH_ELEMENT)){
			buildStack.push(new NorthSouthStretch());
		}
	}

	private void setWallArtReference(Attributes attributes) {
		TileBuilder currentTile = (TileBuilder) buildStack.peek();
		currentTile.addArtReference(attributes.getValue(XMLReader.DIRECTION_ATTRIBUTE),
				Integer.parseInt(attributes.getValue(XMLReader.ART_ID_ATTRIBUTE)));
	}

	private void buildDoor(Attributes attributes) {
		int doorID = Integer.parseInt(attributes.getValue(0));
		TileBuilder currentTile = (TileBuilder) buildStack.peek();
		currentTile.addDoorReference(attributes.getValue(1), doorID);
		boolean locked = attributes.getValue(2).equals(XMLReader.TRUE_VALUE);
		int keyID = 0;
		if(attributes.getLength() == 4){
			keyID = Integer.parseInt(attributes.getValue(3));
		}
		doors.put(doorID, new Door(locked, keyID));
	}

	/**
	 * Adds the given field, defined by localName, with the value defined by 'value' to the objectBuilder
	 * at the top of the stack
	 * @param localName name of Variable
	 * @param value value of Variable
	 */
	private void addFieldToCurrentBuilder(String localName, String value) {
		ObjectBuilder current = buildStack.peek();
		current.addFeild(localName, value);
	}

	/**
	 * Called when parser comes across an end tag for an element
	 *
	 * Completes the object builder relating to the element, as in pops the object builder from
	 * the stack, uses the object builder to create its object, then adds it to the relevant
	 * collection
	 */
	@Override
	public void endElement(String uri, String localName, String qName){
		//TODO: Add a lot more cases once artGame.game is more complete
		//TODO: Add handling for Guards
		//TODO: Add handling for Art
		//TODO: Add handling for inventory
		if(qName.equals(XMLReader.EMPTY_TILE_ELEMENT) || qName.equals(XMLReader.STAIR_TILE_ELEMENT)){
			completeTile();
		} else if(qName.equals(XMLReader.TILE_STRETCH_ELEMENT)){
			completeTileStretch();
		} else if(qName.equals(XMLReader.POSITION_ELEMENT)){
			completePosition(qName);
		} else if(qName.equals(XMLReader.LINKED_TILE_ELEMENT)){
			completeLinkedTile();
		} else if(qName.equals(XMLReader.PLAYER_ELEMENT)){
			completePlayer();
		} else if(qName.equals(XMLReader.PAINTING_ELEMENT)){
			completePainting();
		} else if(qName.equals(XMLReader.SCULPTURE_ELEMENT)){
			completeSculpture();
		}
	}

	private void completeSculpture() {
		SculptureBuilder sculptureBuilder = (SculptureBuilder) buildStack.pop();
		sculptureBuilders.add(sculptureBuilder);
	}

	private void completePainting() {
		ArtBuilder artBuilder = (ArtBuilder) buildStack.pop();
		artBuilders.add(artBuilder);
	}

	private void completeLinkedTile() {
		CoordinateBuilder coordBuilder = (CoordinateBuilder) buildStack.pop();
		StairTileBuilder stairBuilder = (StairTileBuilder) buildStack.peek();
		stairBuilder.setLinkedCoord(coordBuilder.buildObject());
	}

	private void completeTileStretch() {
		TileStretchBuilder tileStretch = (TileStretchBuilder) buildStack.pop();
		tileBuilders.addAll(tileStretch.getTileBuilders());
	}

	/**
	 * Pops a playerBuilder from the top of the stack, builds the player object then adds it to
	 * the players collection
	 */
	private void completePlayer() {
		PlayerBuilder playerBuilder = (PlayerBuilder) buildStack.pop();
		playerBuilders.add(playerBuilder);
	}

	/**
	 * Pops a coordinate builder from the top of the stack. Builds the coordinate object. Adds the
	 * coordinate to the object builder for the object that the coordinate was a variable value for
	 * @param qName Name of variable coordinate to be completed is a value for
	 */
	private void completePosition(String qName) {
		CoordinateBuilder coordBuilder = (CoordinateBuilder) buildStack.pop();
		ObjectBuilder possitionableObjectBuilder = buildStack.peek();
		possitionableObjectBuilder.addFeild(qName, coordBuilder.buildObject());
	}

	/**
	 * Pops a tile builder from the top of the stack. Builds the tile and adds it to the tile map
	 * with its coordinate as the key. If the tile is an exit tile, adds it to the exit tile collection
	 * as well.
	 */
	private void completeTile() {
		TileBuilder tileBuilder = (TileBuilder) buildStack.pop();
		tileBuilders.add(tileBuilder);
	}

	@Override
	/**
	 * Deals with characters in between tags for an element. Usually a value for a field the element
	 * represents.
	 */
	public void characters(char[] ch, int start, int length){
		//TODO: Add a lot more cases once artGame.game is more complete
		//extracts a string from the section of the character array to parse
		String elementDat = new String(Arrays.copyOfRange(ch, start, start + length));
		elementDat = elementDat.trim();//removes whitespace
		if(elementDat.length() > 0){//if it wasn't just whitespace
			//Uses the string as the data for the current element and adds the field to the current builder
			addFieldToCurrentBuilder(currentElement, elementDat);
		}
	}

	/**
	 * Builds a game from the collections that were populated while parsing.
	 * @return Built game from xml data
	 */
	public Game buildGame(){
		buildArt();
		buildScuptures();
		buildTiles();
		buildPlayers();
		buildGuards();
		Tile[][] tileArray = buildTileArray();
		Floor floor = new Floor(tileArray, tileArray.length, tileArray[0].length, exits);
		addSculpturesToFloor(floor);
		addGuardsToFloor(floor);
		return new Game(floor, players);
	}

	private void addGuardsToFloor(Floor floor) {
		for(Guard guard : guards){
			floor.setCharacter(guard, guard.getRow(), guard.getCol());
		}
	}

	private void addSculpturesToFloor(Floor floor) {
		for(Sculpture sculp : sculptures){
			floor.setCharacter(sculp, sculp.getRow(), sculp.getCol());
		}
	}

	private void buildGuards() {
		// TODO Auto-generated method stub

	}

	private void buildPlayers() {
		for(PlayerBuilder playerBuilder: playerBuilders){
			players.add(playerBuilder.buildObject());
		}
	}

	private void buildTiles() {
		for(TileBuilder tileBuilder : tileBuilders){
			//TODO: Add handling of stair tiles
			Tile tile = tileBuilder.buildObject();
			HashMap<Direction, Integer> doorRefs = tileBuilder.getDoorReference();
			for(Direction d : doorRefs.keySet()){
				tile.setWall(d, doors.get(doorRefs.get(d)));
			}
			HashMap<Direction, Integer> artRefs = tileBuilder.getArtReference();
			for(Direction d : artRefs.keySet()){
				tile.getWall(d).setArt(paintings.get(artRefs.get(d)));
			}
			floorTiles.put(tileBuilder.getCoordinate(), tile);
			if(tile instanceof ExitTile){
				exits.add((ExitTile) tile);
			}
		}

	}

	private void buildScuptures() {
		for(SculptureBuilder sculptureBuilder: sculptureBuilders){
			sculptures.add(sculptureBuilder.buildObject());
		}
	}

	private void buildArt() {
		for(ArtBuilder artBuilder : artBuilders){
			paintings.put(artBuilder.getArtID(), artBuilder.buildObject());
		}

	}

	/**
	 * @return a 2D array of tiles built from the tile map
	 */
	private Tile[][] buildTileArray() {
		int width = findFloorWidth();
		int height = findFloorHeight();
		return buildTileArray(width, height);
	}

	/**
	 * Creates a 2d array of tiles from the given height and width and populates it from
	 * the map of coordinates to tiles, using the coordinate keys to find the array positions
	 * for each of the tiles.
	 * @param width Width of tile array
	 * @param height Height of tile array
	 * @return
	 */
	private Tile[][] buildTileArray(int width, int height) {
		Tile[][] tileArray = new Tile[height][width];
		for(Coordinate coord : floorTiles.keySet()){
			tileArray[coord.getY()][coord.getX()] = floorTiles.get(coord);
		}
		return tileArray;
	}

	/**
	 * Goes through the map of coordinates to tiles and finds the largest Y value held by a
	 * coordinate
	 */
	private int findFloorHeight() {
		int largestY = 0;
		for(Coordinate coord : floorTiles.keySet()){
			if(coord.getY() > largestY){
				largestY = coord.getX();
			}
		}
		return largestY + 1;
	}

	/**
	 * Goes through the map of coordinates to tiles and finds the largest X value held by a
	 * coordinate
	 */
	private int findFloorWidth() {
		int largestX = 0;
		for(Coordinate coord : floorTiles.keySet()){
			if(coord.getX() > largestX){
				largestX = coord.getX();
			}
		}
		return largestX + 1;
	}

}
