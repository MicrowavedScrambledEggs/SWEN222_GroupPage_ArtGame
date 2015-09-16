package artGame.game;

public class Door extends Wall {
	private boolean locked;
	private int keyID;
	public Door(boolean locked, int keyID) {
		this.locked = locked;
		this.keyID = keyID;
	}
	
	public boolean unlock(int key){
		if(key==this.keyID){
			this.locked = false;
			return true;
		}
		else return false;
	}
	
	public void unlock(Player p){
		for(Item i:p.getInventory()){
			if(i instanceof Key){
				if(((Key)i).ID == this.keyID){
					this.locked = false;
				}
			}
		}
	}
	
	@Override
	public boolean passable(){
		return !locked;
	}
}
