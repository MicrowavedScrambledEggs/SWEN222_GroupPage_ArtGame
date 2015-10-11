package artGame.ui.screens;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.game.Chest;
import artGame.game.EmptyTile;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Player;
import artGame.game.Sculpture;
import artGame.game.StairTile;
import artGame.game.Tile;
import artGame.ui.gamedata.GameData;
import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Camera;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.Sprite;
import artGame.ui.renderer.animations.Tween;
import artGame.ui.renderer.animations.TweenFloat;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;
import artGame.ui.screens.Screen;

public class GameRenderer implements Screen{

	private long window;

	private Camera camera;
	private Vector3f light;

	private static final float CAMERA_ANGLE = 60f;
	private float currentCameraAngle;

	private Model floor;
	private Model topWall;
	private Model bottomWall;
	private Model leftWall;
	private Model rightWall;
	private Model stairs;
	private Model sculpture1;
	private Model crates;
	private Model loo;
	private Sprite playerSprite;
	private Sprite guardSprite;

	private List<Model> levelCache;
	private Map<artGame.game.Character, Asset> characters;
	
	Map<Sprite, Tween<Vector3f>> spriteTweens;
	Tween<Float> cameraTween;
	
	private float currentTime;

	public GameRenderer(){
		resetAssets();
		levelCache = loadFullLevel();
		characters = loadCharacters();
		
		spriteTweens = new HashMap<Sprite, Tween<Vector3f>>();
		cameraTween = null;
		
		currentCameraAngle = 0;
		
		currentTime = 0;

		window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();

		camera = new Camera(Matrix4f.persp(80f, ratio, 1f, 100f), 5);
		light = new Vector3f(1.0f, 1.0f, 1.0f).normalized();

		camera.rotate(new Vector3f(CAMERA_ANGLE, 0, 0));
	}

	@Override
	public void render(float delta) {
		List<Asset> renderList = getRenderList();
		
		// update tweens
		currentTime += delta;
		
		for (Sprite s : spriteTweens.keySet()) {
			s.setPosition(spriteTweens.get(s).tween(currentTime));
		}
		
		if (cameraTween != null) {
			if (cameraTween.isFinished(currentTime)) {
				cameraTween = null;
				camera.setRotation(new Vector3f(CAMERA_ANGLE, 90*(Math.round(currentCameraAngle/90)), 0));
			} else {
				float tween = cameraTween.tween(currentTime);
				currentCameraAngle = tween;
				camera.setRotation(new Vector3f(CAMERA_ANGLE, tween, 0));
			}
		}
		camera.setPosition(((Sprite)characters.get(GameData.getPlayer())).getPosition().scale(-1));

		for (Asset a : renderList) {
			a.draw(camera, light);
		}
	}

	private List<Asset> getRenderList() {
		List<Asset> scene = new ArrayList<Asset>();

		if (levelCache == null) {
			levelCache = loadFullLevel();
		}

		scene.addAll(levelCache);

		updateCharacters();
		scene.addAll(characters.values());
		return scene;
	}

	private Map<artGame.game.Character, Asset> loadCharacters() {
		Map<artGame.game.Character, Asset> chars = new HashMap<artGame.game.Character, Asset>();
		for (artGame.game.Character c : GameData.getCharacters()) {
			if (c instanceof Player) {
				chars.put(c, playerSprite.instantiate());
			} else if (c instanceof Guard) {
				chars.put(c, guardSprite.instantiate());
			} else if (c instanceof Sculpture) {
				Matrix4f pos = new Matrix4f();
				pos = pos.multiply(Matrix4f.translate(new Vector3f(c.getCol(), 0, c.getRow())));
				chars.put(c, sculpture1.instantiate(pos));
			}
		}
		return chars;
	}

