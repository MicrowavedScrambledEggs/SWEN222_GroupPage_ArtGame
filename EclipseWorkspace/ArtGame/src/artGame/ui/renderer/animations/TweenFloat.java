package artGame.ui.renderer.animations;

public class TweenFloat implements Tween<Float> {
	private float startValue;
	private float duration;
	private float valueRange;
	private float startTime;
	
	public TweenFloat(float startValue, float duration, float valueRange, float startTime) {
		this.startValue = startValue;
	    this.duration = duration;
	    this.valueRange = valueRange;
	    this.startTime = startTime;
	}

	@Override
	public Float tween(float time) {
		float currentTime = (time - startTime)/1000.0f;
	    float t = currentTime / duration;
	    t = Math.max(0, Math.min(1, t));
	    
	    float val = startValue + t * valueRange;
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
