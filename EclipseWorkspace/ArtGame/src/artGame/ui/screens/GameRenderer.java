package artGame.ui.screens;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.Sprite;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;


public class GameRenderer implements Screen {
	
	private long window;
	
	private List<Asset> renderList;
	
	private float angle = 35.2f;
	private float speed = 0.1f;
	
	public GameRenderer(long window){
		this.window=window;	
		
		 // temporary list of assets so something can be displayed
        // TODO replace with better scene-loading solution from game
       renderList = createScene();
       
       GLWindow.setView(GLWindow.INITIAL_VIEW);
       GLWindow.setLight(new Vector3f(1.0f, 1.0f, 0.5f).normalized());
	}
	
	@Override
	public void render(Matrix4f view, Vector3f light) {

        for (Asset a : renderList) {
        	a.draw(view, light);
        }
        
        GLWindow.setView(GLWindow.getView().multiply(Matrix4f.rotate(0.9f, 0f, 1f, 0f)));
	}
	
	public void dispose(){
		
	}

	@Override
	public long getWindow() {
		
		return window;
	}
	
	private List<Asset> createScene() {
		List<Asset> scene = new ArrayList<Asset>();
		
		///*
		Model david = AssetLoader.instance().loadOBJ("res/sculpture_david.obj");
		if (david != null) {
			scene.add(david);
		} else {
			System.out.println("David not loaded");
		}
		//*/
		
		Model floor = AssetLoader.instance().loadOBJ("res/floor.obj");
		if (floor != null) {
			scene.add(floor);
		}
		
		Sprite player = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32);
		if (player != null) {
			scene.add(player);
		}
		return scene;
	}

	
}