	private void updateCharacters() {
		// replace this with something like game.getCharacters() and iterate over it
		List<artGame.game.Character> toRemove = new ArrayList<artGame.game.Character>();
		for (artGame.game.Character c : characters.keySet()) {
			if (c instanceof Player || c instanceof Guard) {
				((Sprite)characters.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
			} else if (c instanceof Sculpture) {
				if (((Sculpture)c).isTaken()) {
					toRemove.add(c);
				}
			}
		}
		
		for (artGame.game.Character c : toRemove) {
			characters.remove(c);
		}
	}

	private List<Model> loadFullLevel() {
		List<Model> level = new ArrayList<Model>();
		Floor world = GameData.getFloor();
		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				Tile t = world.getTile(row, col);
				Matrix4f pos = new Matrix4f();
				pos = pos.multiply(Matrix4f.translate(new Vector3f(col, 0, row)));

				if(t == null){
					continue;
				}

				if (t.getWall(artGame.game.Character.Direction.NORTH) != null){
					level.add(topWall.instantiate(pos));
				}
				if (t.getWall(artGame.game.Character.Direction.SOUTH) != null){
					level.add(bottomWall.instantiate(pos));
				}
				if (t.getWall(artGame.game.Character.Direction.EAST) != null){
					level.add(rightWall.instantiate(pos));
				}
				if (t.getWall(artGame.game.Character.Direction.WEST) != null){
					level.add(leftWall.instantiate(pos));
				}

				if (t instanceof EmptyTile) {
					level.add(floor.instantiate(pos));
				} else if (t instanceof StairTile) {
					StairTile st = (StairTile) t;
					switch(st.getDir()) {
					case NORTH:
						break;
					case EAST:
						pos = pos.multiply(Matrix4f.rotate(90, 0, 1, 0));
						break;
					case SOUTH:
						pos = pos.multiply(Matrix4f.rotate(180, 0, 1, 0));
						break;
					case WEST:
						pos = pos.multiply(Matrix4f.rotate(270, 0, 1, 0));
						break;
					}
					level.add(stairs.instantiate(pos));
				} else if (t instanceof ExitTile) {
					level.add(floor.instantiate(pos));
				} else if (t instanceof Chest) {
					level.add(floor.instantiate(pos));
					
					int id = ((Chest) t).id;
					if (0 <= id && id < 20 ) { // loo
						forloop:
						for (artGame.game.Character.Direction d : artGame.game.Character.Direction.values()) {
							switch (d) {
							case NORTH:
								if (t.getWall(d) == null) {
									pos = pos.multiply(Matrix4f.rotate(180, 0, 1, 0));
									break forloop;
								}
								break;
							case EAST:
								if (t.getWall(d) == null) {
									pos = pos.multiply(Matrix4f.rotate(270, 0, 1, 0));
									break forloop;
								}
								break;
							case SOUTH:
								if (t.getWall(d) == null) {
									break forloop;
								}
								break;
							case WEST:
								if (t.getWall(d) == null) {
									pos = pos.multiply(Matrix4f.rotate(90, 0, 1, 0));
									break forloop;
								}
								break;
							}
						}
						level.add(loo.instantiate(pos));
					} else { // crate
						level.add(crates.instantiate(pos));
					}
				}
			}
		}

		return level;
	}

	private void resetAssets() {
		floor = AssetLoader.instance().loadOBJ("res/floor.obj", new Vector3f(0.9f, 0.9f, 0.9f));
		topWall = AssetLoader.instance().loadOBJ("res/top_wall.obj", new Vector3f(1,1,1));
		bottomWall = AssetLoader.instance().loadOBJ("res/bottom_wall.obj", new Vector3f(1,1,1));
		leftWall = AssetLoader.instance().loadOBJ("res/left_wall.obj", new Vector3f(1,1,1));
		rightWall = AssetLoader.instance().loadOBJ("res/right_wall.obj", new Vector3f(1,1,1));
		stairs = AssetLoader.instance().loadOBJ("res/stair.obj",new Vector3f(1,1,1));
		sculpture1 = AssetLoader.instance().loadOBJ("res/sculpture_david.obj", new Vector3f(1,1,1));
		crates = AssetLoader.instance().loadOBJ("res/crates.obj", new Vector3f(0.45f, 0.29f, 0.16f));
		loo = AssetLoader.instance().loadOBJ("res/loo.obj", new Vector3f(1,1,1));
		playerSprite = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32);
		guardSprite = AssetLoader.instance().loadSpritesheet("res/guard.png", 32);
	}

	public Camera getCamera(){
		return camera;
	}
	
	public void rotateLeft() {
		if (cameraTween == null) {
			cameraTween = new TweenFloat(currentCameraAngle, 0.5f, 90, currentTime);
		}
	}
	
	public void rotateRight() {
		if (cameraTween == null) {
			cameraTween = new TweenFloat(currentCameraAngle, 0.5f, -90, currentTime);
		}
	}

	@Override
	public void dispose(){

	}

}