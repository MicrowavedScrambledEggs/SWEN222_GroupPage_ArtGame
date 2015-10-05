package artGame.control;

public class GetInventoryAction implements Action {
	private final int recipientId;
	private final int inventoryOwner;
	
	public GetInventoryAction(int recipientId, int inventoryOwner) {
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
	public int getClient() {
		return recipientId;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof GetInventoryAction)) return false;
		GetInventoryAction a = (GetInventoryAction)o;
		if (getClient() == a.getClient()
			&& getInventoryOwner() == a.getInventoryOwner()) {
			return true; 
		}
		return false;
	}
	
	public String toString() {
		return "GetInventoryAction: RECIEVER:"+getClient()+"\tOWNER ID:"+getInventoryOwner();
	}	
}