package artGame.ui.screens;

import artGame.ui.renderer.Camera;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public interface Screen {

	public void render(Camera cam, Vector3f light);
	public void dispose();
	
}
