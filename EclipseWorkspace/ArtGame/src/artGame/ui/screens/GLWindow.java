package artGame.ui.screens;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;

import artGame.ui.NetworkKeyCallback;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public class GLWindow {

	private static GLFWErrorCallback errorCallback = Callbacks
			.errorCallbackPrint(System.err);
	private static long window;

	private GLFWKeyCallback keyCallback = new NetworkKeyCallback(null);

	private Set<Screen> screens;

	private static Matrix4f camera;
	private static Vector3f light;

	public GLWindow() {
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
		window = glfwCreateWindow(640, 480, "Renderer Demo Window", NULL, NULL);
		if (window == NULL) {
			glfwTerminate();
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// associate window with key callback
		glfwSetKeyCallback(window, keyCallback);

		// create OpenGL context
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();

		// enable backface culling
		glDisable(GL_CULL_FACE);
		// glEnable(GL_CULL_FACE);
		// glCullFace(GL_BACK);

		initScreens();
	}

	public void begin() {
		while (glfwWindowShouldClose(window) != GL_TRUE) {
			loop();
			
		}
	}

	private void loop() {
		render();
	}

	private void render() {
		for (Screen screen : screens) {
			screen.render(camera, light);
		}
	}

	private void initScreens() {
		screens = new HashSet<Screen>();
		screens.add(new UIRenderer(window));
		screens.add(new GameRenderer(window));

		for (Screen screen : screens) {
			screen.initialize();
		}
	}

	public void dispose() {
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	public static void setView(Matrix4f view) {
		GLWindow.camera = view;
	}

	public static Matrix4f getView() {
		return camera;
	}

	public static void setLight(Vector3f light) {
		GLWindow.light = light;
	}

	public static Vector3f getLight() {
		return light;
	}

	public static void main(String[] args) {
		GLWindow window = new GLWindow();
		window.begin();
	}

}
