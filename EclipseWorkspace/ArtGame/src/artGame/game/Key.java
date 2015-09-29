package artGame.game;

public class Key extends Item {
	public final int keyID;
	public Key(int keyID,int ID){
		super(ID);
		this.keyID = keyID;
	}
	public String toString(){
		return "Key:"+keyID;
	}
}
