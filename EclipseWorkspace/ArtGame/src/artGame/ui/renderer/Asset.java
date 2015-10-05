package artGame.ui.renderer;

import artGame.ui.renderer.math.Vector3f;

public interface Asset {
	public void draw(Camera camera, Vector3f light);
	public void delete();
}
