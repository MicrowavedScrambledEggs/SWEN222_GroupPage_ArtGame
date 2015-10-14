package artGame.xml.load;

/**
 * Error for problems parsing xml file for a Game and building game from
 * that file
 *
 * @author Badi James 300156502
 *
 */
public class LoadError extends RuntimeException {

	public LoadError(String message) {
		super(message);
	}

}
