package artGame.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWKeyCallback;

import artGame.game.Character.Direction;
import artGame.ui.renderer.math.Vector3f;
import artGame.ui.screens.GLWindow;

public class DebugKeyCallback extends GLFWKeyCallback {

	private float xMove = 0, yMove = 0, zMove = 0;

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
			glfwSetWindowShouldClose(window, GL_TRUE);
		}

		// Send press commands..
		if (key == GLFW_KEY_W && action == GLFW_PRESS) {
			GLWindow.getGame().getPlayer().setDir(Direction.NORTH);
			GLWindow.getGame().getFloor().moveCharacter(GLWindow.getGame().getPlayer());
		}
		if (key == GLFW_KEY_A && action == GLFW_PRESS) {
			GLWindow.getGame().getPlayer().setDir(Direction.WEST);
			GLWindow.getGame().getFloor().moveCharacter(GLWindow.getGame().getPlayer());
		}
		if (key == GLFW_KEY_S && action == GLFW_PRESS) {
			GLWindow.getGame().getPlayer().setDir(Direction.SOUTH);
			GLWindow.getGame().getFloor().moveCharacter(GLWindow.getGame().getPlayer());
		}
		if (key == GLFW_KEY_D && action == GLFW_PRESS) {
			GLWindow.getGame().getPlayer().setDir(Direction.EAST);
			GLWindow.getGame().getFloor().moveCharacter(GLWindow.getGame().getPlayer());
		}
		if (key == GLFW_KEY_F && action == GLFW_PRESS) {
			GLWindow.getGame().doAction(GLWindow.getGame().getPlayer(), 'f');
		}
		if (key == GLFW_KEY_R && action == GLFW_PRESS) {
			System.out.println("Here we'd do some examining or something");
		}

		// Send press commands..
		if (key == GLFW_KEY_UP && action == GLFW_PRESS) {

			zMove = 0.05f;
		}
		if (key == GLFW_KEY_LEFT && action == GLFW_PRESS) {
			xMove = 0.05f;
		}
		if (key == GLFW_KEY_DOWN && action == GLFW_PRESS) {
			zMove = -0.05f;
		}
		if (key == GLFW_KEY_RIGHT && action == GLFW_PRESS) {
			xMove = -0.05f;
		}

		if (key == GLFW_KEY_RIGHT_SHIFT && action == GLFW_PRESS) {
			yMove = -0.05f;
		}

		if (key == GLFW_KEY_RIGHT_CONTROL && action == GLFW_PRESS) {
			yMove = 0.05f;
		}

		// Send release commands..
		if (key == GLFW_KEY_UP && action == GLFW_RELEASE) {
			zMove = 0;
		}
		if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
			xMove = 0;
		}
		if (key == GLFW_KEY_DOWN && action == GLFW_RELEASE) {
			zMove = 0;
		}
		if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
			xMove = 0;
		}
		if (key == GLFW_KEY_RIGHT_SHIFT && action == GLFW_RELEASE) {
			yMove = 0;
		}

		if (key == GLFW_KEY_RIGHT_CONTROL && action == GLFW_RELEASE) {
			yMove = 0;
		}
	}

	public Vector3f getCameraMove() {
		return new Vector3f(xMove, yMove, zMove);
	}
}
