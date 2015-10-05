package artGame.control;

/** */
class LoseItemPacket implements Packet {
	private final int PACKET_LENGTH = 3;
	
	/** Attempts to read the byte array into an Action. 
	 * 
	 */
	@Override
	public LoseItemAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException("This packet is not an item loss packet!");
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
		LoseItemAction lia = (LoseItemAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(lia.isWorldUpdate() ? 0 : 1);
		packet[index++] = (byte)lia.getClient();
		packet[index++] = Packet.ITEM_LOSE;
		packet[index++] = (byte)(int)lia.getLoserId();
		packet[index++] = (byte)(int)lia.getItemId();
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (values[Packet.IDX_TYPE] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException("This packet is not an item loss packet!");
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[0] = (byte)values[Packet.IDX_ISWORLD];
		packet[1] = (byte)values[Packet.IDX_PID];
		packet[2] = Packet.ITEM_LOSE;
		packet[3] = (byte)values[3];
		packet[4] = (byte)values[4];
		packet[5] = (byte)values[5];
		packet[6] = (byte)values[6];
		packet[7] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}