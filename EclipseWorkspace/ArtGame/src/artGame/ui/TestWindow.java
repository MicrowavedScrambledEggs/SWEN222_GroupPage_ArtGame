package artGame.ui;

import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Camera;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.Painting;
import artGame.ui.renderer.Sprite;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class TestWindow {
	
	private static GLFWErrorCallback errorCallback = Callbacks.errorCallbackPrint(System.err);
	private static long window;
	private static Camera camera;
	private Vector3f light;
	private float angle = 37.5f;
	private float speed = 0.01f;
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
	        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
	            glfwSetWindowShouldClose(window, GL_TRUE);
	        }
	    }
	};
	
	public TestWindow() {
		
		
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
		//glEnable(GL_CULL_FACE);
		//glCullFace(GL_BACK);
		
		window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();
        width.rewind();
        height.rewind();
		
		camera = new Camera(Matrix4f.persp(80f, ratio, 1f, 100f), 2.5f);
		light = new Vector3f(1.0f, 1.0f, 0.5f).normalized();
		
		camera.rotate(new Vector3f(angle, 0, 0));
        
        List<Asset> renderList = createScene();

		while (glfwWindowShouldClose(window) != GL_TRUE) {

            /* Get width and height to calcualte the ratio */
            glfwGetFramebufferSize(window, width, height);

            /* Rewind buffers for next get */
            width.rewind();
            height.rewind();
            
            //System.out.println(GL11.glGetError());
            
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

            //System.out.println(GL11.glGetError());
            camera.rotate(new Vector3f(0, speed, 0));
		}
		
		// shut down
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	private List<Asset> createScene() {
		List<Asset> scene = new ArrayList<Asset>();
		
		///*
		Model topWall = AssetLoader.instance().loadOBJ("res/top_wall.obj", new Vector3f(1,1,1));
		if (topWall != null) {
			scene.add(topWall);
		}
		//*/
		
		Model floor = AssetLoader.instance().loadOBJ("res/floor.obj", new Vector3f(0.9f, 0.9f, 0.9f));
		if (floor != null) {
			scene.add(floor);
		}
		
		Painting djp = AssetLoader.instance().loadPainting("res/paintings/painting_1.png", 64);
		if (djp != null) {
			scene.add(djp);
		}
		return scene;
	}

	public static void main(String[] args) {
		new TestWindow();
	}

}
