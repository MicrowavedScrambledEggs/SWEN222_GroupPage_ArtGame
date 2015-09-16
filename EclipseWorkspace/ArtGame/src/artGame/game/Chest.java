package artGame.game;

public class Chest extends Tile {
	private Item content;

	public Chest(Item content){
		this.content = content;
	}
	public void takeItem(Player p) {
		if (content != null) {
			p.addItem(content);
			this.content = null;
		}
	}

	@Override
	public boolean walkable() {
		return false;
	}

	public String toString(){
		return "C";
	}
}
