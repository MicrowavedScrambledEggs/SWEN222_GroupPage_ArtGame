package artGame.control;

public class LoseItemAction implements Action {
	private final boolean isWorld;
	private final int recipientId;
	private final int loserId;
	private final int itemId;
	
	public LoseItemAction(boolean isWorld, int recipientId, int receiverId, int itemId) {
		this.recipientId = recipientId;
		this.loserId = receiverId;
		this.itemId = itemId;
		this.isWorld = isWorld;
	}

	public int getLoserId() {
		return loserId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	// TODO get item id
	
	@Override
	public boolean isWorldUpdate() {
		return isWorld;
	}

	@Override
	public int getRecipient() {
		return recipientId;
	}

	public boolean equals(Object o) {
		if (!(o instanceof LoseItemAction)) return false;
		LoseItemAction a = (LoseItemAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
				&& getRecipient() == a.getRecipient()
				&& getItemId() == a.getItemId()
				&& getLoserId() == a.getLoserId())
			{ return true; }
		return false;
	}
}
