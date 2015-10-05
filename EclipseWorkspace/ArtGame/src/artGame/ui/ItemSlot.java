package artGame.ui;

import artGame.ui.renderer.Camera;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

public class ItemSlot {

	private int itemId = -1;
	private Widget widget;
	
	public ItemSlot(Widget w){
		this.widget=w;
	}
	
	public void setItem(int id){
		this.itemId = id;
	}
	
	public int getItem(){
		return itemId;
	}

	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public void draw() {
		widget.draw();
	}

}