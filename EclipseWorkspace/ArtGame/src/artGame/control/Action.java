package artGame.control;

import java.util.Collection;

import artGame.main.Game;

public interface Action {
	/* True if this action requires some facet of the world,
	 * not a player, to change. That is, the packet affects an object,
	 * an aspect of the game-play or a guard, etc.  
	 */
	public boolean isWorldUpdate();
	
	/* Returns the ID of the player to receive this update, or 
	 * if going to the server, the player who sent it. */
	public int getRecipient();
	
	/* Not a pure method! The least pure method! There is nothing 
	 * pure about this method at all!
	 * 
	 * Calling this method will cause the Action to be taken on the 
	 * given Game.
	 */
	// public void execute(Game g);
	
}
