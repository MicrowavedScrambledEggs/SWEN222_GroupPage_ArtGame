package artGame.ui;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

import java.io.File;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import artGame.ui.renderer.Material;
import artGame.ui.renderer.VertexArrayObject;
import artGame.ui.renderer.VertexBufferObject;
import artGame.ui.renderer.math.Matrix4f;

public class ImageButton implements Widget {

	private Matrix4f matrix;
	
	private VertexArrayObject vao;
	private VertexBufferObject vertBufferObject;
	private VertexBufferObject uvBufferObject;
	
	private int vaoId;
	private int vboId;
	
	private int vertCount;
	
	private Material material;
	
	public ImageButton(File image, float width, float height){
		float[] vertices = {
				//Left Bottom triangle
				-0.8f, 0.2f, 0f,
		        -0.8f, -0.8f, 0f,
		        0.2f, -0.8f, 0f,
		        // Right top triangle
		        0.2f, -0.8f, 0f,
		        0.2f, 0.2f, 0f,
		        -0.8f, 0.2f, 0f
		};
		
		FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(vertices.length);
		vertBuffer.put(vertices);
		vertBuffer.flip();
		
		vertCount = 6;
		
		
		
		//Create a new VAO object in memory, and bind it
		vao = new VertexArrayObject();
		vao.bind();
		
		//Create a new VBO in memory and bind it
		vertBufferObject = new VertexBufferObject();
		vertBufferObject.bind(GL15.GL_ARRAY_BUFFER);
		vertBufferObject.uploadBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
		
		 uvBufferObject = new VertexBufferObject();
	        uvBufferObject.bind(GL_ARRAY_BUFFER);
	        uvBufferObject.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

	      //Put VBO in attributes list at [0]
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
	        
	}
	
	@Override
	public void draw(float width, float height, float screenWidth, float screenHeight) {
		matrix = Matrix4f.ortho(-1, 1, 1, -1, -1, 1);

		//Bind to the VAO..
		GL30.glBindVertexArray(vao.getID());
		GL20.glEnableVertexAttribArray(0);
	
		//draw vertices
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertCount);
		
		
		
		
	}

	@Override
	public void delete() {
		vao.delete();
		vertBufferObject.delete();
		//uvBufferObject.delete();
	}

}