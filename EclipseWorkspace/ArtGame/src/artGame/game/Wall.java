package artGame.game;

public class Wall{
	private Art art;

	public Wall(Art art){
		this.art = art;
	}
	public Wall(){
		this.art = null;
	}
	
	public Art getArt() {
		return art;
	}

	public void setArt(Art art) {
		this.art = art;
	}
	
	public boolean passable() {
		return false;
	}
	

}
