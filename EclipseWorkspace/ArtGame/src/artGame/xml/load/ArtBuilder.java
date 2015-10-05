package artGame.xml.load;

import artGame.game.Art;
import artGame.xml.XMLHandler;

public class ArtBuilder implements BuildStrategy {
	
	private String artName;
	private int value;
	private int artID;
	private GameMaker gameMaker;
	
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
	public void addToGame() {
		gameMaker.addPainting(new Art(artName, value, artID));
	}

}
