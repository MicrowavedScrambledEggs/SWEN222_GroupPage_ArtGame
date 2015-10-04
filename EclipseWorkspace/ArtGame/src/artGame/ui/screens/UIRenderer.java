package artGame.ui.screens;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import artGame.game.Item;
import artGame.ui.GameData;
import artGame.ui.ItemSlot;
import artGame.ui.Widget;
import artGame.ui.renderer.Texture;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;


public class UIRenderer implements Screen {
	
	private long window;
	
	private List<Widget> assets;
	private List<ItemSlot> inventory;
	
	private IntBuffer width;
	private IntBuffer height;
	
	private Vector3f testLoc = new Vector3f(0f, 0f, 0f);
	
	private HashMap<Integer, Widget> itemsById;
	
	public UIRenderer(long window){
		this.window = window;
	}
	
	@Override
	public void initialize() {
		width = BufferUtils.createIntBuffer(1);
		height = BufferUtils.createIntBuffer(1);
		createUI();
	}
	
	@Override
	public void render(Matrix4f view, Vector3f light) {
		
		for(Item item: GameData.getCurrentItems()){
			boolean contains = false;
			int freeSlot = -1;
			for(int i = 0; i < inventory.size(); i++){
				ItemSlot slot = inventory.get(i);
				if(slot.getItem() == item.ID){
					contains = true;
				}
				if(slot.getItem() == -1){
					freeSlot = i;
				}
			}
			if(!contains){
				inventory.get(freeSlot).setItem(item.ID);
			}
		}
		
		glfwGetFramebufferSize(getWindow(), width, height);
		float ratio = (float)width.get()/(float)height.get();
		
		for(Widget asset : assets){	
			//testLoc = testLoc.add(new Vector3f(0.01f, 0.01f*ratio, 0f));
			//System.out.println(testLoc.toString());
			//asset.setLocation(testLoc);
			asset.draw(view, light);
		}
		
		for(ItemSlot item : inventory){
			item.draw(view, light);
			if(item.getItem() != -1){
				if(itemsById.containsKey(item.getItem())){
					Widget icon = itemsById.get(item.getItem());
					icon.setScreenLocation(item.getWidget().getX(), item.getWidget().getY());
					icon.draw(view, light);
				}
			}
			
		}
		
		width.rewind();
		height.rewind();
		
	}
	
	public void dispose(){
	
	}

	@Override
	public long getWindow() {
		
		return window;
	}
	
	private void createUI(){
		assets = new ArrayList<>();
		inventory = new ArrayList<>();
		itemsById = new HashMap<>();
		
		float y = 1.02f;
		
		float invSlotCount = 8;
		float startX = 0.1f;
		float endX = 0.913f;
		float gap = 0.01f;
		float size = (endX-startX)/invSlotCount;
		//float size = 0.2f;
		float scale = 0.3f;
		System.out.println(scale);
		float lastX = startX+gap;
		for(int i = 0; i < invSlotCount; i++){
			
			Widget w = loadWidget("res/InventoryBG.png", 64, lastX, y);
			w.setScale(scale);
			if(w != null){
				inventory.add(new ItemSlot(w));
			}
			
			lastX += size+gap;
			
		}
		
		//test key item id == 2
		Widget icon2 = loadWidget("res/key_placeholder.png", 64, 0.8f, 0.8f);
		
		if(icon2 != null){
			itemsById.put(2, icon2);
		}
		
		////Widget square = loadWidget("res/yellow_sq.png", 64, 0.8f, 0.8f);
		//if (square != null) {
		//	assets.add(square);
		//}
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
	
}

