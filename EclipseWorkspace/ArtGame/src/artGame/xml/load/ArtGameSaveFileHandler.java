package artGame.xml.load;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import artGame.main.Game;
import artGame.xml.XMLHandler;

public class ArtGameSaveFileHandler extends DefaultHandler {
	
	private Stack<ObjectBuilder> buildStack = new Stack<ObjectBuilder>();
	private ArrayList<ObjectBuilder> buildList = new ArrayList<ObjectBuilder>();
	private GameMaker gameMaker = new GameMaker();
	private CoordinateBuilder currentCoord;
	private String currentElement;
	
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
		if(qName.equals(XMLHandler.EMPTY_TILE_ELEMENT)){
			//decides the type of tile builder based on if it's for an exit tile or a regular empty tile
			if(attributes.getLength() != 0 &&
					attributes.getValue(XMLHandler.EXIT_ATTRIBUTE).equals(XMLHandler.TRUE_VALUE)){
				buildStack.push(new ObjectBuilder(new ExitTileBuilder(gameMaker)));
			} else {
				buildStack.push(new ObjectBuilder(new TileBuilder(gameMaker)));
			}
		} else if(qName.equals(XMLHandler.TILE_STRETCH_ELEMENT)){
			buildStack.push(new ObjectBuilder(new TileStretchBuilder(gameMaker, 
					Integer.parseInt(attributes.getValue(0)))));
		} else if(qName.equals(XMLHandler.STAIR_TILE_ELEMENT)){
			buildStack.push(new ObjectBuilder(new StairTileBuilder(gameMaker)));
		} else if(qName.equals(XMLHandler.POSITION_ELEMENT) || qName.equals(XMLHandler.START_ELEMENT)
				|| qName.equals(XMLHandler.FINISH_ELEMENT)){
			currentCoord = new CoordinateBuilder();
		} else if(qName.equals(XMLHandler.LINKED_TILE_ELEMENT)){
			ObjectBuilder stairBuilder = buildStack.peek();
			stairBuilder.addField(attributes.getQName(0), attributes.getValue(0));
			currentCoord = new CoordinateBuilder();
		} else if(qName.equals(XMLHandler.X_COORD_ELEMENT)){
			currentCoord.setX(Integer.parseInt(attributes.getValue(0)));
		} else if(qName.equals(XMLHandler.Y_COORD_ELEMENT)){
			currentCoord.setY(Integer.parseInt(attributes.getValue(0)));
		} else if(qName.equals(XMLHandler.WALL_ELEMENT)){
			//Wall variables for tiles
			//if xml file is correctly written, object builder on top of stack should be a tile builder	
			addFieldToCurrentBuilder(qName, attributeStringArray(attributes));
		} else if(qName.equals(XMLHandler.DOOR_ELEMENT)){
			String[] doorInfo = attributeStringArray(attributes);
			addFieldToCurrentBuilder(qName, doorInfo);
			gameMaker.addDoor(doorInfo);
		} else if(qName.equals(XMLHandler.PLAYER_ELEMENT)){
			buildStack.push(new ObjectBuilder(new PlayerBuilder(gameMaker, 
					Integer.parseInt(attributes.getValue(XMLHandler.ID_ATTRIBUTE)))));
		} else if(qName.equals(XMLHandler.DIRECTION_ELEMENT) || qName.equals(XMLHandler.NAME_ELEMENT)
				|| qName.equals(XMLHandler.VALUE_ELEMENT)){
			currentElement = qName;
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
		}
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
		if(qName.equals(XMLHandler.POSITION_ELEMENT) || qName.equals(XMLHandler.START_ELEMENT)
				|| qName.equals(XMLHandler.FINISH_ELEMENT) || qName.equals(XMLHandler.LINKED_TILE_ELEMENT)){
			addFieldToCurrentBuilder(qName, currentCoord.buildCoordinate());
		} else if(qName.equals(XMLHandler.X_PATH_ELEMENT) || qName.equals(XMLHandler.Y_PATH_ELEMENT)
				|| qName.equals(XMLHandler.PATROL_ELEMENT)){
			completePatrolSegment(qName);
		} else if(qName.equals(XMLHandler.GUARD_ELEMENT) || qName.equals(XMLHandler.SCULPTURE_ELEMENT)
				|| qName.equals(XMLHandler.PAINTING_ELEMENT) || qName.equals(XMLHandler.PLAYER_ELEMENT)
				|| qName.equals(XMLHandler.STAIR_TILE_ELEMENT) || qName.equals(XMLHandler.TILE_STRETCH_ELEMENT)
				|| qName.equals(XMLHandler.EMPTY_TILE_ELEMENT)){
			buildList.add(buildStack.pop());
		}	
	}
	
	private void completePatrolSegment(String qName) {
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
