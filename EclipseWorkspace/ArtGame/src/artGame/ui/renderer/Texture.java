package artGame.ui.renderer;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;

public class Texture {
	private BufferedImage image;
	private final int id;
	private final int size;

	public Texture(BufferedImage img, int size) {
		this.image = img;
		id = glGenTextures();
		this.size = size;
		
		glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.size, this.size, 0, GL_RGBA, GL_UNSIGNED_BYTE, AssetLoader.instance().imageToBuffer(image));
	}
	
	public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
	
	public void delete() {
        glDeleteTextures(id);
    }
	
	public int getSize() {
		return size;
	}
}
