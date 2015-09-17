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

public class XMLReader {


	public XMLReader(File xmlFile){
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setValidating(true);
	    try {
	        SAXParser saxParser = factory.newSAXParser();
	        File file = new File("test.xml");
	        DefaultHandler xmlHandler = new DefaultHandler();
	        saxParser.parse(file, xmlHandler);
	    }
	    catch(ParserConfigurationException e1) {
	    }
	    catch(SAXException e1) {
	    }
	    catch(IOException e) {
	    }

	}

}
