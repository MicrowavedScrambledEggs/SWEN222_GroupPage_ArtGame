package artGame.ui.renderer.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix2f {
	// stored as (row, col)
	private float[][] m = new float[2][2];
	
	public Matrix2f() {
		m[0][0] = 1f;
		m[1][0] = 0f;
		
		m[0][1] = 0f;
		m[1][1] = 1f;
	}
	
	public Matrix2f(Vector2f col1, Vector2f col2){
		m[0][0] = col1.getX();
		m[1][0] = col1.getY();
		
		m[0][1] = col2.getX();
		m[1][1] = col2.getY();
	}
	
	public Matrix2f add(Matrix2f other) {
		Matrix2f result = new Matrix2f();
		
		for(int row = 0; row < m.length; row++) {
			for (int col = 0; col < m[row].length; col++) {
				result.m[row][col] = m[row][col] + other.m[row][col];
			}
		}
		
		return result;
	}
	
	public Matrix2f multiply(float scalar) {
		Matrix2f result = new Matrix2f();
		
		for(int row = 0; row < m.length; row++) {
			for (int col = 0; col < m[row].length; col++) {
				result.m[row][col] = m[row][col] * scalar;
			}
		}
		
		return result;
	}
	
	public Matrix2f multiply(Matrix2f other) {
		Matrix2f result = new Matrix2f();
		
		for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < other.m[0].length; j++) {
                for (int k = 0; k < m[0].length; k++) {
                    result.m[i][j] += m[i][k] * other.m[k][j];
                }
            }
		}
		
		return result;
	}
	
	public Vector2f multiply(Vector2f vector) {
		float[] vectorData = vector.toArray();
		float[] v = new float[vectorData.length];
		for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                v[i] += m[i][j] * vectorData[j];
		
		return new Vector2f(v[0],v[1]);
	}
	
	public FloatBuffer toBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(2 * 2);
        buffer.put(m[0][0]).put(m[1][0]);
        buffer.put(m[0][1]).put(m[1][1]);
        buffer.flip();
        return buffer;
	}
}
