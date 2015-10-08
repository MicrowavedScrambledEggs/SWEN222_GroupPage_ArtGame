package artGame.control.cmds;


public class GetItemAction implements Action {
	private static final int type = Packet.ITEM_GAIN;
	private final int recipientId;
	private final boolean isWorld;
	private final int sourceId;
	private final int itemId;
	
	public GetItemAction(boolean isWorld, int recipientId, int sourceId, int itemId) {
		this.recipientId = recipientId;
		this.sourceId = sourceId;
		this.itemId = itemId;
		this.isWorld = isWorld;
	}

	public int getItemSource() {
		return sourceId;
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
		if (!(o instanceof GetItemAction)) return false;
		GetItemAction a = (GetItemAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()
			&& getItemSource() == a.getItemSource()
			&& getItemId() == a.getItemId()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "GetItem: item #"+itemId+" for client "+ recipientId +" from "+recipientId+", isWorld "+isWorld;
	}
	
	public int type() {
		return type;
	}
}
