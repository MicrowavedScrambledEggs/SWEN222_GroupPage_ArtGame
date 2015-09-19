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

public class XMLReader {
	
	public static final String WALL_ELEMENT = "wall";
	public static final String POSITION_ELEMENT = "position";
	public static final String EMPTY_TILE_ELEMENT = "empty_tile";
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

	public XMLReader(File xmlFile){
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
	
	public Game getGame(){
		return xmlHandler.buildGame();
	}

}
