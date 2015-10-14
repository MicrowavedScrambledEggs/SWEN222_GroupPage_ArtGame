package artGame.ui.screens;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.game.Chest;
import artGame.game.Door;
import artGame.game.EmptyTile;
import artGame.game.ExitTile;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Player;
import artGame.game.Room;
import artGame.game.Sculpture;
import artGame.game.StairTile;
import artGame.game.Tile;
import artGame.game.Wall;
import artGame.ui.gamedata.GameData;
import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Camera;
import artGame.ui.renderer.HumanSpriteController;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.Painting;
import artGame.ui.renderer.animations.Tween;
import artGame.ui.renderer.animations.TweenFloat;
import artGame.ui.renderer.animations.TweenVector3f;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;
import artGame.ui.screens.Screen;
import static artGame.game.Character.Direction.*;

/**
 * A class for rendering the current
 * @author [MOOT] (R. v. Motschelnitz)
 *
 */
public class GameRenderer implements Screen{

	private long window;

	private Camera camera;
	private Vector3f light;

	private static final float CAMERA_ANGLE = 60f;
	private float currentCameraAngle;
	
	private static final boolean SPRITE_TWEENING = false;

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
	private HumanSpriteController playerSprite;
	private HumanSpriteController guardSprite;
	private Map<Integer, Painting> paintingCatalogue;

	private Map<artGame.game.Character, Asset> characters;
	private Map<HumanSpriteController, artGame.game.Character> controllerToCharacter;
	private Map<artGame.game.Character, Vector3f> entityPositions;
	private Map<Model, Tile> tiles;
	private Map<Room, List<Model>> rooms;
	private Map<Wall, Painting> paintings;
	
	private Map<HumanSpriteController, Tween<Vector3f>> spriteTweens;
	private Tween<Float> cameraTween;
	
	private float currentTime;

