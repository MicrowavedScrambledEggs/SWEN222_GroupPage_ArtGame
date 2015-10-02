package artGame.xml;

import java.util.ArrayList;

import artGame.game.Coordinate;

public class WestEastStretch implements Stretch{

	private Coordinate start;
	private Coordinate finish;

	@Override
	public void addFeild(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFeild(String name, Object value)
			throws IllegalArgumentException {
		if(name.equals(XMLReader.START_ELEMENT)){
			if(value instanceof Coordinate){
				this.start = (Coordinate) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building patrol strech: "
						+ "Tried to add %s when %s was needed", value.getClass().getName(),
						Coordinate.class.getName()));
			}
		}
		if(name.equals(XMLReader.FINISH_ELEMENT)){
			if(value instanceof Coordinate){
				this.start = (Coordinate) value;
			} else {
				throw new IllegalArgumentException(String.format("Error when building patrol strech: "
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
		int xStart = start.getX();
		int xFinish = finish.getX();
		int y = start.getY();
		int xIncrement = xStart < xFinish ? 1 : -1;
		ArrayList<Coordinate> steps = new ArrayList<Coordinate>();
		for(; xStart != xFinish + xIncrement; xStart+=xIncrement){
			steps.add(new Coordinate(xStart, y));
		}
		return steps;
	}


}
