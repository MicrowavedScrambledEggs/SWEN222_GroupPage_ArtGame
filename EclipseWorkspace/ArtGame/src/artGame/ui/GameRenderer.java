package artGame.ui;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.game.EmptyTile;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.StairTile;
import artGame.game.Tile;
import artGame.main.Game;
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

	private Game game;
	private Model floor;
	private Model topWall;
	private Model bottomWall;
	private Model leftWall;
	private Model rightWall;
	private Model stairs;
	private Model sculpture1;
	private Sprite playerSprite;

	private List<Model> levelCache;
	private List<Asset> characters;

	public GameRenderer(Game game){
		this.game = game;

		resetAssets();
		levelCache = loadFullLevel();
		characters = loadCharacters();

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
	public void render() {
		List<Asset> renderList = getRenderList();
		camera.setPosition(new Vector3f(game.getPlayer().getCol(), 0, game.getPlayer().getRow()));

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
		scene.addAll(characters);
		return scene;
	}

	private List<Asset> loadCharacters() {
		List<Asset> chars = new ArrayList<Asset>();
		chars.add(playerSprite.instantiate());
		return chars;
	}

	private void updateCharacters() {
		// replace this with something like game.getCharacters() and iterate over it
		if (characters.get(0) instanceof Sprite) {
			((Sprite)characters.get(0)).setPosition(new Vector3f(game.getPlayer().getCol(), 0, game.getPlayer().getRow()));
		}
	}

	private List<Model> loadFullLevel() {
		List<Model> level = new ArrayList<Model>();
		Floor world = game.getFloor();
		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				Tile t = world.getTile(row, col);
				Matrix4f pos = new Matrix4f();
				pos = pos.multiply(Matrix4f.translate(new Vector3f(col, 0, row)));

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
				}
			}
		}

		return level;
	}

	private void resetAssets() {
		floor = AssetLoader.instance().loadOBJ("res/floor.obj");
		topWall = AssetLoader.instance().loadOBJ("res/top_wall.obj");
		bottomWall = AssetLoader.instance().loadOBJ("res/bottom_wall.obj");
		leftWall = AssetLoader.instance().loadOBJ("res/left_wall.obj");
		rightWall = AssetLoader.instance().loadOBJ("res/right_wall.obj");
		stairs = AssetLoader.instance().loadOBJ("res/stair.obj");
		sculpture1 = AssetLoader.instance().loadOBJ("res/sculpture_david.obj");
		playerSprite = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32);
	}

	@Override
	public void dispose(){

	}

}
