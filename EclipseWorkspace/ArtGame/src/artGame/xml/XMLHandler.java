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
import artGame.xml.load.ArtGameSaveFileHandler;
import artGame.xml.save.ArtGameSaver;

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
public class XMLHandler {

	public static final String WALL_ELEMENT = "wall";
	public static final String DOOR_ELEMENT = "door";
	public static final String POSITION_ELEMENT = "position";
	public static final String EMPTY_TILE_ELEMENT = "empty_tile";
	public static final String TILE_STRETCH_ELEMENT = "tile_stretch";
	public static final String STAIR_TILE_ELEMENT = "stair_tile";
	public static final String LINKED_TILE_ELEMENT = "linked_tile";
	public static final String X_COORD_ELEMENT = "x";
	public static final String Y_COORD_ELEMENT = "y";
	public static final String PLAYER_ELEMENT = "player";
	public static final String PLAYERS_ELEMENT = "players";
	public static final String GUARD_ELEMENT = "guard";
	public static final String GUARDS_ELEMENT = "guards";
	public static final String PATROL_ELEMENT = "patrol";
	public static final String X_PATH_ELEMENT = "west_east_stretch";
	public static final String Y_PATH_ELEMENT = "north_south_stretch";
	public static final String START_ELEMENT = "start";
	public static final String FINISH_ELEMENT = "finish";
	public static final String PATROL_STEP_ELEMENT = "step";
	public static final String DIRECTION_ELEMENT = "direction";
	public static final String PAINTING_ELEMENT = "painting";
	public static final String SCULPTURE_ELEMENT = "sculpture";
	public static final String NAME_ELEMENT = "name";
	public static final String VALUE_ELEMENT = "value";
	public static final String INVENTORY_ELEMENT = "inventory";
	public static final String ITEM_ELEMENT = "item";
	public static final String CHEST_ELEMENT = "chest";
	public static final String FLOOR_ELEMENT = "floor";
	public static final String ART_ELEMENT = "art";
	public static final String ROOM_ELEMENT = "room";
	public static final String SEGMENT_ELEMENT = "segment";
	public static final String SQUARE_ELEMENT = "square";
	public static final String ROW_ELEMENT = "row";
	public static final String COL_ELEMENT = "col";
	public static final String FROM_ROW_ELEMENT = "from_row";
	public static final String FROM_COL_ELEMENT = "from_col";
	public static final String TO_ROW_ELEMENT = "to_row";
	public static final String TO_COL_ELEMENT = "to_col";

	public static final String LEVEL_ATTRIBUTE = "level";
	public static final String DIRECTION_ATTRIBUTE = "direction";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String ID_ATTRIBUTE = "id";
	public static final String DOOR_ID_ATTRIBUTE = "doorID";
	public static final String KEY_ID_ATTRIBUTE = "keyID";
	public static final String ART_ID_ATTRIBUTE = "artID";
	public static final String LOCKED_ATTRIBUTE = "locked";
	public static final String EXIT_ATTRIBUTE = "isExit";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String UP_ATTRIBUTE = "up";

	public static final String NORTH_VALUE = "NORTH";
	public static final String WEST_VALUE = "WEST";
	public static final String SOUTH_VALUE = "SOUTH";
	public static final String EAST_VALUE = "EAST";
	public static final String TRUE_VALUE = "TRUE";
	public static final String FALSE_VALUE = "FALSE";
	public static final String ART_VALUE = "ART";
	public static final String KEY_VALUE = "KEY";


	private ArtGameSaveFileHandler xmlHandler;
	private ArtGameSaver saver;

	/**
	 * Constructor for class XMLReader. Takes the given xmlFile and parses it
	 * with a handler designed for the artGame xml format
	 *
	 * @param xmlFile artGame xml file to parse
	 */
	public XMLHandler(){
		xmlHandler = new ArtGameSaveFileHandler();
		saver = new ArtGameSaver();
	}

	/**
	 * @return Game built from the handler after parsing xmlFile
	 */
	public Game loadGame(File xmlFile){
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setValidating(true);
	    try {
	        SAXParser saxParser = factory.newSAXParser();
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
		return xmlHandler.buildGame();
	}

	public void saveGame(Game game, String fileName){
		saver.saveGame(game, fileName);
	}

}
