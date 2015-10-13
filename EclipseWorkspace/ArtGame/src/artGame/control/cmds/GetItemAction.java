package artGame.control.cmds;

@Deprecated
public class GetItemAction implements Action {
	private static final int type = Packet.ITEM_GAIN;
	private final int recipientId;
	private final boolean isWorld;
	private final int itemDestination;
	private final int itemId;
	
	public GetItemAction(boolean isWorld, int recipientId, int itemDestination, int itemId) {
		this.recipientId = recipientId;
		this.itemDestination = itemDestination;
		this.itemId = itemId;
		this.isWorld = isWorld;
	}

	public int getItemDestination() {
		return itemDestination;
	}
	
	public int getItemId() {
		return itemId;
	}

	@Override
	public boolean isWorldUpdate() {
		return isWorld;
	}
	
	@Override
	public int getClient() {
		return recipientId;
	}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (!(o instanceof GetItemAction)) return false;
		GetItemAction a = (GetItemAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()
			&& getItemDestination() == a.getItemDestination()
			&& getItemId() == a.getItemId()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "GetItem: item #"+itemId+" for client "+ recipientId +" to entity "+itemDestination+", isWorld "+isWorld;
	}
	
	public int type() {
		return type;
	}
}
