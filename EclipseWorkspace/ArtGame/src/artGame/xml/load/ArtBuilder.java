package artGame.xml.load;

import artGame.game.Art;
import artGame.xml.XMLHandler;

/**
 * Build strategy for storing data related to an art object and building
 * an art object from that data to add to a GameMaker
 *
 * @author Badi James 300156502
 *
 */
public class ArtBuilder implements BuildStrategy {

	private String artName;
	private int value;
	private int artID;
	private GameMaker gameMaker;

	/**
	 * Constructor for class ArtBuilder.
	 *
	 * @param gameMaker Game maker it can add it's built art object to when addToGame() is called
	 * @param artID Id for the art object
	 */
	public ArtBuilder(GameMaker gameMaker, int artID){
		this.artID = artID;
		this.gameMaker = gameMaker;
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.NAME_ELEMENT)){
			this.artName = (String) values[0];
		} else if (name.equals(XMLHandler.VALUE_ELEMENT)){
			this.value = Integer.parseInt((String)values[0]);
		}
	}

	@Override
	/**
	 * Builds an art object from the fields and adds it to the gameMaker's list of
	 * paintings
	 */
	public void addToGame() {
		if(artName == null || value == Integer.MIN_VALUE){
			String missing = "";
			if(artName == null){
				missing += " Name,";
			}
			if(value == Integer.MIN_VALUE){
				missing += " Art's Value,";
			}
			throw new LoadError(String.format("Attempted to build art object id: %d "
					+ "to add to game without adding required feilds first\n"
					+ "Missing: %s", artID, missing));
		}
		gameMaker.addPainting(new Art(artName, value, artID));
	}

}
