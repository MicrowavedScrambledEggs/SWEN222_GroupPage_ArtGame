package artGame.ui.gamedata;

import artGame.game.Wall;
import artGame.game.Character.Direction;

public class TileData {

	public int row;
	public int col;
	public int itemId;
	public int itemDir;
	
	public int[] artIds;
	
	public TileData(int row, int col, int id, int dir, Wall[] walls){
		this.row=row;
		this.col=col;
		this.itemId=id;
		this.itemDir=dir;
		
		artIds = new int[walls.length];
		for(int i = 0; i < walls.length; i++){
			if(walls[i] == null){ 
				continue;
			}
			if(walls[i].getArt() != null){
				artIds[i] = walls[i].getArt().ID;
			}
		}
	}
	
	public TileData(int row, int col, int id, int dir, int[] walls){
		this.row=row;
		this.col=col;
		this.itemId=id;
		this.itemDir=dir;
		
		artIds = walls;
	}
	
	/**
	 * Returns the wall in this tiles given direction
	 */
	public int getArt(Direction dir) {
		if (dir == Direction.NORTH) {
			return artIds[0];
		} else if (dir == Direction.WEST) {
			return artIds[1];
		} else if (dir == Direction.SOUTH) {
			return artIds[2];
		} else if (dir == Direction.EAST) {
			return artIds[3];
		} else
			return -1;
	}
	
}
