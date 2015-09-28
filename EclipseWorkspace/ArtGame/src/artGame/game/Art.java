package artGame.game;
//TODO add ids and stuff to this - kai
public class Art extends Item{
	public final String name;
	public final int value;
	public Art(String name,int value,int ID) {
		super(ID);
		this.name = name;
		this.value = value;
	}

}
