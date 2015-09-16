package artGame.game;

public abstract class Character {
	public enum Direction{NORTH,SOUTH,EAST,WEST};
	int row;
	int col;
	private Direction dir;
	
	public Character(Direction dir) {
		this.dir = dir;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public Direction getDir() {
		return dir;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}
	
	
}
