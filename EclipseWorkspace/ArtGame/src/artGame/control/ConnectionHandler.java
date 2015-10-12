package artGame.control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import artGame.main.Main;

public class ConnectionHandler {

	private ServerSocket publicSocket;

	private int port, gameClock, maxClients, nextServerIdx;
	private long startTime;
	
	private ServerThread[] children;

	public ConnectionHandler(int port, int gameClock, int maxClients) {

		this.port = port;
		this.gameClock = gameClock;
		this.maxClients = maxClients;

		startTime = System.currentTimeMillis();

		try {
			publicSocket = new ServerSocket(port, maxClients + 1,
					InetAddress.getLocalHost());
			System.out.println("The server has set up shop at "
					+ publicSocket.getLocalSocketAddress());
			
			nextServerIdx = 0;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ServerThread waitForClient() {
		if(publicSocket == null){
			System.out.println("public socket null");
			return null;
		}
		
		try {
			System.out.println("waiting for client");
			Socket s = publicSocket.accept();
			ServerThread client = new ServerThread(Main.getGame(), s, Main.WAIT_PERIOD);
			
			return client;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
