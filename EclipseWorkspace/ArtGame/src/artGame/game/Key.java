package artGame.game;

public class Key extends Item {
	public final int ID;
	public Key(int id){
		this.ID = id;
	}
	public String toString(){
		return "Key:"+ID;
	}
}
