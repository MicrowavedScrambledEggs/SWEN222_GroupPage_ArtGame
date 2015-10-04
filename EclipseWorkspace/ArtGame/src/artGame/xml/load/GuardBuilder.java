package artGame.xml.load;

import artGame.game.Guard;
import artGame.xml.XMLHandler;

public class GuardBuilder extends CharacterBuilder {
	
	private Patrol patrol;
	
	public GuardBuilder(GameMaker gameMaker, int id) {
		super(gameMaker, id);
	}
	
	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		super.addField(name, values);
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
		Guard guard = new Guard(super.getDirection(), super.getiD(), patrol.getSteps());
		guard.setRow(super.getCoord().getY());
		guard.setCol(super.getCoord().getX());
		getGameMaker().addNPC(guard);
		getGameMaker().addCharacterArtRefs(guard, getArtRefs());
		getGameMaker().addCharacterKeyRefs(guard, getKeyRefs());
	}

}
