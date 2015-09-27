package artGame.control;

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
	
	/* obsolete */
	private BasicPacketParser(int act, int id, byte[] packet) throws IncompletePacketException {
		if (packet == null) throw new IncompletePacketException();
		this.ACTION = act;
		this.ID = id;
		bytepacket = packet;
		bytepacket = getBytes();
	}	
	
	/** Thread reads input as an array of integers. Give me your delicious
	 * integers, I will consume them all. 
	 * @param packet
	 * @throws IncompletePacketException
	 */
	private BasicPacketParser(int[] packet) throws IncompletePacketException {
		this.ACTION = packet[2];
		this.ID = packet[1];
		intpacket = Arrays.copyOf(packet, packet.length);
		bytepacket = getBytes();
	}
	
	/** Returns a copy of the Parser's contents as a byte array,
	 * containing the header, contents and terminating character.
	 * @return
	 * @throws IncompletePacketException If the Parser has not
	 * been given a byte packet. 
	 */
	public byte[] getBytes() throws IncompletePacketException {
		if (bytepacket != null) {
			for (int i = 0; i < bytepacket.length; i++) {
				if (bytepacket[i] == Packet.TERMINAL) {
					return Arrays.copyOf(bytepacket, i+1);
				}
			}
			return Arrays.copyOf(bytepacket, bytepacket.length);
		} else if (intpacket != null) {
			byte[] b = new byte[intpacket.length];
			for (int i = 0; i < intpacket.length; i++) {
				if (intpacket[i] == Packet.TERMINAL) {
					return Arrays.copyOf(b,i+1);
				}
				b[i] = (byte) intpacket[i];
			}
			return b;
		}
		throw new IncompletePacketException();
	}
	
	public static Action getActionFromBytes(byte[] bytepacket) throws IncompletePacketException{
		return new BasicPacketParser(bytepacket[2],bytepacket[1],bytepacket).executePacket();
	}
	
	public static Action getActionFromInts(int[] intpacket) throws IncompletePacketException{
		return new BasicPacketParser(intpacket).executePacket();
	}
	
	Packet.EVENT_TYPE translateType(int t) {
		Packet.CLIENT[] client = Packet.CLIENT.values();
		Packet.NETWORK[] network = Packet.NETWORK.values();
		Packet.WORLD[] world = Packet.WORLD.values();
		if (t <= client[client.length-1].ordinal()) {
			return Packet.EVENT_TYPE.CLIENT;
		} else if (t <= network[network.length-1].ordinal()) {
			return Packet.EVENT_TYPE.NETWORK;
		} else if (t <= world[world.length-1].ordinal()) {
			return Packet.EVENT_TYPE.WORLD;
		}
		throw new IllegalArgumentException();
	}
	
	public Action executePacket() throws IncompletePacketException {
		try {
			switch (ACTION) {
				case (Packet.MOVE):
					System.out.println("Moving");
					return (new MovePlayerPacket()).read(bytepacket);
				case (Packet.ITEM_LOSE):
					System.out.println("Giving");
					return (new GetItemPacket()).read(bytepacket);
				case (Packet.INVENTORY):
					System.out.println("");
				// TODO add the rest of the cases!
			}
		} catch (IncompatiblePacketException e) {
			e.printStackTrace();
		}
		throw new IncompletePacketException();
	}
}