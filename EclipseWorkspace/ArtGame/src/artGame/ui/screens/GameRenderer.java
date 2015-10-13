package artGame.ui.screens;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.game.Chest;
import artGame.game.Door;
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
import artGame.ui.renderer.animations.TweenVector3f;
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
	private Model topDoor;
	private Model bottomDoor;
	private Model leftDoor;
	private Model rightDoor;
	private Model stairs;
	private Model sculpture1;
	private Model crates;
	private Model loos;
	private Model sinks;
	private Model desks;
	private Sprite playerSprite;
	private Sprite guardSprite;

	private List<Model> levelCache;
	private Map<artGame.game.Character, Asset> characters;
	private Map<artGame.game.Character, Vector3f> entityPositions;
	
	private Map<Sprite, Tween<Vector3f>> spriteTweens;
	private Tween<Float> cameraTween;
	
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

		camera = new Camera(Matrix4f.persp(80f, ratio, 1f, 100f), 4);
		light = new Vector3f(1.0f, 1.0f, 1.0f).normalized();

		camera.rotate(new Vector3f(CAMERA_ANGLE, 0, 0));
	}

	@Override
	public void render(float delta) {
		List<Asset> renderList = getRenderList();
		
		for (artGame.game.Character c : entityPositions.keySet()) {
			if (c.getCol() != entityPositions.get(c).getX() || 
				c.getRow() != entityPositions.get(c).getZ()) {
				float distZ = Math.abs(c.getRow()-entityPositions.get(c).getZ());
				float distX = Math.abs(c.getRow()-entityPositions.get(c).getX());
				
				if(distX > 50 || distZ > 50){
					continue;
				}
				
				//Vector3f start = entityPositions.get(c);
				Vector3f end = new Vector3f(c.getCol(), 0, c.getRow());
				entityPositions.put(c, new Vector3f(c.getCol(), 0, c.getRow()));
				if (spriteTweens.get((Sprite)characters.get(c)) == null) {
					spriteTweens.put((Sprite)characters.get(c), new TweenVector3f(((Sprite)characters.get(c)).getPosition(), 0.05f, end, currentTime));
				}
			}
		}
			
		
		// update tweens
		currentTime += delta;
		
		List<Sprite> stopTweening = new ArrayList<Sprite>();
		for (Sprite s : spriteTweens.keySet()) {
			if (spriteTweens.get(s).isFinished(currentTime)) {
				stopTweening.add(s);
			} else {
				s.setPosition(spriteTweens.get(s).tween(currentTime));
			}
		}
		
		for (Sprite s : stopTweening) {
			s.setPosition(spriteTweens.get(s).getEndValue());
			spriteTweens.remove(s);
		}
		
		if (cameraTween != null) {
			if (cameraTween.isFinished(currentTime)) {
				cameraTween = null;
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
		entityPositions = new HashMap<artGame.game.Character, Vector3f>();
		for (artGame.game.Character c : GameData.getCharacters()) {
			if (c instanceof Player) {
				chars.put(c, playerSprite.instantiate());
				((Sprite)chars.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
				entityPositions.put(c, new Vector3f(c.getCol(), 0, c.getRow()));
			} else if (c instanceof Guard) {
				chars.put(c, guardSprite.instantiate());
				((Sprite)chars.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
				entityPositions.put(c, new Vector3f(c.getCol(), 0, c.getRow()));
			} else if (c instanceof Sculpture) {
				Matrix4f pos = new Matrix4f();
				pos = pos.multiply(Matrix4f.translate(new Vector3f(c.getCol(), 0, c.getRow())));
				chars.put(c, sculpture1.instantiate(pos));
			}
		}
		return chars;
	}

	private void updateCharacters() {
		List<artGame.game.Character> toRemove = new ArrayList<artGame.game.Character>();
		for (artGame.game.Character c : GameData.getCharacters()) {
			if (c instanceof Player || c instanceof Guard) {
				//get current tween loc..
				Vector3f loc = entityPositions.get(c);
				entityPositions.remove(c);
				entityPositions.put(c, loc);
				//((Sprite)characters.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
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
					if (t.getWall(artGame.game.Character.Direction.NORTH) instanceof Door) {
						level.add(topDoor.instantiate(pos));
					} else {
						level.add(topWall.instantiate(pos));
					}
				}
				if (t.getWall(artGame.game.Character.Direction.SOUTH) != null){
					if (t.getWall(artGame.game.Character.Direction.SOUTH) instanceof Door) {
						level.add(bottomDoor.instantiate(pos));
					} else {
						level.add(bottomWall.instantiate(pos));
					}
				}
				if (t.getWall(artGame.game.Character.Direction.EAST) != null){
					if (t.getWall(artGame.game.Character.Direction.EAST) instanceof Door) {
						level.add(rightDoor.instantiate(pos));
					} else {
						level.add(rightWall.instantiate(pos));
					}
				}
				if (t.getWall(artGame.game.Character.Direction.WEST) != null){
					if (t.getWall(artGame.game.Character.Direction.WEST) instanceof Door) {
						level.add(leftDoor.instantiate(pos));
					} else {
						level.add(leftWall.instantiate(pos));
					}
				}

				if (t instanceof EmptyTile) {
					level.add(floor.instantiate(pos));
				} else if (t instanceof StairTile) {
					StairTile st = (StairTile) t;
					switch(st.getDir()) {
					case NORTH:
						pos = pos.multiply(Matrix4f.rotate(180, 0, 1, 0));
						break;
					case EAST:
						pos = pos.multiply(Matrix4f.rotate(270, 0, 1, 0));
						break;
					case SOUTH:
						
						break;
					case WEST:
						pos = pos.multiply(Matrix4f.rotate(90, 0, 1, 0));
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
						level.add(loos.instantiate(pos));
					} else if (20 <= id && id < 40) {
						// rotate 180 if there's a wall at the bottom
						if (t.getWall(artGame.game.Character.Direction.SOUTH) != null) {
							pos = pos.multiply(Matrix4f.rotate(180, 0, 1, 0));
						}
						level.add(sinks.instantiate(pos));
					} else if (40 <= id && id < 60) {
						level.add(desks.instantiate(pos));
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
		
		topDoor = AssetLoader.instance().loadOBJ("res/top_door.obj", new Vector3f(0.45f, 0.29f, 0.16f));
		bottomDoor = AssetLoader.instance().loadOBJ("res/bottom_door.obj", new Vector3f(0.45f, 0.29f, 0.16f));
		leftDoor = AssetLoader.instance().loadOBJ("res/left_door.obj", new Vector3f(0.45f, 0.29f, 0.16f));
		rightDoor = AssetLoader.instance().loadOBJ("res/right_door.obj", new Vector3f(0.45f, 0.29f, 0.16f));
		
		stairs = AssetLoader.instance().loadOBJ("res/stair.obj",new Vector3f(1,1,1));
		sculpture1 = AssetLoader.instance().loadOBJ("res/sculpture_david.obj", new Vector3f(1,1,1));
		crates = AssetLoader.instance().loadOBJ("res/crates.obj", new Vector3f(0.45f, 0.29f, 0.16f));
		loos = AssetLoader.instance().loadOBJ("res/loo.obj", new Vector3f(1,1,1));
		sinks = AssetLoader.instance().loadOBJ("res/sink.obj", new Vector3f(1,1,1));
		desks = AssetLoader.instance().loadOBJ("res/desk.obj", new Vector3f(0.95f, 0.95f, 0.95f));
		playerSprite = AssetLoader.instance().loadSpritesheet("res/red_player.png", 32);
		guardSprite = AssetLoader.instance().loadSpritesheet("res/guard.png", 32);
	}

	public Camera getCamera(){
		return camera;
	}
	
	public void rotateLeft() {
		if (cameraTween == null) {
			cameraTween = new TweenFloat(currentCameraAngle, 0.5f, 90*(Math.round((currentCameraAngle + 90)/90)), currentTime);
		}
	}
	
	public void rotateRight() {
		if (cameraTween == null) {
			cameraTween = new TweenFloat(currentCameraAngle, 0.5f, 90*(Math.round((currentCameraAngle - 90)/90)), currentTime);
		}
	}

	@Override
	public void dispose(){

	}

	public float getCameraAngle() {
		return currentCameraAngle;
	}

}