package artGame.game;

import java.util.ArrayList;
import java.util.List;

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
	private List<Guard> guards;
	/*
	 * generating the floor. this is poorly done and
	 * should be replaced by a proper parser
	 */
	public Floor(){
		// initial sweep
		guards = new ArrayList<Guard>();
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
		((EmptyTile)floor[1][1]).setArt(new Art("Art1",3000));
		((EmptyTile)floor[3][1]).setArt(new Art("Art2",1000));
		((EmptyTile)floor[1][9]).setArt(new Art("Art3",10000));
		//setting chest
		floor[2][1] = new Chest(new Key(1));
		//setting door
		floor[2][5] = new Door(true,1);
		//setting guard
		Guard guard = new Guard(Character.Direction.WEST);
		addCharacter(guard,3,9);
		guards.add(guard);
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

	/**
	 * has the player interact with whatever is in front of
	 * or on their tile
	 * priority is art>chest>door
	 */
	public void interact(Player p) {
		//checking if tile has art first
		if(floor[p.getRow()][p.getCol()] instanceof EmptyTile){
			EmptyTile currentTile = ((EmptyTile)floor[p.getRow()][p.getCol()]);
			if(currentTile.hasArt()){
				currentTile.stealFrom(p);
				return;
			}
		}
		//dealing with chests
		if(tileCharacterFacing(p) instanceof Chest){
			Chest chest = (Chest)tileCharacterFacing(p);
			chest.takeItem(p);
		}		
		//dealing with door
		else if(tileCharacterFacing(p) instanceof Door){
			Door door = (Door)tileCharacterFacing(p);
			door.unlock(p);
		}
		//otherwise no action should be taken
	}

	/**
	 * checks if a specific guard can see a player. if not,
	 * returns a null
	 */
	private Player checkGuard(Guard g){
		Direction dir = g.getDir();
		int cOff = 0; //col offset
		int rOff = 0; //row offset
		//first determine offsets
		if(dir==Direction.NORTH){
			rOff = -1;
		}
		else if(dir==Direction.WEST){
			cOff = -1;
		}
		else if(dir==Direction.SOUTH){
			rOff = 1;
		}
		else if(dir==Direction.WEST){
			cOff = 1;
		}
		//cycle through offset tiles and check for players
		//using distance = 3, straight line
		for(int i=1;i<4;i++){
			//target coords. works because only one of
			//row or col offset will be nonzero
			int tarRow = g.getRow() + i*rOff;
			int tarCol = g.getCol() + i*cOff;
			if(floor[tarRow][tarCol].getOccupant() instanceof Player){
				return (Player) floor[tarRow][tarCol].getOccupant();
			}
		}
		return null;
	}
	/**
	 * returns true if guards see someone, else false
	 */
	public boolean checkGuards() {
		for(Guard g:guards){
			//right now only checks for a single player
			//will be extended to collection in final version
			Player caught = checkGuard(g);
			if(caught!=null){
				return true;
			}
		}
		return false;
	}
	
}
