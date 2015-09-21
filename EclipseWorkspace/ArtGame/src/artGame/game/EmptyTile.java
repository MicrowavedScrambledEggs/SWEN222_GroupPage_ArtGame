package artGame.game;

public class EmptyTile extends Tile{
	public EmptyTile(boolean nwall, boolean wwall, boolean swall, boolean ewall) {
		super(nwall, wwall, swall, ewall);
	}

	
	@Override
	public boolean walkable() {
		return occupant==null;
	}
	
	public String toString(){
		if(occupant!=null){
			if(occupant instanceof Player) return "P";
			else if(occupant instanceof Guard) return "G";
			else return "C";
		}
		else return "X";
	}
}
