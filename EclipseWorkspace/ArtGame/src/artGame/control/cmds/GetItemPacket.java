package artGame.control.cmds;

import artGame.control.IncompatiblePacketException;

/** GetItemPacket contains the name of 
 * 
 * 
 * 
 * 
 * */
class GetItemPacket implements Packet {
	private final int PACKET_LENGTH = 2;
	
	@Override
	public GetItemAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException("This packet is not an item gain packet!");
		}
		boolean isWorld = (packet[0] == 1) ? true : false;
		return new GetItemAction(isWorld, (int)packet[Packet.IDX_PID], (int)packet[3], (int)packet[4]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof GetItemAction)) throw new IncompatiblePacketException();
		GetItemAction tia = (GetItemAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(tia.isWorldUpdate() ? 0 : 1);
		packet[index++] = (byte)tia.getClient();
		packet[index++] = Packet.ITEM_GAIN;
		packet[index++] = (byte)(int)tia.getItemDestination();
		packet[index++] = (byte)(int)tia.getItemId();
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (values[Packet.IDX_TYPE] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException("This packet is not an item gain packet!");
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		packet[Packet.IDX_ISWORLD] = (byte)values[Packet.IDX_ISWORLD];
		packet[Packet.IDX_PID] = (byte)values[Packet.IDX_PID];
		packet[Packet.IDX_TYPE] = Packet.ITEM_GAIN;
		packet[3] = (byte)values[3];
		packet[4] = (byte)values[4];
		packet[5] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}