package artGame.game;

/**
 * Represents a key used to open locked doors
 * @author Kaishuo Yang 300335418
 *
 */
public class Key extends Item {
	public final int keyID;
	public Key(int keyID,int ID){
		super(ID);
		this.keyID = keyID;
		this.description = "A key. It's ID is "+keyID;
	}
	
	@Override
	public String toString(){
		return "Key:"+keyID;
	}
}