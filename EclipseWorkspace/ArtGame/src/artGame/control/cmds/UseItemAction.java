package artGame.control.cmds;


public class UseItemAction implements Action {
	private static final int type = Packet.ITEM_USE;
	private final int recipientId;
	private final boolean isWorld;
	private final int user;
	private final int entityId;
	private final int itemId;
	
	public UseItemAction(boolean isWorld, int recipientId, int user, int entityId, int itemId) {
		this.recipientId = recipientId;
		this.user = user;
		this.entityId = entityId;
		this.itemId = itemId;
		this.isWorld = isWorld;
	}

	/** Returns the ID of the entity the object is being used on. */
	public int getEntityId() {
		return entityId;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public int getUsersId() {
		return user;
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
		if (!(o instanceof UseItemAction)) return false;
		UseItemAction a = (UseItemAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()
			&& getUsersId() == a.getUsersId()
			&& getEntityId() == a.getEntityId()
			&& getItemId() == a.getItemId()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "UseItem: for client "+ recipientId +", player "+user+" acting on "+ entityId +" with "+ itemId +", isWorld? "+isWorld;
	}
	
	public int type() {
		return type;
	}
}
