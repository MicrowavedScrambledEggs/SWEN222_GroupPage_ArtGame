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

	private Shader vert;
	private Shader frag;
    
	public Sprite(Texture[][] spritesheet) {
		position = new Vector3f(0, 0.5f, 0);
		this.spritesheet = spritesheet;
		
		//spritesheet[0][0].bind();
		
		vao = new VertexArrayObject();
		vao.bind();
		
		vertBuffer = BufferUtils.createFloatBuffer(3 * 4);
		vertBuffer.put(-0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(0.5f).put(0.0f);
		vertBuffer.flip();
		
		verts = new VertexBufferObject();
		verts.bind(GL_ARRAY_BUFFER);
		verts.uploadBufferData(GL_ARRAY_BUFFER, vertBuffer,
				GL_STATIC_DRAW);
		
		vert = new Shader(GL_VERTEX_SHADER, AssetLoader.instance().loadShaderSource("res/sprite.vert"));
        frag = new Shader(GL_FRAGMENT_SHADER, AssetLoader.instance().loadShaderSource("res/sprite.frag"));
		
		program = new ShaderProgram();
        program.attachShader(vert);
        program.attachShader(frag);
        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
		program.use();
		
		int vertAttrib = program.getAttributeLocation("squareVerts");
        program.enableVertexAttribute(vertAttrib);
        verts.bind(GL_ARRAY_BUFFER);
        program.setVertexAttributePointer(vertAttrib, 3, 0, 0);
        
        viewUniform = program.getUniformLocation("view");
        cameraRightUniform = program.getUniformLocation("cameraRight");
        cameraUpUniform = program.getUniformLocation("cameraUp");
        positionUniform = program.getUniformLocation("position");
        //textureUniform = program.getUniformLocation("texture");
        
        long window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();

        Matrix4f projection = Matrix4f.persp(80f, ratio, 1f, 100f);
        projUniform = program.getUniformLocation("projection");
        program.setUniform(projUniform, projection);
	}
	
	@Override
	public void draw(Matrix4f view, Vector3f light) {
		float[][] v = view.getData();
		
		Vector3f cameraRight = new Vector3f(v[0][0], v[1][0], v[2][0]);
		Vector3f cameraUp = new Vector3f(v[0][1], v[1][1], v[2][1]);
		
		program.setUniform(cameraRightUniform, cameraRight);
		program.setUniform(cameraUpUniform, cameraUp);
		program.setUniform(positionUniform, position);
		program.setUniform(viewUniform, view);
		//program.setUniform(textureUniform, 0);
		
		
        //spritesheet[0][0].bind();
        program.use();
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        
	}

	@Override
	public void delete() {
		vert.delete();
		frag.delete();
		program.delete();
	}

}
