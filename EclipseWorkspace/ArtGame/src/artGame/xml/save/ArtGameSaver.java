package artGame.xml.save;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import artGame.game.*;
import artGame.game.Character;
import artGame.game.Character.Direction;
import artGame.main.Game;
import artGame.xml.XMLHandler;

/**
 * Class for creating xml save files for the game
 *
 * @author Badi James 300156502
 *
 */
public class ArtGameSaver {

	private ArrayList<Door> doors;
	private HashSet<Art> paintings;
	private ArrayList<Player> players;
	private ArrayList<Guard> guards;
	private ArrayList<Sculpture> sculptures;
	private HashMap<Room, ArrayList<Coordinate>> rooms;

	/**
	 * Uses the Game and all its objects to write a save file for the
	 * game with the path/name of the given string.
	 *
	 * Uses an XMLOutPutFactory and a XMLStreamWriter to write the file
	 *
	 * Writes in the format of:
	 * - An overall game element
	 * - floor element. Just one of level 0 like implemented in game logic
	 * 		- elements for tiles such as empty tile, stair tile and chest
	 * 			- has position and wall elements
	 * 			- Stair tiles have location of linked stair tile
	 * 			- chests have inventory references
	 * 		- elements for defining rooms
	 * 			- Just has 'square' elements for each tile in room
	 * - players element
	 * 		- player elements with id attribute
	 * 			-has direction, inventory and position elements
	 * - guards element
	 * 		- guard elements with id attribute
	 * 			-has direction, inventory and position elements
	 * 			-has patrol (if guard object has patrol)
	 * 				-just has 'step' elements for each coordinate in patrol path
	 * - art element
	 * 		- painting element with id attribute
	 * 			-has name and value elements
	 * 		- sculpture element with id attribute
	 * 			-has direction and position elements
	 * 			-has name and value elements
	 *
	 * @param game Game to save
	 * @param fileName string representation of file name path
	 */
	public void saveGame(Game game, String fileName) {
		this.doors = new ArrayList<Door>();
		this.paintings = new HashSet<Art>();
		this.players = new ArrayList<Player>();
		this.guards = new ArrayList<Guard>();
		this.sculptures = new ArrayList<Sculpture>();
		this.rooms = new HashMap<Room, ArrayList<Coordinate>>();
		FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(fileName);
	        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
	        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos);
	        writer.writeStartDocument();
	        writer.writeStartElement("game");
	        //writes the element containing all the floor data, with all tiles
	        writeFloor(game, writer);
	        writePlayers(writer);
	        writeGuards(writer);
	        writeArt(writer);
	        writer.writeEndElement();
	        writer.flush();
	    }
	    catch(IOException exc) {
	    	throw new Error(exc);
	    }
	    catch(XMLStreamException exc) {
	    	throw new Error(exc);
	    }
	    finally {
	    }
	}

	private void writeGuards(XMLStreamWriter writer) throws XMLStreamException {
		//writes a "guards" element
		writer.writeStartElement(XMLHandler.GUARDS_ELEMENT);
		for(Guard guard : guards){
			writer.writeStartElement(XMLHandler.GUARD_ELEMENT);
			writer.writeAttribute(XMLHandler.ID_ATTRIBUTE, String.valueOf(guard.getId()));
			writer.writeStartElement(XMLHandler.DIRECTION_ELEMENT);
			writer.writeCharacters(guard.getDir().toString());
			writer.writeEndElement();
			writeCoordinate(guard.getRow(), guard.getCol(), writer, XMLHandler.POSITION_ELEMENT);
			writeCharacterInventory(guard.getInventory(), writer);
			List<Coordinate> path = guard.getPath();
			if(path != null){
				writer.writeStartElement(XMLHandler.PATROL_ELEMENT);
				for(Coordinate step : path){
					writeCoordinate(step.getRow(), step.getCol(), writer, XMLHandler.PATROL_STEP_ELEMENT);
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	private void writeArt(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.ART_ELEMENT);
		for(Art painting : paintings){
			writePainting(writer, painting);
		}
		for(Sculpture sculpt : sculptures){
			writeSculpture(writer, sculpt);
		}
		writer.writeEndElement();
	}

	private void writeSculpture(XMLStreamWriter writer, Sculpture sculpt)
			throws XMLStreamException {
		writer.writeStartElement(XMLHandler.SCULPTURE_ELEMENT);
		writer.writeAttribute(XMLHandler.ART_ID_ATTRIBUTE, String.valueOf(sculpt.getId()));
		writer.writeStartElement(XMLHandler.DIRECTION_ELEMENT);
		writer.writeCharacters(sculpt.getDir().toString());
		writer.writeEndElement();
		writeCoordinate(sculpt.getRow(), sculpt.getCol(), writer, XMLHandler.POSITION_ELEMENT);
		writer.writeStartElement(XMLHandler.NAME_ELEMENT);
		writer.writeCharacters(sculpt.getName());
		writer.writeEndElement();
		writer.writeStartElement(XMLHandler.VALUE_ELEMENT);
		writer.writeCharacters(String.valueOf(sculpt.getValue()));
		writer.writeEndElement();
		writer.writeEndElement();
	}

	private void writePainting(XMLStreamWriter writer, Art painting)
			throws XMLStreamException {
		writer.writeStartElement(XMLHandler.PAINTING_ELEMENT);
		writer.writeAttribute(XMLHandler.ART_ID_ATTRIBUTE, String.valueOf(painting.ID));
		writer.writeStartElement(XMLHandler.NAME_ELEMENT);
		writer.writeCharacters(painting.name);
		writer.writeEndElement();
		writer.writeStartElement(XMLHandler.VALUE_ELEMENT);
		writer.writeCharacters(String.valueOf(painting.value));
		writer.writeEndElement();
		writer.writeEndElement();
	}

	private void writePlayers(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.PLAYERS_ELEMENT);
		for(Player player : players){
			writer.writeStartElement(XMLHandler.PLAYER_ELEMENT);
			writer.writeAttribute(XMLHandler.ID_ATTRIBUTE, String.valueOf(player.getId()));
			writer.writeStartElement(XMLHandler.DIRECTION_ELEMENT);
			writer.writeCharacters(player.getDir().toString());
			writer.writeEndElement();
			writeCoordinate(player.getRow(), player.getCol(), writer, XMLHandler.POSITION_ELEMENT);
			writeCharacterInventory(player.getInventory(), writer);
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}

	private void writeCharacterInventory(Set<Item> inventory, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.INVENTORY_ELEMENT);
		for(Item item : inventory){
			writeItem(writer, item);
		}
		writer.writeEndElement();
	}

	private void writeFloor(Game game, XMLStreamWriter writer) throws XMLStreamException {
		Floor gameFloor = game.getFloor();
		writer.writeStartElement(XMLHandler.FLOOR_ELEMENT);
		writer.writeAttribute(XMLHandler.LEVEL_ATTRIBUTE, "0");
		for(int r = 0; r < gameFloor.getHeight(); r++){
			for(int c = 0; c < gameFloor.getWidth(); c++){
				Tile tile = gameFloor.getTile(r, c);
				if(tile != null){
					writeTile(tile, r, c, writer);
				}
			}
		}
		writeRooms(writer);
		writer.writeEndElement();
	}

	private void writeRooms(XMLStreamWriter writer) throws XMLStreamException {
		for(Room room : rooms.keySet()){
			writeRoom(room, writer, rooms.get(room));
		}
	}

	private void writeRoom(Room room, XMLStreamWriter writer, ArrayList<Coordinate> roomCoords) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.ROOM_ELEMENT);
		for(Coordinate tileCoord : roomCoords){
			writeSingleTile(tileCoord, writer);
		}
		writer.writeEndElement();
	}

	private void writeSingleTile(Coordinate tileCoord, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.SQUARE_ELEMENT);
		writer.writeEmptyElement(XMLHandler.ROW_ELEMENT);
		writer.writeAttribute(XMLHandler.VALUE_ATTRIBUTE, String.valueOf(tileCoord.getRow()));
		writer.writeEmptyElement(XMLHandler.COL_ELEMENT);
		writer.writeAttribute(XMLHandler.VALUE_ATTRIBUTE, String.valueOf(tileCoord.getCol()));
		writer.writeEndElement();
	}

	private void writeTile(Tile tile, int r, int c, XMLStreamWriter writer) throws XMLStreamException {
		Room tileRoom = tile.getRoom();
		//Collects rooms to write for later
		if(rooms.get(tileRoom) == null){
			rooms.put(tileRoom, new ArrayList<Coordinate>());
		}
		rooms.get(tileRoom).add(new Coordinate(c, r));
		if(tile instanceof EmptyTile || tile instanceof ExitTile){
			writeEmptyTile(tile, r, c, writer);
		} else if(tile instanceof StairTile){
			writeStairTile((StairTile) tile, r, c, writer);
		} else if(tile instanceof Chest){
			writeChest((Chest) tile, r, c, writer);
		}
	}

	private void writeChest(Chest chest, int r, int c, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.CHEST_ELEMENT);
		writer.writeAttribute(XMLHandler.ID_ATTRIBUTE, String.valueOf(chest.id));
		writeCoordinate(r, c, writer, XMLHandler.POSITION_ELEMENT);
		Item storedItem = chest.getContent();
		writeChestInventory(writer, storedItem);
		writeWalls(chest, writer);
		writer.writeEndElement();
	}

	private void writeChestInventory(XMLStreamWriter writer, Item storedItem)
			throws XMLStreamException {
		if(storedItem != null){
			writer.writeStartElement(XMLHandler.INVENTORY_ELEMENT);
			writeItem(writer, storedItem);
			writer.writeEndElement();
		}
	}

	private void writeItem(XMLStreamWriter writer, Item storedItem)
			throws XMLStreamException {
		writer.writeEmptyElement(XMLHandler.ITEM_ELEMENT);
		String typeValue = "";
		if(storedItem instanceof Art){
			typeValue = XMLHandler.ART_VALUE;
			paintings.add((Art) storedItem);
		} else {
			typeValue = XMLHandler.KEY_VALUE;
		}
		writer.writeAttribute(XMLHandler.TYPE_ATTRIBUTE, typeValue);
		writer.writeAttribute(XMLHandler.ID_ATTRIBUTE, String.valueOf(storedItem.ID));
	}

	private void writeStairTile(StairTile stairs, int r, int c,
			XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.STAIR_TILE_ELEMENT);
		writer.writeAttribute(XMLHandler.DIRECTION_ATTRIBUTE, stairs.getDir().toString());
		String upValue = stairs.isGoingUp() ? XMLHandler.TRUE_VALUE : XMLHandler.FALSE_VALUE;
		writer.writeAttribute(XMLHandler.UP_ATTRIBUTE, upValue);
		writeCoordinate(r, c, writer, XMLHandler.POSITION_ELEMENT);
		StairTile linkedTile = stairs.getLinkedTile();
		writeLinkedTile(writer, linkedTile);
		writeWalls(stairs, writer);
		storeCharacter(stairs.getOccupant());//collects characters to write later
		writer.writeEndElement();
	}

	private void writeLinkedTile(XMLStreamWriter writer, StairTile linkedTile)
			throws XMLStreamException {
		writer.writeStartElement(XMLHandler.LINKED_TILE_ELEMENT);
		writer.writeAttribute(XMLHandler.LEVEL_ATTRIBUTE, String.valueOf(0));
		writer.writeEmptyElement(XMLHandler.X_COORD_ELEMENT);
		writer.writeAttribute(XMLHandler.VALUE_ATTRIBUTE, String.valueOf(linkedTile.getCol()));
		writer.writeEmptyElement(XMLHandler.Y_COORD_ELEMENT);
		writer.writeAttribute(XMLHandler.VALUE_ATTRIBUTE, String.valueOf(linkedTile.getRow()));
		writer.writeEndElement();
	}

	private void storeCharacter(Character occupant) {
		if(occupant == null){
			return;
		} else if(occupant instanceof Player){
			players.add((Player) occupant);
		} else if(occupant instanceof Guard){
			guards.add((Guard) occupant);
		} else if(occupant instanceof Sculpture){
			sculptures.add((Sculpture) occupant);
		}
	}

	private void writeEmptyTile(Tile tile, int r, int c, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement(XMLHandler.EMPTY_TILE_ELEMENT);
		if(tile instanceof ExitTile){
			writer.writeAttribute(XMLHandler.EXIT_ATTRIBUTE, XMLHandler.TRUE_VALUE);
		}
		String coordType = XMLHandler.POSITION_ELEMENT;
		writeCoordinate(r, c, writer, coordType);
		writeWalls(tile, writer);
		storeCharacter(tile.getOccupant());//collect characters to write for later
		writer.writeEndElement();
	}

	private void writeWalls(Tile tile, XMLStreamWriter writer)
			throws XMLStreamException {
		for(Direction dir : Direction.values()){
			Wall wall = tile.getWall(dir);
			if(wall != null){
				writeWall(wall, dir, writer);
			}
		}
	}

	private void writeWall(Wall wall, Direction dir, XMLStreamWriter writer) throws XMLStreamException {
		if(wall instanceof Door){
			writeDoor((Door) wall, dir, writer);
		} else {
			writer.writeEmptyElement(XMLHandler.WALL_ELEMENT);
			writer.writeAttribute(XMLHandler.DIRECTION_ATTRIBUTE, dir.toString());
			Art wallArt = wall.getArt();
			if(wallArt != null){
				paintings.add(wallArt);//collect art to write for later
				writer.writeAttribute(XMLHandler.ART_ID_ATTRIBUTE, String.valueOf(wallArt.ID));
			}
		}
	}

	private void writeDoor(Door door, Direction dir, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeEmptyElement(XMLHandler.DOOR_ELEMENT);
		int doorID;
		if(doors.contains(door)){
			doorID = doors.indexOf(door);
		} else {
			doorID = doors.size();
			doors.add(door);
		}
		writer.writeAttribute(XMLHandler.DOOR_ID_ATTRIBUTE, String.valueOf(doorID));
		writer.writeAttribute(XMLHandler.DIRECTION_ATTRIBUTE, dir.toString());
		String lockedValue = door.passable() ? XMLHandler.FALSE_VALUE : XMLHandler.TRUE_VALUE;
		writer.writeAttribute(XMLHandler.LOCKED_ATTRIBUTE, lockedValue);
		if(door.getKeyID() != -1){
			writer.writeAttribute(XMLHandler.KEY_ID_ATTRIBUTE, String.valueOf(door.getKeyID()));
		}
	}

	private void writeCoordinate(int r, int c, XMLStreamWriter writer,
			String coordType) throws XMLStreamException {
		writer.writeStartElement(coordType);
		writer.writeEmptyElement(XMLHandler.X_COORD_ELEMENT);
		writer.writeAttribute(XMLHandler.VALUE_ATTRIBUTE, String.valueOf(c));
		writer.writeEmptyElement(XMLHandler.Y_COORD_ELEMENT);
		writer.writeAttribute(XMLHandler.VALUE_ATTRIBUTE, String.valueOf(r));
		writer.writeEndElement();
	}

}
