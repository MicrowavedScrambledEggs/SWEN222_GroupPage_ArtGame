package artGame.game;

public class Chest extends Tile {
	public final int id;
	private Item content;

	public Chest(int id,boolean nwall, boolean wwall, boolean swall, boolean ewall) {
		super(nwall, wwall, swall, ewall);
		this.id = id;
	}

	public void takeItem(Player p) {
		if (content != null) {
			p.addItem(content);
			this.content = null;
		}
	}

	public Item getContent() {
		return content;
	}

	public void setContent(Item content) {
		this.content = content;
	}

	@Override
	public boolean walkable() {
		return false;
	}

	public String toString() {
		return "C";
	}
}
