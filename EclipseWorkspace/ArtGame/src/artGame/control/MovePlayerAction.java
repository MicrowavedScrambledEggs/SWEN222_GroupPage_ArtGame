package artGame.control;

import java.awt.Point;

public class MovePlayerAction implements Action {
	private final int recipientId;
	private final boolean isWorld;
	private final int pid;
	private final Point current;
	private final Point destination;
	
	/** This constructor creates a non-world update action. 
	 *  
	 * @param recipientId Client to receive (or who sent) this action.
	 * @param movingPlayerId Id of the player to move
	 * @param current The current location of the moving player
	 * @param destination The destination of the moving player 
	 */
	public MovePlayerAction(int recipientId, int movingPlayerId, Point current, Point destination) {
		isWorld = false;
		this.recipientId = recipientId;
		this.pid = movingPlayerId;
		this.current = new Point((int)current.getX(), (int)current.getY());
		this.destination = new Point((int)destination.getX(), (int)destination.getY());
	}
	
	/**
	 * 
	 * @param isWorld Whether this is a world update or not. 
	 * @param recipientId Client to receive (or who sent) this action. 
	 * @param pid Id of the player to move
	 * @param current The current location of the moving player
	 * @param destination The destination of the moving player 
	 */
	public MovePlayerAction(boolean isWorld, int recipientId, int pid, Point current, Point destination) {
		this.recipientId = recipientId;
		this.isWorld = isWorld;
		this.pid = pid;
		this.current = new Point((int)current.getX(), (int)current.getY());
		this.destination = new Point((int)destination.getX(), (int)destination.getY());
	}
	
	/** Returns the location of the player affected by this update, at the time the action was created. */
	public Point getCurrent() {
		return new Point((int)current.getX(), (int)current.getY());
	}
	
	/** Returns the destination of the player affected by this update, at the time the action was created. */
	public Point getDestination() {
		return new Point((int)destination.getX(), (int)destination.getY());
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
	public int getRecipient() {
		return recipientId;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof MovePlayerAction)) return false;
		MovePlayerAction a = (MovePlayerAction)o;
		if (getCurrent().equals(a.getCurrent())
			&& getDestination().equals(a.getDestination())
			&& getRecipient() == a.getRecipient()
			&& isWorldUpdate() == a.isWorldUpdate()
			&& getPlayerId() == a.getPlayerId()) { 
			return true; 
		}
		return false;
	}
}