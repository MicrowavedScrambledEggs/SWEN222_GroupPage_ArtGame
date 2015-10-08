package artGame.xml.load;

import artGame.game.Guard;
import artGame.xml.XMLHandler;

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
	public void addToGame() {
		Guard guard = null;
		if(patrol != null){
			guard = new Guard(super.getDirection(), super.getiD(), patrol.getSteps());
		} else {
			guard = new Guard(super.getDirection(), super.getiD());
		}
		guard.setRow(super.getCoord().getY());
		guard.setCol(super.getCoord().getX());
		getGameMaker().addNPC(level, guard);
		getGameMaker().addCharacterArtRefs(guard, getArtRefs());
		getGameMaker().addCharacterKeyRefs(guard, getKeyRefs());
	}

}
