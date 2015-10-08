package artGame.control.cmds;

import artGame.control.IncompatiblePacketException;

public class EscapePacket implements Packet {
	private final int PACKET_LENGTH = 0;
	
	@Override
	public EscapeAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.ESCAPE) {
			throw new IncompatiblePacketException("This packet is not an escape packet!");
		}
		return new EscapeAction((int)packet[Packet.IDX_PID]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof EscapeAction)) throw new IncompatiblePacketException();
		EscapeAction ea = (EscapeAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(ea.isWorldUpdate() ? 1 : 0); // should always be 1
		packet[index++] = (byte)ea.getClient();
		packet[index++] = Packet.ESCAPE;
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH + 1) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (values[Packet.IDX_TYPE] != Packet.ITEM_GAIN) {
			throw new IncompatiblePacketException("This packet is not an escape packet!");
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		packet[Packet.IDX_ISWORLD] = (byte)values[Packet.IDX_ISWORLD];
		packet[Packet.IDX_PID] = (byte)values[Packet.IDX_PID];
		packet[Packet.IDX_TYPE] = Packet.ESCAPE;
		packet[3] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}