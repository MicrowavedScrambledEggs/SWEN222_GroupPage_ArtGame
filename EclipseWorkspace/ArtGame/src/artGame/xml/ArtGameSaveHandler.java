package artGame.xml;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.game.Tile;

public class ArtGameSaveHandler extends DefaultHandler {

	private ArrayList<ArrayList<Tile>> floorTiles = new ArrayList<ArrayList<Tile>>();
	private Player player

	public ArtGameSaveHandler(){
		super();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes){

	}

}
