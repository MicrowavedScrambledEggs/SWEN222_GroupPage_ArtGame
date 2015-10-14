package artGame.ui.renderer;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.ui.renderer.math.*;

/**
 * A material class for use with {@link Model}s. Serves as an interface between
 * {@link Model}s and {@link Shader}s.
 * 
 * @author Owner
 *
 */
public class Material {
	private Shader vert;
	private Shader frag;
	private ShaderProgram program;

	private int posAttrib = 0;
	private int uvAttrib = 1;
	private int normAttrib = 2;

	private int modelUniform;
	private int viewUniform;
	private int projUniform;
	private int lightUniform;

	private Vector3f color;

	/**
	 * {@link Material} Constructor.
	 * 
	 * @param verts
	 *            A {@link VertexBufferObject} containing the vertex data.
	 * @param uvs
	 *            A {@link VertexBufferObject} containing the uv data.
	 * @param norms
	 *            A {@link VertexBufferObject} containing the vertex normal
	 *            data.
	 * @param color
	 *            The color of the material.
	 * @param vertSource
	 *            The source code of the vertex shader.
	 * @param fragSource
	 *            The source code of the fragment shader.
	 */
	public Material(VertexBufferObject verts, VertexBufferObject uvs,
			VertexBufferObject norms, Vector3f color, CharSequence vertSource,
			CharSequence fragSource) {
		vert = new Shader(GL_VERTEX_SHADER, vertSource);
		frag = new Shader(GL_FRAGMENT_SHADER, fragSource);

		this.color = color;

		program = new ShaderProgram();
		program.attachShader(vert);
		program.attachShader(frag);
		program.bindFragmentDataLocation(0, "fragColor");

		program.bindAttributeLocation("position", posAttrib);
		program.enableVertexAttribute(posAttrib);
		verts.bind(GL_ARRAY_BUFFER);
		program.setVertexAttributePointer(posAttrib, 3, 0, 0);

		program.bindAttributeLocation("uv", uvAttrib);
		program.enableVertexAttribute(uvAttrib);
		uvs.bind(GL_ARRAY_BUFFER);
		program.setVertexAttributePointer(uvAttrib, 2, 0, 0);

		program.bindAttributeLocation("normal", normAttrib);
		program.enableVertexAttribute(normAttrib);
		norms.bind(GL_ARRAY_BUFFER);
		program.setVertexAttributePointer(normAttrib, 3, 0, 0);

		program.link();
		program.use();

		modelUniform = program.getUniformLocation("model");
		viewUniform = program.getUniformLocation("view");
		lightUniform = program.getUniformLocation("light");
		program.setUniform(program.getUniformLocation("matColor"), this.color);
		projUniform = program.getUniformLocation("projection");

		program.disable();
	}

	/**
	 * @param model
	 *            The model matrix corresponding to the provided vertex data.
	 * @param camera
	 *            The camera to render to.
	 * @param light
	 *            The light direction vector.
	 */
	public void update(Matrix4f model, Camera camera, Vector3f light) {
		program.setUniform(modelUniform, model);
		program.setUniform(viewUniform, camera.getView());
		program.setUniform(lightUniform, light);
		program.setUniform(projUniform, camera.getProjection());
	}

	/**
	 * Enables the {@link Material} for use.
	 */
	public void enable() {
		program.enableVertexAttribute(posAttrib);
		program.enableVertexAttribute(uvAttrib);
		program.enableVertexAttribute(normAttrib);
		program.use();
	}

	/**
	 * Disables the {@link Material}.
	 */
	public void disable() {
		program.disableVertexAttribute(posAttrib);
		program.disableVertexAttribute(uvAttrib);
		program.disableVertexAttribute(normAttrib);
		program.disable();
	}

	/**
	 * Deletes the {@link Material}.
	 */
	public void delete() {
		vert.delete();
		frag.delete();
		program.delete();
	}

	/**
	 * Gets the {@link Material}'s {@link ShaderProgram}.
	 * 
	 * @return The {@link Material}'s {@link ShaderProgram}.
	 */
	public ShaderProgram getProgram() {
		return program;
	}
}
