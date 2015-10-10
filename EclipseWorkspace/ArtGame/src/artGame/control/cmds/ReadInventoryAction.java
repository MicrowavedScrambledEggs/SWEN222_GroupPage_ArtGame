package artGame.control.cmds;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReadInventoryAction implements Action {
	private static final int type = Packet.READ_INVENTORY;
	private final int[] inventory;
	private final int recipientId;
	private final int inventoryOwner;
	
	public ReadInventoryAction(int recipientId, int inventoryOwner, List<Integer> inv) {
		this.inventoryOwner = inventoryOwner;
		this.inventory = new int[inv.size()];
		for (int i = 0; i < inv.size(); i++) {
			inventory[i] = inv.get(i);
		}
		this.recipientId = recipientId;
	}	
	
	public ReadInventoryAction(int recipientId, int inventoryOwner, int[] inv) {
		this.inventoryOwner = inventoryOwner;
		this.inventory = Arrays.copyOf(inv, inv.length);
		this.recipientId = recipientId;
	}

	@Override
	public boolean isWorldUpdate() {
		return false;
	}

	@Override
	public int getClient() {
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
			&& getClient() == a.getClient()
			&& getInventoryOwner() == a.getInventoryOwner()) {
			return true;
		}
		return false;
	}

	public String toString() {
		String s = "ReadInventoryAction: RECIEVER:"+getClient()+"\tID:"+getInventoryOwner();
		for (int i = 0; i < inventory.length; i++) {
			s += ", "+inventory[0];
		}
		return s;
	}
	
	public int type() {
		return type;
	}
}
