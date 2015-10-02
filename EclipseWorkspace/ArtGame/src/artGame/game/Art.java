package artGame.game;
/**
 * a piece of art in the players inventory
 */
public class Art extends Item{
	public final String name;
	public final int value;
	public Art(String name,int value,int ID) {
		super(ID);
		this.name = name;
		this.value = value;
	}

}
