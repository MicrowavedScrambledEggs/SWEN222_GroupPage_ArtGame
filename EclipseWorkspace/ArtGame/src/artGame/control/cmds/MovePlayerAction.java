package artGame.control.cmds;

import java.awt.Point;

import artGame.game.Character.Direction;

public class MovePlayerAction implements Action {
	private static final int type = Packet.MOVE; 
	private final Direction curDir;
//	private final Direction newDir;
	private final int recipientId;
	private final boolean isWorld;
	private final int pid;
	private final Point current;
//	private final Point destination;
	private long time;
	
	/** This constructor will move an entity as a non-world update action. 
	 *  
	 * @param recipientId Client to receive (or who sent) this action.
	 * @param movingPlayerId Id of the player to move
	 * @param current The current location of the moving player
	 * @param destination The destination of the moving player 
	 */
//	public MovePlayerAction(int recipientId, int movingPlayerId, Point current, Direction direction, Point destination, Direction newDirection) {
//		isWorld = false;
//		this.recipientId = recipientId;
//		this.pid = movingPlayerId;
//		this.current = new Point((int)current.getX(), (int)current.getY());
//		this.destination = new Point((int)destination.getX(), (int)destination.getY());
//		this.curDir = direction;
//		newDir = newDirection;
//		System.out.println(toString());
//	}
	
	public MovePlayerAction(int recipientId, int movingPlayerId, Point current, Direction direction, long time) {
		isWorld = false;
		this.recipientId = recipientId;
		this.pid = movingPlayerId;
		this.current = new Point((int)current.getX(), (int)current.getY());
		this.curDir = direction;
		this.time = time;
	}
	
	/** Returns the time at which this packet was issued. */
	public long getTime() {
		return time;
	}
	
	/** The direction the player is facing as a result of this move. */
	public Direction getCurrentDirection() {
		return curDir;
	}
	
//	public Direction getDestinationDirection() {
//		return newDir;
//	}
	
	/**
	 * 
	 * @param isWorld Whether this is a world update or not. 
	 * @param recipientId Client to receive (or who sent) this action. 
	 * @param pid Id of the player to move
	 * @param current The current location of the moving player
	 * @param destination The destination of the moving player 
	 */
//	public MovePlayerAction(boolean isWorld, int recipientId, int movingPlayerId, Point current, Direction curDir, Point destination, Direction newDirection) {
//		this.recipientId = recipientId;
//		this.isWorld = isWorld;
//		this.pid = movingPlayerId;
//		this.current = new Point((int)current.getX(), (int)current.getY());
//		this.destination = new Point((int)destination.getX(), (int)destination.getY());
//		this.curDir = curDir;
//		newDir = newDirection;
//	}
	
	/** Returns the location of the player affected by this update, at the time the action was created. */
	public Point getCurrent() {
		return new Point((int)current.getX(), (int)current.getY());
	}
	
	/** Returns the destination of the player affected by this update, at the time the action was created. */
//	public Point getDestination() {
//		return new Point((int)destination.getX(), (int)destination.getY());
//	}
	
	public int type() {
		return type;
	}
	
	/** The id of the moving player. */
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
//			&& getDestination().equals(a.getDestination())
			&& getClient() == a.getClient()
			&& isWorldUpdate() == a.isWorldUpdate()
			&& getCurrentDirection() == a.getCurrentDirection()
			&& getTime() == a.getTime()
//			&& getDestinationDirection() == a.getDestinationDirection()
			&& getPlayerId() == a.getPlayerId()) { 
			return true; 
		}
		return false;
	}
	
	public String toString() {
		return "MovePlayerAction: RECIEVER:"+getClient()+"\tID:"+getPlayerId()+
				"\tCUR:"+getCurrent().getX()+","+getCurrent().getY()+"\t"
				+" @ "+time
//				+"\tDES:"+getDestination().getX()+","+getDestination().getY();
				;
	}
}