package artGame.ui.renderer;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;

/*
 * Adapted from LWJGL GitHub wiki
 */
public class VertexBufferObject {
	
	private final int id;
	
	public VertexBufferObject() {
		id = glGenBuffers();
	}
	
	public void bind(int target) {
		glBindBuffer(target, id);
	}
	
	public void uploadBufferData(int target, FloatBuffer data, int usage) {
		glBufferData(target, data, usage);
	}
	
	public void uploadBufferSubData(int target, long offset, FloatBuffer data) {
		glBufferSubData(target, offset, data);
	}
	
	public void delete() {
		glDeleteBuffers(id);
	}
	
	public int getID() {
		return id;
	}

	public void unbind(int target) {
		glBindBuffer(target, 0);
	}

}
