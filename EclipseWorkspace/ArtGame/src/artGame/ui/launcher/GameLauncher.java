package artGame.ui.launcher;

import javax.swing.SwingUtilities;

import artGame.ui.AWTWindowTest;
import artGame.ui.TestWindow;
import artGame.ui.UIRenderer;

import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
/**
 * 
 * @author Tim King
 *
 */
public class GameLauncher {

	private AWTWindowTest window; 
	private UIRenderer ui;
	
	public GameLauncher(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				ui = new UIRenderer(640, 480){
					@Override
					public void dispose(){
						if(window != null){
							window.dispose();
						}
					}
				};
			}
		});
		window = new AWTWindowTest();
		glfwSetWindowPos(window.getWindow(), (int)ui.getLocationOnScreen().getX(), (int)ui.getLocationOnScreen().getY());
		System.out.println("here");
		ui.repaint();
		
		
		start();
	}
	
	public void start(){
		while(glfwWindowShouldClose(window.getWindow()) != GL_TRUE){
			update();
		}
	}
	
	public void update(){
		window.render();
	}
	
	public static void main(String[] args){
		new GameLauncher();
	}
	
}