package artGame.ui.renderer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;
import artGame.ui.renderer.math.*;

/*
 * Adapted from LWJGL GitHub wiki
 */
public class ShaderProgram {
	private int id;
	
	public ShaderProgram() {
		id = glCreateProgram();
	}
	
	public ShaderProgram(Shader vert, Shader frag) {
		id = glCreateProgram();
		attachShader(vert);
		attachShader(frag);
	}

	public void attachShader(Shader shader) {
		glAttachShader(id, shader.getID());
	}
	
	public void bindFragmentDataLocation(int color, CharSequence name) {
		glBindFragDataLocation(id, color, name);
	}
	
	public void link() {
		glLinkProgram(id);
		checkStatus();
	}
	
	public void checkStatus() {
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException("Shader linking error: " + glGetProgramInfoLog(id));
        }
    }
	
	public void bindAttributeLocation(CharSequence name, int location) {
		glBindAttribLocation(id, location, name);
	}
	
	public int getAttributeLocation(CharSequence name) {
        int out = glGetAttribLocation(id, name);
        if (out < 0) throw new RuntimeException("Attribute not found");
        return out;
    }
	
	public void enableVertexAttribute(int location) {
        glEnableVertexAttribArray(location);
    }
	
	public void disableVertexAttribute(int location) {
        glDisableVertexAttribArray(location);
    }
	
	public void setVertexAttributePointer(int location, int size, int stride, int offset) {
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
    }
	
	public int getUniformLocation(CharSequence name) {
        return glGetUniformLocation(id, name);
    }
	
	public void setUniform(int location, int value) {
		glUniform1i(location, value);
	}
	
	public void setUniform(int location, float value) {
		glUniform1f(location, value);
	}
	
	public void setUniform(int location, Vector2f value) {
		glUniform2fv(location, value.toBuffer());
	}
	
	public void setUniform(int location, Vector3f value) {
		glUniform3fv(location, value.toBuffer());
	}
	
	public void setUniform(int location, Vector4f value) {
		glUniform4fv(location, value.toBuffer());
	}
	
	public void setUniform(int location, Matrix2f value) {
		glUniformMatrix2fv(location, false, value.toBuffer());
	}
	
	public void setUniform(int location, Matrix3f value) {
		glUniformMatrix3fv(location, false, value.toBuffer());
	}
	
	public void setUniform(int location, Matrix4f value) {
		glUniformMatrix4fv(location, false, value.toBuffer());
	}
	
	public void use() {
		glUseProgram(id);
	}
	
	public void disable() {
		glUseProgram(0);
	}

	public void delete() {
		glDeleteProgram(id);
	}
}
