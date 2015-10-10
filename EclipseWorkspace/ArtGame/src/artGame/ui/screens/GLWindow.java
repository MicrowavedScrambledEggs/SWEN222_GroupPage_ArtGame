package artGame.ui.screens;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;

import artGame.main.Game;
import artGame.ui.DebugKeyCallback;
import artGame.ui.NetworkKeyCallback;
import artGame.ui.renderer.Camera;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;
import artGame.xml.XMLHandler;

public class GLWindow {

	private static GLFWErrorCallback errorCallback = Callbacks
			.errorCallbackPrint(System.err);
	private static long window;

	private GLFWKeyCallback keyCallback = new NetworkKeyCallback(null);

	private List<Screen> screens;

	private static Camera camera;
	private static Vector3f light;

	private static Camera bufferedCam;
	private static Vector3f bufferedLight;

	private IntBuffer width;
	private IntBuffer height;

	private DebugKeyCallback debugKeys;

	public static final Matrix4f INITIAL_VIEW = Matrix4f.translate(
			new Vector3f(0, 0, -3))
			.multiply(Matrix4f.rotate(35.2f, 1f, 0f, 0f));

	private GameRenderer gameRender;

	private static Game game;

	static {
		XMLHandler gameLoader = new XMLHandler();
		game = gameLoader.loadGame(new File("res/GroundFloorBasic.xml"));
		//game = new Game();
		//game.initialise();
	}

	public GLWindow() {

		debugKeys = new DebugKeyCallback();
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
		// glfwSetKeyCallback(window, keyCallback);
		glfwSetKeyCallback(window, debugKeys);

		// create OpenGL context
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();

		// enable backface culling
		glDisable(GL_CULL_FACE);
		// glEnable(GL_CULL_FACE);
		// glCullFace(GL_BACK);

		// declare buffers for using inside the loop
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);

		GLWindow.setCamera(new Camera(INITIAL_VIEW, 5));

		initScreens();
	}

	public void begin() {
		while (glfwWindowShouldClose(window) != GL_TRUE) {
			loop();
		}
		dispose();
	}

	private void loop() {

		/* Get width and height to calcualte the ratio */
		glfwGetFramebufferSize(window, width, height);

		/* Rewind buffers for next get */
		width.rewind();
		height.rewind();

		/* Set viewport and clear screen */
		glViewport(0, 0, width.get(), height.get());
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		render();

		/* Swap buffers and poll Events */
		glfwSwapBuffers(window);
		glfwPollEvents();

		/* Flip buffers for next loop */
		width.flip();
		height.flip();
		//gameRender.getCamera().setPosition(new Vector3f(10, 0, 10));
		getCamera().translate(debugKeys.getCameraMove());
		gameRender.getCamera().translate(debugKeys.getCameraMove());
		camera = bufferedCam;
		light = bufferedLight;
		// System.out.println(GL11.glGetError());

	}

	private void render() {
		for (Screen screen : screens) {
			screen.render();
		}
	}

	private void initScreens() {
		screens = new ArrayList<Screen>();

		this.gameRender = new GameRenderer(game);
		screens.add(this.gameRender);
		screens.add(new UIRenderer(window));

		camera = bufferedCam;
		light = bufferedLight;
	}

	public void dispose() {
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	public static void setView(Matrix4f view) {
		// GLWindow.bufferedCam = view;
	}

	public static Matrix4f getView() {
		// return camera;
		return null;
	}

	public static void setLight(Vector3f light) {
		GLWindow.bufferedLight = light;
	}

	public static Vector3f getLight() {
		return light;
	}

	public static long getWindow() {
		return window;
	}

	public static Camera getCamera() {
		return camera;
	}

	public static void setCamera(Camera cam) {
		GLWindow.bufferedCam = cam;
	}

	public static void main(String[] args) {
		// this is for testing the new renderer
		new GLWindow().begin();
	}

	public static Game getGame() {
		return game;
	}

}
