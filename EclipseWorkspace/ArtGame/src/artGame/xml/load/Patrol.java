package artGame.xml.load;

import java.util.ArrayList;
import java.util.LinkedList;

import artGame.game.Coordinate;
import artGame.xml.XMLHandler;

/**
 * Build strategy for guard patrol paths. Collects stretches defining coordinates of the
 * guards patrol path
 *
 * @author Badi James 300156502
 *
 */
public class Patrol implements Stretch {

	//Compozit dezine pattun right here give me mega marks
	private LinkedList<Stretch> stretches = new LinkedList<Stretch>();

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.X_PATH_ELEMENT) || name.equals(XMLHandler.Y_PATH_ELEMENT)
				|| name.equals(XMLHandler.PATROL_STEP_ELEMENT)){
			//only stores stretches
			if(values[0] instanceof Stretch){
				stretches.add((Stretch) values[0]);
			} else {
				throw new IllegalArgumentException(String.format("Error when building patrol: "
						+ "Tried to add %s when %s was needed", values[0].getClass().getName(),
						Stretch.class.getName()));
			}
		}
	}

	@Override
	public void addToGame() {
		// Does Nothing!!
	}

	@Override
	/**
	 * Iterates through all the stored stretches and calls getSteps() to get their coordinates
	 * Puts all the coordinates in the list to return for use in a Guard constructor
	 */
	public ArrayList<Coordinate> getSteps() {
		ArrayList<Coordinate> steps = new ArrayList<Coordinate>();
		for(Stretch s : stretches){
			steps.addAll(s.getSteps());
		}
		return steps;
	}

}
