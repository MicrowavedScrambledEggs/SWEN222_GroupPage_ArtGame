package artGame.ui.gamedata;

import artGame.game.Wall;

public class WallData {

	private int row, col;
	private Wall wall;

	public WallData(Wall w, int row, int col){
		this.wall=w;
		this.row=row;
		this.col=col;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + ((wall == null) ? 0 : wall.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WallData other = (WallData) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		if (wall == null) {
			if (other.wall != null)
				return false;
		} else if (!wall.equals(other.wall))
			return false;
		return true;
	}


}
