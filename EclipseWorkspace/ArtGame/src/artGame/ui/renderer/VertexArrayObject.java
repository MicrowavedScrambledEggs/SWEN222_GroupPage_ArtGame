package artGame.ui.renderer;

import static org.lwjgl.opengl.GL30.*;

/*
 * Adapted from LWJGL GitHub wiki
 */
public class VertexArrayObject {
	private int id;
	
	public VertexArrayObject() {
		id = glGenVertexArrays();
	}
	
	public void bind() {
		glBindVertexArray(id);
	}
	
	public void delete() {
		glDeleteVertexArrays(id);
	}
	
	public int getID() {
		return id;
	}
}
