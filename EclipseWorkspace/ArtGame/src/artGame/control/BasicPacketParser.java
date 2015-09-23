package artGame.control;

import java.awt.Point;
import java.util.Arrays;

/**
 * @author Vicki
 *
 */
public class BasicPacketParser implements PacketParser {
	public final int ACTION;
	public final int ID;
	//public final int length;
	
	private byte[] bytepacket;
	private int[] intpacket;
	
	static final int MOVE = 0;
	static final int INVENTORY = 1;
	static final int ITEM_TAKE = 2;
	static final int ITEM_GIVE = 3;
	static final int ESCAPE = 4;
	static final int LOSE = 5;
	
	static final int GAME_START = 10;
	static final int GAME_END = 11;
	static final int DISCONNECT = 12;
	
	static final int OBJECT_CHANGED = 20;

	public static enum WORLD { OBJECT_CHANGED };
	public static enum CLIENT {PKT_MOVE, PKT_INVENTORY, PKT_ITEM_TAKE, 
		PKT_ITEM_GIVE, PKT_ESCAPED, PKT_CAUGHT };
	public static enum NETWORK { GAME_START, GAME_END, DISCONNECT };
	
	public static enum EVENT_TYPE { CLIENT, NETWORK, WORLD };
	
	private BasicPacketParser(int act, int id, byte[] packet) {
		this.ACTION = act;
		this.ID = id;
		int i = 0;
		bytepacket = Arrays.copyOf(packet, packet.length);
	}
	
	private BasicPacketParser(int act, int id, int[] packet) throws IncompletePacketException {
		this.ACTION = act;
		this.ID = id;
		int i = 0;
		intpacket = Arrays.copyOf(packet, packet.length);
		bytepacket = getBytes();
	}
	
	@Override
	public byte[] getBytes() throws IncompletePacketException {
		if (bytepacket != null) {
			return Arrays.copyOf(bytepacket,bytepacket.length);
		} else if (intpacket != null) {
			byte[] b = new byte[intpacket.length];
			for(int i = 0; i < intpacket.length; i++) {
				b[i] = (byte) intpacket[i];
			}
			return b;
		}
		throw new IncompletePacketException();
	}
	
	public static PacketParser getPacketFromBytes(byte[] bytepacket) throws IncompletePacketException{
		return new BasicPacketParser(bytepacket[2],bytepacket[1],bytepacket);
	}
	
	public static PacketParser getPacketFromInts(int[] intpacket) throws IncompletePacketException{
		return new BasicPacketParser(intpacket[2],intpacket[1],intpacket);
	}
	
	EVENT_TYPE translateType(int t) {
		CLIENT[] client = CLIENT.values();
		NETWORK[] network = NETWORK.values();
		WORLD[] world = WORLD.values();
		if (t <= client[client.length-1].ordinal()) {
			return EVENT_TYPE.CLIENT;
		} else if (t <= network[network.length-1].ordinal()) {
			return EVENT_TYPE.NETWORK;
		} else if (t <= world[world.length-1].ordinal()) {
			return EVENT_TYPE.WORLD;
		}
		throw new IllegalArgumentException();
	}
	
	public Action executePacket() {
		Action m = null;
		return m;
	}
}