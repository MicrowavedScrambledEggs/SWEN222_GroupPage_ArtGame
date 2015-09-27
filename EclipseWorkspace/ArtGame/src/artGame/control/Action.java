package artGame.control;

import java.util.Collection;

public interface Action {
	/* True if this action requires some facet of the world,
	 * not a player, to change. That is, the packet affects an object,
	 * an aspect of the game-play or a guard, etc.  
	 */
	public boolean isWorldUpdate();
	
	/* Returns the ID of the player to receive this update, or 
	 * if going to the server, the player who sent it. */
	public int getRecipient();
	
}
