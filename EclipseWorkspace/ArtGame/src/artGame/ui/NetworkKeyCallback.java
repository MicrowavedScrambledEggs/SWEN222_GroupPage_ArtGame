package artGame.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.awt.Point;

import org.lwjgl.glfw.GLFWKeyCallback;

import artGame.control.ClientThread;
import artGame.control.cmds.*;
import artGame.game.Player;
import artGame.game.Character.Direction;
import artGame.ui.screens.GLWindow;

public class NetworkKeyCallback extends GLFWKeyCallback {

	private ClientThread connection;
	
	public NetworkKeyCallback(ClientThread connection){
		this.connection = connection;
	}
	
	/** 
	 * 
	 * REQUIRES: that the Game at the time of the method call is already at the state
	 * that should be broadcast. (Ie, if the player moves, Game should already indicate
	 * the position and direction the player now exists in.)
	 */
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, GL_TRUE);
		}
		
		Player p = GLWindow.getGame().getPlayer(); // we want our player, not someone else's
		Action a = null;
		//Send press commands..
		
		// none of these methods need to do sanity checking-- that is done by the 
		// Game simply not updating state if the parameters are invalid.
		if (key == GLFW_KEY_W && action == GLFW_PRESS) {
			System.out.println("Send move request W: "+ p.getRow() +","+ p.getCol());
			a = new MovePlayerAction(p.getId(), p.getId(), 
					new Point(p.getRow(),p.getCol()), Direction.NORTH, System.currentTimeMillis());
		}
		if (key == GLFW_KEY_A && action == GLFW_PRESS) {
			System.out.println("Send move request A: "+ p.getRow() +","+ p.getCol());
			a = new MovePlayerAction(p.getId(), p.getId(), 
					new Point(p.getRow(),p.getCol()), Direction.WEST, System.currentTimeMillis());
		}
		if (key == GLFW_KEY_S && action == GLFW_PRESS) {
			System.out.println("Send move request S: "+ p.getRow() +","+ p.getCol());
			a = new MovePlayerAction(p.getId(), p.getId(), 
					new Point(p.getRow(),p.getCol()), Direction.SOUTH, System.currentTimeMillis());
		}
		if (key == GLFW_KEY_D && action == GLFW_PRESS) {
			System.out.println("Send move request D: "+ p.getRow() +","+ p.getCol());
			a = new MovePlayerAction(p.getId(), p.getId(), 
					new Point(p.getRow(),p.getCol()), Direction.EAST, System.currentTimeMillis());
		}
		if (key == GLFW_KEY_F && action == GLFW_PRESS) {
			System.out.println("Interact request F");
			a = new InteractAction(p.getId());
		}
		if (key == GLFW_KEY_R && action == GLFW_PRESS) {
			System.out.println("Examining or something (local command)");
		}
		
		//Send release commands..
		if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop movin request W");
			a = sendStop(p, p.getDir());
		}
		if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop movin request A");
			a = sendStop(p, p.getDir());
		}
		if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop movin request S");
			a = sendStop(p, p.getDir());
		}
		if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop moving request D");
			a = sendStop(p, p.getDir());
		}
		if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
			System.out.println("Here we'd do some examining or something");
			a = sendStop(p, p.getDir());
		}
		
		connection.sendAction(a);
	}

	private Action sendStop(Player p, Direction d) {
		Action a = new MovePlayerAction(p.getId(), p.getId(), 
				new Point(p.getRow(),p.getCol()), d, System.currentTimeMillis());
		return a;
	}
}
