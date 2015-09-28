package artGame.game;

import artGame.game.Character.Direction;

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
			else if(occupant instanceof Sculpture) return "$";
			else return "C";
		}
		else {
			return getWallString();
		}
	}


	private String getWallString() {
		Wall northWall = super.getWall(Direction.NORTH);
		Wall westWall = super.getWall(Direction.WEST);
		Wall southWall = super.getWall(Direction.SOUTH);
		Wall eastWall = super.getWall(Direction.EAST);
		if(northWall == null && westWall == null && southWall == null && eastWall == null){
			return ".";
		} else if(northWall != null && westWall == null && southWall == null && eastWall == null){
			return "-";
		} else if(northWall != null && westWall != null && southWall == null && eastWall == null){
			return "F";
		} else if(northWall == null && westWall != null && southWall == null && eastWall == null){
			return "b";
		} else if(northWall == null && westWall != null && southWall != null && eastWall == null){
			return "L";
		} else if(northWall != null && westWall != null && southWall != null && eastWall == null){
			return "[";
		} else if(northWall == null && westWall != null && southWall != null && eastWall != null){
			return "U";
		} else if(northWall == null && westWall == null && southWall != null && eastWall == null){
			return "_";
		} else if(northWall == null && westWall == null && southWall != null && eastWall != null){
			return "j";
		} else if(northWall == null && westWall == null && southWall == null && eastWall != null){
			return "d";
		} else if(northWall != null && westWall == null && southWall != null && eastWall != null){
			return "]";
		} else if(northWall != null && westWall == null && southWall == null && eastWall != null){
			return "7";
		} else if(northWall != null && westWall == null && southWall != null && eastWall == null){
			return "I";
		} else if(northWall == null && westWall != null && southWall == null && eastWall != null){
			return "H";
		} else if(northWall != null && westWall != null && southWall == null && eastWall != null){
			return "^";
		} else {
			return "O";
		}
	}
}

