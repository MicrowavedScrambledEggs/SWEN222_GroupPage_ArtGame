package artGame.control.cmds;

/** TODO
 * 
 * @author Vicki
 *
 */
public class Command {
	public final char action;
	public int id;

	public Command (char action, int entityId) {
		this.action = action;
		this.id = entityId; 
	}
	
	public int id() {
		return id;
	}
	
	public int key() {
		return action;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Command) {
			Command c = (Command)o;
			return c.id == id && c.action == action;
		}
		return false;
	}
	
	public String toString() {
		return "CMD "+id+" => "+action;
	}
}
