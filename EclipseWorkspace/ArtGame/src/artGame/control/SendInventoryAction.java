package artGame.control;

public class SendInventoryAction implements Action {
	private final int recipientId;
	private final int inventoryOwner;
	
	public SendInventoryAction(int recipientId, int inventoryOwner) {
		this.recipientId = recipientId;
		this.inventoryOwner = inventoryOwner;
	}
	
	public int getInventoryOwner() {
		return inventoryOwner;
	}

	@Override
	public boolean isWorldUpdate() {
		return false;
	}

	@Override
	public int getRecipient() {
		return recipientId;
	}
	
}
