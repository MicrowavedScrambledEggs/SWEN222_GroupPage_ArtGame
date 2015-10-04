package artGame.ui.screens;


public class UIRenderer implements Screen {
	
	private long window;
	
	public UIRenderer(long window){
		this.window = window;
	}
	
	@Override
	public void initialize() {
		
	}
	
	@Override
	public void render() {
		
	}
	
	public void dispose(){
		
	}

	@Override
	public long getWindow() {
		
		return window;
	}
	
}

