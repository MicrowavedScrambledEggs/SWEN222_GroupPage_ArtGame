package artGame.control;

/** */
public class UseItemPacket implements Packet {
	private final int PACKET_LENGTH = 3;
	
	@Override
	public UseItemAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.ITEM_USE) {
			throw new IncompatiblePacketException("This packet is not using-item packet!");
		}
		boolean isWorld = (packet[0] == 1) ? true : false;
		return new UseItemAction(isWorld, (int)packet[Packet.IDX_PID], (int)packet[3], (int)packet[4], (int)packet[5]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof UseItemAction)) throw new IncompatiblePacketException();
		UseItemAction uia = (UseItemAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(uia.isWorldUpdate() ? 1 : 0);
		packet[index++] = (byte)uia.getClient();
		packet[index++] = Packet.ITEM_GAIN;
		packet[index++] = (byte)uia.getUsersId();
		packet[index++] = (byte)(int)uia.getEntityId();
		packet[index++] = (byte)(int)uia.getItemId();
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
		packet[5] = (byte)values[5];
		packet[6] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}