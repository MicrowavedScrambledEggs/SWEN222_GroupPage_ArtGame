package artGame.control;

public class CapturedAction implements Action {
	private final int pid;
	private final boolean isWorld;
	
	public CapturedAction(int pid) {
		isWorld = true;
		this.pid = pid;
	}

	@Override
	public boolean isWorldUpdate() {
		return isWorld;
	}
	
	@Override
	public int getClient() {
		return pid;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof CapturedAction)) return false;
		CapturedAction a = (CapturedAction)o;
		if (isWorldUpdate() == a.isWorldUpdate()
			&& getClient() == a.getClient()) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "CapturedAction for client "+ pid;
	}
}