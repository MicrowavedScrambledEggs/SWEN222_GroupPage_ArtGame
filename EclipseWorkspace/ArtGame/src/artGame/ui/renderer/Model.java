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

public class Model implements Asset {
	private FloatBuffer vertBuffer;
	private FloatBuffer uvBuffer;
	private VertexArrayObject vao;
	private VertexBufferObject vertBufferObject;
	private VertexBufferObject uvBufferObject;
	
	private Matrix4f model;
	private int numVerts;
	private Material material;

	public Model(List<Vector3f> verts, List<Vector2f> uvs, List<Vector3f> norms, Matrix4f modelMatrix) {
		numVerts = verts.size();
		
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
		
		vertBufferObject = new VertexBufferObject();
        vertBufferObject.bind(GL_ARRAY_BUFFER);
        vertBufferObject.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);
        
        uvBufferObject = new VertexBufferObject();
        uvBufferObject.bind(GL_ARRAY_BUFFER);
        uvBufferObject.uploadBufferData(GL_ARRAY_BUFFER, uvBuffer, GL_STATIC_DRAW);
		
		material = new Material(vertBufferObject, uvBufferObject);
	}

	@Override
	public void draw(Matrix4f view, Vector3f light) {
		material.update(model, view);
		glDrawArrays(GL_TRIANGLES, 0, numVerts);
	}
	
	@Override
	public void delete() {
		vao.delete();
		vertBufferObject.delete();
		uvBufferObject.delete();
	}
}
