package artGame.ui.renderer;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.ui.renderer.math.*;

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

	public Material(VertexBufferObject verts, VertexBufferObject uvs, VertexBufferObject norms, Vector3f color, CharSequence vertSource, CharSequence fragSource) {
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
	
	public void update(Matrix4f model, Camera camera, Vector3f light) {
        program.setUniform(modelUniform, model);
        program.setUniform(viewUniform, camera.getView());
        program.setUniform(lightUniform, light);
        program.setUniform(projUniform, camera.getProjection());
	}
	
	public void enable() {
		program.enableVertexAttribute(posAttrib);
		program.enableVertexAttribute(uvAttrib);
		program.enableVertexAttribute(normAttrib);
		program.use();
	}
	
	public void disable() {
		program.disableVertexAttribute(posAttrib);
		program.disableVertexAttribute(uvAttrib);
		program.disableVertexAttribute(normAttrib);
		program.disable();
	}
	
	public void delete() {
		vert.delete();
		frag.delete();
		program.delete();
	}

	public ShaderProgram getProgram() {
		// TODO Auto-generated method stub
		return program;
	}
}
