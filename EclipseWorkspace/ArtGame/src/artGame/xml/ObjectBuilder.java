package artGame.xml;

/**
 * Interface for ObjectBuilder. Subclasses used to receive data from xml Parser
 * and then use data to create a artGame.game object
 *
 * @author Badi James
 *
 */
public interface ObjectBuilder {

	/**
	 * Adds data for a field to builder. Ensures name matches a variable of the object
	 * to build. If type of variable not a string, parses variable value from given value
	 * string
	 *
	 * @param name Name/Type of variable
	 * @param value Variable's value
	 */
	public void addFeild(String name, String value);

	/**
	 * Adds data for a field to builder. Ensures name matches a variable of the object
	 * to build, and class of value matches the variables type
	 *
	 * @param name Name/Type of variable
	 * @param value Variable's value
	 * @throws IllegalArgumentException If value's class does not match variable type
	 */
	public void addFeild(String name, Object value) throws IllegalArgumentException;

	/**
	 * Builds the artGame.game object
	 * @return built object
	 */
	public <T> T buildObject();
}
