package artGame.ui.gamedata;

import artGame.game.Art;
import artGame.game.Character.Direction;
import artGame.game.Tile;
import artGame.game.Wall;

public class ArtItem extends Art {

	private Tile location;
	private Wall wall;
	private Direction dir;
	private int row, col;

	public ArtItem(String name,int value,int ID, Tile location, Wall wall, Direction dir) {
		super(name, value, ID);
		this.location=location;
		this.wall=wall;
		this.dir=dir;
	}

	public void setWorldLocation(int row, int col){
		this.row=row;
		this.col=col;
	}
	
	public Wall getWall(){
		return wall;
	}

	public Tile getLocation(){
		return location;
	}

	public Direction getDirection(){
		return dir;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

}
