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
	
	public ClientThread(Socket s, Game g) {
		this.socket = s;
		game = g;
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
			try {
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				byte[] packet = null;
				// write first
<<<<<<< HEAD
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
				r++;
				Thread.sleep(500);
			} catch (IOException e) { 
				e.printStackTrace(); 
			} catch (InterruptedException e) { 
				e.printStackTrace();
			} catch (IncompatiblePacketException e) {
				// TODO Auto-generated catch block
				System.err.println("Cannot be having this, you monster.");
				e.printStackTrace();
			}
			runcount++;
		}
	}
=======
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
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b

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

<<<<<<< HEAD
	@Override
	public int getPlayerId() {
		return pid;
=======
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
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
	}
}
