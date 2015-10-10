package artGame.control.cmds;

import artGame.game.Character.Direction;

@Deprecated
public class InteractAction implements Action {
	private static final int type = Packet.INTERACT;
	private final int recipientId;
	private final boolean isWorld;
	private final int x;
	private final int y;
	private final Direction dir;
	
	public InteractAction(int recipientId, int x, int y, Direction dir) {
		this.recipientId = recipientId;
		this.isWorld = true;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	@Override
	public boolean isWorldUpdate() {
		return isWorld;
	}
	
	public int getX() {
		return x;		
	}
	
	public int getY() {
		return y;
	}
	
	public Direction getDirection() {
		return dir;
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
		return "Interact: client "+ recipientId +" at ("+x+","+y+") facing "+dir;
	}
	
	public int type() {
		return type;
	}
}
