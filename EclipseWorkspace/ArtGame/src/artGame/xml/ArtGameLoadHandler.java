package artGame.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.game.Coordinate;
import artGame.game.Guard;
import artGame.game.Player;
import artGame.game.Tile;

public class ArtGameLoadHandler extends DefaultHandler {

	private HashMap<Coordinate, Tile> floorTiles = new HashMap<Coordinate, Tile>();
	private ArrayList<Player> player = new ArrayList<Player>();
	private ArrayList<Guard> guards = new ArrayList<Guard>();
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
		
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		addFieldToCurrentBuilder(currentElement, new String(ch));
	}

}
