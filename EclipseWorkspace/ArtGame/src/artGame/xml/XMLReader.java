package artGame.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import artGame.main.Game;

/**
 * Class for parsing xml files for saved games and new games 
 * 
 * Static fields match the terms used in the xml files for different elements,
 * attributes and values etc. Typically an element represents an artGame.game class
 * and it's attributes and values it's fields. Exceptions X_COORD_ELEMENT and
 * Y_COORD_ELEMENT which are fields for Coordinate
 * 
 * @author Badi James
 *
 */
public class XMLReader {
	
	//TODO: Add a lot more strings for elements etc once artGame.game is more complete
	//TODO: Add strings for Guards
	//TODO: Add handling for Art
	//TODO: Add handling for inventory
	public static final String WALL_ELEMENT = "wall";
	public static final String DOOR_ELEMENT = "door";
	public static final String POSITION_ELEMENT = "position";
	public static final String EMPTY_TILE_ELEMENT = "empty_tile";
	public static final String TILE_STRETCH_ELEMENT = "tile_stretch";
	public static final String STAIR_TILE_ELEMENT = "stair_tile";
	public static final String X_COORD_ELEMENT = "x";
	public static final String Y_COORD_ELEMENT = "y";
	public static final String PLAYER_ELEMENT = "player";
	public static final String DIRECTION_ELEMENT = "direction";
	
	public static final String DIRECTION_ATTRIBUTE = "direction";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String ID_ATTRIBUTE = "id";
	public static final String EXIT_ATTRIBUTE = "isExit";
	
	public static final String NORTH_VALUE = "NORTH";
	public static final String WEST_VALUE = "WEST";
	public static final String SOUTH_VALUE = "SOUTH";
	public static final String EAST_VALUE = "EAST";
	public static final String TRUE_VALUE = "TRUE";
	
	private ArtGameLoadHandler xmlHandler;
	
	/**
	 * Constructor for class XMLReader. Takes the given xmlFile and parses it
	 * with a handler designed for the artGame xml format
	 * 
	 * @param xmlFile artGame xml file to parse
	 */
	public XMLReader(File xmlFile){
		//create the
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setValidating(true);
	    try {
	        SAXParser saxParser = factory.newSAXParser();
	        xmlHandler = new ArtGameLoadHandler();
	        saxParser.parse(xmlFile, xmlHandler);
	    }
	    catch(ParserConfigurationException e1) {
	    	System.out.println("Problem with parsing: " + e1);
	    }
	    catch(SAXException e1) {
	    	System.out.println("Problem with reading xml file: " + e1);
	    }
	    catch(IOException e) {
	    	System.out.println("Problem with reading xml file: " + e);
	    }

	}
	
	/**
	 * @return Game built from the handler after parsing xmlFile
	 */
	public Game getGame(){
		return xmlHandler.buildGame();
	}

}
