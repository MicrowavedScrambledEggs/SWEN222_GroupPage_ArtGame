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

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector2f;
import artGame.ui.renderer.math.Vector3f;

/**
 * Loads assets from file, with a couple helpful asset processing methods.
 * Structured as a singleton so that the methods can be accessed anywhere, and c
 * 
 * @author Reiker v. Motschelnitz 300326917
 *
 */
public class AssetLoader {

	// singleton
	private static AssetLoader instance = new AssetLoader();

	/**
	 * Accesses the singleton object
	 * 
	 * @return The singleton object
	 */
	public static AssetLoader instance() {
		return instance;
	}

	private AssetLoader() {

	}

	/**
	 * Creates a {@link Model} from a .obj file.
	 * 
	 * @param filepath
	 *            The location of the .obj file to read from
	 * @param color
	 *            A vector in RGB colorspace representing the model's color
	 * @return A new model using the data from the .obj file
	 */
	public Model loadOBJ(String filepath, Vector3f color) {
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
					vertList.add(new Vector3f(scan.nextFloat(), scan
							.nextFloat(), scan.nextFloat()));
				} else if (next.equals("vt")) {
					uvList.add(new Vector2f(scan.nextFloat(), scan.nextFloat()));
				} else if (next.equals("vn")) {
					normList.add(new Vector3f(scan.nextFloat(), scan
							.nextFloat(), scan.nextFloat()));
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
			for (int i = 0; i < vertIndices.size(); i++) {
				int vertexIndex = vertIndices.get(i);
				int uvIndex = uvIndices.get(i);
				int normIndex = normIndices.get(i);

				outVerts.add(vertList.get(vertexIndex - 1));
				outUVs.add(uvList.get(uvIndex - 1));
				outNorms.add(normList.get(normIndex - 1));
			}

			return new Model(outVerts, outUVs, outNorms, color, new Matrix4f());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (scan != null) {
				scan.close();
			}
		}
	}

	/**
	 * Creates a string from a file containing the source code of a shader.
	 * 
	 * @param filepath
	 *            The location of the .vert or .frag file to read from
	 * @return A CharSequence containing the source code
	 */
	public CharSequence loadShaderSource(String filepath) {
		Scanner scan = null;
		try {
			scan = new Scanner(new File(filepath));
			scan.useDelimiter("\\Z");
			return scan.next();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (scan != null) {
				scan.close();
			}
		}
	}

	/**
	 * Loads a {@link Sprite} from a spritesheet.
	 * 
	 * @param filepath
	 *            The location of the spritesheet to read from.
	 * @param size
	 *            The size of each sprite. Must be square, powers of 2 ideal.
	 * @return
	 */
	public Sprite loadSpritesheet(String filepath, int size) {
		BufferedImage sheet;
		try {
			sheet = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		BufferedImage[][] sprites = new BufferedImage[sheet.getWidth() / size][sheet
				.getHeight() / size];
		Texture[][] textures = new Texture[sheet.getWidth() / size][sheet
				.getHeight() / size];
		for (int col = 0; col < sprites.length; col++) {
			for (int row = 0; row < sprites[col].length; row++) {
				sprites[col][row] = sheet.getSubimage(col * size, row * size,
						size, size);
				textures[col][row] = new Texture(sprites[col][row], size);
			}
		}
		return new Sprite(textures, new Vector3f(0, 0.5f, 0));
	}

	/**
	 * A helper method for converting BufferedImages to ByteBuffers for ease of
	 * use with OpenGL. Adapted from LWJGL GitHub Wiki.
	 * 
	 * @param image
	 *            An image to be converted
	 * @return A ByteBuffer
	 */
	public ByteBuffer imageToBuffer(BufferedImage image) {
		if (image != null) {

			AffineTransform transform = AffineTransform.getScaleInstance(1f,
					-1f);
			transform.translate(0, -image.getHeight());
			AffineTransformOp operation = new AffineTransformOp(transform,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = operation.filter(image, null);

			int width = image.getWidth();
			int height = image.getHeight();

			int[] pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);

			ByteBuffer buffer = BufferUtils
					.createByteBuffer(width * height * 4);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {

					int pixel = pixels[y * width + x];
					// Red component
					buffer.put((byte) ((pixel >> 16) & 0xFF));
					// Green component
					buffer.put((byte) ((pixel >> 8) & 0xFF));
					// Blue component
					buffer.put((byte) (pixel & 0xFF));
					// Alpha component
					buffer.put((byte) ((pixel >> 24) & 0xFF));
				}
			}

			buffer.flip();

			return buffer;
		}
		return null;
	}

	/**
	 * Creates a {@link Painting} from an image.
	 * 
	 * @param filepath
	 *            The location of the image to read from.
	 * @param size
	 *            The size of the image. Must be square, powers of 2 ideal.
	 * @return A Painting made using the specified image.
	 */
	public Painting loadPainting(String filepath, int size) {
		BufferedImage sheet;
		try {
			sheet = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new Painting(new Texture(sheet, size), new Matrix4f());
	}
}
