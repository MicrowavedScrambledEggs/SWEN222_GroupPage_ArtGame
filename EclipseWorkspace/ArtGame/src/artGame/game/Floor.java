package artGame.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import artGame.game.Character.Direction;

/**
 * game floor TODO 
 * proper docs
 *
 */
public class Floor {
	private int maxR = 3;
	private int maxC = 7;
	private Tile[][] floor;
	private List<ExitTile> exits;
	private List<Guard> guards;

	public Floor(Tile[][] tiles,int maxR,int maxC, Collection<Guard> guards, Collection<ExitTile> exits){
		floor = tiles;
		this.exits = new ArrayList<ExitTile>(exits);
		this.guards = new ArrayList<Guard>();
		this.maxR = maxR;
		this.maxC = maxC;
		for(Guard g:guards){
			setCharacter(g,g.getRow(),g.getCol());
		}
	}
	
	/*
	 * generating the floor. this is poorly done and should be replaced by a
	 * proper parser
	 */
	public Floor() {
		// initial sweep
		guards = new ArrayList<Guard>();
		floor = new Tile[maxR][maxC];
		for (int i = 0; i < maxC; i++) {
			floor[0][i] = new EmptyTile(true, false, false, false);
			floor[1][i] = new EmptyTile(false, false, false, false);
			floor[2][i] = new EmptyTile(false, false, true, false);
		}

		// cleanup
		// setting west walls
		floor[0][0].setWall(Direction.WEST, new Wall(new Art("Art1", 1000)));
		floor[1][0] = new Chest(0,false, true, false, false);
		((Chest) floor[1][0]).setContent(new Key(1));
		floor[1][0].setWall(Direction.WEST, new Wall());
		floor[2][0].setWall(Direction.WEST, new Wall(new Art("Art2", 4000)));
		// east walls
		floor[0][5].setWall(Direction.EAST, new Wall(new Art("Art3", 9000)));
		floor[2][5].setWall(Direction.EAST, new Wall());
		// mid walls
		floor[0][3].setWall(Direction.WEST, new Wall());
		floor[0][2].setWall(Direction.EAST, new Wall());
		floor[2][3].setWall(Direction.WEST, new Wall());
		floor[2][2].setWall(Direction.EAST, new Wall());
		// setting door
		Door door = new Door(true, 1);
		floor[1][2].setWall(Direction.EAST, door);
		floor[1][3].setWall(Direction.WEST, door);
		// setting exit
		floor[1][6] = new ExitTile(true, false, true, true);
		exits = new ArrayList<ExitTile>();
		exits.add((ExitTile) floor[1][6]);
		// setting guard
		Guard guard = new Guard(Character.Direction.WEST,0);
		setCharacter(guard, 2, 5);
		guards.add(guard);
	}

	public Tile getTile(int row, int col) {
		return floor[row][col];
	}

	public void setCharacter(Character c, int row, int col) {
		floor[row][col].setOccupant(c);
		c.setRow(row);
		c.setCol(col);
	}

	public Character isOnExit() {
		for(ExitTile exit : exits){
			if(exit.getOccupant() != null){
				return exit.getOccupant();
			}
		}
		return null;
	}

	/**
	 * prints the floor
	 */
	public void printFloor() {
		for (int i = 0; i < maxR; i++) {
			for (int j = 0; j < maxC; j++) {
				Tile toPrint = floor[i][j];
				if(toPrint == null){
					System.out.print(" ");
				} else {
					System.out.print(toPrint);
				}
			}
			System.out.println("");
		}
	}

	/**
	 * moves a character 1 tile(does nothing if invalid move)
	 */
	public void moveCharacter(Character c) {
		int oldRow = c.getRow();
		int oldCol = c.getCol();
		// can walk there
		if (tileCharacterFacing(c) != null && tileCharacterFacing(c).walkable()) {
			// no walls blocking in direction char is facing
			if (floor[c.getRow()][c.getCol()].getWall(c.getDir()) == null
					|| floor[c.getRow()][c.getCol()].getWall(c.getDir())
							.passable()) {
				// valid move
				if (c.getDir() == Direction.NORTH) {
					setCharacter(c, oldRow - 1, oldCol);
					floor[oldRow][oldCol].setOccupant(null);
				} else if (c.getDir() == Direction.WEST) {
					setCharacter(c, oldRow, oldCol - 1);
					floor[oldRow][oldCol].setOccupant(null);
				} else if (c.getDir() == Direction.SOUTH) {
					setCharacter(c, oldRow + 1, oldCol);
					floor[oldRow][oldCol].setOccupant(null);
				} else if (c.getDir() == Direction.EAST) {
					setCharacter(c, oldRow, oldCol + 1);
					floor[oldRow][oldCol].setOccupant(null);
				}
			}
		}
	}

