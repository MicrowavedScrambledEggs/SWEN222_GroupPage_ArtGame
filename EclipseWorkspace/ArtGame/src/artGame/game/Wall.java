package artGame.game;

public class Wall extends Tile {

	@Override
	public boolean walkable() {
		return false;
	}
	
	public String toString(){
		return "W";
	}
}
