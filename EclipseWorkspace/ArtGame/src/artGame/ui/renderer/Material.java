package artGame.ui.renderer;

import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import artGame.ui.renderer.math.Matrix4f;

public class Material {
	private Shader vert;
	private Shader frag;
	private ShaderProgram program;
	
	private int modelUniform;
	private int viewUniform;
	private int projUniform;

	private final CharSequence vertSource = "#version 150 core\n" + "\n"
			+ "in vec3 position;\n" + "in vec2 uv;\n" + "\n"
			+ "out vec3 vertexColor;\n" + "\n" + "uniform mat4 model;\n"
			+ "uniform mat4 view;\n" + "uniform mat4 projection;\n" + "\n"
			+ "void main() {\n" + "    vertexColor = vec3(1.0, 1.0, 1.0);\n"
			+ "    mat4 mvp = projection * view * model;\n"
			+ "    gl_Position = mvp * vec4(position, 1.0);\n" + "}";
	private final CharSequence fragSource = "#version 150 core\n" + "\n"
			+ "in vec3 vertexColor;\n" + "\n" + "out vec4 fragColor;\n" + "\n"
			+ "void main() {\n" + "    fragColor = vec4(vertexColor, 1.0);\n"
			+ "}";

	public Material(VertexBufferObject verts, VertexBufferObject uvs) {
		vert = new Shader(GL_VERTEX_SHADER, vertSource);
        frag = new Shader(GL_FRAGMENT_SHADER, fragSource);

        program = new ShaderProgram();
        program.attachShader(vert);
        program.attachShader(frag);
        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
		program.use();
        
        int posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        verts.bind(GL_ARRAY_BUFFER);
        program.setVertexAttributePointer(posAttrib, 3, 0, 0);

        int uvAttrib = program.getAttributeLocation("uv");
        program.enableVertexAttribute(uvAttrib);
        uvs.bind(GL_ARRAY_BUFFER);
        program.setVertexAttributePointer(uvAttrib, 2, 0, 0);
        
        modelUniform = program.getUniformLocation("model");
        viewUniform = program.getUniformLocation("view");
        
        long window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();

        Matrix4f projection = Matrix4f.ortho(-ratio, ratio, -1f, 1f, -1f, 100f);
        projUniform = program.getUniformLocation("projection");
        program.setUniform(projUniform, projection);
	}
	
	public void update(Matrix4f model, Matrix4f view) {
        program.setUniform(modelUniform, model);
        program.setUniform(viewUniform, view);
	}
	
	public void delete() {
		vert.delete();
		frag.delete();
		program.delete();
	}
}
