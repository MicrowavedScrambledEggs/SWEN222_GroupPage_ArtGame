package artGame.ui.screens;

import java.util.ArrayList;
import java.util.List;

import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Sprite;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;


public class UIRenderer implements Screen {
	
	private long window;
	
	private List<Asset> assets;
	
	public UIRenderer(long window){
		this.window = window;
	}
	
	@Override
	public void initialize() {
		createUI();
	}
	
	@Override
	public void render(Matrix4f view, Vector3f light) {
		for(Asset asset : assets){
			asset.draw(view, light);
		}
	}
	
	public void dispose(){
		
	}

	@Override
	public long getWindow() {
		
		return window;
	}
	
	private void createUI(){
		assets = new ArrayList<>();
		Sprite player = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32);
		if (player != null) {
			assets.add(player);
		}
	}
	
}

