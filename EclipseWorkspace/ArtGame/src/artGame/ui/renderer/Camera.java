package artGame.ui.renderer;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public class Camera {
	private Matrix4f projection;
	private Vector3f position;
	private Vector3f rotation;
	
	public Camera(Matrix4f projectionMatrix) {
		projection = projectionMatrix;
		position = new Vector3f();
		rotation = new Vector3f();
	}
	
	public Matrix4f getProjection() {
		return projection;
	}
	
	public void rotate(Vector3f eulerAngles) {
		rotation = rotation.add(eulerAngles);
	}
	
	public void translate(Vector3f delta) {
		position = position.add(delta);
	}
	
	public Matrix4f getView() {
		Matrix4f view = new Matrix4f();
		view = view.multiply(Matrix4f.rotate(rotation.getX(), 1, 0, 0));
		view = view.multiply(Matrix4f.rotate(rotation.getY(), 0, 1, 0));
		view = view.multiply(Matrix4f.rotate(rotation.getZ(), 0, 0, 1));
		view = view.multiply(Matrix4f.translate(position));
		return view;
	}
}
