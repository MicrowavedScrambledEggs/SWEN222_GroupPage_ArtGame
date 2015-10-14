package artGame.xml.load;

/**
 * Interface for build strategies. Sub classes handle data parsed from xml files
 * in specific ways based on the game object or parsing object they are a build strategy for
 *
 * @author Badi James 300156502
 *
 */
public interface BuildStrategy {

	/**
	 * Adds a the given field values for the field defined by the given name to the
	 * build strategy
	 * @param name Name of field to add values to
	 * @param values Values of field to add
	 * @throws IllegalArgumentException Usually when the values' Classes don't match the type
	 * for the given name
	 */
	public void addField(String name, Object... values) throws IllegalArgumentException;

	/**
	 * Gets the build strategy to add its data, object etc to the strategy's game maker
	 * if it has one.
	 * @throws LoadError if called before all required fields have been added
	 */
	public void addToGame();

}
