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

public class ArtGameLoadHandler extends DefaultHandler {

	private HashMap<Coordinate, Tile> floorTiles = new HashMap<Coordinate, Tile>();
	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Guard> guards = new ArrayList<Guard>();
	private ArrayList<ExitTile> exits = new ArrayList<ExitTile>();
	private Stack<ObjectBuilder> buildStack = new Stack<ObjectBuilder>();
	private String currentElement;

	public ArtGameLoadHandler(){
		super();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		if(qName.equals(XMLReader.EMPTY_TILE_ELEMENT)){	
			if(attributes.getLength() != 0 &&
					attributes.getValue(XMLReader.EXIT_ATTRIBUTE).equals(XMLReader.TRUE_VALUE)){
				buildStack.push(new ExitTileBuilder());
			} else {
				buildStack.push(new TileBuilder());
			}
		} else if(qName.equals(XMLReader.POSITION_ELEMENT)){
			buildStack.push(new CoordinateBuilder());
		} else if(qName.equals(XMLReader.X_COORD_ELEMENT) || qName.equals(XMLReader.Y_COORD_ELEMENT)){
			addFieldToCurrentBuilder(qName, attributes.getValue(XMLReader.VALUE_ATTRIBUTE));
		} else if(qName.equals(XMLReader.WALL_ELEMENT)){
			addFieldToCurrentBuilder(qName, attributes.getValue(XMLReader.DIRECTION_ATTRIBUTE));
		} else if(qName.equals(XMLReader.PLAYER_ELEMENT)){
			buildStack.push(new PlayerBuilder());
			addFieldToCurrentBuilder(XMLReader.ID_ATTRIBUTE, attributes.getValue(XMLReader.ID_ATTRIBUTE));
		} else if(qName.equals(XMLReader.DIRECTION_ELEMENT)){
			currentElement = qName;
		}
	}
	
	private void addFieldToCurrentBuilder(String localName, String value) {
		ObjectBuilder current = buildStack.peek();
		current.addFeild(localName, value);		
	}

	@Override
	public void endElement(String uri, String localName, String qName){
		if(qName.equals(XMLReader.EMPTY_TILE_ELEMENT)){
			TileBuilder tileBuilder = (TileBuilder) buildStack.pop();
			Tile builtTile = tileBuilder.buildObject();
			floorTiles.put(tileBuilder.getCoordinate(), builtTile);
			if(builtTile instanceof ExitTile){
				exits.add((ExitTile) builtTile);
			}
		} else if(qName.equals(XMLReader.POSITION_ELEMENT)){
			CoordinateBuilder coordBuilder = (CoordinateBuilder) buildStack.pop();
			ObjectBuilder possitionableObjectBuilder = buildStack.peek();
			possitionableObjectBuilder.addFeild(qName, coordBuilder.buildObject());	
		} else if(qName.equals(XMLReader.PLAYER_ELEMENT)){
			PlayerBuilder playerBuilder = (PlayerBuilder) buildStack.pop();
			players.add(playerBuilder.buildObject());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
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
