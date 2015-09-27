package artGame.control;

import java.util.Arrays;
import java.util.Collection;

public class ReadInventoryAction implements Action {
	private final int[] inventory;
	private final int recipientId;
	private final int inventoryOwner;
	
	public ReadInventoryAction(int recipientId, int inventoryOwner, int[] inventory) {
		this.inventoryOwner = inventoryOwner;
		this.inventory = inventory;
		this.recipientId = recipientId;
	}

	public int[] getItems() {
		// TODO need to have a way to convert from item IDs to item objects
		return Arrays.copyOf(inventory, inventory.length);
	}

	@Override
	public boolean isWorldUpdate() {
		return false;
	}

	@Override
	public int getRecipient() {
		return recipientId;
	}
	
	public int getInventoryOwner() {
		return inventoryOwner;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ReadInventoryAction)) return false;
		ReadInventoryAction a = (ReadInventoryAction) o;
		if (Arrays.equals(inventory, a.getItems())
			&& isWorldUpdate() == a.isWorldUpdate()
			&& getRecipient() == a.getRecipient()
			&& getInventoryOwner() == a.getInventoryOwner()) {
			return true;
		}
		return false;
	}
}
