package artGame.ui.renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector2f;
import artGame.ui.renderer.math.Vector3f;

public class AssetLoader {
	// all methods in this class must return an implementation of Asset

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

	public Sprite[][] loadSpritesheet(String filepath) {
		// TODO
		return null;
	}
}
