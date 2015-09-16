package artGame.game;

public class ExitTile extends Tile{

	@Override
	public boolean walkable() {
		return true;
	}

	public String toString(){
		return "E";
	}
}
