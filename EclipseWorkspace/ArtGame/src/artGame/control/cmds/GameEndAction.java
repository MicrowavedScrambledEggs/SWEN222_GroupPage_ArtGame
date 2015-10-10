package artGame.control.cmds;


public class GameEndAction implements Action {
	private final int type = Packet.GAME_END;
	private final int recipientId;
	
	public GameEndAction(int recipientId) {
		this.recipientId = recipientId;
	}

	@Override
	public boolean isWorldUpdate() {
		return true;
	}
	
	@Override
	public int getClient() {
		return recipientId;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof GameEndAction)) return false;
		GameEndAction a = (GameEndAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "GameEnd: for client "+ recipientId;
	}
	
	public int type() {
		return type;
	}
}
