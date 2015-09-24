package artGame.control;

public class SendInventoryAction implements Action {
	private final int inventoryRecipient;
	private final int inventoryOwner;
	
	public SendInventoryAction(int inventoryRecipient, int inventoryOwner) {
		this.inventoryRecipient = inventoryRecipient;
		this.inventoryOwner = inventoryOwner;
	}
	
}
