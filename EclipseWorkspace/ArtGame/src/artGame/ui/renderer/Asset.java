package artGame.ui.renderer;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public interface Asset {
	public void draw(Matrix4f camera, Vector3f light);
	public void delete();
}
