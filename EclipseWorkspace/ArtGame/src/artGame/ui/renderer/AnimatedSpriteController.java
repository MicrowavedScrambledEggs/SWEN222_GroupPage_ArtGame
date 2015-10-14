package artGame.ui.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import artGame.ui.renderer.math.Vector2f;
import artGame.ui.renderer.math.Vector3f;

/**
 * A controller for an animated sprite that is viewable from all directions,
 * based off a config file. The chosen sprite will change depending on the
 * camera angle and the direction the sprite is meant to be facing.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class AnimatedSpriteController implements Asset {
	private Sprite sprite;

	private List<List<Vector2f>> animations;

	/**
	 * {@link AnimatedSpriteController} Constructor.
	 * 
	 * @param sprite
	 *            The sprite to control.
	 */
	public AnimatedSpriteController(Sprite sprite) {
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

	private AnimatedSpriteController(Sprite sprite,
			List<List<Vector2f>> animations) {
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

	/**
	 * Updates the sprite's texture. Should be called before drawing.
	 * 
	 * @param dir
	 *            The direction the sprite is facing.
	 * @param angle
	 *            The angle of the camera.
	 * @param tweenValue
	 *            A value between 0 and 1 used for animating the controller
	 *            during movement.
	 */
	public void updateImage(artGame.game.Character.Direction dir, float angle,
			Float tweenValue) {
		float relativeAngle = angle;

		switch (dir) {
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
		if (relativeAngle < 0) {
			relativeAngle += 360;
		}

		int index = 0;
		if (45 <= relativeAngle && relativeAngle < 135) {
			index = 1;
		} else if (135 <= relativeAngle && relativeAngle < 225) {
			index = 2;
		} else if (225 <= relativeAngle && relativeAngle < 315) {
			index = 3;
		}

		if (tweenValue == null) {
			int row = (int) (animations.get(index).get(0).getY());
			int col = (int) (animations.get(index).get(0).getX());
			sprite.setRow(row);
			sprite.setCol(col);
		} else {
			int frame = (int) (tweenValue * animations.get(index).size());
			int row = (int) (animations.get(index).get(frame).getY());
			int col = (int) (animations.get(index).get(frame).getX());
			sprite.setRow(row);
			sprite.setCol(col);
		}

	}

	/**
	 * Creates a copy of the {@link AnimatedSpriteController}.
	 * 
	 * @return
	 */
	public AnimatedSpriteController instantiate() {
		List<List<Vector2f>> animationsCopy = new ArrayList<List<Vector2f>>();

		for (int i = 0; i < animations.size(); i++) {
			animationsCopy.add(new ArrayList<Vector2f>());
			for (int j = 0; j < animations.get(i).size(); j++) {
				float x = animations.get(i).get(j).getX();
				float y = animations.get(i).get(j).getY();
				animationsCopy.get(i).add(new Vector2f(x, y));
			}
		}

		return new AnimatedSpriteController(sprite.instantiate(),
				animationsCopy);
	}

	/**
	 * Sets the {@link AnimatedSpriteController}'s position.
	 * 
	 * @param position
	 *            The new position vector.
	 */
	public void setPosition(Vector3f position) {
		sprite.setPosition(position);
	}

	/**
	 * Gets the {@link AnimatedSpriteController}'s position.
	 * 
	 * @return The position of the {@link AnimatedSpriteController}.
	 */
	public Vector3f getPosition() {
		return sprite.getPosition();
	}

}
