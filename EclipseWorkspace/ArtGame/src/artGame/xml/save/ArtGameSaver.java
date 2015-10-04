package artGame.xml.save;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import artGame.game.Floor;
import artGame.main.Game;
import artGame.xml.XMLHandler;

public class ArtGameSaver {

	public void saveGame(Game game, String fileName) {
		FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(fileName);
	        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
	        XMLStreamWriter writer = xmlOutFact.createXMLStreamWriter(fos);
	        writer.writeStartDocument();
	        writer.writeStartElement("game");
	        writeFloor(game, writer);
	        writer.writeEndElement();
	        writer.flush();
	    }
	    catch(IOException exc) {
	    }
	    catch(XMLStreamException exc) {
	    }
	    finally {
	    }
	}

	private void writeFloor(Game game, XMLStreamWriter writer) throws XMLStreamException {
		Floor gameFloor = game.getFloor();
		writer.writeStartElement(XMLHandler.FLOOR_ELEMENT);
		//TODO : add floor number once multiple levels have been implemented in game logic
		//TODO : write tiles
		writer.writeEndElement();
	}

}
