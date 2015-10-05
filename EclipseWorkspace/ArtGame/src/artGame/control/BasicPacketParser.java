package artGame.control;

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
	
	public static Action getActionFromBytes(byte[] bytepacket, boolean fromClient) throws IncompletePacketException{
		return new BasicPacketParser(bytepacket).executePacket(false);
	}
	
	public static Action getActionFromInts(int[] intpacket, boolean fromClient) throws IncompletePacketException{
		return new BasicPacketParser(intpacket).executePacket(false);
	}
	
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
	}

	@Override
	public Action executePacket(boolean fromClient) throws IncompletePacketException {
		return BasicPacketParser.executePacket(bytepacket,false);
	}
}