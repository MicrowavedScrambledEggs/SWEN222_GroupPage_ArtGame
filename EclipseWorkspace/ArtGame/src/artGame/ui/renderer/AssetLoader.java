package artGame.ui.renderer;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector2f;
import artGame.ui.renderer.math.Vector3f;

public class AssetLoader {

	// singleton
	private static AssetLoader instance = new AssetLoader();

	public static AssetLoader instance() {
		return instance;
	}

	private AssetLoader() {
		
	}

	public Model loadOBJ(String filepath) {
		// TODO
		List<Vector3f> vertList = new ArrayList<Vector3f>();
		List<Vector2f> uvList = new ArrayList<Vector2f>();
		List<Vector3f> normList = new ArrayList<Vector3f>();
		
		List<Integer> vertIndices = new ArrayList<Integer>();
		List<Integer> uvIndices = new ArrayList<Integer>();
		List<Integer> normIndices = new ArrayList<Integer>();

		Scanner scan = null;
		try {
			scan = new Scanner(new File(filepath));

			while (scan.hasNext()) {
				String next = scan.next();

				if (next.equals("#")) {
					// skip over comments
					scan.nextLine();
				} else if (next.equals("v")) {
					// add a vertex
					vertList.add(new Vector3f(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
				} else if (next.equals("vt")) {
					uvList.add(new Vector2f(scan.nextFloat(), scan.nextFloat()));
				} else if (next.equals("vn")) {
					normList.add(new Vector3f(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
				} else if (next.equals("f")) {
					String nextLine = scan.nextLine().trim();
					String[] indexTriples = nextLine.split(" ");
					
					for (int i = 0; i < indexTriples.length; i++) {
						String[] indices = indexTriples[i].split("\\/");
						vertIndices.add(Integer.parseInt(indices[0]));
						uvIndices.add(Integer.parseInt(indices[1]));
						normIndices.add(Integer.parseInt(indices[2]));
					}
				}
			}
			
			List<Vector3f> outVerts = new ArrayList<Vector3f>();
			List<Vector2f> outUVs = new ArrayList<Vector2f>();
			List<Vector3f> outNorms = new ArrayList<Vector3f>();
			
			// vertex indexing
			for (int i = 0; i < vertIndices.size(); i++){
				int vertexIndex = vertIndices.get(i);
				int uvIndex = uvIndices.get(i);
				int normIndex = normIndices.get(i);
				
				outVerts.add(vertList.get(vertexIndex - 1));
				outUVs.add(uvList.get(uvIndex - 1));
				outNorms.add(normList.get(normIndex - 1));
			}
			
			return new Model(outVerts, outUVs, outNorms, new Matrix4f());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (scan != null) {
				scan.close();
			}
		}
	}
	
	public CharSequence loadShaderSource(String filepath) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filepath));
			scan.useDelimiter("\\Z");
			return scan.next();
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (scan != null) {
				scan.close();
			}
		}
	}

	public Sprite loadSpritesheet(String filepath, int size) {
		BufferedImage sheet;
		try {
			sheet = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		BufferedImage[][] sprites = new BufferedImage[sheet.getWidth() / size][sheet.getHeight() / size];
		Texture[][] textures = new Texture[sprites.length][sprites[0].length];
		for (int col = 0; col < sprites.length; col++) {
			for(int row = 0; row < sprites[col].length; row++) {
				sprites[col][row] = sheet.getSubimage(col * size, row * size, size, size);
				textures[row][col] = new Texture(sprites[col][row], size);
			}
		}
		return new Sprite(textures, new Vector3f(1, 0.5f, 0));
	}
	
	public ByteBuffer imageToBuffer(BufferedImage image) {
		if (image != null) {
            /* Flip image Horizontal to get the origin to bottom left */
            AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
            transform.translate(0, -image.getHeight());
            AffineTransformOp operation = new AffineTransformOp(transform,
                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = operation.filter(image, null);

            /* Get width and height of image */
            int width = image.getWidth();
            int height = image.getHeight();

            /* Get pixel data of image */
            int[] pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);

            /* Put pixel data into a ByteBuffer */
            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    /* Pixel as RGBA: 0xAARRGGBB */
                    int pixel = pixels[y * width + x];
                    /* Red component 0xAARRGGBB >> 16 = 0x0000AARR */
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    /* Green component 0xAARRGGBB >> 8 = 0x00AARRGG */
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    /* Blue component 0xAARRGGBB >> 0 = 0xAARRGGBB */
                    buffer.put((byte) (pixel & 0xFF));
                    /* Alpha component 0xAARRGGBB >> 24 = 0x000000AA */
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            /* Do not forget to flip the buffer! */
            buffer.flip();
            
            return buffer;
		}
		return null;
	}
}
