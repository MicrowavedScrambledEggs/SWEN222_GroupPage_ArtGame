package artGame.control;

class GetInventoryPacket implements Packet {
	private final int PACKET_LENGTH = 1;
	
	@Override
	public GetInventoryAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.INVENTORY) {
			throw new IncompatiblePacketException("This packet is not a get-inventory packet!");
		}
		return new GetInventoryAction(packet[Packet.IDX_PID],packet[3]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof GetInventoryAction)) throw new IncompatiblePacketException();
		GetInventoryAction ma = (GetInventoryAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(ma.isWorldUpdate() ? 0 : 1);
		packet[index++] = (byte)ma.getRecipient();
		packet[index++] = Packet.INVENTORY;
		packet[index++] = (byte)ma.getInventoryOwner();
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;		
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (values[Packet.IDX_TYPE] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException("This packet is not an inventory request packet!");
		}
		byte[] packet = new byte[Packet.HEAD_LENGTH + packetLength() + 1];
		packet[0] = (byte)values[0];
		packet[1] = (byte)values[1];
		packet[2] = Packet.INVENTORY;
		packet[3] = (byte)values[3];
		packet[4] = (byte)Packet.TERMINAL;
		return packet;
	}
}