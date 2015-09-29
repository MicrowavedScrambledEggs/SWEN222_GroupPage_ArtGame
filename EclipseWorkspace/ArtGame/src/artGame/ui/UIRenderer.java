package artGame.ui;

import java.util.ArrayList;
import java.util.List;

public class UIRenderer {

	private List<Widget> overlayList;
	
	public UIRenderer(){
		overlayList = createOverlay();
		
	}
	
	public void render(float width, float height, float screenWidth, float screenHeight){
		for(Widget w : overlayList) {
			w.draw(width, height, screenWidth, screenHeight);
		}
	}
	
	public void dispose(){
		
	}
	
	private List<Widget> createOverlay() {
		List<Widget> widgets = new ArrayList<>();
		widgets.add(new ImageButton(null, 50, 50));
		return widgets;
	}
	
}

