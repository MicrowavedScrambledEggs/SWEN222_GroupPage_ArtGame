package artGame.xml.load;

import artGame.game.Sculpture;
import artGame.xml.XMLHandler;

/**
 * Build strategy for building sculpture objects. In addition to the usual
 * character fields, it also stores fields relating to art objects
 *
 * @author Badi James 300156502
 *
 */
public class SculptureBuilder extends CharacterBuilder {

	private String artName;
	private int value;
	private int level;

	public SculptureBuilder(GameMaker gameMaker, int id) {
		super(gameMaker, id);
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		super.addField(name, values);
		if(name.equals(XMLHandler.LEVEL_ATTRIBUTE)){
			if(values[0] instanceof String){
				String lev = (String) values[0];
				level = Integer.parseInt(lev);
			} else {
				throw new IllegalArgumentException(String.format("Error when building sculpture: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						"Level Integer"));
			}
		}
		if(name.equals(XMLHandler.NAME_ELEMENT)){
			this.artName = (String) values[0];
		} else if (name.equals(XMLHandler.VALUE_ELEMENT)){
			this.value = Integer.parseInt((String)values[0]);
		}
	}

	@Override
	/**
	 * Builds sculpture and adds it to the game maker
	 */
	public void addToGame() {
		Sculpture sculpture = new Sculpture(super.getDirection(), super.getiD(), value, artName);
		sculpture.setCol(super.getCoord().getCol());
		sculpture.setRow(super.getCoord().getRow());
		getGameMaker().addNPC(level, sculpture);
	}

}
