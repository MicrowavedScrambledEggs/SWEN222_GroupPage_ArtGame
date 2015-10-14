package artGame.ui.renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import artGame.ui.renderer.math.*;

/**
 * A compiled OpenGL shader program, ready for use.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class ShaderProgram {
	private int id;

	/**
	 * {@link ShaderProgram} Constructor.
	 */
	public ShaderProgram() {
		id = glCreateProgram();
	}

	/**
	 * {@link ShaderProgram} Constructor.
	 * 
	 * @param vert
	 *            The vertex shader to use.
	 * @param frag
	 *            The fragment shader to use.
	 */
	public ShaderProgram(Shader vert, Shader frag) {
		id = glCreateProgram();
		attachShader(vert);
		attachShader(frag);
	}

	/**
	 * Attaches a shader to this program.
	 * 
	 * @param shader
	 *            The shader to attach.
	 */
	public void attachShader(Shader shader) {
		glAttachShader(id, shader.getID());
	}

	/**
	 * Binds the fragment data output location.
	 * 
	 * @param location
	 *            The location to bind the output data to.
	 * @param name
	 *            The name in the shader of the output data variable.
	 */
	public void bindFragmentDataLocation(int location, CharSequence name) {
		glBindFragDataLocation(id, location, name);
	}

	/**
	 * Compiles the shader.
	 */
	public void link() {
		glLinkProgram(id);
		checkStatus();
	}

	/**
	 * Checks the shader's compilation status.
	 */
	public void checkStatus() {
		int status = glGetProgrami(id, GL_LINK_STATUS);
		if (status != GL_TRUE) {
			throw new RuntimeException("Shader linking error: "
					+ glGetProgramInfoLog(id));
		}
	}

	/**
	 * Binds an attribute to the shader program.
	 * 
	 * @param name
	 *            The attribute's name.
	 * @param location
	 *            The attribute's location.
	 */
	public void bindAttributeLocation(CharSequence name, int location) {
		glBindAttribLocation(id, location, name);
	}

	/**
	 * Gets the location of an attribute.
	 * 
	 * @param name
	 *            The attribute's name.
	 * @return The attribute's location.
	 */
	public int getAttributeLocation(CharSequence name) {
		int out = glGetAttribLocation(id, name);
		if (out < 0)
			throw new RuntimeException("Attribute not found");
		return out;
	}

	/**
	 * Enables a vertex attribute.
	 * 
	 * @param location
	 *            The attribute's location.
	 */
	public void enableVertexAttribute(int location) {
		glEnableVertexAttribArray(location);
	}

	/**
	 * Disables a vertex attribute.
	 * 
	 * @param location
	 *            The attribute's location.
	 */
	public void disableVertexAttribute(int location) {
		glDisableVertexAttribArray(location);
	}

	/**
	 * Sets the vertex attribute pointer for loading attribute data.
	 * 
	 * @param location The attribute's location.
	 * @param size The size of each attribute.
	 * @param stride Offset between consecutive attributes.
	 * @param offset Initial offset.
	 */
	public void setVertexAttributePointer(int location, int size, int stride,
			int offset) {
		glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
	}

	/**
	 * Gets the location of a uniform.
	 * 
	 * @param name
	 *            The uniform's name.
	 * @return The uniform's location.
	 */
	public int getUniformLocation(CharSequence name) {
		return glGetUniformLocation(id, name);
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, int value) {
		glUniform1i(location, value);
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, float value) {
		glUniform1f(location, value);
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, Vector2f value) {
		glUniform2fv(location, value.toBuffer());
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, Vector3f value) {
		glUniform3fv(location, value.toBuffer());
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, Vector4f value) {
		glUniform4fv(location, value.toBuffer());
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, Matrix2f value) {
		glUniformMatrix2fv(location, false, value.toBuffer());
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, Matrix3f value) {
		glUniformMatrix3fv(location, false, value.toBuffer());
	}

	/**
	 * Adds or modifies one of the program's uniforms.
	 * 
	 * @param location
	 *            The uniform location.
	 * @param value
	 *            The value to set the uniform to.
	 */
	public void setUniform(int location, Matrix4f value) {
		glUniformMatrix4fv(location, false, value.toBuffer());
	}

	/**
	 * Enables the shader program.
	 */
	public void use() {
		glUseProgram(id);
	}

	/**
	 * Disables the shader program.
	 */
	public void disable() {
		glUseProgram(0);
	}

	/**
	 * Deletes the shader program.
	 */
	public void delete() {
		glDeleteProgram(id);
	}
}
