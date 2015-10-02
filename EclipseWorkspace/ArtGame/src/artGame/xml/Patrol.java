package artGame.xml;

import java.util.ArrayList;
import java.util.LinkedList;

import artGame.game.Coordinate;

public class Patrol implements Stretch {

	private LinkedList<Stretch> stretches = new LinkedList<Stretch>();

	@Override
	public void addFeild(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		if(name.equals(XMLReader.X_PATH_ELEMENT) || name.equals(XMLReader.Y_PATH_ELEMENT)){
			if(value instanceof Stretch){
				stretches.add((Stretch) value);
			} else {
				throw new IllegalArgumentException(String.format("Error when building patrol: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(),
						Coordinate.class.getName()));
			}
		}

	}

	@Override
	public <T> T buildObject() {
		// TODO Auto-generated method stub
		return null;
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
