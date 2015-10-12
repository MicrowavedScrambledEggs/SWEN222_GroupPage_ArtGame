package artGame.xml.load;

import java.util.ArrayList;

import artGame.game.Coordinate;

public class PatrolStep extends CoordinateBuilder implements Stretch {

	public PatrolStep() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToGame() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Coordinate> getSteps() {
		ArrayList<Coordinate> toReturn = new ArrayList<Coordinate>();
		toReturn.add(buildCoordinate());
		return toReturn;
	}

}
