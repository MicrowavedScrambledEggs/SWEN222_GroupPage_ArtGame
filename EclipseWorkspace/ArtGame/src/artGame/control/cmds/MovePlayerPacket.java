package artGame.control.cmds;

import java.awt.Point;
import java.util.Arrays;

import artGame.control.IncompatiblePacketException;
import artGame.game.Character.Direction;

/** MovePlayerPacket is capable of reading and writing move packets. 
 * 
 * @author Vicki
 * 
 */
class MovePlayerPacket implements Packet {
	private static final int N = 1;
	private static final int S = 3;
	private static final int E = 2;
	private static final int W = 4;
	private final int PACKET_LENGTH = 7;

	@Override
	public MovePlayerAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.MOVE) {
			throw new IncompatiblePacketException("This packet is not a move packet!");
		}
		Point playerPos = new Point(packet[4],packet[5]);
		long time = 0;
		for (int i = 7; i < 11; i++) // thanks to http://stackoverflow.com/questions/1026761/how-to-convert-a-byte-array-to-its-numeric-value-java
		{
		   time += ((long) packet[i] & 0xffL) << (4 * i);
		}
		System.out.println(packet[0] +" for "+ packet[1]+" ("+playerPos.getX()+","+playerPos.getY()+") @ "+time);
		return new MovePlayerAction((int)packet[Packet.IDX_PID], (int)packet[3], 
				playerPos, byteToDirection(packet[6]), time);
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
		packet[index++] = (byte)ma.getClient();
		packet[index++] = Packet.MOVE;
		packet[index++] = (byte)(int)ma.getPlayerId();
		packet[index++] = (byte)(int)ma.getCurrent().getX();
		packet[index++] = (byte)(int)ma.getCurrent().getY();
		packet[index++] = directionToByte(ma.getCurrentDirection());
//		packet[index++] = (byte)(int)ma.getDestination().getX();
//		packet[index++] = (byte)(int)ma.getDestination().getY();
//		packet[index++] = directionToByte(ma.getDestinationDirection());
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
		packet[index++] = (byte)(int)values[8];
		packet[index++] = (byte)(int)values[9];
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
	
	public static Direction byteToDirection(int i) {
		switch (i) {
			case N:
				return Direction.NORTH;
			case E:
				return Direction.EAST;
			case S:
				return Direction.SOUTH;
			case W:
				return Direction.SOUTH;
		}
		return Direction.SOUTH;
	}
	
	public static byte directionToByte(Direction d) {
		switch (d) {
			case NORTH:
				return N;
			case EAST:
				return E;
			case SOUTH:
				return S;
			case WEST:
				return W;
		}
		return S;
	}
}