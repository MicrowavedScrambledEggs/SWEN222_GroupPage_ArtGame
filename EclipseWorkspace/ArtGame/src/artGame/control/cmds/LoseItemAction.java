package artGame.control.cmds;

@Deprecated
public class LoseItemAction implements Action {
	private static final int type = Packet.ITEM_LOSE;
	private final boolean isWorld;
	private final int recipientId;
	private final int loserId;
	private final int itemId;
	
	public LoseItemAction(boolean isWorld, int recipientId, int inventoryToLoseFrom, int itemId) {
		this.recipientId = recipientId;
		this.loserId = inventoryToLoseFrom;
		this.itemId = itemId;
		this.isWorld = isWorld;
	}

	/** Returns the ID of the entity whose inventory the item should be removed from. */
	public int getItemSource() {
		return loserId;
	}
	
	/** Returns the ID of the item being lost. */
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
		if (!(o instanceof LoseItemAction)) return false;
		LoseItemAction a = (LoseItemAction)o;
		if (getClient() == a.getClient()
			&& isWorldUpdate() == a.isWorldUpdate()
			&& getItemSource() == a.getItemSource()
			&& getItemId() == a.getItemId() ) { 
			return true; 
		}
		return false;
	}
	
	public String toString() {
		return "LoseItemAction: RECIEVER:"+getClient()+"\tLOSER_ID:"+loserId+"\tITEM_ID:"+itemId;
	}
	
	public int type() {
		return type;
	}
}
