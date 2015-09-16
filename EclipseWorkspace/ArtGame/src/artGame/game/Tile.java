package artGame.game;

public abstract class Tile {
	public abstract boolean walkable();
	protected Character occupant;
	public Character getOccupant() {
		return occupant;
	}
	public void setOccupant(Character occupant) {
		if(walkable()){
		this.occupant = occupant;
		}
		else throw new GameError("Trying to move character to invalid tile");
	}
	
}
