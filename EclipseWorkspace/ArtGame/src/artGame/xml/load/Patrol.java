package artGame.xml.load;

import java.util.ArrayList;
import java.util.LinkedList;

import artGame.game.Coordinate;
import artGame.xml.XMLHandler;

public class Patrol implements Stretch {
	
	private LinkedList<Stretch> stretches = new LinkedList<Stretch>();

	@Override
	public void addField(String name, Object... values)
			throws IllegalArgumentException {
		if(name.equals(XMLHandler.X_PATH_ELEMENT) || name.equals(XMLHandler.Y_PATH_ELEMENT)
				|| name.equals(XMLHandler.PATROL_STEP_ELEMENT)){
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
	public ArrayList<Coordinate> getSteps() {
		ArrayList<Coordinate> steps = new ArrayList<Coordinate>();
		for(Stretch s : stretches){
			steps.addAll(s.getSteps());
		}
		return steps;
	}

}
