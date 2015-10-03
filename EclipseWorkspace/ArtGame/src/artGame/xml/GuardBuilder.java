package artGame.xml;

import artGame.game.Coordinate;
import artGame.game.Character.Direction;
import artGame.game.Guard;

public class GuardBuilder extends CharacterBuilder {

	private Patrol patrol;

	public GuardBuilder(int iD){
		super(iD);
	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		super.addFeild(name, value);
		if(name.equals(XMLReader.PATROL_ELEMENT)){
			if(value instanceof Patrol){
				this.patrol = (Patrol) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building guard: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(),
						Coordinate.class.getName()));
			}
		}
	}

	@Override
	public Guard buildObject() {
		Guard guard = new Guard(super.getDirection(), super.getID(), patrol.getSteps());
		guard.setRow(super.getCoord().getY());
		guard.setCol(super.getCoord().getX());
		return guard;
	}

}
