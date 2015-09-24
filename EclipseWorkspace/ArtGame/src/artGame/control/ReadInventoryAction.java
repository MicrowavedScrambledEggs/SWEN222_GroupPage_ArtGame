package artGame.control;

public class ReadInventoryAction implements Action {
	private final int[] inventory;
	
	public ReadInventoryAction(int[] inventory) {
		this.inventory = inventory;
	}

}
