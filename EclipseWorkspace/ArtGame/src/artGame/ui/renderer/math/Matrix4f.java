package artGame.ui.renderer.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4f {
	// stored as (row, col)
	private float[][] m = new float[4][4];

	public Matrix4f() {
		m[0][0] = 1f;
		m[1][0] = 0f;
		m[2][0] = 0f;
		m[3][0] = 0f;

		m[0][1] = 0f;
		m[1][1] = 1f;
		m[2][1] = 0f;
		m[3][1] = 0f;

		m[0][2] = 0f;
		m[1][2] = 0f;
		m[2][2] = 1f;
		m[3][2] = 0f;
		
		m[0][3] = 0f;
		m[1][3] = 0f;
		m[2][3] = 0f;
		m[3][3] = 1f;
	}

	public Matrix4f(Vector4f col1, Vector4f col2, Vector4f col3, Vector4f col4) {
		m[0][0] = col1.getX();
		m[1][0] = col1.getY();
		m[2][0] = col1.getZ();
		m[3][0] = col1.getW();

		m[0][1] = col2.getX();
		m[1][1] = col2.getY();
		m[2][1] = col2.getZ();
		m[3][1] = col2.getW();

		m[0][2] = col3.getX();
		m[1][2] = col3.getY();
		m[2][2] = col3.getZ();
		m[3][2] = col3.getW();
		
		m[0][3] = col4.getX();
		m[1][3] = col4.getY();
		m[2][3] = col4.getZ();
		m[3][3] = col4.getW();
	}

	public Matrix4f add(Matrix4f other) {
		Matrix4f result = new Matrix4f();

		for (int row = 0; row < m.length; row++) {
			for (int col = 0; col < m[row].length; col++) {
				result.m[row][col] = m[row][col] + other.m[row][col];
			}
		}

		return result;
	}

	public Matrix4f multiply(float scalar) {
		Matrix4f result = new Matrix4f();

		for (int row = 0; row < m.length; row++) {
			for (int col = 0; col < m[row].length; col++) {
				result.m[row][col] = m[row][col] * scalar;
			}
		}

		return result;
	}

	public Matrix4f multiply(Matrix4f other) {
		Matrix4f result = new Matrix4f();

		result.m[0][0] = this.m[0][0] * other.m[0][0] + this.m[0][1] * other.m[1][0] + this.m[0][2] * other.m[2][0] + this.m[0][3] * other.m[3][0];
        result.m[1][0] = this.m[1][0] * other.m[0][0] + this.m[1][1] * other.m[1][0] + this.m[1][2] * other.m[2][0] + this.m[1][3] * other.m[3][0];
        result.m[2][0] = this.m[2][0] * other.m[0][0] + this.m[2][1] * other.m[1][0] + this.m[2][2] * other.m[2][0] + this.m[2][3] * other.m[3][0];
        result.m[3][0] = this.m[3][0] * other.m[0][0] + this.m[3][1] * other.m[1][0] + this.m[3][2] * other.m[2][0] + this.m[3][3] * other.m[3][0];

        result.m[0][1] = this.m[0][0] * other.m[0][1] + this.m[0][1] * other.m[1][1] + this.m[0][2] * other.m[2][1] + this.m[0][3] * other.m[3][1];
        result.m[1][1] = this.m[1][0] * other.m[0][1] + this.m[1][1] * other.m[1][1] + this.m[1][2] * other.m[2][1] + this.m[1][3] * other.m[3][1];
        result.m[2][1] = this.m[2][0] * other.m[0][1] + this.m[2][1] * other.m[1][1] + this.m[2][2] * other.m[2][1] + this.m[2][3] * other.m[3][1];
        result.m[3][1] = this.m[3][0] * other.m[0][1] + this.m[3][1] * other.m[1][1] + this.m[3][2] * other.m[2][1] + this.m[3][3] * other.m[3][1];

        result.m[0][2] = this.m[0][0] * other.m[0][2] + this.m[0][1] * other.m[1][2] + this.m[0][2] * other.m[2][2] + this.m[0][3] * other.m[3][2];
        result.m[1][2] = this.m[1][0] * other.m[0][2] + this.m[1][1] * other.m[1][2] + this.m[1][2] * other.m[2][2] + this.m[1][3] * other.m[3][2];
        result.m[2][2] = this.m[2][0] * other.m[0][2] + this.m[2][1] * other.m[1][2] + this.m[2][2] * other.m[2][2] + this.m[2][3] * other.m[3][2];
        result.m[3][2] = this.m[3][0] * other.m[0][2] + this.m[3][1] * other.m[1][2] + this.m[3][2] * other.m[2][2] + this.m[3][3] * other.m[3][2];

        result.m[0][3] = this.m[0][0] * other.m[0][3] + this.m[0][1] * other.m[1][3] + this.m[0][2] * other.m[2][3] + this.m[0][3] * other.m[3][3];
        result.m[1][3] = this.m[1][0] * other.m[0][3] + this.m[1][1] * other.m[1][3] + this.m[1][2] * other.m[2][3] + this.m[1][3] * other.m[3][3];
        result.m[2][3] = this.m[2][0] * other.m[0][3] + this.m[2][1] * other.m[1][3] + this.m[2][2] * other.m[2][3] + this.m[2][3] * other.m[3][3];
        result.m[3][3] = this.m[3][0] * other.m[0][3] + this.m[3][1] * other.m[1][3] + this.m[3][2] * other.m[2][3] + this.m[3][3] * other.m[3][3];
		/*
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < other.m[0].length; j++) {
				for (int k = 0; k < m[0].length; k++) {
					result.m[i][j] += m[i][k] * other.m[k][j];
				}
			}
		}
		*/

		return result;
	}

	public Vector4f multiply(Vector4f vector) {
		float[] vectorData = vector.toArray();
		float[] v = new float[vectorData.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				v[i] += m[i][j] * vectorData[j];

		return new Vector4f(v[0], v[1], v[2], v[3]);
	}

	public FloatBuffer toBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
		buffer.put(m[0][0]).put(m[1][0]).put(m[2][0]).put(m[3][0]);
		buffer.put(m[0][1]).put(m[1][1]).put(m[2][1]).put(m[3][1]);
		buffer.put(m[0][2]).put(m[1][2]).put(m[2][2]).put(m[3][2]);
		buffer.put(m[0][3]).put(m[1][3]).put(m[2][3]).put(m[3][3]);
		buffer.flip();
		return buffer;
	}
	
	public static Matrix4f ortho(float left, float right, float bottom, float top, float near, float far) {
        Matrix4f ortho = new Matrix4f();

        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        ortho.m[0][0] = 2f / (right - left);
        ortho.m[1][1] = 2f / (top - bottom);
        ortho.m[2][2] = -2f / (far - near);
        ortho.m[0][3] = tx;
        ortho.m[1][3] = ty;
        ortho.m[2][3] = tz;

        return ortho;
    }
	
	public static Matrix4f persp(float fovy, float aspect, float near, float far) {
        Matrix4f perspective = new Matrix4f();

        float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));

        perspective.m[0][0] = f / aspect;
        perspective.m[1][1] = f;
        perspective.m[2][2] = (far + near) / (near - far);
        perspective.m[3][2] = -1f;
        perspective.m[2][3] = (2f * far * near) / (near - far);
        perspective.m[3][3] = 0f;

        return perspective;
    }
	
	public static Matrix4f translate(Vector3f delta) {
        Matrix4f trans = new Matrix4f();

        trans.m[0][3] = delta.getX();
        trans.m[1][3] = delta.getY();
        trans.m[2][3] = delta.getZ();

        return trans;
    }
	
	public static Matrix4f rotate(float angle, float x, float y, float z) {
		Matrix4f rotation = new Matrix4f();

        float c = (float) Math.cos(Math.toRadians(angle));
        float s = (float) Math.sin(Math.toRadians(angle));
        Vector3f vec = new Vector3f(x, y, z);
        if (vec.length() != 1f) {
            vec = vec.normalized();
            x = vec.getX();
            y = vec.getY();
            z = vec.getZ();
        }

        rotation.m[0][0] = x * x * (1f - c) + c;
        rotation.m[1][0] = y * x * (1f - c) + z * s;
        rotation.m[2][0] = x * z * (1f - c) - y * s;
        rotation.m[0][1] = x * y * (1f - c) - z * s;
        rotation.m[1][1] = y * y * (1f - c) + c;
        rotation.m[2][1] = y * z * (1f - c) + x * s;
        rotation.m[0][2] = x * z * (1f - c) + y * s;
        rotation.m[1][2] = y * z * (1f - c) - x * s;
        rotation.m[2][2] = z * z * (1f - c) + c;

        return rotation;
    }
	
	public static Matrix4f scale(float x, float y, float z) {
        Matrix4f scaling = new Matrix4f();

        scaling.m[0][0] = x;
        scaling.m[1][1] = y;
        scaling.m[2][2] = z;

        return scaling;
    }

	public float[][] getData() {
		return m;
	}
}
