package artGame.xml.load;

import artGame.game.Sculpture;
import artGame.xml.XMLHandler;

public class SculptureBuilder extends CharacterBuilder {
	
	private String artName;
	private int value;
	
	public SculptureBuilder(GameMaker gameMaker, int id) {
		super(gameMaker, id);
	}
	
	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		super.addField(name, values);
		if(name.equals(XMLHandler.NAME_ELEMENT)){
			this.artName = (String) values[0];
		} else if (name.equals(XMLHandler.VALUE_ELEMENT)){
			this.value = Integer.parseInt((String)values[0]);
		}
	}
	
	@Override
	public void addToGame() {
		Sculpture sculpture = new Sculpture(super.getDirection(), super.getiD(), value, artName);
		sculpture.setCol(super.getCoord().getX());
		sculpture.setRow(super.getCoord().getY());
		getGameMaker().addNPC(sculpture);
	}

}
