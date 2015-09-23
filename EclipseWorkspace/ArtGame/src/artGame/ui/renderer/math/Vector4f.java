package artGame.ui.renderer.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

public class Vector4f {
private float[] v = new float[4];
	
	public Vector4f() {
		v[0] = 0f;
		v[1] = 0f;
	}
	
	public Vector4f(float x, float y, float z, float w) {
		v[0] = x;
		v[1] = y;
		v[2] = z;
		v[3] = w;
	}

	public FloatBuffer toBuffer() {
		FloatBuffer b = BufferUtils.createFloatBuffer(2);
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
	
	public float getW() {
		return v[3];
	}
	
	public Vector4f scale(float scalar) {
		float x = v[0] * scalar;
		float y = v[1] * scalar;
		float z = v[2] * scalar;
		float w = v[3] * scalar;
		return new Vector4f(x, y, z, w);
	}
	
	public Vector4f add(Vector4f other) {
		float x = v[0] + other.v[0];
		float y = v[1] + other.v[1];
		float z = v[2] + other.v[2];
		float w = v[3] + other.v[3];
		return new Vector4f(x, y, z, w);
	}
	
	public float length() {
		return (float) Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2] + v[3]*v[3]);
	}
	
	public Vector4f normalized() {
		return scale(1/length());
	}
	
	public float dot(Vector4f other) {
		return v[0] * other.v[0] + v[1] * other.v[1]  + v[2] * other.v[2] + v[3] * other.v[3];
	}
	
	public Vector4f lerp(Vector4f other, float alpha) {
        return scale(1f - alpha).add(other.scale(alpha));
    }

	public float[] toArray() {
		return Arrays.copyOf(v, v.length);
	}
}
