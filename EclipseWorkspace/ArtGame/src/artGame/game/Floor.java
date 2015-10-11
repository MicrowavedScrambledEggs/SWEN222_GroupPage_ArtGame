package artGame.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import artGame.game.Character.Direction;

/**
 * Represents the entire game Floor/world where the action is taking place. Most
 * of the logic happens here
 * 
 * @author Kaishuo
 *
 */
public class Floor {
	private int maxR = 3;
	private int maxC = 7;

	private int offset = 0; // used for calculating coordinate offsets for
							// multiple floors
	private Tile[][] floor;
	private List<ExitTile> exits;
	private List<Guard> guards;

	private int itemIDC; // Item ID Counter

	/**
	 * Default simple floor used for testing games without a XML file
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

		// setting west walls
		floor[0][0].setWall(Direction.WEST, new Wall(new Art("Art1", 1000,
				itemIDC++)));
		floor[1][0] = new Chest(0, false, true, false, false);
		((Chest) floor[1][0]).setContent(new Key(1, itemIDC++));
		floor[1][0].setWall(Direction.WEST, new Wall());
		floor[2][0].setWall(Direction.WEST, new Wall(new Art("Art2", 4000,
				itemIDC++)));
		// east walls
		floor[0][5].setWall(Direction.EAST, new Wall(new Art("Art3", 9000,
				itemIDC++)));
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
		Guard guard = new Guard(Character.Direction.WEST, 0);
		setCharacter(guard, 2, 5);
	}

	/**
	 * Original version of the constructor for single floor games
	 */
	public Floor(Tile[][] tiles, int maxR, int maxC, Collection<ExitTile> exits) {
		floor = tiles;
		this.exits = new ArrayList<ExitTile>(exits);
		this.guards = new ArrayList<Guard>();
		this.maxR = maxR;
		this.maxC = maxC;
		this.itemIDC = 0;
	}

	/**
	 * Upgraded version of constructor, takes a variable number of floors and
	 * automatically converges them into a single array
	 */
	public Floor(Collection<ExitTile> exits, Tile[][]... floors) {
		// calculating offset
		int localC = 0; // local number of cols of a floor
		int localR = 0; // local number of rows
		for (Tile[][] tiles : floors) {
			if (tiles.length > localR)
				localR = tiles.length;
			if (tiles[0].length > localC)
				localC = tiles[0].length;
		}
		offset = localC * 2; // offset is double the maximum width of a floor
		// moving tiles of non 0 floors to correct location
		this.maxC = localC * (floors.length * 2 - 1);
		floor = new Tile[localR][maxC];
		for (int i = 0; i < floors.length; i++) {
			Tile[][] currentFloor = floors[i];
			for (int r = 0; r < currentFloor.length; r++) {
				for (int c = 0; c < currentFloor[0].length; c++) {
					floor[r][c + offset * i] = currentFloor[r][c];
				}
			}
		}
		this.exits = new ArrayList<ExitTile>(exits);
		this.guards = new ArrayList<Guard>();
		this.maxR = localR;
		this.itemIDC = 0;
	}

	/**
	 * Gets the next available item id, incrementing it in the process
	 */
	public int nextItemID() {
		return itemIDC++;
	}

	/**
	 * Returns tile at target coordinate
	 */
	public Tile getTile(int row, int col) {
		return floor[row][col];
	}

	/**
	 * Returns tile at target coordinate of a specified floor
	 */
	public Tile getTile(int row, int col, int floorNumber) {
		return floor[row][col + offset * floorNumber];
	}

	/**
	 * Links the two stair tiles specified by r,c,f coordinates. Throws a error
	 * if either tile is not a stair tile
	 */
	public void linkStairs(int row1, int col1, int floor1, int row2, int col2,
			int floor2) {
		if (!(floor[row1][col1 + offset * floor1] instanceof StairTile)) {
			throw new GameError("First position not stair, link fail");
		}
		if (!(floor[row2][col2 + offset * floor2] instanceof StairTile)) {
			throw new GameError("Second position not stair, link fail");
		}
		StairTile st1 = (StairTile) floor[row1][col1 + offset * floor1];
		StairTile st2 = (StairTile) floor[row2][col2 + offset * floor2];
		st1.setLinkedTile(st2);
		st1.setLoc(row1, col1+offset*floor1);
		st2.setLinkedTile(st1);
		st2.setLoc(row2, col2+offset*floor2);
	}

	/**
	 * Sets the character c to position row, col without regard to legality of
	 * move or face direction. Useful for initialising positions
	 */
	public void setCharacter(Character c, int row, int col) {
		c.setRow(row);
		c.setCol(col);
		floor[row][col].setOccupant(c);
		if (c instanceof Guard && !guards.contains((Guard) c)) {
			guards.add((Guard) c);
		}
	}

	/**
	 * Sets the character c to position row, col of a given floor without regard
	 * to legality of move or face direction. Useful for initialising positions
	 */
	public void setCharacter(Character c, int row, int col, int floorNumber) {
		floor[row][col + offset * floorNumber].setOccupant(c);
		c.setRow(row);
		c.setCol(col);
		if (c instanceof Guard && !guards.contains((Guard) c)) {
			((Guard) c).offsetPath(offset * floorNumber);
			guards.add((Guard) c);
		}
	}

	/**
	 * Gets all characters(players) currently on exit tiles
	 */
	public Character isOnExit() {
		for (ExitTile exit : exits) {
			if (exit.getOccupant() != null) {
				return exit.getOccupant();
			}
		}
		return null;
	}

