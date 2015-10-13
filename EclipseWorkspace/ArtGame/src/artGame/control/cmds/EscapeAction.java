package artGame.control.cmds;

@Deprecated
public class EscapeAction implements Action {
	private final int type = Packet.ESCAPE;
	private final int pid;
	private final boolean isWorld;
	
	public EscapeAction(int pid) {
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
		if (o == null) { return false; }
		if (o instanceof EscapeAction) {
			return getClient() == ((EscapeAction)o).getClient();
		}
		return false;
	}
	
	public String toString() {
		return "EscapeAction for client ID "+ pid;
	}
	
	public int type() {
		return type;
	}
}
