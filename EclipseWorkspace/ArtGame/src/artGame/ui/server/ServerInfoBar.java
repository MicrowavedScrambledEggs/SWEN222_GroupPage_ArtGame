package artGame.ui.server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class ServerInfoBar extends JPanel {

	private boolean running;
	private String ip = "";
	private int port;
	private int connections = 0;
	
	private float statusX = -10000;
	
	public ServerInfoBar(int width, int height){
		setPreferredSize(new Dimension(width, height));
	}
	
	public void setRunningStatus(boolean running){
		this.running = running;
	}
	
	public void setServerIP(String ip){
		this.ip=ip;
	}
	
	public void setPort(int port){
		this.port=port;
	}
	
	public void setConnectionCount(int connections){
		this.connections = connections;
	}
	
	@Override
	public void paintComponent(Graphics g){
		String status = "server off";
		
		g.setColor(Color.RED);
		if(running){
			status = "running on ";
			status += ip + ":" + port + " , " + connections + " connected";	
			g.setColor(Color.GREEN);
		}
		g.fillRect(0,  0,  getWidth(),  getHeight());
		
		Rectangle2D bounds = g.getFontMetrics().getStringBounds(status, g);
		
		g.setColor(Color.WHITE);
		
		if(statusX == -10000){
			statusX = (int)(getWidth()/2f-bounds.getWidth()/2f);
		} else if(statusX > getWidth()){
			statusX = (int)-bounds.getWidth();
		} else {
			statusX += 0.01f;
		}
		
		g.drawString(status, (int)statusX, (int)(getHeight()/2f-bounds.getHeight()/2f) + g.getFontMetrics().getAscent());
	}
	
}
