package artGame.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import artGame.main.Game;

public class ClientThread extends SocketThread {
	private final Game game;
	private Socket socket;
	private boolean isPlaying = true;
	private int pid = 404;
	private volatile long timeout;
	
	public ClientThread(Socket s, Game g) {
		this.socket = s;
		game = g;
		timeout = System.currentTimeMillis() + SocketThread.CONNECTION_TIMEOUT;
		System.err.println("SERVER INFO:\nPID: "+pid
				+ "\nSOCKETADDR  "+socket.getLocalAddress()+" PORT: "+socket.getLocalPort()
				+ "\nCONNECT TO: "+socket.getInetAddress() +" PORT: "+socket.getPort());
	}
	
	public void run() {
		System.out.println("RUN");
		int runcount = 0; 
		int curX = 0;
		int curY = 0;
		int r = 0;
		while (!socket.isClosed()) {
			try {
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				byte[] packet = null;
				// write first
				if (r == 0) {
					packet = new MovePlayerPacket().write(0, pid, Packet.MOVE, pid, curX, curY, curX+1, curY+1, Packet.TERMINAL);
					System.out.println("GAVE: "+packet[3]+" is moving ("+packet[4]+", "+packet[5]+") going to ("+packet[6]+","+packet[7]+"), size "+packet.length);
					curX++;
					curY++;
				} else if (r==1) {
					packet = new GetItemPacket().write(0, pid, Packet.ITEM_GAIN, 400, 60, Packet.TERMINAL);
					System.err.println("GAVE: "+ packet[4]);
				} else {
					packet = new MovePlayerPacket().write(0, pid, Packet.MOVE, pid, curX, curY, curX+1, curY+1, Packet.TERMINAL);
					System.out.println("GAVE: "+packet[3]+" is moving ("+packet[4]+", "+packet[5]+") going to ("+packet[6]+","+packet[7]+"), size "+packet.length);
					curX++;
					curY++;
				}
				if (packet != null) {
					output.write(packet);
				}
				// TODO this is where the part that reads the messages goes!
				// [get time]
				timeout = System.currentTimeMillis() + SocketThread.CONNECTION_TIMEOUT;
				// [read for]
				// [if timeout, close]
				// [otherwise, keep looping!]
				r++;
				Thread.sleep(500);
			} catch (IOException e) { 
				e.printStackTrace(); 
			} catch (InterruptedException e) { 
				e.printStackTrace();
			} catch (IncompatiblePacketException e) {
				e.printStackTrace();
			}
			runcount++;
		}
	}

	@Override
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	@Override
	public int getPort() {
		return socket.getPort();
	}

	@Override
	public boolean isSocketSafe() {
		return socket.isBound() && socket.isConnected() && !socket.isClosed();
	}

	@Override
	public boolean close() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean isTimedOut() {
		return System.currentTimeMillis() < timeout;
	}

	@Override
	public int getPlayerId() {
		return pid;
	}
}
