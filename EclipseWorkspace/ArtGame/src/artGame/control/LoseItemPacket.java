package artGame.control;

/** */
class LoseItemPacket implements Packet {
	private final int PACKET_LENGTH = 3;
	
	/** Attempts to read the byte array into an Action. 
	 * 
	 */
	@Override
	public LoseItemAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length <= Packet.HEAD_LENGTH || packet[3] != Packet.ITEM_LOSE) {
			throw new IncompatiblePacketException();
		}
		System.out.println("TAKEPACKET: Reading a TAKE packet");
		boolean isWorld = (packet[0] == 1) ? true : false;
		return new LoseItemAction(isWorld, (int)packet[1], (int)packet[3], (int)packet[5]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof LoseItemAction)) throw new IncompatiblePacketException();
		LoseItemAction tia = (LoseItemAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(tia.isWorldUpdate() ? 0 : 1);
		packet[index++] = (byte)tia.getRecipient();
		packet[index++] = Packet.ITEM_LOSE;
		packet[index++] = (byte)(int)tia.getLoserId();
		packet[index++] = (byte)(int)tia.getItemId();
		packet[index] = (byte)Integer.MAX_VALUE;
		
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
		packet[index++] = Packet.ITEM_LOSE;
		packet[index++] = (byte)values[3];
		packet[index++] = (byte)values[4];
		packet[index++] = (byte)values[5];
		packet[index++] = (byte)values[6];
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}