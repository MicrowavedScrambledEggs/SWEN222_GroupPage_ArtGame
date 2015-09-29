package artGame.control;

import java.util.Arrays;

import artGame.main.Main;

class ReadInventoryPacket implements Packet {
	private final int PACKET_LENGTH = Integer.MAX_VALUE;
	
	@Override
	public ReadInventoryAction read(byte[] packet) throws IncompatiblePacketException {
//		if (packet.length < Packet.HEAD_LENGTH) throw new IncompatiblePacketException();
		System.out.println("INVENTORY: Reading an INVENTORY packet");
		int index = Packet.HEAD_LENGTH;
		boolean isWorld = (packet[0] == 1) ? true : false;
		int[] inv = new int[Main.LARGE_PACKET_SIZE];
		// +1 because [3] == id of the inventory's owner. 
		int i = Packet.HEAD_LENGTH + 1;
		for ( ; i < inv.length; i++) {
			if (i == packet.length - 1) {
				System.out.println("Out!");
				break;
			}
			inv[i] = packet[i];
		}
		return new ReadInventoryAction(packet[2], packet[3], Arrays.copyOf(inv, i - Packet.HEAD_LENGTH - 1));
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof ReadInventoryAction)) throw new IncompatiblePacketException();
		ReadInventoryAction rinva = (ReadInventoryAction)a;
		int[] items = rinva.getInventory(); 
		byte[] packet = new byte[rinva.getInventory().length + Packet.HEAD_LENGTH + 1];
		packet[0] = (byte)(rinva.isWorldUpdate() ? 0 : 1);
		packet[1] = (byte)rinva.getRecipient();
		packet[2] = Packet.INVENTORY;
		packet[3] = (byte)rinva.getInventoryOwner();
		int i = 4;
		for ( ; i < items.length; i++) {
			if (items[i - 4] == -1) { break; }
			else { 
				packet[i] = (byte)items[i - 4];
			}
		}

		packet[i]	= (byte)Packet.TERMINAL;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH || values[3] != Packet.ITEM_LOSE) {
			throw new IncompatiblePacketException();
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)values[0];
		packet[index++] = (byte)values[1];
		packet[index++] = Packet.INVENTORY;
		packet[index++] = (byte)values[3];
		packet[index++] = (byte)values[4];
		packet[index++] = (byte)values[5];
		packet[index++] = (byte)values[6];
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}