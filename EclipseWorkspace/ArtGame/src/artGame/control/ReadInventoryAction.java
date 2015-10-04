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
	
	public int[] getInventory() {
		return Arrays.copyOf(inventory,inventory.length);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ReadInventoryAction)) return false;
		ReadInventoryAction a = (ReadInventoryAction) o;
		if (Arrays.equals(inventory, a.getInventory())
			&& isWorldUpdate() == a.isWorldUpdate()
			&& getRecipient() == a.getRecipient()
			&& getInventoryOwner() == a.getInventoryOwner()) {
			return true;
		}
		return false;
	}

	public String toString() {
		String s = "ReadInventoryAction: RECIEVER:"+getRecipient()+"\tID:"+getInventoryOwner();
		for (int i = 0; i < inventory.length; i++) {
			s += ", "+inventory[0];
		}
		return s;
	}
}
