package artGame.xml.load;

import java.util.ArrayList;

import artGame.game.Coordinate;

/**
 * Build strategy for a single step in a guard's patrol path.
 *
 * @author Badi James 300156502
 *
 */
public class PatrolStep extends CoordinateBuilder implements Stretch {

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		// Does nothing
	}

	@Override
	public void addToGame() {
		// Does nothing
	}

	@Override
	/**
	 * Uses the super class CoordinateBuilder to build a single coordinate to return
 	 * in a ArrayList
 	 * @return collection with just one coordinate
	 */
	public ArrayList<Coordinate> getSteps() {
		ArrayList<Coordinate> toReturn = new ArrayList<Coordinate>();
		toReturn.add(buildCoordinate());
		return toReturn;
	}

}