	/**
	 * Prints the floor. Used in debugging console version only
	 */
	public void printFloor() {
		for (int i = 0; i < maxR; i++) {
			for (int j = 0; j < maxC; j++) {
				Tile toPrint = floor[i][j];
				if (toPrint == null) {
					System.out.print(" ");
				} else {
					System.out.print(toPrint);
				}
			}
			System.out.println("");
		}
	}

	/**
	 * Moves a character 1 tile in the direction they're facing(does nothing if
	 * invalid move)
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
	 * Moves a character to a targeted square and faces them the right way.
	 * Throws a error if invalid move
	 */
	public void moveCharacter(Character c, int row, int col) {
		int colDiff = c.getCol() - col;
		int rowDiff = c.getRow() - row;
		// invalid move if one of them not 0 and trying to move >1 in any
		// direction
		if (colDiff * rowDiff != 0
				&& (Math.abs(colDiff) > 1 || Math.abs(rowDiff) > 1))
			throw new GameError("trying to move more than 1 square at once");
		if (colDiff == 1) {
			c.setDir(Direction.WEST);
			this.moveCharacter(c);
		} else if (colDiff == -1) {
			c.setDir(Direction.EAST);
			this.moveCharacter(c);
		} else if (rowDiff == 1) {
			c.setDir(Direction.NORTH);
			this.moveCharacter(c);
		} else if (rowDiff == -1) {
			c.setDir(Direction.SOUTH);
			this.moveCharacter(c);
		}
		// base case character not moving, direction update not required
	}

	/**
	 * Updates the positions of all guards as specified by their path
	 */
	public void moveGuards() {
		for (Guard g : guards) {
			Coordinate nextCoord = g.nextCoord();
			moveCharacter(g, nextCoord.getRow(), nextCoord.getCol());
		}
	}

	/**
	 * Returns the tile directly in front of the given character
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
	 * Helper method to check if target coordinate is valid
	 */
	private boolean validLocation(int row, int col) {
		if (row >= maxR || row < 0)
			return false;
		if (col >= maxC || col < 0)
			return false;
		return true;
	}

	/**
	 * Gets the wall that the character c is currently facing
	 */
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
	 * Has the player interact with whatever is in front of or on their tile.
	 * Priority is door>art>chest>sculpture>guard
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
		// stealing sculptures
		else if (tileCharacterFacing(p).getOccupant() instanceof Sculpture) {
			Art stolenSculpture = ((Sculpture) tileCharacterFacing(p)
					.getOccupant()).toItem(this);
			p.addItem(stolenSculpture);
			tileCharacterFacing(p).setOccupant(null);
		}
		// guards
		else if (tileCharacterFacing(p).getOccupant() instanceof Guard) {
			Guard g = (Guard) tileCharacterFacing(p).getOccupant();
			for (Item i : g.getInventory()) {
				p.addItem(i);
			}
			g.clearInventory();
		}
		// nothing valid to interact with
	}

	/**
	 * Returns the first Player that Guard g can see
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
		} else if (dir == Direction.EAST) {
			cOff = 1;
		}
		// cycle through offset tiles and check for players
		// using distance = 3, straight line
		for (int i = 1; i < 4; i++) {
			// target coords. works because only one of
			// row or col offset will be nonzero
			int tarRow = g.getRow() + i * rOff;
			int tarCol = g.getCol() + i * cOff;
			if(tarRow<0 || tarRow>maxR || tarCol<0 || tarCol>maxC) return null;
			if (floor[tarRow][tarCol].getOccupant() instanceof Player) {
				return (Player) floor[tarRow][tarCol].getOccupant();
			}
			if (floor[tarRow][tarCol].getWall(dir) != null) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Returns set of Players caught by Guards
	 */
	public Set<Player> checkGuards() {
		Set<Player> caught = new HashSet<Player>();
		for (Guard g : guards) {
			// right now only checks for a single player
			// will be extended to collection in final version
			Player victim = checkGuard(g);
			if (victim != null) {
				victim.gotCaught();
				caught.add(victim);
			} //
		}
		return caught;
	}

	/**
	 * Has the Player p inspect whatever is in front of them
	 */
	public void inspect(Player p) {
		// checking if wall
		Wall wall = wallCharacterFacing(p);
		if (wall != null) {
			if (wall instanceof Door) {
				System.out.println(((Door) wall).getDescription());
			} else if (wall.getArt() != null) {
				System.out.println(wall.getArt().getDescription());
			}
		}
		// dealing with chests
		else if (tileCharacterFacing(p) instanceof Chest) {
			Chest chest = (Chest) tileCharacterFacing(p);
			System.out.println(chest.getDescription());
		}
		// sculpture
		else if (tileCharacterFacing(p).getOccupant() instanceof Sculpture) {
			Sculpture sculpture = ((Sculpture) tileCharacterFacing(p)
					.getOccupant());
			System.out.println(sculpture.getDescription());
		}
		// otherwise no action should be taken

	}

	/**
	 * returns the height of the entire floor array(number of rows)
	 */
	public int getHeight() {
		return floor.length;
	}
	
	/**
	 * returns the width of the entire floor array(number of cols)
	 */
	public int getWidth() {
		return floor[0].length;
	}

	/**
	 * Returns list of Guards in this floor
	 */
	public List<Guard> getGuards() {
		return guards;
	}

}
