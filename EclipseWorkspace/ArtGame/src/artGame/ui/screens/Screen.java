package artGame.ui.screens;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public interface Screen {

	public void render(Matrix4f view, Vector3f light);
	public void dispose();
	
}
