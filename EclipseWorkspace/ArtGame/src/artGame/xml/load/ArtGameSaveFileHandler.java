package artGame.xml.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.game.Character.Direction;
import artGame.game.GameError;
import artGame.main.Game;
import artGame.xml.XMLHandler;

/**
 * Sax Handler for parsing xml files for game. The 'loader'. Makes ObjectBuilders from data
 * in the majority of the elements, using a stack to match the xml structure. ObjectBuilders
 * then make their object and it to a GameMaker, that can then make a Game object
 *
 * @author Badi James
 *
 */
public class ArtGameSaveFileHandler extends DefaultHandler {

	private Stack<ObjectBuilder> buildStack = new Stack<ObjectBuilder>();
	private ArrayList<ObjectBuilder> buildList = new ArrayList<ObjectBuilder>();
	private GameMaker gameMaker = new GameMaker();
	private CoordinateBuilder currentCoord;
	private String currentElement;
	private int currentLevel;

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
		//TODO: Add handling for item descriptions
		if(qName.equals(XMLHandler.FLOOR_ELEMENT)){
			//Sets current level for tiles in this floor element
			currentLevel = Integer.parseInt(attributes.getValue(0));
		} else if(qName.equals(XMLHandler.LEVEL_ATTRIBUTE) || qName.equals(XMLHandler.ROW_ELEMENT)
				|| qName.equals(XMLHandler.COL_ELEMENT) || qName.equals(XMLHandler.FROM_COL_ELEMENT)
				|| qName.equals(XMLHandler.FROM_ROW_ELEMENT) || qName.equals(XMLHandler.TO_ROW_ELEMENT)
				|| qName.equals(XMLHandler.TO_COL_ELEMENT)){
			//Empty elements with one attribute, typically elements that only represent a number
			addFieldToCurrentBuilder(qName, attributes.getValue(0));
		}
		if(qName.equals(XMLHandler.EMPTY_TILE_ELEMENT)){
			//decides the type of tile builder based on if it's for an exit tile or a regular empty tile
			if(attributes.getLength() != 0 &&
					attributes.getValue(XMLHandler.EXIT_ATTRIBUTE).equals(XMLHandler.TRUE_VALUE)){
				buildStack.push(new ObjectBuilder(new ExitTileBuilder(currentLevel, gameMaker)));
			} else {
				buildStack.push(new ObjectBuilder(new TileBuilder(currentLevel, gameMaker)));
			}
		} else if(qName.equals(XMLHandler.TILE_STRETCH_ELEMENT)){
			buildStack.push(new ObjectBuilder(new TileStretchBuilder(currentLevel, gameMaker,
					Integer.parseInt(attributes.getValue(0)))));
		} else if(qName.equals(XMLHandler.STAIR_TILE_ELEMENT)){
			//Gets the stair's direction and if its going up/down from the attributes
			Direction dir = directionFromString(attributes.getValue(0));
			boolean up = Boolean.parseBoolean(attributes.getValue(1));
			buildStack.push(new ObjectBuilder(new StairTileBuilder(currentLevel, dir, up, gameMaker)));
		} else if(qName.equals(XMLHandler.POSITION_ELEMENT) || qName.equals(XMLHandler.START_ELEMENT)
				|| qName.equals(XMLHandler.FINISH_ELEMENT)){
			//Creates a coordinate builder, seperate from object builders, to build a coordinate to
			//add to the object builder at the top of the build stack
			currentCoord = new CoordinateBuilder();
		} else if(qName.equals(XMLHandler.PATROL_STEP_ELEMENT)){
			currentCoord = new PatrolStep();
		} else if(qName.equals(XMLHandler.LINKED_TILE_ELEMENT)){
			//If xml file is correctly written, ObjectBuilder on top of stack should be a StairTileBuilder
			//A coordinate reference for stair tiles that say the position of the stairs the are
			//linked to. Gets the currently in construction stair builder from the stack and
			//adds the linked tile's level attribute to the stair builder
			ObjectBuilder stairBuilder = buildStack.peek();
			stairBuilder.addField(attributes.getQName(0), attributes.getValue(0));
			currentCoord = new CoordinateBuilder();
		} else if(qName.equals(XMLHandler.X_COORD_ELEMENT)){//adds X or Y value to current coordinate builder
			currentCoord.setX(Integer.parseInt(attributes.getValue(0)));
		} else if(qName.equals(XMLHandler.Y_COORD_ELEMENT)){
			currentCoord.setY(Integer.parseInt(attributes.getValue(0)));
		} else if(qName.equals(XMLHandler.WALL_ELEMENT)){
			//Wall variables for tiles
			//if xml file is correctly written, object builder on top of stack should be a tile builder
			addFieldToCurrentBuilder(qName, attributeStringArray(attributes));
		} else if(qName.equals(XMLHandler.DOOR_ELEMENT)){
			//Another type of wall variable for tiles
			String[] doorInfo = attributeStringArray(attributes);//gets the data needed to this door to tiles
			addFieldToCurrentBuilder(qName, doorInfo);//adds a door reference to a tile builder
			gameMaker.addDoor(doorInfo);//game keeps a collection of doors to match them up with tiles when
			//building game
		} else if(qName.equals(XMLHandler.PLAYER_ELEMENT)){
			buildStack.push(new ObjectBuilder(new PlayerBuilder(gameMaker,
					Integer.parseInt(attributes.getValue(XMLHandler.ID_ATTRIBUTE)))));
		} else if(qName.equals(XMLHandler.DIRECTION_ELEMENT) || qName.equals(XMLHandler.NAME_ELEMENT)
				|| qName.equals(XMLHandler.VALUE_ELEMENT)){
			currentElement = qName;//for xml elements with just character data. "leaf" elements
		} else if(qName.equals(XMLHandler.PAINTING_ELEMENT)){
			buildStack.push(new ObjectBuilder(new ArtBuilder(gameMaker,
					Integer.parseInt(attributes.getValue(0)))));
		} else if(qName.equals(XMLHandler.SCULPTURE_ELEMENT)){
			buildStack.push(new ObjectBuilder(new SculptureBuilder(gameMaker,
					Integer.parseInt(attributes.getValue(0)))));
		} else if(qName.equals(XMLHandler.GUARD_ELEMENT)){
			buildStack.push(new ObjectBuilder(new GuardBuilder(gameMaker,
					Integer.parseInt(attributes.getValue(0)))));
		} else if(qName.equals(XMLHandler.PATROL_ELEMENT)){
			buildStack.push(new ObjectBuilder(new Patrol()));
		} else if(qName.equals(XMLHandler.X_PATH_ELEMENT)){
			buildStack.push(new ObjectBuilder(new WestEastStretch()));
		} else if(qName.equals(XMLHandler.Y_PATH_ELEMENT)){
			buildStack.push(new ObjectBuilder(new NorthSouthStretch()));
		} else if(qName.equals(XMLHandler.ITEM_ELEMENT)){
			addFieldToCurrentBuilder(qName, this.attributeStringArray(attributes));
		} else if(qName.equals(XMLHandler.CHEST_ELEMENT)){
			buildStack.push(new ObjectBuilder(new ChestBuilder(currentLevel, gameMaker,
					Integer.parseInt(attributes.getValue(0)))));
		} else if(qName.equals(XMLHandler.ROOM_ELEMENT)){
			buildStack.push(new ObjectBuilder(new RoomBuilder(gameMaker)));
		} else if(qName.equals(XMLHandler.SEGMENT_ELEMENT)){
			buildStack.push(new ObjectBuilder(new SquareArea(currentLevel)));
		} else if(qName.equals(XMLHandler.SQUARE_ELEMENT)){
			buildStack.push(new ObjectBuilder(new SingleTile(currentLevel)));
		}
	}

	/**
	 * Used to get a Direction object from its representative string
	 * @param direction String that supposedly represents a direction
	 * @return Direction object that matches the string
	 */
	private Direction directionFromString(String direction) {
		if(direction.equals(XMLHandler.NORTH_VALUE)){
			return Direction.NORTH;
		} else if (direction.equals(XMLHandler.SOUTH_VALUE)){
			return Direction.SOUTH;
		} else if (direction.equals(XMLHandler.WEST_VALUE)){
			return Direction.WEST;
		} else if (direction.equals(XMLHandler.EAST_VALUE)){
			return Direction.EAST;
		} else {
			throw new IllegalArgumentException(String.format("Need a valid direction. \"%s\" "
					+ "is not a valid direction.\n", direction));
		}
	}

	/**
	 * Called when parser comes across an end tag for an element
	 *
	 * For elements representing coordinates, builds the coordinate from the currentCoord
	 * coordinate builder and adds it to the object builder at the top of the stack.
	 *
	 * For elements where
	 */
	@Override
	public void endElement(String uri, String localName, String qName){
		//TODO: Add a lot more cases once artGame.game is more complete
		if(qName.equals(XMLHandler.POSITION_ELEMENT) || qName.equals(XMLHandler.START_ELEMENT)
				|| qName.equals(XMLHandler.FINISH_ELEMENT) || qName.equals(XMLHandler.LINKED_TILE_ELEMENT)){
			addFieldToCurrentBuilder(qName, currentCoord.buildCoordinate());
		} else if(qName.equals(XMLHandler.PATROL_STEP_ELEMENT)){
			addFieldToCurrentBuilder(qName, currentCoord);
		} else if(qName.equals(XMLHandler.X_PATH_ELEMENT) || qName.equals(XMLHandler.Y_PATH_ELEMENT)
				|| qName.equals(XMLHandler.PATROL_ELEMENT) || qName.equals(XMLHandler.SQUARE_ELEMENT)
				|| qName.equals(XMLHandler.SEGMENT_ELEMENT)){
			addBuildStrategyAsFeild(qName);
		} else if(qName.equals(XMLHandler.GUARD_ELEMENT) || qName.equals(XMLHandler.SCULPTURE_ELEMENT)
				|| qName.equals(XMLHandler.PAINTING_ELEMENT) || qName.equals(XMLHandler.PLAYER_ELEMENT)
				|| qName.equals(XMLHandler.STAIR_TILE_ELEMENT) || qName.equals(XMLHandler.TILE_STRETCH_ELEMENT)
				|| qName.equals(XMLHandler.EMPTY_TILE_ELEMENT) || qName.equals(XMLHandler.CHEST_ELEMENT)
				|| qName.equals(XMLHandler.ROOM_ELEMENT)){
			buildList.add(buildStack.pop());
		}
	}

	private void addBuildStrategyAsFeild(String qName) {
		ObjectBuilder patrolSegmentBuilder = buildStack.pop();
		BuildStrategy patrolSegment = patrolSegmentBuilder.getBuildStrategy();
		addFieldToCurrentBuilder(qName, patrolSegment);
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
	 * Adds the given field, defined by localName, with the value defined by 'value' to the objectBuilder
	 * at the top of the stack
	 * @param localName name of Variable
	 * @param value value of Variable
	 */
	private void addFieldToCurrentBuilder(String localName, Object... values) {
		if(buildStack.isEmpty()){
			throw new GameError("Problem when parsing a " + localName + " with " + values[0].toString());
		}
		ObjectBuilder current = buildStack.peek();
		current.addField(localName, values);
	}

	private String[] attributeStringArray(Attributes attributes){
		String[] attributeStringArray = new String[attributes.getLength()];
		for(int i = 0; i < attributes.getLength(); i++){
			attributeStringArray[i] = attributes.getValue(i);
		}
		return attributeStringArray;
	}

	public Game buildGame(){
		for(ObjectBuilder objectBuilder : buildList){
			objectBuilder.addToGame();
		}
		return gameMaker.makeGame();
	}

}
