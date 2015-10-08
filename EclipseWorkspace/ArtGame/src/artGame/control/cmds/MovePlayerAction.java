package artGame.control.cmds;

import java.awt.Point;

public class MovePlayerAction implements Action {
	private static final int type = Packet.MOVE; 
	private final int curDir;
	private final int newDir;
	private final int recipientId;
	private final boolean isWorld;
	private final int pid;
	private final Point current;
	private final Point destination;
	
	/** This constructor will move an entity as a non-world update action. 
	 *  
	 * @param recipientId Client to receive (or who sent) this action.
	 * @param movingPlayerId Id of the player to move
	 * @param current The current location of the moving player
	 * @param destination The destination of the moving player 
	 */
	public MovePlayerAction(int recipientId, int movingPlayerId, Point current, int curDir, Point destination, int newDirection) {
		isWorld = false;
		this.recipientId = recipientId;
		this.pid = movingPlayerId;
		this.current = new Point((int)current.getX(), (int)current.getY());
		this.destination = new Point((int)destination.getX(), (int)destination.getY());
		this.curDir = curDir;
		newDir = newDirection;
		System.out.println(toString());
	}
	
	public int getCurrentDirection() {
		return curDir;
	}
	
	public int getDestinationDirection() {
		return newDir;
	}
	
	/**
	 * 
	 * @param isWorld Whether this is a world update or not. 
	 * @param recipientId Client to receive (or who sent) this action. 
	 * @param pid Id of the player to move
	 * @param current The current location of the moving player
	 * @param destination The destination of the moving player 
	 */
	public MovePlayerAction(boolean isWorld, int recipientId, int movingPlayerId, Point current, int curDir, Point destination, int newDirection) {
		this.recipientId = recipientId;
		this.isWorld = isWorld;
		this.pid = movingPlayerId;
		this.current = new Point((int)current.getX(), (int)current.getY());
		this.destination = new Point((int)destination.getX(), (int)destination.getY());
		this.curDir = curDir;
		newDir = newDirection;
	}
	
	/** Returns the location of the player affected by this update, at the time the action was created. */
	public Point getCurrent() {
		return new Point((int)current.getX(), (int)current.getY());
	}
	
	/** Returns the destination of the player affected by this update, at the time the action was created. */
	public Point getDestination() {
		return new Point((int)destination.getX(), (int)destination.getY());
	}
	
	public int type() {
		return type;
	}
	
	/** The id of the player moving player. */
	public int getPlayerId() {
		return pid;
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
		if (!(o instanceof MovePlayerAction)) return false;
		MovePlayerAction a = (MovePlayerAction)o;
		if (getCurrent().equals(a.getCurrent())
			&& getDestination().equals(a.getDestination())
			&& getClient() == a.getClient()
			&& isWorldUpdate() == a.isWorldUpdate()
			&& getCurrentDirection() == a.getCurrentDirection()
			&& getDestinationDirection() == a.getDestinationDirection()
			&& getPlayerId() == a.getPlayerId()) { 
			return true; 
		}
		return false;
	}
	
	public String toString() {
		return "MovePlayerAction: RECIEVER:"+getClient()+"\tID:"+getPlayerId()+
				"\tCUR:"+getCurrent().getX()+","+getCurrent().getY()+"\t"+
				"\tDES:"+getDestination().getX()+","+getDestination().getY();
	}
}