	/**
	 * GameRenderer Constructor
	 */
	public GameRenderer(){
		resetAssets();
		controllerToCharacter = new HashMap<HumanSpriteController, artGame.game.Character>();
		paintings = new HashMap<Wall, Painting>();
		paintingCatalogue = loadPaintings("res/paintings.txt");
		tiles = loadFullLevel();
		rooms = createRooms();
		characters = loadCharacters();
		
		spriteTweens = new HashMap<HumanSpriteController, Tween<Vector3f>>();
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
		
		// update tweens
		currentTime += delta;
		
		if (SPRITE_TWEENING) {
			for (artGame.game.Character c : entityPositions.keySet()) {
				if (c.getCol() != entityPositions.get(c).getX() || 
					c.getRow() != entityPositions.get(c).getZ()) {
					
					//Vector3f start = entityPositions.get(c);
					Vector3f end = new Vector3f(c.getCol(), 0, c.getRow());
					entityPositions.put(c, new Vector3f(c.getCol(), 0, c.getRow()));
					if (spriteTweens.get((HumanSpriteController)characters.get(c)) == null) {
						spriteTweens.put((HumanSpriteController)characters.get(c), new TweenVector3f(((HumanSpriteController)characters.get(c)).getPosition(), 0.5f, end, currentTime));
					}
				}
			}
			
			List<HumanSpriteController> stopTweening = new ArrayList<HumanSpriteController>();
			for (HumanSpriteController s : spriteTweens.keySet()) {
				if (spriteTweens.get(s).isFinished(currentTime)) {
					stopTweening.add(s);
					s.setPosition(spriteTweens.get(s).getEndValue());
					s.updateImage(controllerToCharacter.get(s).getDir(), currentCameraAngle, null);
				} else {
					s.setPosition(spriteTweens.get(s).tween(currentTime));
					s.updateImage(controllerToCharacter.get(s).getDir(), currentCameraAngle, spriteTweens.get(s).completion(currentTime));
				}
			}
			
			for (HumanSpriteController s : stopTweening) {
				spriteTweens.remove(s);
			}
		} else {
			for (artGame.game.Character c : characters.keySet()) {
				if (characters.get(c) instanceof HumanSpriteController) {
					((HumanSpriteController) characters.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
					((HumanSpriteController) characters.get(c)).updateImage(c.getDir(), currentCameraAngle, null);
				}
			}
		}
		
		if (cameraTween != null) {
			if (cameraTween.isFinished(currentTime)) {
				cameraTween = null;
				currentCameraAngle = 90*(Math.round(currentCameraAngle/90));
			} else {
				float tween = cameraTween.tween(currentTime);
				currentCameraAngle = tween;
			}
		}
		
		camera.setRotation(new Vector3f(CAMERA_ANGLE, currentCameraAngle, 0));
		camera.setPosition(((HumanSpriteController)characters.get(GameData.getPlayer())).getPosition().scale(-1));

		for (Asset a : renderList) {
			a.draw(camera, light);
		}
	}
	private Map<Room, List<Model>> createRooms() {
		Map<Room, List<Model>> temp = new HashMap<Room, List<Model>>();
		for (Model m : tiles.keySet()) {
			Room r = tiles.get(m).getRoom();
			List<Model> ml = temp.get(r);
			if (ml == null) {
				ml = new ArrayList<Model>();
			}
			if (temp.get(r) == null) {
				temp.put(r, ml);
			}
			ml.add(m);
		}
		return temp;
	}

	private List<Asset> getRenderList() {
		List<Asset> scene = new ArrayList<Asset>();

		if (tiles == null) {
			tiles = loadFullLevel();
		}
		

		int row = GameData.getPlayer().getRow();
		int col = GameData.getPlayer().getCol();
		Tile playerTile = GameData.getFloor().getTile(row, col);
		//System.out.println(rooms == null);
		//System.out.println(rooms.get(playerTile.getRoom()) == null);
		scene.addAll(rooms.get(playerTile.getRoom()));

		updateSculptures();
		for (artGame.game.Character c : characters.keySet()) {
			if (playerTile.getRoom().getTiles().contains(GameData.getFloor().getTile(c.getRow(), c.getCol()))) {
				scene.add(characters.get(c));
			}
		}
		
		updatePaintings();
		//for (Wall w : paintings.keySet()) {
		//	if (w.) {
		//		
		//	}
		//}
		scene.addAll(paintings.values());
		return scene;
	}

	private void updatePaintings() {
		List<Wall> toRemove = new ArrayList<Wall>();
		for (Wall w : paintings.keySet()) {
			if (w.getArt() == null) {
				toRemove.add(w);
			}
		}
		
		for (Wall w: toRemove) {
			paintings.remove(w);
		}
	}

	private Map<artGame.game.Character, Asset> loadCharacters() {
		Map<artGame.game.Character, Asset> chars = new HashMap<artGame.game.Character, Asset>();
		entityPositions = new HashMap<artGame.game.Character, Vector3f>();
		for (artGame.game.Character c : GameData.getCharacters()) {
			if (c instanceof Player) {
				chars.put(c, playerSprite.instantiate());
				controllerToCharacter.put((HumanSpriteController)chars.get(c), c);
				((HumanSpriteController)chars.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
				entityPositions.put(c, new Vector3f(c.getCol(), 0, c.getRow()));
			} else if (c instanceof Guard) {
				chars.put(c, guardSprite.instantiate());
				controllerToCharacter.put((HumanSpriteController)chars.get(c), c);
				((HumanSpriteController)chars.get(c)).setPosition(new Vector3f(c.getCol(), 0, c.getRow()));
				entityPositions.put(c, new Vector3f(c.getCol(), 0, c.getRow()));
			} else if (c instanceof Sculpture) {
				Matrix4f pos = new Matrix4f();
				pos = pos.multiply(Matrix4f.translate(new Vector3f(c.getCol(), 0, c.getRow())));
				chars.put(c, sculpture1.instantiate(pos));
			}
		}
		return chars;
	}

	private void updateSculptures() {
		List<artGame.game.Character> toRemove = new ArrayList<artGame.game.Character>();
		for (artGame.game.Character c : characters.keySet()) {
			if (c instanceof Sculpture) {
				if (((Sculpture)c).isTaken()) {
					toRemove.add(c);
				}
			}
		}
		
		for (artGame.game.Character c : toRemove) {
			characters.remove(c);
		}
	}
	
	private Map<Integer, Painting> loadPaintings(String idFilePath) {
		Map<Integer, Painting> temp = new HashMap<Integer, Painting>();

		Scanner scan = null;
		try {
			scan = new Scanner(new File(idFilePath));
			while(scan.hasNext()) {
				temp.put(scan.nextInt(), AssetLoader.instance().loadPainting("res/paintings/" + scan.next(), 64));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scan != null) {
				scan.close();
			}
		}

		return temp;
	}

	private Map<Model, Tile> loadFullLevel() {
		Map<Model, Tile> level = new HashMap<Model, Tile>();
		Floor world = GameData.getFloor();
		for (int row = 0; row < world.getHeight(); row++) {
			for (int col = 0; col < world.getWidth(); col++) {
				Tile t = world.getTile(row, col);
				Matrix4f pos = new Matrix4f();
				pos = pos.multiply(Matrix4f.translate(new Vector3f(col, 0, row)));

				if(t == null){
					continue;
				}

				if (t.getWall(NORTH) != null){
					if (t.getWall(NORTH) instanceof Door) {
						level.put(topDoor.instantiate(pos), t);
					} else {
						level.put(topWall.instantiate(pos), t);
						if (t.getWall(NORTH).getArt() != null) {
							paintings.put(t.getWall(NORTH), paintingCatalogue.get(1).instantiate(pos));
						}
					}
				}
				if (t.getWall(artGame.game.Character.Direction.SOUTH) != null){
					if (t.getWall(artGame.game.Character.Direction.SOUTH) instanceof Door) {
						level.put(bottomDoor.instantiate(pos), t);
					} else {
						level.put(bottomWall.instantiate(pos), t);
						Matrix4f paintingPos = pos.multiply(Matrix4f.rotate(180, 0, 1, 0));
						if (t.getWall(SOUTH).getArt() != null) {
							paintings.put(t.getWall(SOUTH), paintingCatalogue.get(1).instantiate(paintingPos));
						}
					}
				}
				if (t.getWall(artGame.game.Character.Direction.EAST) != null){
					if (t.getWall(artGame.game.Character.Direction.EAST) instanceof Door) {
						level.put(rightDoor.instantiate(pos), t);
					} else {
						level.put(rightWall.instantiate(pos), t);
						Matrix4f paintingPos = pos.multiply(Matrix4f.rotate(270, 0, 1, 0));
						if (t.getWall(EAST).getArt() != null) {
							paintings.put(t.getWall(EAST), paintingCatalogue.get(1).instantiate(paintingPos));
						}
					}
				}
				if (t.getWall(artGame.game.Character.Direction.WEST) != null){
					if (t.getWall(artGame.game.Character.Direction.WEST) instanceof Door) {
						level.put(leftDoor.instantiate(pos), t);
					} else {
						level.put(leftWall.instantiate(pos), t);
						Matrix4f paintingPos = pos.multiply(Matrix4f.rotate(90, 0, 1, 0));
						if (t.getWall(WEST).getArt() != null) {
							paintings.put(t.getWall(WEST), paintingCatalogue.get(1).instantiate(paintingPos));
						}
					}
				}

				if (t instanceof EmptyTile) {
					level.put(floor.instantiate(pos), t);
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
					level.put(stairs.instantiate(pos), t);
				} else if (t instanceof ExitTile) {
					level.put(floor.instantiate(pos), t);
				} else if (t instanceof Chest) {
					level.put(floor.instantiate(pos), t);
					
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
						level.put(loos.instantiate(pos), t);
					} else if (20 <= id && id < 40) {
						// rotate 180 if there's a wall at the bottom
						if (t.getWall(artGame.game.Character.Direction.SOUTH) != null) {
							pos = pos.multiply(Matrix4f.rotate(180, 0, 1, 0));
						}
						level.put(sinks.instantiate(pos), t);
					} else if (40 <= id && id < 60) {
						level.put(desks.instantiate(pos), t);
					} else { // crate
						level.put(crates.instantiate(pos), t);
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
		
		
		playerSprite = new HumanSpriteController(AssetLoader.instance().loadSpritesheet("res/red_player.png", 32));
		guardSprite = new HumanSpriteController(AssetLoader.instance().loadSpritesheet("res/guard.png", 32));
	}

	/**
	 * Gets the renderer's camera.
	 * @return The renderer's camera
	 */
	public Camera getCamera(){
		return camera;
	}
	
	/**
	 * Rotates the renderer's camera 90 degrees to the left.
	 */
	public void rotateLeft() {
		if (cameraTween == null) {
			cameraTween = new TweenFloat(currentCameraAngle, 0.5f, 90*(Math.round((currentCameraAngle + 90)/90)), currentTime);
		}
	}
	
	/**
	 * Rotates the renderer's camera 90 degrees to the right.
	 */
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
