package artGame.game;

/**
 * this is a sculpture that is in the game floor
 * as opposed to one in a players inventory
 * @author Kaishuo
 *
 */
public class Sculpture extends Character{
	private int value;
	private String name;
	public Sculpture(Direction dir,int ID, int value,String name) {
		super(dir, ID);
		this.value = value;
		this.name = name;
	}

	/**
	 * converts a sculpture on the floor to a item in bag
	 * aka when it has been picked up by a player
	 * @return
	 */
	public Art toItem(){
		return new Art(name,value);
	}
	
}
