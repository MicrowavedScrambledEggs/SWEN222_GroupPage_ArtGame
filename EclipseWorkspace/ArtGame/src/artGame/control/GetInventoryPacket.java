package artGame.control;

class GetInventoryPacket implements Packet {
	private final int PACKET_LENGTH = 3;
	
	@Override
	public GetInventoryAction read(byte[] packet) throws IncompatiblePacketException {
		return new GetInventoryAction(packet[2],packet[3]);
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
		// TODO Auto-generated method stub
		return null;
	}
}