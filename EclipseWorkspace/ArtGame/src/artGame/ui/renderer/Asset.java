package artGame.ui.renderer;

import artGame.ui.renderer.math.Matrix4f;;

public interface Asset {
	public void draw(Matrix4f view);
	public void delete();
}
