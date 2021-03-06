package artGame.ui.renderer.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix3f {
	// stored as (row, col)
		private float[][] m = new float[3][3];
		
		public Matrix3f() {
			m[0][0] = 1f;
			m[1][0] = 0f;
			m[2][0] = 0f;
			
			m[0][1] = 0f;
			m[1][1] = 1f;
			m[2][1] = 0f;
			
			m[0][2] = 0f;
			m[1][2] = 0f;
			m[2][2] = 1f;
		}
		
		public Matrix3f(Vector3f col1, Vector3f col2, Vector3f col3){
			m[0][0] = col1.getX();
			m[1][0] = col1.getY();
			m[2][0] = col1.getZ();
			
			m[0][1] = col2.getX();
			m[1][1] = col2.getY();
			m[2][1] = col2.getZ();
			
			m[0][2] = col3.getX();
			m[1][2] = col3.getY();
			m[2][2] = col3.getZ();
		}
		
		public Matrix3f add(Matrix3f other) {
			Matrix3f result = new Matrix3f();
			
			for(int row = 0; row < m.length; row++) {
				for (int col = 0; col < m[row].length; col++) {
					result.m[row][col] = m[row][col] + other.m[row][col];
				}
			}
			
			return result;
		}
		
		public Matrix3f multiply(float scalar) {
			Matrix3f result = new Matrix3f();
			
			for(int row = 0; row < m.length; row++) {
				for (int col = 0; col < m[row].length; col++) {
					result.m[row][col] = m[row][col] * scalar;
				}
			}
			
			return result;
		}
		
		public Matrix3f multiply(Matrix3f other) {
			Matrix3f result = new Matrix3f();
			
			for (int i = 0; i < m.length; i++) {
	            for (int j = 0; j < other.m[0].length; j++) {
	                for (int k = 0; k < m[0].length; k++) {
	                    result.m[i][j] += m[i][k] * other.m[k][j];
	                }
	            }
			}
			
			return result;
		}
		
		public Vector3f multiply(Vector3f vector) {
			float[] vectorData = vector.toArray();
			float[] v = new float[vectorData.length];
			for (int i = 0; i < m.length; i++)
	            for (int j = 0; j < m[0].length; j++)
	                v[i] += m[i][j] * vectorData[j];
			
			return new Vector3f(v[0],v[1], v[2]);
		}
		
		public FloatBuffer toBuffer() {
			FloatBuffer buffer = BufferUtils.createFloatBuffer(3 * 3);
	        buffer.put(m[0][0]).put(m[1][0]).put(m[2][0]);
	        buffer.put(m[0][1]).put(m[1][1]).put(m[2][1]);
	        buffer.put(m[0][2]).put(m[1][2]).put(m[2][2]);
	        buffer.flip();
	        return buffer;
		}
}
