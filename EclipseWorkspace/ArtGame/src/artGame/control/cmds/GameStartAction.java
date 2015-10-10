package artGame.control.cmds;

public class GameStartAction implements Action {
	private final int type = Packet.GAME_START;
	private final int recipientId;
	
	public GameStartAction(int recipientId) {
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
		if (!(o instanceof GameStartAction)) return false;
		GameStartAction a = (GameStartAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "GameStart: for client "+ recipientId;
	}
	
	public int type() {
		return type;
	}
}
