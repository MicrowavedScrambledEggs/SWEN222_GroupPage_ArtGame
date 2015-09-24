package artGame.control;

import java.awt.Point;

public class MovePlayerAction implements Action {
	private final int pid;
	private final Point current;
	private final Point destination;
	
	public MovePlayerAction(int pid, Point current, Point destination) {
		this.pid = pid;
		this.current = new Point((int)current.getX(), (int)current.getY());
		this.destination = new Point((int)destination.getX(), (int)destination.getY());
	}
	
	public Point getCurrent() {
		return new Point((int)current.getX(), (int)current.getY());
	}
	
	public Point getDestination() {
		return new Point((int)destination.getX(), (int)destination.getY());
	}
	
	public int getPlayerId() {
		return pid;
	}
}