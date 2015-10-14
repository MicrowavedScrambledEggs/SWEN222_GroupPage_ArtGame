package artGame.ui.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import artGame.ui.renderer.math.Vector2f;
import artGame.ui.renderer.math.Vector3f;

public class HumanSpriteController implements Asset {
	private Sprite sprite;
	
	private List<List<Vector2f>> animations;
	
	public HumanSpriteController(Sprite sprite) {
		this.sprite = sprite.instantiate();
		animations = new ArrayList<List<Vector2f>>();
		Scanner scan = null;
		try {
			scan = new Scanner(new File("res/human.spriteconfig"));
			for (int i = 0; scan.hasNextLine(); i++) {
				animations.add(new ArrayList<Vector2f>());
				String line = scan.nextLine();
				String[] vectors = line.split(" ");
				for (int j = 0; j < vectors.length; j++) {
					String[] vector = vectors[j].split(",");
					int x = Integer.parseInt(vector[0]);
					int y = Integer.parseInt(vector[1]);
					animations.get(i).add(new Vector2f(x, y));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scan != null) {
				scan.close();
			}
		}
	}
	
	private HumanSpriteController(Sprite sprite, List<List<Vector2f>> animations) {
		this.sprite = sprite;
		this.animations = animations;
	}

	@Override
	public void draw(Camera camera, Vector3f light) {
		sprite.draw(camera, light);
	}

	@Override
	public void delete() {
		sprite.delete();
	}
	
	public void updateImage(artGame.game.Character.Direction dir, float angle, Float tweenValue) {
		float relativeAngle = angle;
		
		switch(dir) {
		case NORTH:
			relativeAngle += 180;
			break;
		case EAST:
			relativeAngle += 90;
			break;
		case SOUTH:
			relativeAngle += 0;
			break;
		case WEST:
			relativeAngle += 270;
			break;
		default:
			break;
		
		}
		
		relativeAngle = relativeAngle % 360;
		if (relativeAngle < 0)
		{
		    relativeAngle += 360;
		}
		
		int index = 0;
		if (45 <= relativeAngle && relativeAngle < 135) {
			index = 1;
		} else if (135 <= relativeAngle && relativeAngle < 225){
			index = 2;
		} else if (225 <= relativeAngle && relativeAngle < 315) {
			index = 3;
		}
		
		if (tweenValue == null) {
			int row = (int)(animations.get(index).get(0).getY());
			int col = (int)(animations.get(index).get(0).getX());
			sprite.setRow(row);
			sprite.setCol(col);
		} else {
			int frame = (int) (tweenValue * animations.get(index).size());
			int row = (int)(animations.get(index).get(frame).getY());
			int col = (int)(animations.get(index).get(frame).getX());
			sprite.setRow(row);
			sprite.setCol(col);
		}
		
	}
	
	public HumanSpriteController instantiate() {
		List<List<Vector2f>> animationsCopy = new ArrayList<List<Vector2f>>();
		
		for (int i = 0; i < animations.size(); i++) {
			animationsCopy.add(new ArrayList<Vector2f>());
			for (int j = 0; j < animations.get(i).size(); j++) {
				float x = animations.get(i).get(j).getX();
				float y = animations.get(i).get(j).getY();
				animationsCopy.get(i).add(new Vector2f(x, y));
			}
		}
		
		return new HumanSpriteController(sprite.instantiate(), animationsCopy);
	}
	
	public void setPosition(Vector3f position) {
		sprite.setPosition(position);
	}
	
	public Vector3f getPosition() {
		return sprite.getPosition();
	}

}