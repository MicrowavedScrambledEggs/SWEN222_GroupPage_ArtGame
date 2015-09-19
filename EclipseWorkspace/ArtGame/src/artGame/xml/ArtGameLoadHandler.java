package artGame.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.game.Coordinate;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Player;
import artGame.game.Tile;
import artGame.main.Game;

public class ArtGameLoadHandler extends DefaultHandler {

	private HashMap<Coordinate, Tile> floorTiles = new HashMap<Coordinate, Tile>();
	private HashMap<Coordinate, Player> players = new HashMap<Coordinate, Player>();
	private HashMap<Coordinate, Guard> guards = new HashMap<Coordinate, Guard>();
	private Stack<ObjectBuilder> buildStack = new Stack<ObjectBuilder>();
	private String currentElement;

	public ArtGameLoadHandler(){
		super();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		if(localName.equals(XMLReader.EMPTY_TILE_ELEMENT)){
			buildStack.push(new TileBuilder());
		} else if(localName.equals(XMLReader.POSITION_ELEMENT)){
			buildStack.push(new CoordinateBuilder());
		} else if(localName.equals(XMLReader.X_COORD_ELEMENT) || localName.equals(XMLReader.Y_COORD_ELEMENT)){
			addFieldToCurrentBuilder(localName, attributes.getValue(XMLReader.VALUE_ATTRIBUTE));
		} else if(localName.equals(XMLReader.WALL_ELEMENT)){
			addFieldToCurrentBuilder(localName, attributes.getValue(XMLReader.DIRECTION_ATTRIBUTE));
		} else if(localName.equals(XMLReader.PLAYER_ELEMENT)){
			buildStack.push(new PlayerBuilder());
		} else if(localName.equals(XMLReader.DIRECTION_ELEMENT)){
			currentElement = localName;
		}
	}
	
	private void addFieldToCurrentBuilder(String localName, String value) {
		ObjectBuilder current = buildStack.peek();
		current.addFeild(localName, value);		
	}

	@Override
	public void endElement(String uri, String localName, String qName){
		if(localName.equals(XMLReader.EMPTY_TILE_ELEMENT)){
			TileBuilder tileBuilder = (TileBuilder) buildStack.pop();
			floorTiles.put(tileBuilder.getCoordinate(), tileBuilder.buildObject());
		} else if(localName.equals(XMLReader.POSITION_ELEMENT)){
			CoordinateBuilder coordBuilder = (CoordinateBuilder) buildStack.pop();
			ObjectBuilder possitionableObjectBuilder = buildStack.peek();
			possitionableObjectBuilder.addFeild(localName, coordBuilder.buildObject());	
		} else if(localName.equals(XMLReader.PLAYER_ELEMENT)){
			PlayerBuilder playerBuilder = (PlayerBuilder) buildStack.pop();
			players.put(playerBuilder.getCoordinate(), playerBuilder.buildObject());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		addFieldToCurrentBuilder(currentElement, new String(ch));
	}
	
	public Game buildGame(){
		Tile[][] tileArray = buildTileArray();
		Floor floor = new Floor(tileArray, tileArray.length, tileArray[0].length, guards.values());
		return new Game(floor, players.values());
	}

	private Tile[][] buildTileArray() {
		int width = findFloorWidth();
		int height = findFloorHeight();
		return buildTileArray(width, height);	
	}

	private Tile[][] buildTileArray(int width, int height) {
		Tile[][] tileArray = new Tile[width][height];
		for(Coordinate coord : floorTiles.keySet()){
			tileArray[coord.getX()][coord.getY()] = floorTiles.get(coord);
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
