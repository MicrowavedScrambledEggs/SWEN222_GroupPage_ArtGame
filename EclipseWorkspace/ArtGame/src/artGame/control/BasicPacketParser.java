package artGame.control;

import java.awt.Point;
import java.util.Arrays;

/**
 * @author Vicki
 *
 * Note to self: there could just be an array of Packets and you can loop around those
 * to look for the one that doesn't throw an exception... Massive if-blocks are nonessential. 
 */
public class BasicPacketParser implements PacketParser {
	public final int ACTION;
	public final int ID;
	
	private byte[] bytepacket;
	private int[] intpacket;
	
<<<<<<< HEAD
	/* obsolete */
	private BasicPacketParser(byte[] packet) throws IncompletePacketException {
		if (packet == null) throw new IncompletePacketException();
		this.ACTION = packet[Packet.IDX_TYPE];
		this.ID = packet[Packet.IDX_PID];
		bytepacket = packet;
		bytepacket = getBytes();
	}	
	
	/** Thread reads input as an array of integers. Give me your delicious
	 * integers, I will consume them all. 
	 * @param packet
	 * @throws IncompletePacketException
	 */
	private BasicPacketParser(int[] packet) throws IncompletePacketException {
		this.ACTION = packet[Packet.IDX_TYPE];
		this.ID = packet[Packet.IDX_PID];
=======
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
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
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
	
<<<<<<< HEAD
	public static Action getActionFromBytes(byte[] bytepacket, boolean fromClient) throws IncompletePacketException{
		return new BasicPacketParser(bytepacket).executePacket(false);
	}
	
	public static Action getActionFromInts(int[] intpacket, boolean fromClient) throws IncompletePacketException{
		return new BasicPacketParser(intpacket).executePacket(false);
=======
	public static PacketParser getPacketFromBytes(byte[] bytepacket) throws IncompletePacketException{
		return new BasicPacketParser(bytepacket[2],bytepacket[1],bytepacket);
	}
	
	public static PacketParser getPacketFromInts(int[] intpacket) throws IncompletePacketException{
		return new BasicPacketParser(intpacket[2],intpacket[1],intpacket);
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
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
	
<<<<<<< HEAD
	/** This method interprets a byte array packet as an Action. */
	public static Action executePacket(byte[] packet, boolean fromClient) throws IncompletePacketException {
		int action = (int)packet[Packet.IDX_TYPE];
		try {
			if (action == Packet.MOVE) {
				System.out.println("Moving");
				return (new MovePlayerPacket()).read(packet);
			} 
			// item packets!
			else if (action == Packet.ITEM_GAIN) {
				System.out.println("Giving item");
				return (new GetItemPacket()).read(packet);
			} 
			else if (action == Packet.ITEM_LOSE) {
				System.out.println("Losing item");
				return (new LoseItemPacket()).read(packet);
			} 
			// Inventory packets!
			else if (action == Packet.INVENTORY && fromClient) {
				System.out.println("Server reads get-inventory request");
				return (new GetInventoryPacket().read(packet));
			} 
			else if (action == Packet.INVENTORY && !fromClient) {
				System.out.println("Client reads sent inventory");
				return (new ReadInventoryPacket().read(packet));
			}
			// TODO Being captured
			// TODO Escaping
			// TODO start game
			// TODO end game
			// TODO disconnect
			// TODO object change
		} catch (IncompatiblePacketException e) {
			e.printStackTrace();
		}
		throw new IncompletePacketException();
=======
	public Action executePacket() {
		Action m = null;
		return m;
>>>>>>> 342b35d931854225238b59e40ad31c79ee42260b
	}

	@Override
	public Action executePacket(boolean fromClient) throws IncompletePacketException {
		return BasicPacketParser.executePacket(bytepacket,false);
	}
}