	/**
	 * returns the tile that the given character is currently facing
	 */
	public Tile tileCharacterFacing(Character c) {
		if (c.getDir() == Direction.NORTH) {
			if (validLocation(c.getRow() - 1, c.getCol()))
				return floor[c.getRow() - 1][c.getCol()];
			else
				return null;
		} else if (c.getDir() == Direction.WEST) {
			if (validLocation(c.getRow(), c.getCol() - 1))
				return floor[c.getRow()][c.getCol() - 1];
			else
				return null;
		} else if (c.getDir() == Direction.SOUTH) {
			if (validLocation(c.getRow() + 1, c.getCol()))
				return floor[c.getRow() + 1][c.getCol()];
			else
				return null;
		} else if (c.getDir() == Direction.EAST) {
			if (validLocation(c.getRow(), c.getCol() + 1))
				return floor[c.getRow()][c.getCol() + 1];
			else
				return null;
		} else
			return null;// shouldnt reach this
	}

	/**
	 * helper method to check if target coord is valid
	 */
	private boolean validLocation(int row, int col) {
		if (row >= maxR || row < 0)
			return false;
		if (col >= maxC || col < 0)
			return false;
		return true;
	}

	public Wall wallCharacterFacing(Character c) {
		if (c.getDir() == Direction.NORTH) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else if (c.getDir() == Direction.WEST) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else if (c.getDir() == Direction.SOUTH) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else if (c.getDir() == Direction.EAST) {
			return floor[c.getRow()][c.getCol()].getWall(c.getDir());
		} else
			return null;// shouldnt reach this
	}

	/**
	 * has the player interact with whatever is in front of or on their tile
	 * priority is art>chest>door
	 */
	public void interact(Player p) {
		// checking if wall
		Wall wall = wallCharacterFacing(p);
		if (wall != null) {
			if (wall instanceof Door) {
				((Door) wall).unlock(p);
			} else if (wall.getArt() != null) {
				p.addItem(wall.getArt());
				wall.setArt(null);
			}
		}
		// dealing with chests
		else if (tileCharacterFacing(p) instanceof Chest) {
			Chest chest = (Chest) tileCharacterFacing(p);
			chest.takeItem(p);
		}
		//stealing sculptures
		//TODO note this has not been tested yet, unsure if simply setting occupant
		//		to null is enough to avoid errors
		else if (tileCharacterFacing(p).getOccupant() instanceof Sculpture){
			Art stolenSculpture = ((Sculpture)tileCharacterFacing(p).getOccupant()).toItem();
			p.addItem(stolenSculpture);
			tileCharacterFacing(p).setOccupant(null);
		}
		// otherwise no action should be taken
	}

	/**
	 * checks if a specific guard can see a player. if not, returns a null
	 */
	private Player checkGuard(Guard g) {
		Direction dir = g.getDir();
		int cOff = 0; // col offset
		int rOff = 0; // row offset
		// first determine offsets
		if (dir == Direction.NORTH) {
			rOff = -1;
		} else if (dir == Direction.WEST) {
			cOff = -1;
		} else if (dir == Direction.SOUTH) {
			rOff = 1;
		} else if (dir == Direction.WEST) {
			cOff = 1;
		}
		// cycle through offset tiles and check for players
		// using distance = 3, straight line
		for (int i = 1; i < 4; i++) {
			// target coords. works because only one of
			// row or col offset will be nonzero
			int tarRow = g.getRow() + i * rOff;
			int tarCol = g.getCol() + i * cOff;
			if (floor[tarRow][tarCol].getOccupant() instanceof Player) {
				return (Player) floor[tarRow][tarCol].getOccupant();
			}
			if (floor[tarRow][tarCol].getWall(dir) != null) {
				break;
			}
		}
		return null;
	}

	/**
	 * returns true if guards see someone, else false
	 */
	public boolean checkGuards() {
		for (Guard g : guards) {
			// right now only checks for a single player
			// will be extended to collection in final version
			Player caught = checkGuard(g);
			if (caught != null) {
				return true;
			}
		}
		return false;
	}

}
