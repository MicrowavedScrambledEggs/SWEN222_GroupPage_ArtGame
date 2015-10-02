package artGame.ui;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;

import artGame.control.ClientThread;
import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public class RenderWindowTest implements Screen {

	private static GLFWErrorCallback errorCallback = Callbacks
			.errorCallbackPrint(System.err);
	private static long window;
	private static Matrix4f camera;
	private Vector3f light;
	private float angle = 35.2f;
	private float speed = 0.3f;
	
	private IntBuffer width;
	private IntBuffer height;
	
	private List<Asset> renderList;
	
	private GLFWKeyCallback keyCallback;
	
	public RenderWindowTest(ClientThread connection){
		keyCallback = new NetworkKeyCallback(connection);
	}

	@Override
	public void initialize() {
		camera = Matrix4f.translate(new Vector3f(0, 0, -3)).multiply(
				Matrix4f.rotate(angle, 1f, 0f, 0f));
		light = new Vector3f(1.0f, 1.0f, 0.5f).normalized();

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
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		// declare buffers for using inside the loop
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);

		// temporary list of assets so something can be displayed
		// TODO replace with better scene-loading solution from game
		renderList = createScene();
	}

	@Override
	public void render() {
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

		for (Asset a : renderList) {
			a.draw(camera, light);
		}

		/* Swap buffers and poll Events */
		glfwSwapBuffers(window);
		glfwPollEvents();

		/* Flip buffers for next loop */
		width.flip();
		height.flip();

		camera = camera.multiply(Matrix4f.rotate(speed, 0f, 1f, 0f));
	}

	@Override
	public void dispose() {
		// shut down
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	@Override
	public long getWindow() {
		return window;
	}
	
	private List<Asset> createScene() {
		List<Asset> scene = new ArrayList<Asset>();
		Model david = null;
		try {
			david = AssetLoader.instance().loadOBJ(FileUtils.getFilePath("sculpture_david.obj"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (david != null) {
			scene.add(david);
		}
		Model floor = null;
		try {
			floor = AssetLoader.instance().loadOBJ(FileUtils.getFilePath("floor.obj"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (floor != null) {
			scene.add(floor);
		}
		return scene;
	}

}
