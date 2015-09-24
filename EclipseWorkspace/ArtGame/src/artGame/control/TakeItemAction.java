package artGame.control;

import java.awt.Point;

public class TakeItemAction implements Action {
	private final int receiverId;
	private final int itemId;
	private final boolean isWorld;
	
	public TakeItemAction(boolean isWorld, int receiverId, int itemId) {
		this.receiverId = receiverId;
		this.itemId = itemId;
		this.isWorld = isWorld;
	}
}
