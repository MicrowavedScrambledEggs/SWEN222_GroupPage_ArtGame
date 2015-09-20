package artGame.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.game.Coordinate;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Player;
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

	private HashMap<Coordinate, Tile> floorTiles = new HashMap<Coordinate, Tile>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Guard> guards = new ArrayList<Guard>();
	private ArrayList<ExitTile> exits = new ArrayList<ExitTile>();
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
		} else if(qName.equals(XMLReader.POSITION_ELEMENT)){
			buildStack.push(new CoordinateBuilder());
		} else if(qName.equals(XMLReader.X_COORD_ELEMENT) || qName.equals(XMLReader.Y_COORD_ELEMENT)){
			//Variables for coordinate
			//if xml file is correctly written, object builder on top of stack should be a coordinate builder 
			addFieldToCurrentBuilder(qName, attributes.getValue(XMLReader.VALUE_ATTRIBUTE));
		} else if(qName.equals(XMLReader.WALL_ELEMENT)){
			//Wall variables for tiles
			//if xml file is correctly written, object builder on top of stack should be a tile builder 
			addFieldToCurrentBuilder(qName, attributes.getValue(XMLReader.DIRECTION_ATTRIBUTE));
		} else if(qName.equals(XMLReader.PLAYER_ELEMENT)){
			buildStack.push(new PlayerBuilder());
			//adds the iD value in the id attribute to the new player builder
			addFieldToCurrentBuilder(XMLReader.ID_ATTRIBUTE, attributes.getValue(XMLReader.ID_ATTRIBUTE));
		} else if(qName.equals(XMLReader.DIRECTION_ELEMENT)){
			currentElement = qName;
		}
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
		if(qName.equals(XMLReader.EMPTY_TILE_ELEMENT)){
			completeTile();
		} else if(qName.equals(XMLReader.POSITION_ELEMENT)){
			completePosition(qName);	
		} else if(qName.equals(XMLReader.PLAYER_ELEMENT)){
			completePlayer();
		}
	}
	
	/**
	 * Pops a playerBuilder from the top of the stack, builds the player object then adds it to 
	 * the players collection
	 */
	private void completePlayer() {
		PlayerBuilder playerBuilder = (PlayerBuilder) buildStack.pop();
		players.add(playerBuilder.buildObject());
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
		Tile builtTile = tileBuilder.buildObject();
		floorTiles.put(tileBuilder.getCoordinate(), builtTile);
		if(builtTile instanceof ExitTile){
			exits.add((ExitTile) builtTile);
		}
	}
	
	public void characters(char[] ch, int start, int length){
		//TODO: Add a lot more cases once artGame.game is more complete
		String elementDat = new String(Arrays.copyOfRange(ch, start, start + length));
		elementDat = elementDat.trim();
		if(elementDat.length() > 0){
			addFieldToCurrentBuilder(currentElement, elementDat);
		}
	}
	
	public Game buildGame(){
		Tile[][] tileArray = buildTileArray();
		Floor floor = new Floor(tileArray, tileArray.length, tileArray[0].length, guards, exits);
		return new Game(floor, players);
	}

	private Tile[][] buildTileArray() {
		int width = findFloorWidth();
		int height = findFloorHeight();
		return buildTileArray(width, height);	
	}

	private Tile[][] buildTileArray(int width, int height) {
		Tile[][] tileArray = new Tile[height][width];
		for(Coordinate coord : floorTiles.keySet()){
			tileArray[coord.getY()][coord.getX()] = floorTiles.get(coord);
		}
		return tileArray;
	}

	private int findFloorHeight() {
		int largestY = 0;
		for(Coordinate coord : floorTiles.keySet()){
			if(coord.getY() > largestY){
				largestY = coord.getX();
			}
		}
		return largestY + 1;
	}

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
