package artGame.ui.renderer;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector2f;
import artGame.ui.renderer.math.Vector3f;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * A 3D model using modern OpenGL.
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class Model implements Asset {
	private FloatBuffer vertBuffer;
	private FloatBuffer uvBuffer;
	private FloatBuffer normBuffer;

	private VertexArrayObject vao;
	private VertexBufferObject vertBufferObject;
	private VertexBufferObject uvBufferObject;
	private VertexBufferObject normBufferObject;

	private Matrix4f model;
	private int numVerts;
	private Material material;
	private Vector3f color;

	/**
	 * {@link Model} Constructor.
	 * 
	 * @param verts
	 *            The list of vertices used in the model.
	 * @param uvs
	 *            The list of UVs used in the model.
	 * @param norms
	 *            The list of vertex normals used in the model.
	 * @param color
	 *            The color of the model.
	 * @param modelMatrix
	 *            The model's model matrix.
	 */
	public Model(List<Vector3f> verts, List<Vector2f> uvs,
			List<Vector3f> norms, Vector3f color, Matrix4f modelMatrix) {
		numVerts = verts.size();

		this.color = color;

		model = modelMatrix;
		vao = new VertexArrayObject();
		vao.bind();

		vertBuffer = BufferUtils.createFloatBuffer(3 * verts.size());
		for (Vector3f vert : verts) {
			vertBuffer.put(vert.getX()).put(vert.getY()).put(vert.getZ());
		}
		vertBuffer.flip();

		uvBuffer = BufferUtils.createFloatBuffer(2 * uvs.size());
		for (Vector2f vert : uvs) {
			uvBuffer.put(vert.getX()).put(vert.getY());
		}
		uvBuffer.flip();

		normBuffer = BufferUtils.createFloatBuffer(3 * norms.size());
		for (Vector3f norm : norms) {
			normBuffer.put(norm.getX()).put(norm.getY()).put(norm.getZ());
		}
		normBuffer.flip();

		vertBufferObject = new VertexBufferObject();
		vertBufferObject.bind(GL_ARRAY_BUFFER);
		vertBufferObject.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer,
				GL_STATIC_DRAW);

		uvBufferObject = new VertexBufferObject();
		uvBufferObject.bind(GL_ARRAY_BUFFER);
		uvBufferObject.uploadBufferData(GL_ARRAY_BUFFER, uvBuffer,
				GL_STATIC_DRAW);

		normBufferObject = new VertexBufferObject();
		normBufferObject.bind(GL_ARRAY_BUFFER);
		normBufferObject.uploadBufferData(GL_ARRAY_BUFFER, normBuffer,
				GL_STATIC_DRAW);

		material = new Material(vertBufferObject, uvBufferObject,
				normBufferObject, color, AssetLoader.instance()
						.loadShaderSource("res/BasicLit.vert"), AssetLoader
						.instance().loadShaderSource("res/Basic.frag"));
	}

	/**
	 * @param verts
	 *            The buffer of vertices used in the model.
	 * @param uvs
	 *            The buffer of UVs used in the model.
	 * @param norms
	 *            The buffer of vertex normals used in the model.
	 * @param color
	 *            The color of the model.
	 * @param modelMatrix
	 *            The model's model matrix.
	 */
	public Model(FloatBuffer verts, FloatBuffer uvs, FloatBuffer norms,
			Vector3f color, Matrix4f modelMatrix) {
		numVerts = verts.capacity();

		this.color = color;

		model = modelMatrix;
		vao = new VertexArrayObject();
		vao.bind();

		vertBuffer = verts;
		uvBuffer = uvs;
		normBuffer = norms;

		vertBufferObject = new VertexBufferObject();
		vertBufferObject.bind(GL_ARRAY_BUFFER);
		vertBufferObject.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer,
				GL_STATIC_DRAW);

		uvBufferObject = new VertexBufferObject();
		uvBufferObject.bind(GL_ARRAY_BUFFER);
		uvBufferObject.uploadBufferData(GL_ARRAY_BUFFER, uvBuffer,
				GL_STATIC_DRAW);

		normBufferObject = new VertexBufferObject();
		normBufferObject.bind(GL_ARRAY_BUFFER);
		normBufferObject.uploadBufferData(GL_ARRAY_BUFFER, normBuffer,
				GL_STATIC_DRAW);

		material = new Material(vertBufferObject, uvBufferObject,
				normBufferObject, color, AssetLoader.instance()
						.loadShaderSource("res/BasicLit.vert"), AssetLoader
						.instance().loadShaderSource("res/Basic.frag"));
	}

	@Override
	public void draw(Camera camera, Vector3f light) {
		vao.bind();
		material.enable();
		material.update(model, camera, light);
		glDrawArrays(GL_TRIANGLES, 0, numVerts);
		material.disable();
		vao.unbind();
		// System.out.println("Model drawn");
	}

	/**
	 * Creates a copy using the model's vertex, uv and vertex normal data.
	 * 
	 * @param model
	 *            The model matrix to instantiate the new model with.
	 * @return A copy of this model.
	 */
	public Model instantiate(Matrix4f model) {
		return new Model(vertBuffer, uvBuffer, normBuffer, color, model);
	}

	@Override
	public void delete() {
		vao.delete();
		vertBufferObject.delete();
		uvBufferObject.delete();
	}

	/**
	 * Changes the color of the model.
	 * 
	 * @param color
	 *            The new color of the model.
	 */
	public void setColor(Vector3f color) {
		this.color = color;
	}
}
