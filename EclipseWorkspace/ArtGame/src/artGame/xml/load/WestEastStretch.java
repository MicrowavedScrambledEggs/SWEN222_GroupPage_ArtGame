package artGame.xml.load;

import java.util.ArrayList;

import artGame.game.Coordinate;
import artGame.xml.XMLHandler;

/**
 * Build strategy for building a straight line of coordinates across a row of tiles
 * for use in defining a guard's patrol path
 *
 * @author Badi James 300156502
 *
 */
public class WestEastStretch implements Stretch {

	private Coordinate start;
	private Coordinate finish;

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.START_ELEMENT)){
			if(values[0] instanceof Coordinate){
				this.start = (Coordinate) values[0];
			} else {
				throw new IllegalArgumentException(String.format("Error when building patrol strech: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						Coordinate.class.getName()));
			}
		}
		if(name.equals(XMLHandler.FINISH_ELEMENT)){
			if(values[0] instanceof Coordinate){
				this.finish = (Coordinate) values[0];
			} else {
				throw new IllegalArgumentException(String.format("Error when building patrol strech: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						Coordinate.class.getName()));
			}
		}
	}

	@Override
	public void addToGame() {
		//Does Nothing!!
	}

	@Override
	/**
	 * Builds an ordered arraylist of the coordinates from the start coordinate
	 * to the finish coordinate inclusive.
	 * @return List of coordinates for patrol path
	 */
	public ArrayList<Coordinate> getSteps() {
		int xStart = start.getCol();
		int xFinish = finish.getCol();
		int y = start.getRow();
		int xIncrement = xStart < xFinish ? 1 : -1;
		ArrayList<Coordinate> steps = new ArrayList<Coordinate>();
		for(; xStart != xFinish + xIncrement; xStart+=xIncrement){
			steps.add(new Coordinate(xStart, y));
		}
		return steps;
	}

}
