package artGame.ui.renderer.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

public class Vector3f {

	private float[] v = new float[3];
	
	public Vector3f() {
		v[0] = 0f;
		v[1] = 0f;
		v[2] = 0f;
	}
	
	public Vector3f(float x, float y, float z) {
		v[0] = x;
		v[1] = y;
		v[2] = z;
	}

	public FloatBuffer toBuffer() {
		FloatBuffer b = BufferUtils.createFloatBuffer(3);
		b.put(v);
		b.flip();
		return b;
	}
	
	public float getX() {
		return v[0];
	}
	
	public float getY() {
		return v[1];
	}
	
	public float getZ() {
		return v[2];
	}
	
	public Vector3f scale(float scalar) {
		float x = v[0] * scalar;
		float y = v[1] * scalar;
		float z = v[2] * scalar;
		return new Vector3f(x, y, z);
	}
	
	public Vector3f add(Vector3f other) {
		float x = v[0] + other.v[0];
		float y = v[1] + other.v[1];
		float z = v[2] + other.v[2];
		return new Vector3f(x,y,z);
	}
	
	public float length() {
		return (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
	}
	
	public Vector3f normalized() {
		return scale(1/length());
	}
	
	public float dot(Vector3f other) {
		return v[0] * other.v[0] + v[1] * other.v[1] + v[2] * other.v[2];
	}
	
	public Vector3f cross(Vector3f other) {
        float x = v[1] * other.v[2] - v[2] * other.v[1];
        float y = v[2] * other.v[0] - v[0] * other.v[2];
        float z = v[0] * other.v[1] - v[1] * other.v[0];
        return new Vector3f(x, y, z);
    }
	
	public Vector3f lerp(Vector3f other, float alpha) {
        return this.scale(1f - alpha).add(other.scale(alpha));
    }

	public float[] toArray() {
		return Arrays.copyOf(v, v.length);
	}
	
	@Override
	public String toString() {
		return v[0] + ", " + v[1] + ", " + v[2];
	}
}
