package artGame.ui;

import java.util.ArrayList;
import java.util.List;

import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.math.Matrix4f;

public class GameRenderer {

	private List<Asset> renderList;
	private Matrix4f camera;
	
	private float angle = 35.2f;
	private float speed = 0.1f;
	
	public GameRenderer(){
		
		camera = Matrix4f.scale(0.25f, 0.25f, 0.25f).multiply(
				Matrix4f.rotate(angle, 1f, 0f, 0f));
		
		renderList = createScene();
	}
	
	public void render(){
		
		for (Asset a : renderList) {
			a.draw(camera);
		}
		
		camera = camera.multiply(Matrix4f.rotate(speed, 0f, 1f, 0f));
	}
	
	public void setView(Matrix4f view){
		this.camera = view;
	}
	
	private List<Asset> createScene() {	
		List<Asset> scene = new ArrayList<Asset>();
		Model stair = AssetLoader.instance().loadOBJ("EclipseWorkspace/ArtGame/res/stair.obj");
		if (stair != null) {
			scene.add(stair);
		}
		return scene;
	}
	
	public void dispose(){
		
	}
	
}
