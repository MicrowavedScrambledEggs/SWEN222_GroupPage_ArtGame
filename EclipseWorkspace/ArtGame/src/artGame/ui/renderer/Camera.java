package artGame.ui.renderer;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

/**
 * A Camera class for ease of rendering from particular positions and angles.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class Camera {
	private Matrix4f projection;
	private float offset;
	private Vector3f position;
	private Vector3f rotation;

	/**
	 * Camera constructor.
	 * 
	 * @param projectionMatrix
	 *            The projection matrix to be used for the camera.
	 *            {@link Matrix4f#persp(float, float, float, float)} or
	 *            {@link Matrix4f#ortho(float, float, float, float, float, float)}
	 *            recommended.
	 * @param offset
	 *            The z-distance of the camera from its focus (the position
	 *            where the camera is and rotates around).
	 */
	public Camera(Matrix4f projectionMatrix, float offset) {
		projection = projectionMatrix;
		this.offset = offset;
		position = new Vector3f();
		rotation = new Vector3f();
	}

	/**
	 * Gets the camera's projection matrix.
	 * 
	 * @return The camera's projection matrix.
	 */
	public Matrix4f getProjection() {
		return projection;
	}

	/**
	 * Rotates the camera by a specified set of angles.
	 * 
	 * @param rotation
	 *            The (x,y,z) angles to rotate by, represented as a vector.
	 */
	public void rotate(Vector3f rotation) {
		this.rotation = this.rotation.add(rotation);
	}

	/**
	 * Translates the camera by a specified vector.
	 * 
	 * @param delta
	 *            The translation vector.
	 */
	public void translate(Vector3f delta) {
		position = position.add(delta);
	}

	/**
	 * Gets the camera's view matrix.
	 * 
	 * @return The camera's view matrix.
	 */
	public Matrix4f getView() {
		Matrix4f view = new Matrix4f();
		view = view.multiply(Matrix4f.translate(new Vector3f(0, 0, -offset)));
		view = view.multiply(Matrix4f.rotate(rotation.getX(), 1, 0, 0));
		view = view.multiply(Matrix4f.rotate(rotation.getY(), 0, 1, 0));
		view = view.multiply(Matrix4f.rotate(rotation.getZ(), 0, 0, 1));
		view = view.multiply(Matrix4f.translate(position));
		return view;
	}

	/**
	 * Sets the camera's position.
	 * 
	 * @param position
	 *            The new value of the camera's position.
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * Sets the camera's rotation.
	 * 
	 * @param rotation
	 *            The new value of the camera's rotation.
	 */
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
}
