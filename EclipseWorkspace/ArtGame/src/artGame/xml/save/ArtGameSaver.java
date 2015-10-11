package artGame.xml.save;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import artGame.game.*;
import artGame.game.Character;
import artGame.game.Character.Direction;
import artGame.main.Game;
import artGame.xml.XMLHandler;

public class ArtGameSaver {
	
	private ArrayList<Door> doors;
	private ArrayList<Art> paintings;
	private ArrayList<Player> players;
	private ArrayList<Guard> guards;
	private ArrayList<Sculpture> sculptures;

	public void saveGame(Game game, String fileName) {
		this.doors = new ArrayList<Door>();
		this.paintings = new ArrayList<Art>();
		this.players = new ArrayList<Player>();
		this.guards = new ArrayList<Guard>();
		this.sculptures = new ArrayList<Sculpture>();
		FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(fileName);
	        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
	        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos);
	        writer.writeStartDocument();
	        writer.writeStartElement("game");
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
		writer.writeStartElement(XMLHandler.GUARDS_ELEMENT);
		for(Guard guard : guards){
			writer.writeStartElement(XMLHandler.GUARD_ELEMENT);
			writer.writeAttribute(XMLHandler.ID_ATTRIBUTE, String.valueOf(guard.getId()));
			writer.writeStartElement(XMLHandler.DIRECTION_ELEMENT);
			writer.writeCharacters(guard.getDir().toString());
			writer.writeEndElement();
			writeCoordinate(guard.getRow(), guard.getCol(), writer, XMLHandler.POSITION_ELEMENT);
			writeCharacterInventory(guard.getInventory(), writer);
			//TODO: write patrol path. Might have to add 'step' element
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
		writer.writeEndElement();
	}

	private void writeTile(Tile tile, int r, int c, XMLStreamWriter writer) throws XMLStreamException {
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
		//TODO: linked tile's coordinate. Talk to Kai
		writeWalls(stairs, writer);
		storeCharacter(stairs.getOccupant());
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
		storeCharacter(tile.getOccupant());
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
				paintings.add(wallArt);
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
