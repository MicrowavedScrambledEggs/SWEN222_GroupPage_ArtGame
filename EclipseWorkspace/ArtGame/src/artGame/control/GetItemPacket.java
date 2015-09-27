package artGame.control;

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
//		if (packet.length != Packet.HEAD_LENGTH + PACKET_LENGTH) throw new IncompatiblePacketException();
		boolean isWorld = (packet[0] == 1) ? true : false;
		return new GetItemAction(isWorld, (int)packet[1], (int)packet[3], (int)packet[4]);
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
		packet[index++] = (byte)tia.getRecipient();
		packet[index++] = Packet.ITEM_GAIN;
		packet[index++] = (byte)(int)tia.getItemSource();
		packet[index++] = (byte)(int)tia.getItemId();
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH || values[3] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException();
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)values[0];
		packet[index++] = (byte)values[1];
		packet[index++] = Packet.ITEM_GAIN;
		packet[index++] = (byte)values[3];
		packet[index++] = (byte)values[4];
		packet[index++] = (byte)values[5];
		packet[index++] = (byte)values[6];
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}