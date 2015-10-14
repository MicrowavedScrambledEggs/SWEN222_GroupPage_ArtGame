package artGame.ui.renderer;

import artGame.ui.renderer.math.Vector3f;

/**
 * Represents a drawable OpenGL asset, for example a 3D model or a sprite.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public interface Asset {
	/**
	 * Renders the asset to the current OpenGL window.
	 * 
	 * @param camera
	 *            The camera used for rendering the asset.
	 * @param light
	 *            The light direction vector used for illumination.
	 */
	public void draw(Camera camera, Vector3f light);

	/**
	 * Frees the asset from OpenGL's memory.
	 */
	public void delete();
}
