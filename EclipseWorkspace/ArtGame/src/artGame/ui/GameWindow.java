<<<<<<< HEAD
package artGame.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

/**
 * 
 * @author Tim King
 *
 */
public class GameWindow {

	private GameRenderer game;
	private UIRenderer ui;

	private int screenWidth = 640;
	private int screenHeight = 480;

	private static long window;

	private static GLFWErrorCallback errorCallback = Callbacks
			.errorCallbackPrint(System.err);
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action,
				int mods) {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
				glfwSetWindowShouldClose(window, GL_TRUE);
			}
		}
	};

	public GameWindow() {
		
		glfwSetErrorCallback(errorCallback);

		if (glfwInit() != GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// create OpenGL 3.2 window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		window = glfwCreateWindow(getWidth(), getHeight(), "Renderer Demo Window", NULL,
				NULL);

		if (window == NULL) {
			glfwTerminate();
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - getWidth()) / 2,
				(GLFWvidmode.height(vidmode) - getHeight()) / 2);

		// create OpenGL context
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();

		game= new GameRenderer();
		ui = new UIRenderer();
		
		run();
		
		
	}	
	
	public void run(){
		
		// declare buffers for using inside the loop
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);	
		
		while (glfwWindowShouldClose(window) == GL_FALSE) {

			float ratio;

			/* Get width and height to calcualte the ratio */
			glfwGetFramebufferSize(window, width, height);
			ratio = width.get() / (float) height.get();

			/* Rewind buffers for next get */
			width.rewind();
			height.rewind();

			/* Set viewport and clear screen */
			glClear(GL_COLOR_BUFFER_BIT);
			glViewport(0, 0, width.get(), height.get());
			
			

			/*
			 * Here we should 1. do the 3D rendering and 2. Render the overlays.
			 */
			
			game.render();
			
			ui.render(5, 5, getWidth(), getHeight());
	
			
			//deselect
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			
			//deselect VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			/* Swap buffers and poll Events */
			glfwSwapBuffers(window);
			glfwPollEvents();

			/* Flip buffers for next loop */
			width.flip();
			height.flip();		
			
		}
		
		dispose();
		
	}
	
	public void dispose(){
		
		ui.dispose();
		game.dispose();
		
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}
	
	public int getHeight() {
		return screenHeight;
	}

	public int getWidth() {
		return screenWidth;
	}

	public static void main(String[] args) {
		new GameWindow();
	}

}
=======
package artGame.ui;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class GameWindow {

	private GameRenderer game;
	private UIRenderer ui;

	private int screenWidth = 640;
	private int screenHeight = 480;

	private static long window;

	private static GLFWErrorCallback errorCallback = Callbacks
			.errorCallbackPrint(System.err);
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action,
				int mods) {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
				glfwSetWindowShouldClose(window, GL_TRUE);
			}
		}
	};

	public GameWindow() {
		
		glfwSetErrorCallback(errorCallback);

		if (glfwInit() != GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// create OpenGL 3.2 window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		window = glfwCreateWindow(getWidth(), getHeight(), "Renderer Demo Window", NULL,
				NULL);

		if (window == NULL) {
			glfwTerminate();
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Get the resolution of the primary monitor
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - getWidth()) / 2,
				(GLFWvidmode.height(vidmode) - getHeight()) / 2);

		// create OpenGL context
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();

		game= new GameRenderer();
		ui = new UIRenderer();
		
		run();
		
		
	}	
	
	public void run(){
		
		// declare buffers for using inside the loop
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);	
		
		while (glfwWindowShouldClose(window) == GL_FALSE) {

			float ratio;

			/* Get width and height to calcualte the ratio */
			glfwGetFramebufferSize(window, width, height);
			ratio = width.get() / (float) height.get();

			/* Rewind buffers for next get */
			width.rewind();
			height.rewind();

			/* Set viewport and clear screen */
			glClear(GL_COLOR_BUFFER_BIT);
			glViewport(0, 0, width.get(), height.get());
			
			

			/*
			 * Here we should 1. do the 3D rendering and 2. Render the overlays.
			 */
			
			game.render();
			
			ui.render(5, 5, getWidth(), getHeight());
	
			
			//deselect
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			
			//deselect VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			/* Swap buffers and poll Events */
			glfwSwapBuffers(window);
			glfwPollEvents();

			/* Flip buffers for next loop */
			width.flip();
			height.flip();		
			
		}
		
		dispose();
		
	}
	
	public void dispose(){
		
		ui.dispose();
		game.dispose();
		
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}
	
	public int getHeight() {
		return screenHeight;
	}

	public int getWidth() {
		return screenWidth;
	}

	public static void main(String[] args) {
		new GameWindow();
	}
}
>>>>>>> branch 'master' of https://github.com/MicrowavedScrambledEggs/SWEN222_GroupPage_ArtGame.git
