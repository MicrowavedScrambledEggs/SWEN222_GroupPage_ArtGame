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
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, GL_TRUE);
		}
		
		Player p = GLWindow.getGame().getPlayer(); // we want our player, not someone else's
		Command c = null;
		//Send press commands..
		
		// none of these methods need to do sanity checking-- that is done by the 
		// Game simply not updating state if the parameters are invalid.
		if (key == GLFW_KEY_W && action == GLFW_PRESS) {
			c = new Command('w',p.getId());
		}
		if (key == GLFW_KEY_A && action == GLFW_PRESS) {
			c = new Command('a',p.getId());
		}
		if (key == GLFW_KEY_S && action == GLFW_PRESS) {
			c = new Command('s',p.getId());
		}
		if (key == GLFW_KEY_D && action == GLFW_PRESS) {
			c = new Command('d',p.getId());
		}
		if (key == GLFW_KEY_F && action == GLFW_PRESS) {
			c = new Command('f',p.getId());
		}
		if (key == GLFW_KEY_R && action == GLFW_PRESS) {
			c = new Command('r',p.getId());
		}
		
		//Send release commands..
		if (key == GLFW_KEY_W && action == GLFW_RELEASE
				|| key == GLFW_KEY_A && action == GLFW_RELEASE
				|| key == GLFW_KEY_S && action == GLFW_RELEASE
				|| key == GLFW_KEY_D && action == GLFW_RELEASE) {
			c = new Command('!',p.getId());
		}
		
		connection.sendCommand(c);
	}

	private Action sendStop(Player p, Direction d) {
		Action a = new MovePlayerAction(p.getId(), p.getId(), 
				new Point(p.getRow(),p.getCol()), d, System.currentTimeMillis());
		return a;
	}
}
