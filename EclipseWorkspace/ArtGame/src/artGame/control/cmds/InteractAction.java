package artGame.control.cmds;


public class InteractAction implements Action {
	private static final int type = Packet.INTERACT;
	private final int recipientId;
	private final boolean isWorld;
	
	public InteractAction(int recipientId) {
		this.recipientId = recipientId;
		this.isWorld = true;
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
		if (!(o instanceof InteractAction)) return false;
		InteractAction a = (InteractAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "interact for client "+ recipientId;
	}
	
	public int type() {
		return type;
	}
}
