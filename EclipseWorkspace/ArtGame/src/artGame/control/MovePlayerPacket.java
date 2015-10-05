package artGame.control;

import java.awt.Point;
import java.util.Arrays;

/** MovePlayerPacket contains the player's current and destination coordinates. 
 * 
 * 
 * 
 * 
 * */
class MovePlayerPacket implements Packet {
	private final int PACKET_LENGTH = 5;

	@Override
	public MovePlayerAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.MOVE) {
			throw new IncompatiblePacketException("This packet is not a move packet!");
		}
		int index = Packet.HEAD_LENGTH;
		Point playerPos = new Point(packet[index++],packet[index++]);
		Point playerDes = new Point(packet[index++],packet[index]);
		System.out.println(packet[0] +" for "+ packet[1]+" ("+playerPos.getX()+","+playerPos.getY()+") -> ("+playerDes.getX()+","+playerDes.getY()+")");
		return new MovePlayerAction((int)packet[1],(int)packet[3],playerPos,playerDes);
	}

	@Override
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof MovePlayerAction)) throw new IncompatiblePacketException();
		MovePlayerAction ma = (MovePlayerAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(ma.isWorldUpdate() ? 0 : 1);
		packet[index++] = (byte)ma.getRecipient();
		packet[index++] = Packet.MOVE;
		packet[index++] = (byte)(int)ma.getPlayerId();
		packet[index++] = (byte)(int)ma.getCurrent().getX();
		packet[index++] = (byte)(int)ma.getCurrent().getY();
		packet[index++] = (byte)(int)ma.getDestination().getX();
		packet[index++] = (byte)(int)ma.getDestination().getY();
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (values[Packet.MOVE] != Packet.MOVE) {
			throw new IncompatiblePacketException("This packet is not a move packet!");
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(int)values[0];
		packet[index++] = (byte)(int)values[1];
		packet[index++] = Packet.MOVE;
		packet[index++] = (byte)(int)values[3];
		packet[index++] = (byte)(int)values[4];
		packet[index++] = (byte)(int)values[5];
		packet[index++] = (byte)(int)values[6];
		packet[index++] = (byte)(int)values[7];
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}