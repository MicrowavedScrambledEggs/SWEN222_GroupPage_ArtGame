package artGame.ui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import org.lwjgl.glfw.GLFWKeyCallback;

import artGame.control.ClientThread;

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
		
		//Send press commands..
		if (key == GLFW_KEY_W && action == GLFW_PRESS) {
			System.out.println("Here we'd send move request W");
		}
		if (key == GLFW_KEY_A && action == GLFW_PRESS) {
			System.out.println("Here we'd send move request A");
		}
		if (key == GLFW_KEY_S && action == GLFW_PRESS) {
			System.out.println("Here we'd send move request S");
		}
		if (key == GLFW_KEY_D && action == GLFW_PRESS) {
			System.out.println("Here we'd send move request D");
		}
		if (key == GLFW_KEY_F && action == GLFW_PRESS) {
			System.out.println("Here we'd interact request F");
		}
		if (key == GLFW_KEY_R && action == GLFW_PRESS) {
			System.out.println("Here we'd do some examining or something");
		}
		
		//Send release commands..
		if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop movin request W");
		}
		if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop movin request A");
		}
		if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop movin request S");
		}
		if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
			System.out.println("Here we'd send stop moving request D");
		}
		if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
			System.out.println("Here we'd do some examining or something");
		}
	}

}
