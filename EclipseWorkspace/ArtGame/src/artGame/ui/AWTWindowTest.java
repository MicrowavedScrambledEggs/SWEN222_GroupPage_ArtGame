package artGame.ui;

import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class AWTWindowTest {
	
	private static GLFWErrorCallback errorCallback = Callbacks.errorCallbackPrint(System.err);
	private static long window;
	private static Matrix4f camera;
	private Vector3f light;
	private float angle = 35.2f;
	private float speed = 0.01f;
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
	        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
	            glfwSetWindowShouldClose(window, GL_TRUE);
	        }
	    }
	};
	
	private IntBuffer width;
	private IntBuffer height;
	private List<Asset> renderList;
	
	public AWTWindowTest() {
		camera = Matrix4f.translate(new Vector3f(0, 0, -3)).multiply(Matrix4f.rotate(angle, 1f, 0f, 0f));
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
	
	public void render(){
		// no proper 'game loop', as this is a test.
				// TODO associate proper Window class with Game class
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
	
	public void dispose(){
		// shut down
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	public long getWindow(){
		return window;
	}
	
	private List<Asset> createScene() {
		List<Asset> scene = new ArrayList<Asset>();
		Model david = AssetLoader.instance().loadOBJ("res/sculpture_david.obj");
		if (david != null) {
			scene.add(david);
		}
		Model floor = AssetLoader.instance().loadOBJ("res/floor.obj");
		if (floor != null) {
			scene.add(floor);
		}
		return scene;
	}

	public static void main(String[] args) {
		new TestWindow();
	}

}
