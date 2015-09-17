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
	private Player player;
	private ArrayList<Guard> guards = new ArrayList<Guard>();
	private Stack<ObjectBuilder> buildStack = new Stack<ObjectBuilder>();

	public ArtGameLoadHandler(){
		super();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){
		if(localName.equals(XMLReader.EMPTY_TILE_ELEMENT)){
			buildStack.push(new TileBuilder());
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName){
		
	}

}
