package artGame.game;

/**
 * this is a stair tile used to move between floors;
 * any character that steps into a stair tile is immediately
 * teleported to its linked tile
 * @author Kaishuo
 *
 */
public class StairTile extends Tile{
	private StairTile linkedTile;
	public StairTile(boolean nwall, boolean wwall, boolean swall, boolean ewall) {
		super(nwall, wwall, swall, ewall);
	}

	
	@Override
	public boolean walkable() {
		return this.occupant == null && this.linkedTile.occupant == null;
	}


	public StairTile getLinkedTile() {
		return linkedTile;
	}


	public void setLinkedTile(StairTile linkedTile) {
		this.linkedTile = linkedTile;
	}
	
	/**
	 * when we try to normally move a character on to this tile,
	 * we should instead move it to the linked tile
	 */
	@Override
	public void setOccupant(Character occupant){
		this.linkedTile.setOccupantHelper(occupant);
	}
	/**
	 * actual method to set the occupant
	 */
	public void setOccupantHelper(Character occupant){
		this.occupant = occupant;
	}

}
