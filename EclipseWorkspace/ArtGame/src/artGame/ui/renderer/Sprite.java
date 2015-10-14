package artGame.ui.renderer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

/**
 * A Sprite asset; a textured plane that always faces the camera.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class Sprite implements Asset {
	private FloatBuffer vertBuffer;
	private VertexArrayObject vao;
	private VertexBufferObject verts;
	private ShaderProgram program;

	private Vector3f position;

	private int positionUniform;
	private int viewUniform;
	private int projUniform;
	private int cameraRightUniform;
	private int cameraUpUniform;
	private int textureUniform;

	private Texture[][] spritesheet;
	private int row;
	private int col;

	private Shader vert;
	private Shader frag;

	/**
	 * Sprite Constructor.
	 * 
	 * @param spritesheet
	 *            The textures to use.
	 * @param pos
	 *            The position of the sprite.
	 */
	public Sprite(Texture[][] spritesheet, Vector3f pos) {

		position = pos;
		this.spritesheet = spritesheet;

		row = 0;
		col = 0;

		vao = new VertexArrayObject();
		vao.bind();

		vertBuffer = BufferUtils.createFloatBuffer(3 * 6);
		vertBuffer.put(-0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		// vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		// vertBuffer.put(-0.5f).put(-0.5f).put(0.0f);
		vertBuffer.flip();

		verts = new VertexBufferObject();
		verts.bind(GL_ARRAY_BUFFER);
		verts.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

		vert = new Shader(GL_VERTEX_SHADER, AssetLoader.instance()
				.loadShaderSource("res/sprite.vert"));
		frag = new Shader(GL_FRAGMENT_SHADER, AssetLoader.instance()
				.loadShaderSource("res/sprite.frag"));

		program = new ShaderProgram();
		program.attachShader(vert);
		program.attachShader(frag);
		program.bindFragmentDataLocation(0, "fragColor");

		program.bindAttributeLocation("squareVerts", 0);
		program.enableVertexAttribute(0);
		verts.bind(GL_ARRAY_BUFFER);
		program.setVertexAttributePointer(0, 3, 0, 0);

		program.link();
		program.use();

		viewUniform = program.getUniformLocation("view");
		cameraRightUniform = program.getUniformLocation("cameraRight");
		cameraUpUniform = program.getUniformLocation("cameraUp");
		positionUniform = program.getUniformLocation("position");
		textureUniform = program.getUniformLocation("sprite");

		long window = GLFW.glfwGetCurrentContext();
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetFramebufferSize(window, width, height);
		float ratio = width.get() / (float) height.get();

		Matrix4f projection = Matrix4f.persp(80f, ratio, 1f, 100f);
		projUniform = program.getUniformLocation("projection");
		program.setUniform(projUniform, projection);
		program.disable();
		verts.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
	}

	@Override
	public void draw(Camera camera, Vector3f light) {
		Matrix4f view = camera.getView();
		float[][] v = view.getData();
		// System.out.println(GL11.glGetError());

		Vector3f cameraRight = new Vector3f(v[0][0], v[0][1], v[0][2]);
		Vector3f cameraUp = new Vector3f(v[1][0], v[1][1], v[1][2]);

		program.use();
		program.setUniform(cameraRightUniform, cameraRight);
		// System.out.println(cameraRight.toString());
		program.setUniform(cameraUpUniform, cameraUp);
		program.setUniform(positionUniform, position);
		program.setUniform(viewUniform, view);
		program.setUniform(textureUniform, 0);

		// System.out.println(cameraUp.toString());

		spritesheet[row][col].bind();

		vao.bind();
		// System.out.println(GL11.glGetError());
		verts.bind(GL_ARRAY_BUFFER);

		// glDepthMask(GL_FALSE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		// draw transparent object ...
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glDisable(GL_BLEND);
		// glDepthMask(GL_TRUE);
		// System.out.println(GL11.glGetError());
		program.disable();
		// System.out.println(GL11.glGetError());
		verts.unbind(GL_ARRAY_BUFFER);
		vao.unbind();
		System.out.println();
	}

	@Override
	public void delete() {
		vert.delete();
		frag.delete();
		program.delete();
	}

	/**
	 * Sets the position of the sprite.
	 * 
	 * @param position
	 *            The new position of the sprite.
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * Gets the sprite's position.
	 * 
	 * @return The sprite's position.
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Creates a copy of the sprite at the same position.
	 * 
	 * @return A copy of the sprite.
	 */
	public Sprite instantiate() {
		return new Sprite(spritesheet, position);
	}

	/**
	 * Gets the row of the spritesheet currently displayed by the sprite.
	 * 
	 * @return The row displayed.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Changes the row of the spritesheet displayed by the sprite.
	 * 
	 * @param row
	 *            The row to use.
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Gets the column of the spritesheet currently displayed by the sprite.
	 * 
	 * @return The column displayed.
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Changes the column of the spritesheet displayed by the sprite.
	 * 
	 * @param col
	 *            The column to use.
	 */
	public void setCol(int col) {
		this.col = col;
	}
}
