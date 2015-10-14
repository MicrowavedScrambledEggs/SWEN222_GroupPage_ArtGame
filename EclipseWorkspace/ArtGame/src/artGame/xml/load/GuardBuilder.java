package artGame.xml.load;

import artGame.game.Guard;
import artGame.xml.XMLHandler;

/**
 * Build strategy for building Guards. In addition to the inherited fields from CharacterBuilder,
 * also stores a Patrol object, used for building the list of coordinates for the guards patrol
 * path when building the guard and adding it to the game Maker
 *
 * @author Badi James 300156502
 *
 */
public class GuardBuilder extends CharacterBuilder {

	private Patrol patrol;
	private int level;

	public GuardBuilder(GameMaker gameMaker, int id) {
		super(gameMaker, id);
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		super.addField(name, values);
		//Adding the field for the level the guard is on
		if(name.equals(XMLHandler.LEVEL_ATTRIBUTE)){
			if(values[0] instanceof String){
				String lev = (String) values[0];
				level = Integer.parseInt(lev);
			} else {
				throw new IllegalArgumentException(String.format("Error when building guard: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						"Level Integer"));
			}
		}
		//Adding the field for the Patrol
		if(name.equals(XMLHandler.PATROL_ELEMENT)){
			if(values[0] instanceof Patrol){
				this.patrol = (Patrol) values[0];
			} else {
				throw new IllegalArgumentException(String.format("Error when building guard: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						Patrol.class.getName()));
			}
		}
	}

	@Override
	/**
	 * Builds the guard character from the direction and id fields, and the patrol's list of
	 * coordinate if the guard has a patrol
	 *
	 * Adds the guard to the game maker, with inventory references
	 */
	public void addToGame() {
		Guard guard = null;
		if(patrol != null){
			guard = new Guard(super.getDirection(), super.getiD(), patrol.getSteps());
		} else {
			guard = new Guard(super.getDirection(), super.getiD());
		}
		guard.setRow(super.getCoord().getRow());
		guard.setCol(super.getCoord().getCol());
		getGameMaker().addNPC(level, guard);
		getGameMaker().addCharacterArtRefs(guard, getArtRefs());
		getGameMaker().addCharacterKeyRefs(guard, getKeyRefs());
	}

}
