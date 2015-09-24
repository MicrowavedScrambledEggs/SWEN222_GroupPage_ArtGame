package artGame.control;

import java.awt.Point;
import java.util.Arrays;

import artGame.main.Main;

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
	 * integets, I will consume them all. 
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
					return (new MovePacket()).read(bytepacket);
				case (Packet.ITEM_TAKE):
					return (new TakeItemPacket()).read(bytepacket);
				
				// TODO add the rest of the cases!
			}
		} catch (IncompatiblePacketException e) {
			e.printStackTrace();
		}
		throw new IncompletePacketException();
	}
}



/** MovePacket contains the player's current and destination coordinates. 
 * 
 * 
 * 
 * 
 * */
class MovePacket implements Packet {
	private final int PACKET_LENGTH = 4;

	@Override
	public Action read(byte[] packet) throws IncompatiblePacketException { // FIXME why can't this be package-visible?
//		if (packet.length != PACKET_LENGTH + Packet.HEAD_LENGTH || packet.length > Main.LARGE_PACKET_SIZE) {
//			throw new IncompatiblePacketException(Packet.HEAD_LENGTH + PACKET_LENGTH +" is wrong!"+ packet.length);
//		}
		int index = Packet.HEAD_LENGTH;
		System.out.println("MOVEPACKET: Reading a MOVE packet");
		Point playerPos = new Point(packet[index++],packet[index++]);
		Point playerDes = new Point(packet[index++],packet[index]);
		System.out.println(packet[1]+" ("+playerPos.getX()+","+playerPos.getY()+") -> ("+playerDes.getX()+","+playerDes.getY()+")");
		return new MovePlayerAction(packet[1],playerPos,playerDes);
	}

	@Override
	public int packetLength() {
		return PACKET_LENGTH;
	}
}

/** TakeItemPacket contains the name of 
 * 
 * 
 * 
 * 
 * */
class TakeItemPacket implements Packet {
	private final int PACKET_LENGTH = 3;
	
	@Override
	public Action read(byte[] packet) throws IncompatiblePacketException {
//		if (packet.length != Packet.HEAD_LENGTH + PACKET_LENGTH) throw new IncompatiblePacketException();
		System.out.println("TAKEPACKET: Reading a TAKE packet");
		int index = Packet.HEAD_LENGTH;
		boolean isWorld = (packet[0] == 1) ? true : false;
		return new TakeItemAction(isWorld, packet[index++], packet[index]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}
}

class ReadInventoryPacket implements Packet {
	private final int PACKET_LENGTH = -1;
	
	@Override
	public Action read(byte[] packet) throws IncompatiblePacketException {
//		if (packet.length < Packet.HEAD_LENGTH) throw new IncompatiblePacketException();
		System.out.println("INVENTORY: Reading an INVENTORY packet");
		int index = Packet.HEAD_LENGTH;
		boolean isWorld = (packet[0] == 1) ? true : false;
		int[] inv = new int[packet.length-Packet.HEAD_LENGTH];
		for (int i = 0; i < inv.length; i++) {
			inv[i] = packet[i];
		}
		return new ReadInventoryAction(inv);
	}
	
	@Override	
	public int packetLength() {
		return Integer.MAX_VALUE;
	}
}

class SendInventory implements Packet {
	private final int PACKET_LENGTH = 3;
	
	@Override
	public Action read(byte[] packet) throws IncompatiblePacketException {
//		if (packet.length != Packet.HEAD_LENGTH + PACKET_LENGTH) throw new IncompatiblePacketException();
		return new SendInventoryAction(packet[2],packet[3]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}
}