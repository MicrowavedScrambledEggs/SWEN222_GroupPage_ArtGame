package artGame.control;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.lwjgl.Sys;

import artGame.game.Floor;
import artGame.main.Game;
import artGame.main.Main;

public class ClientThread extends SocketThread {
	private final Game game;
	private Socket socket;
	private boolean isPlaying = false;
	private int pid = 404;
	
	public ClientThread(Socket s) {
		this.socket = s;
		game = null;
		System.err.println("SERVER INFO:\nPID: "+pid
				+ "\nSOCKETADDR  "+socket.getLocalAddress()+" PORT: "+socket.getLocalPort()
				+ "\nCONNECT TO: "+socket.getInetAddress() +" PORT: "+socket.getPort());
	}
	
	public void run() {
		int runcount = 0; 
		int curX = 0;
		int curY = 0;
		int r = 0;
		while (!socket.isClosed()) {
			long then = System.currentTimeMillis();
			try {
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				// write first
				if (r==0) {
					writeMovePacket(output,pid,curX,curY,++curX,++curY);
					System.out.println("GAVE: "+curX+", "+curY +" "+r);
				} else if (r==1) {
					int item = ((int)Math.random()*10)+100;
					writeTakeItemPacket(output,pid,item);
					System.err.println("GAVE: "+item);
				} else {
					writeMovePacket(output,pid,curX,curY,++curX,++curY);
					System.out.println("GAVE: "+curX+", "+curY +" "+r);
				}
				//while(input.available() <= 0) {}
				r = r+1;
				Thread.sleep(4000);
			} catch (IOException e) { e.printStackTrace(); }
			catch (InterruptedException e) { e.printStackTrace(); }
			runcount++;
		}
	}
	
	private void writeTakeItemPacket(DataOutputStream output, int pid, int itemId) throws IOException {
		output.writeInt(1);
		output.writeInt(pid);
		output.writeInt(BasicPacketParser.ITEM_TAKE);
		output.writeInt(pid);
		output.writeInt(0); // because item is being taken from a player
		output.writeInt(itemId);
		output.writeInt(Integer.MAX_VALUE);
	}

	private void writeMovePacket(DataOutputStream output, int pid, int curX, int curY, int destX, int destY) throws IOException {
		output.writeInt(1);
		output.writeInt(pid);
		output.writeInt(BasicPacketParser.MOVE);
		output.writeInt(curX);
		output.writeInt(curY);
		output.writeInt(destX);
		output.writeInt(destY);
		output.writeInt(Integer.MAX_VALUE);
	}
	
	private void readInventoryPacket(DataInputStream output, int pid) throws IOException {
		int val = -1;
		while (val < Integer.MAX_VALUE) {
			val = output.readInt();
			System.out.println("ITEM :"+val);
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
		return false;
	}

	//FIXME DELETE THIS
	/** In between data segments, waits and checks to ensure the connection hasn't
	 * timed out. 
	 * @param s
	 */
	private void waitFor(InputStream s) {
		long TIMEOUT = System.currentTimeMillis() + Main.CONNECTION_TIMEOUT;
		try {
			while (s.available() <= 0 && TIMEOUT > System.currentTimeMillis()) {
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// we've been interrupted! let's get back to work.
					TIMEOUT = 0;
					System.out.println("hmm?");
				}
			}
			if (s.available() > 0) { // great, there's input, let's get back to business
				return;
			}
			System.err.println("Connection timed out! Waiting to close.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("uh... so there's been a terrible accident...");
		}
	}
}
