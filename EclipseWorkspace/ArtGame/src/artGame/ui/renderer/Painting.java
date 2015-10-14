package artGame.ui.renderer;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public class Painting implements Asset {
	private FloatBuffer vertBuffer;
	private VertexArrayObject vao;
	private VertexBufferObject verts;
	private ShaderProgram program;
	
	private Matrix4f model;
	
	private int modelUniform;
	private int viewUniform;
	private int projUniform;
    private int textureUniform;
    
    private Texture sprite;

	private Shader vert;
	private Shader frag;
    
	public Painting(Texture sprite, Matrix4f modelMatrix) {

		model = modelMatrix;
		this.sprite = sprite;
		
		
		vao = new VertexArrayObject();
		vao.bind();

		vertBuffer = BufferUtils.createFloatBuffer(3 * 6);
		vertBuffer.put(-0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		vertBuffer.flip();

		verts = new VertexBufferObject();
		verts.bind(GL_ARRAY_BUFFER);
		verts.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer,
				GL_STATIC_DRAW);
		
		vert = new Shader(GL_VERTEX_SHADER, AssetLoader.instance().loadShaderSource("res/painting.vert"));
        frag = new Shader(GL_FRAGMENT_SHADER, AssetLoader.instance().loadShaderSource("res/sprite.frag"));

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
        modelUniform = program.getUniformLocation("model");
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
		
		program.use();
		program.setUniform(modelUniform, model);
		program.setUniform(viewUniform, view);
		program.setUniform(textureUniform, 0);

        //System.out.println(cameraUp.toString());
		
        sprite.bind();

        vao.bind();
        //System.out.println(GL11.glGetError());
        verts.bind(GL_ARRAY_BUFFER);
        
        //glDepthMask(GL_FALSE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
        //draw transparent object ...
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glDisable(GL_BLEND);
        //glDepthMask(GL_TRUE);
        //System.out.println(GL11.glGetError());
        program.disable();
        //System.out.println(GL11.glGetError());
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

	public Painting instantiate(Matrix4f pos) {
		return new Painting(sprite, pos);
	}
}