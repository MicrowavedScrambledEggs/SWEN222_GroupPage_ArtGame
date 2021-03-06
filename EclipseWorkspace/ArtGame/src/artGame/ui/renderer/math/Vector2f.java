package artGame.ui.renderer.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

public class Vector2f {
	
	private float[] v = new float[2];
	
	public Vector2f() {
		v[0] = 0f;
		v[1] = 0f;
	}
	
	public Vector2f(float x, float y) {
		v[0] = x;
		v[1] = y;
	}

	public FloatBuffer toBuffer() {
		FloatBuffer b = BufferUtils.createFloatBuffer(2);
		b.put(v);
		b.flip();
		return b;
	}
	
	public float[] toArray() {
		return Arrays.copyOf(v, v.length);
	}
	
	public float getX() {
		return v[0];
	}
	
	public float getY() {
		return v[1];
	}
	
	public Vector2f scale(float scalar) {
		float x = v[0] * scalar;
		float y = v[1] * scalar;
		return new Vector2f(x, y);
	}
	
	public Vector2f add(Vector2f other) {
		float x = v[0] + other.v[0];
		float y = v[1] + other.v[1];
		return new Vector2f(x,y);
	}
	
	public float length() {
		return (float) Math.sqrt(v[0]*v[0]+v[1]*v[1]);
	}
	
	public Vector2f normalized() {
		return scale(1/length());
	}
	
	public float dot(Vector2f other) {
		return v[0] * other.v[0] + v[1] * other.v[1];
	}
	
	public Vector2f lerp(Vector2f other, float alpha) {
        return scale(1f - alpha).add(other.scale(alpha));
    }
}
