package artGame.ui.renderer.animations;

import artGame.ui.renderer.math.Vector3f;

public class TweenVector3f implements Tween<Vector3f> {
	private Vector3f startValue;
	private float duration;
	private Vector3f valueRange;
	private float startTime;
	private Vector3f currentValue;

	public TweenVector3f(Vector3f startValue, float duration, Vector3f valueRange, float startTime) {
		this.startValue = startValue;
	    this.duration = duration;
	    this.valueRange = valueRange;
	    this.startTime = startTime;
	}
	
	@Override
	public Vector3f tween(float time) {
		float currentTime = (time - startTime)/1000.0f;
	    float t = currentTime / duration;
	    t = Math.max(0, Math.min(1, t));
	    
	    Vector3f val = startValue.lerp(startValue.add(valueRange), t);
	    currentValue = val;
	    return val;
	}

	@Override
	public boolean isFinished(float time) {
		float currentTime = (time - startTime)/1000.0f;
	    float t = currentTime / duration;
	    t = Math.max(0, Math.min(1, t));
		return t >= 1;
	}

}