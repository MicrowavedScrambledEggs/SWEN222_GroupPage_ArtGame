
package artGame.ui;

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

import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Camera;
import artGame.ui.renderer.Shader;
import artGame.ui.renderer.ShaderProgram;
import artGame.ui.renderer.Texture;
import artGame.ui.renderer.VertexArrayObject;
import artGame.ui.renderer.VertexBufferObject;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;
import artGame.ui.screens.GLWindow;

/**
 * A 2D Widget class based on Reikers Sprite class
 * @author Tim
 *
 */
public class Widget implements Asset {
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
	
	private float x = 0, y = 0, scale = 1f;
	private float right = 3f, bottom = 3f;
    
	public Widget(Texture[][] spritesheet, Vector3f pos) {

		position = pos;
		this.spritesheet = spritesheet;
		
		//spritesheet[0][0].bind();
		
		vao = new VertexArrayObject();
		vao.bind();

		vertBuffer = BufferUtils.createFloatBuffer(3 * 6);
		vertBuffer.put(-0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		//vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(-0.5f).put(0.5f).put(0.0f);
		vertBuffer.put(0.5f).put(-0.5f).put(0.0f);
		//vertBuffer.put(-0.5f).put(-0.5f).put(0.0f);
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
        
        int widthVal = width.get();
        int heightVal = height.get();
        float ratio = widthVal / (float) heightVal;
       // Matrix4f projection = Matrix4f.persp(80f, ratio, 1f, 100f);
       // Matrix4f projection = new Matrix4f();
        bottom = bottom/ratio;
        System.out.println("Right: " + right + ", Bottom: " + bottom);
        Matrix4f projection = Matrix4f.ortho(0, right, 0, bottom, 1f, 16777215.0f);
        projUniform = program.getUniformLocation("projection");
        program.setUniform(projUniform, projection);
        program.disable();
        verts.unbind(GL_ARRAY_BUFFER);
        vao.unbind();
        
	}
	
	@Override
	public void draw(Camera cam, Vector3f light) {
		Matrix4f view = cam.getView();
		view = GLWindow.INITIAL_VIEW;
		//view =  Matrix4f.translate(new Vector3f(0, 0, -3)).multiply(Matrix4f.rotate(35.2f, 1f, 0f, 0f));;
	
		float[][] v = view.getData();
		
        //System.out.println(GL11.glGetError());
	
		Vector3f cameraRight = new Vector3f(v[0][0]*scale, v[0][1], v[0][2]);
		Vector3f cameraUp = new Vector3f(v[1][0]*scale, v[1][1]*scale, v[1][2]*scale);
		
		program.use();
		program.setUniform(cameraRightUniform, cameraRight);
       // System.out.println(cameraRight.toString());
		program.setUniform(cameraUpUniform, cameraUp);
		program.setUniform(positionUniform, position);
		program.setUniform(viewUniform, view);
		program.setUniform(textureUniform, 0);

        //System.out.println(cameraUp.toString());
		
        spritesheet[0][0].bind();

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
	
	public void draw() {
		Matrix4f view = GLWindow.INITIAL_VIEW;
		//view =  Matrix4f.translate(new Vector3f(0, 0, -3)).multiply(Matrix4f.rotate(35.2f, 1f, 0f, 0f));;
	
		float[][] v = view.getData();
		
        //System.out.println(GL11.glGetError());
	
		Vector3f cameraRight = new Vector3f(v[0][0]*scale, v[0][1], v[0][2]);
		Vector3f cameraUp = new Vector3f(v[1][0]*scale, v[1][1]*scale, v[1][2]*scale);
		
		program.use();
		program.setUniform(cameraRightUniform, cameraRight);
       // System.out.println(cameraRight.toString());
		program.setUniform(cameraUpUniform, cameraUp);
		program.setUniform(positionUniform, position);
		program.setUniform(viewUniform, view);
		program.setUniform(textureUniform, 0);

        //System.out.println(cameraUp.toString());
		
        spritesheet[0][0].bind();

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
	
	public void setScale(float scale){
		this.scale=scale;
	}
	
	public void setLocation(Vector3f location){
		this.position = location;
	}
	
	public void setScreenLocation(float x, float y){
		this.x=x;
		this.y=y+0.1f; //TODO fix bug with ratio, this seems to work for now..
		this.setLocation(new Vector3f(this.x*right, this.y*bottom, position.getZ()));
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}

	@Override
	public void delete() {
		vert.delete();
		frag.delete();
		program.delete();
	}

	public float getScale() {
		return scale;
	}

}


