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
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;
import artGame.ui.screens.Screen;

public class GameRenderer implements Screen{

	private long window;

	private Camera camera;
	private Vector3f light;

	private static final float CAMERA_ANGLE = 60f;

	private Model floor;
	private Model topWall;
	private Model bottomWall;
	private Model leftWall;
	private Model rightWall;
	private Model stairs;
	private Model sculpture1;
	private Model crates;
	private Sprite playerSprite;
	private Sprite guardSprite;

	private List<Model> levelCache;
	private Map<artGame.game.Character, Asset> characters;
	
	private float currentTime;

	public GameRenderer(){
		resetAssets();
		levelCache = loadFullLevel();
		characters = loadCharacters();
		
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
		camera.setPosition(((Sprite)characters.get(GameData.getPlayer())).getPosition().scale(-1));

		for (Asset a : renderList) {
			a.draw(camera, light);
		}
		currentTime += delta;
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
					level.add(stairs.instantiate(pos));
				} else if (t instanceof ExitTile) {
					level.add(floor.instantiate(pos));
				} else if (t instanceof Chest) {
					level.add(crates.instantiate(pos));
					level.add(floor.instantiate(pos));
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
		playerSprite = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32);
		guardSprite = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32); // TODO Add guard sprite
	}

	public Camera getCamera(){
		return camera;
	}
	
	public void rotateLeft() {
		camera.rotate(new Vector3f(0, 90, 0));
	}
	
	public void rotateRight() {
		camera.rotate(new Vector3f(0, -90, 0));
	}

	@Override
	public void dispose(){

	}

}