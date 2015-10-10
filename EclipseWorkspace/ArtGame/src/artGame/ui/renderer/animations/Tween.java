package artGame.ui.renderer.animations;

public interface Tween<E> {
	public E tween(float time);
	public boolean isFinished(float time);
}
