package artGame.xml.load;

/**
 * Class to hold data from elements in XML file being parsed. Takes a 
 * build strategy specific to the object it is to build.
 * 
 * Essentially a wrapper for build strategy to avoid ugly type casting
 * 
 * @author Badi James
 *
 */
public class ObjectBuilder {
	
	private BuildStrategy buildStrat;
	
	public ObjectBuilder(BuildStrategy bs){
		buildStrat = bs;
	}
	
	/**
	 * Adds a the given field values for the field defined by the given name to the
	 * build strategy
	 * @param name Name of field to add values to
	 * @param values Values of field to add
	 * @throws IllegalArgumentException Usually when the values Class don't match the type
	 * for the given name 
	 */
	public void addField(String name, Object... values) throws IllegalArgumentException{
		buildStrat.addField(name, values);
	}
	
	/**
	 * Gets the build strategy to add its data, object etc to the strategy's game maker
	 */
	public void addToGame(){
		buildStrat.addToGame();
	}
	
	/**
	 * @return the Build Strategy
	 */
	public BuildStrategy getBuildStrategy(){
		return buildStrat;
	}
	
}
