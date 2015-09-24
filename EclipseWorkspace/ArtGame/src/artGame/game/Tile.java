package artGame.game;

import artGame.game.Character.Direction;

public abstract class Tile {
	protected Wall[] walls;
	protected Character occupant;
	

	public abstract boolean walkable();
	
	public Tile(boolean nwall,boolean wwall,boolean swall,boolean ewall){
		walls = new Wall[4];
		if(nwall){this.walls[0] = new Wall();}
		if(wwall){this.walls[1] = new Wall();}
		if(swall){this.walls[2] = new Wall();}
		if(ewall){this.walls[3] = new Wall();}
	}
	
	public Character getOccupant() {
		return occupant;
	}
	public void setOccupant(Character occupant) {
			this.occupant = occupant;
	}
	
	public void setWall(Direction dir,Wall wall){
		if(dir==Direction.NORTH){
			walls[0] = wall;
		}
		else if(dir==Direction.WEST){
			walls[1] = wall;
		}
		else if(dir==Direction.SOUTH){
			walls[2] = wall;
		}
		else if(dir==Direction.EAST){
			walls[3] = wall;
		}
	}	
	
	public Wall getWall(Direction dir){
		if(dir==Direction.NORTH){
			return walls[0];
		}
		else if(dir==Direction.WEST){
			return walls[1];
		}
		else if(dir==Direction.SOUTH){
			return walls[2];
		}
		else if(dir==Direction.EAST){
			return walls[3];
		}
		else return null;
	}
}
