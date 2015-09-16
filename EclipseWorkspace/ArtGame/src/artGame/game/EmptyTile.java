package artGame.game;

public class EmptyTile extends Tile{
	private Art art;
	public EmptyTile(Art art){
		this.art = art;
	}
	
	public EmptyTile(){
		this.art = null;
	}
	
	public void setArt(Art art){
		this.art = art;
	}
	
	public boolean hasArt(){
		return art!=null;
	}
	
	public void stealFrom(Player p){
		if(art==null) throw new GameError("Trying to steal art from a empty square");
		p.addArt(art);
		this.art = null;
	}
	@Override
	public boolean walkable() {
		return true;
	}
	
	public String toString(){
		if(occupant!=null){
			if(occupant instanceof Player) return "P";
			else if(occupant instanceof Guard) return "G";
			else return "C";
		}
		else if(art!=null) return "A";
		else return " ";
	}
}
