package artGame.control.cmds;

import artGame.control.IncompatiblePacketException;

/** */
public class GameStartPacket implements Packet {
	private final int PACKET_LENGTH = 0;
	
	@Override
	public GameStartAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.GAME_START) {
			throw new IncompatiblePacketException("This packet is not using-item packet!");
		}
		boolean isWorld = (packet[0] == 1) ? true : false;
		return new GameStartAction((int)packet[Packet.IDX_PID]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof GameStartAction)) throw new IncompatiblePacketException();
		GameStartAction gsa = (GameStartAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(gsa.isWorldUpdate() ? 1 : 0);
		packet[index++] = (byte)gsa.getClient();
		packet[index++] = Packet.GAME_START;
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
		packet[Packet.IDX_TYPE] = Packet.GAME_START;
		packet[4] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}