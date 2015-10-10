package artGame.game;

@SuppressWarnings("serial")
/**
 * Game error thrown when something goes wrong
 * @author Kaishuo
 *
 */
public class GameError extends RuntimeException {
	public GameError(String msg){
		super(msg);
    }
}
