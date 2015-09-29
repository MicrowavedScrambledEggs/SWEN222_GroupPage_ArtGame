package artGame.xml;

import artGame.game.Coordinate;
import artGame.game.Character.Direction;

public class GuardBuilder extends CharacterBuilder {
	
	private Patrol patrol;
	
	public GuardBuilder(int iD){
		super(iD);
	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T buildObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
