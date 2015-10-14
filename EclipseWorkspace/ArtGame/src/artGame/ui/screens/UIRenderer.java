package artGame.ui.screens;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import artGame.game.Item;
import artGame.game.Sculpture;
import artGame.ui.FontHandler;
import artGame.ui.ItemSlot;
import artGame.ui.Widget;
import artGame.ui.gamedata.ArtItem;
import artGame.ui.gamedata.GameData;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Camera;
import artGame.ui.renderer.Painting;
import artGame.ui.renderer.Texture;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

/**
 * A Screen implementation for the UI
 * @author Tim King 300282037
 *
 */
public class UIRenderer implements Screen {

	private long window;

	private List<Widget> assets;
	private List<ItemSlot> inventory;

	private IntBuffer width;
	private IntBuffer height;

	private Vector3f testLoc = new Vector3f(0f, 0f, 0f);

	private Map<Integer, Widget> itemsById;

	private Widget fontWidget;
	
	//For storing item id -> item value
	private Map<Integer, Integer> valueById;
	
	public UIRenderer(long window){
		this.window = window;
		
		valueById = new HashMap<>();
		
		//Load in values..
		valueById = getItemValues();
		
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);
		createUI();
		
		setPopupText("welcome : - 0000");
		
	}

	/**
	 * Renders the UI 
	 */
	@Override
	public void render(float delta) {
		Camera cam = GLWindow.getCamera();
		Matrix4f view = null;
		if(cam != null){
			view = cam.getView();
		}
		
		setPopupText("art value: "+getInventoryValue());
		
		if(GameData.isOut()){
			setPopupText("YOU ARE OUT");
		}
		
		Vector3f light = GLWindow.getLight();

		updateInventory();

		glfwGetFramebufferSize(GLWindow.getWindow(), width, height);
		int wVal = width.get();
		int hVal = height.get();
		float ratio = (float)wVal/(float)hVal;

		for(Widget asset : assets){
			//testLoc = testLoc.add(new Vector3f(0.01f, 0.01f*ratio, 0f));
			//System.out.println(testLoc.toString());
			//asset.setLocation(testLoc);
			asset.draw(cam, light);
		}
		int count = 0;
		for(ItemSlot item : inventory){
			item.draw();
			if(item.getItem() != -1){
				if(itemsById.containsKey(item.getItem())){
					Widget icon = itemsById.get(item.getItem());

					float xOff = (item.getWidget().getScale() * 64)/wVal;
					xOff = 0;
					float yOff = -0.3f*item.getWidget().getScale();

					icon.setScreenLocation(item.getWidget().getX()+xOff, item.getWidget().getY()+yOff);
					icon.draw();
				} else {
					Widget icon = itemsById.get(-1);

					float xOff = (item.getWidget().getScale() * 64)/wVal;
					xOff = 0;
					float yOff = -0.3f*item.getWidget().getScale();

					icon.setScreenLocation(item.getWidget().getX()+xOff, item.getWidget().getY()+yOff);
					icon.draw();
				}
			}
			count++;

		}
		
		fontWidget.draw();

		width.rewind();
		height.rewind();
	}

	private int getInventoryValue(){
		int value = 0;
		for(ItemSlot s : inventory){
			if(s != null && s.getItem() > 0 && valueById.containsKey(s.getItem())){
				value += valueById.get(s.getItem());
			}
		}
		return value;
	}
	
	/**
	 * Updates the inventory with the latest data from server
	 */
	private void updateInventory() {
		resetInventory();
		
		if(GameData.getPlayer() == null){
			return;
		}
		
		List<Integer> sortedIds = new ArrayList<>();
		
		for(Item item : GameData.getPlayer().getInventory()){
			sortedIds.add(item.ID);
		}
		
		Collections.sort(sortedIds);
		
		for(Integer item : sortedIds){
			int freeSlot = -1;
			for(int i = 0; i < inventory.size(); i++){
				ItemSlot slot = inventory.get(i);

				if(slot.getItem() == -1){
					freeSlot = i;
					break;
				}
			}

			if(freeSlot != -1){
				inventory.get(freeSlot).setItem(item);
			}
		}
	}

	private void resetInventory() {
		for(ItemSlot slot : inventory){
			slot.setItem(-1);
		}
	}

	/**
	 * Called when the screen is destroyed
	 */
	@Override
	public void dispose(){

	}

	/**
	 * Creates the UI - initiates widgets, loads images
	 */
	private void createUI(){
		assets = new ArrayList<>();
		inventory = new ArrayList<>();
		itemsById = new HashMap<>();

		float y = -0.2f;

		float invSlotCount = 10;
		float startX = 0.1f;
		float endX = 0.913f;
		float gap = 0.01f;
		float size = (endX-startX)/invSlotCount;
		//float size = 0.2f;
		float scale = 0.3f;

		float lastX = startX+gap;
		for(int i = 0; i < invSlotCount; i++){

			Widget w = loadWidget("res/InventoryBG.png", 64, lastX, y);
			w.setScale(scale);
			if(w != null){
				ItemSlot slot = new ItemSlot(w);
				slot.setItem(-1);
				inventory.add(slot);
			}

			lastX += size+gap;

		}

		//test key item id == 2
		Widget icon2 = loadWidget("res/key.png", 32, 0.8f, 0.8f);
		Widget defIcon = loadWidget("res/default_icon.png", 64, 0.8f, 0.8f);
		
		Map<Integer, Widget> paintings = loadPaintings("res/paintings.txt");
		for(int i : paintings.keySet()){
			if(paintings.get(i) != null){
				paintings.get(i).setScale(scale*0.6f);
				itemsById.put(i, paintings.get(i));
			}
		}
		
		
		if(icon2 != null){
			icon2.setScale(scale);
			itemsById.put(1, icon2);
		}
		if(defIcon != null){
			defIcon.setScale(scale);
			itemsById.put(-1, defIcon);
		}

		////Widget square = loadWidget("res/yellow_sq.png", 64, 0.8f, 0.8f);
		//if (square != null) {
		//	assets.add(square);
		//}
	}
	
	/**
	 * Sets the popup text to be displayed on screen
	 * @param string
	 */
	public void setPopupText(String string){
		BufferedImage fontImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		fontImage = FontHandler.process(fontImage, string, new Color(0.2f, 0.2f, 0.9f, 0.8f));
		
		fontWidget = this.loadWidgetByImage(fontImage, 256, 0.5f, -0.3f);
		fontWidget.setScale(1);
	}
	
	/**
	 * Loads in paintings for use with icons
	 * @param idFilePath
	 * @return
	 */
	private Map<Integer, Widget> loadPaintings(String idFilePath) {
		Map<Integer, Widget> temp = new HashMap<Integer, Widget>();
		
		Scanner scan = null;
		try {
			scan = new Scanner(new File(idFilePath));
			while(scan.hasNext()) {
				temp.put(scan.nextInt(), loadWidget("res/paintings/" + scan.next(), 64, 1f, 1f));
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
	
	/**
	 * Loads in item values from the base game map
	 * @return
	 */
	private Map<Integer, Integer> getItemValues() {
		Map<Integer, Integer> map = new HashMap<>();
		
		for(ArtItem a : GameData.getAllArt()){
			map.put(a.ID, a.value);
			System.out.println("Setting art " + a.ID + " to value: " + a.value);
		}
		
		for(Sculpture s : GameData.getStartingSculptures()){
			map.put(s.getId(), s.getValue());
			System.out.println("Setting sculp " + s.getId() + " to value: " + s.getValue());
		}
		
		return map;
	}

	private Widget loadWidget(String filepath, int size, float x, float y) {
		BufferedImage sheet;
		try {
			sheet = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		// TODO Fix spritesheet bug
		BufferedImage[][] sprites = new BufferedImage[sheet.getWidth() / size][sheet.getHeight() / size];
		Texture[][] textures = new Texture[sheet.getWidth() / size][sheet.getHeight() / size];
		for (int col = 0; col < sprites.length; col++) {
			for(int row = 0; row < sprites[col].length; row++) {
				sprites[col][row] = sheet.getSubimage(col * size, row * size, size, size);
				textures[row][col] = new Texture(sprites[col][row], size);
			}
		}
		Widget w = new Widget(textures, new Vector3f(0, 0, 0));
		w.setScreenLocation(x, y);
		w.setScale(0.095f);
		return w;
	}
	
	private Widget loadWidgetByImage(BufferedImage img, int size, float x, float y) {
		BufferedImage sheet = img;

		// TODO Fix spritesheet bug
		BufferedImage[][] sprites = new BufferedImage[sheet.getWidth() / size][sheet.getHeight() / size];
		Texture[][] textures = new Texture[sheet.getWidth() / size][sheet.getHeight() / size];
		for (int col = 0; col < sprites.length; col++) {
			for(int row = 0; row < sprites[col].length; row++) {
				sprites[col][row] = sheet.getSubimage(col * size, row * size, size, size);
				textures[row][col] = new Texture(sprites[col][row], size);
			}
		}
		Widget w = new Widget(textures, new Vector3f(0, 0, 0));
		w.setScreenLocation(x, y);
		w.setScale(0.095f);
		return w;
	}

}

