package artGame.game;

import artGame.game.Character.Direction;

/**
 * for sake of testing this is the game floor: 
 * WWWWWWWWWWW 
 * WA   W   AW
 * WCP  D    E
 * WA   W   GW 
 * WWWWWWWWWWW
 * 
 * @author Kaishuo
 *
 */
public class Floor {
	private int row = 4;
	private int col = 10;
	private Tile[][] floor;
	private ExitTile exit;
	/*
	 * generating the floor. this is poorly done and
	 * should be replaced by a proper parser
	 */
	public Floor(){
		// initial sweep
		floor = new Tile[5][11];
		for(int i=0;i<11;i++){
			floor[0][i] = new Wall();
			floor[1][i] = new EmptyTile();
			floor[2][i] = new EmptyTile();
			floor[3][i] = new EmptyTile();
			floor[4][i] = new Wall();
		}
		
		//cleanup
		//setting walls
		floor[1][0] = new Wall();
		floor[2][0] = new Wall();
		floor[3][0] = new Wall();
		floor[1][5] = new Wall();
		floor[3][5] = new Wall();
		floor[1][10] = new Wall();
		floor[3][10] = new Wall();
		//setting exit tile
		floor[2][10] = new ExitTile();
		exit = (ExitTile) floor[2][10];
		//setting art
		((EmptyTile)floor[1][1]).setArt(new Art("Art1"));
		((EmptyTile)floor[3][1]).setArt(new Art("Art2"));
		((EmptyTile)floor[1][9]).setArt(new Art("Art3"));
		//setting chest
		floor[2][1] = new Chest(new Key(1));
		//setting door
		floor[2][5] = new Door(true,1);
		//setting guard
		Guard guard = new Guard(Character.Direction.WEST);
		floor[3][9].setOccupant(guard);
	}
	
	public Tile getTile(int row,int col){
		return floor[row][col];
	}
	
	public void addCharacter(Character c,int row, int col){
		floor[row][col].setOccupant(c);
		c.setRow(row);
		c.setCol(col);
	}
	
	public Character isOnExit(){
		return exit.getOccupant();
	}
	
	/**
	 * prints the floor
	 */
	public void printFloor(){
		for(int i=0;i<5;i++){
			for(int j=0;j<11;j++){
				System.out.print(floor[i][j]);
			}
			System.out.println("");
		}
	}
	
	/**
	 * moves a character(does nothing if invalid move)
	 */
	public void moveCharacter(Character c,int oldRow,int oldCol,int newRow,int newCol){
		if(getTile(newRow,newCol).walkable()){
			c.setRow(newRow);
			c.setCol(newCol);
			floor[newRow][newCol].setOccupant(c);
			floor[oldRow][oldCol].setOccupant(null);			
		}
	}
	
	/**
	 * returns the tile that the given character is currently facing
	 */
	public Tile tileCharacterFacing(Character c){
		if(c.getDir()==Direction.NORTH){
			return floor[c.getRow()-1][c.getCol()];
		}
		else if(c.getDir()==Direction.WEST){
			return floor[c.getRow()][c.getCol()-1];
		}
		else if(c.getDir()==Direction.SOUTH){
			return floor[c.getRow()+1][c.getCol()];
		}
		else if(c.getDir()==Direction.EAST){
			return floor[c.getRow()][c.getCol()+1];
		}
		else return null;//shouldnt reach this
	}

	public void interact(Player p) {
		if(floor[p.getRow()][p.getCol()] instanceof EmptyTile){
			EmptyTile currentTile = ((EmptyTile)floor[p.getRow()][p.getCol()]);
			if(currentTile.hasArt()){
				currentTile.stealFrom(p);
				return;
			}
		}
		if(tileCharacterFacing(p) instanceof Chest){
			Chest chest = (Chest)tileCharacterFacing(p);
			chest.takeItem(p);
		}		
		else if(tileCharacterFacing(p) instanceof Door){
			Door door = (Door)tileCharacterFacing(p);
			door.unlock(p);
		}
		
	}
	
}
