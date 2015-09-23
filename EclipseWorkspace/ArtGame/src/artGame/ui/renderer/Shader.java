package artGame.ui.renderer;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

/*
 * Adapted from LWJGL GitHub wiki
 */
public class Shader {
	
	private int id;

	public Shader(int type, CharSequence source) {
		id = glCreateShader(type);
		glShaderSource(id, source);
		glCompileShader(id);
		
		checkStatus();
	}

	private void checkStatus() {
		int status = glGetShaderi(id, GL_COMPILE_STATUS);
		
		if (status != GL_TRUE) {
			throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(id));
		}
	}
	
	public void delete() {
		glDeleteShader(id);
	}
	
	public int getID() {
		return id;
	}
}
