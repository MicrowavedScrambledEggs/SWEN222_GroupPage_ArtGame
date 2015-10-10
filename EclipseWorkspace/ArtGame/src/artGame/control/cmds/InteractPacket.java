package artGame.control.cmds;

import artGame.control.IncompatiblePacketException;

/** An InteractPacket is sent to the server when a client wishes
 * to interact with some entity in the world. The server's processing
 * of this therefore occurs independently of what occurs on the
 * client machine. 
 *
 * @author Vicki
 */
class InteractPacket implements Packet {
	private final int PACKET_LENGTH = 0;
	
	@Override
	public InteractAction read(byte[] packet) throws IncompatiblePacketException {
		if (packet.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (packet[Packet.IDX_TYPE] != Packet.INTERACT) {
			throw new IncompatiblePacketException("This packet is not an interact packet!");
		}
		return new InteractAction((int)packet[Packet.IDX_PID]);
	}
	
	@Override	
	public int packetLength() {
		return PACKET_LENGTH;
	}

	@Override
	public byte[] write(Action a) throws IncompatiblePacketException {
		if (!(a instanceof InteractAction)) throw new IncompatiblePacketException();
		InteractAction ia = (InteractAction)a;
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		int index = 0;
		packet[index++] = (byte)(ia.isWorldUpdate() ? 0 : 1);
		packet[index++] = (byte)ia.getClient();
		packet[index++] = Packet.ITEM_GAIN;
		packet[index] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}

	@Override
	public byte[] write(int... values) throws IncompatiblePacketException {
		if (values.length < packetLength() + Packet.HEAD_LENGTH) {
			throw new IncompatiblePacketException("This packet is too short!");
		} else if (values[Packet.IDX_TYPE] != Packet.INTERACT) {
			throw new IncompatiblePacketException("This packet is not an interact packet!");
		}
		byte[] packet = new byte[packetLength() + Packet.HEAD_LENGTH + 1];
		packet[Packet.IDX_ISWORLD] = (byte)values[Packet.IDX_ISWORLD];
		packet[Packet.IDX_PID] = (byte)values[Packet.IDX_PID];
		packet[Packet.IDX_TYPE] = Packet.INTERACT;
		packet[4] = (byte)Integer.MAX_VALUE;
		
		return packet;
	}
}