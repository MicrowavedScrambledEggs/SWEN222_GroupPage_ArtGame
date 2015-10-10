package artGame.game;

/**
 * The exit where the player can flee to safety
 * @author Kaishuo
 *
 */
public class ExitTile extends Tile{

	public ExitTile(boolean nwall, boolean wwall, boolean swall, boolean ewall) {
		super(nwall, wwall, swall, ewall);
	}

	@Override
	public boolean walkable() {
		return true;
	}

	public String toString(){
		return "E";
	}
}
