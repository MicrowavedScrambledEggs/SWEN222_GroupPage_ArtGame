package artGame.ui.renderer;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

/**
 * An OpenGL shader.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class Shader {

	private int id;

	/**
	 * @param type
	 *            The type of shader; either GL_VERTEX_SHADER or
	 *            GL_FRAGMENT_SHADER.
	 * @param source
	 *            The source code of the shader.
	 */
	public Shader(int type, CharSequence source) {
		id = glCreateShader(type);
		glShaderSource(id, source);
		glCompileShader(id);

		checkStatus();
	}

	private void checkStatus() {
		int status = glGetShaderi(id, GL_COMPILE_STATUS);

		if (status != GL_TRUE) {
			throw new RuntimeException("Shader compile error: "
					+ glGetShaderInfoLog(id));
		}
	}

	/**
	 * Deletes the shader.
	 */
	public void delete() {
		glDeleteShader(id);
	}

	/**
	 * Gets the OpenGL ID of the shader.
	 * 
	 * @return The shader's ID.
	 */
	public int getID() {
		return id;
	}
}
