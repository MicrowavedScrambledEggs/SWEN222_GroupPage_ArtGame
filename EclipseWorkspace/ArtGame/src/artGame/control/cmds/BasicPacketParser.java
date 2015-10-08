package artGame.control.cmds;

import java.util.Arrays;

import artGame.control.IncompatiblePacketException;
import artGame.control.IncompletePacketException;

/**
 * @author Vicki
 *
 * Note to self: there could just be an array of Packets and you can loop around those
 * to look for the one that doesn't throw an exception... Massive if-blocks are nonessential. 
 */
public class BasicPacketParser {
	private static final MovePlayerPacket MOVE_PKT = new MovePlayerPacket();
	private static final GetItemPacket ITEM_GAIN_PKT = new GetItemPacket();
	private static final LoseItemPacket ITEM_LOSE_PKT = new LoseItemPacket();
	private static final UseItemPacket ITEM_USE_PKT = new UseItemPacket();
	private static final GetInventoryPacket GET_INV_PKT = new GetInventoryPacket();
	private static final ReadInventoryPacket READ_INV_PKT = new ReadInventoryPacket();
	private static final CapturedPacket LOSE_PKT = new CapturedPacket();
	private static final EscapePacket ESCAPE_PKT = new EscapePacket();
	private static final GameStartPacket GAME_START_PKT = new GameStartPacket();
	private static final GameEndPacket GAME_END_PKT = new GameEndPacket();
	
	
	/** This method interprets a byte array packet as an Action. */
	public static Action getActionFromPacket(byte[] packet, boolean fromClient) throws IncompletePacketException {
		int action = (int)packet[Packet.IDX_TYPE];
		try {
			if (action == Packet.MOVE) {
				System.out.println("Moving");
				return MOVE_PKT.read(packet);
			} 
			// item packets!
			else if (action == Packet.ITEM_GAIN) {
				System.out.println("Giving item");
				return ITEM_GAIN_PKT.read(packet);
			} 
			else if (action == Packet.ITEM_LOSE) {
				System.out.println("Losing item");
				return ITEM_LOSE_PKT.read(packet);
			} 
			else if (action == Packet.ITEM_USE) {
				System.out.println("Using item");
				return ITEM_USE_PKT.read(packet);
			}
			// Inventory packets!
			else if (action == Packet.GET_INVENTORY) {
				System.out.println("Server reads get-inventory request");
				return GET_INV_PKT.read(packet);
			} 
			else if (action == Packet.READ_INVENTORY) {
				System.out.println("Client reads inventory");
				return READ_INV_PKT.read(packet);
			}
			// Losing-the-game packets!
			else if (action == Packet.LOSE) {
				System.out.println("Losing game");
				return LOSE_PKT.read(packet);
			}
			// Escape packets!
			else if (action == Packet.ESCAPE) {
				System.out.println("Escaping game!");
				return ESCAPE_PKT.read(packet);
			}
			// Start game
			else if (action == Packet.GAME_START) {
				System.out.println("Starting game! (should be received by client)");
				return GAME_START_PKT.read(packet);
			}
			// end game
			else if (action == Packet.GAME_END) {
				System.out.println("Ending game! (should be received by client)");
				return GAME_END_PKT.read(packet);
			}
			// TODO object change
		} catch (IncompatiblePacketException e) {
			e.printStackTrace();
		}
		throw new IncompletePacketException();
	}
	
	/** 
	 * 
	 * @param a
	 * @param fromClient
	 * @return
	 */
	public static byte[] getPacket(Action a, boolean fromClient) {
		try {
		switch (a.type()) {
			case Packet.MOVE:
				return MOVE_PKT.write(a);
			case Packet.ITEM_GAIN:
				return ITEM_GAIN_PKT.write(a);
			case Packet.ITEM_LOSE:
				return ITEM_LOSE_PKT.write(a);
			case Packet.ITEM_USE:
				return ITEM_USE_PKT.write(a);
			case Packet.GET_INVENTORY:
				return GET_INV_PKT.write(a);
			case Packet.READ_INVENTORY:
				return READ_INV_PKT.write(a);
			case Packet.LOSE:
				return LOSE_PKT.write(a);
			case Packet.ESCAPE:
				return ESCAPE_PKT.write(a);
			case Packet.GAME_START:
				return GAME_START_PKT.write(a);
			case Packet.GAME_END:
				return GAME_END_PKT.write(a);
		}
		} catch (IncompatiblePacketException e) {
			System.err.println("error! could not read action:\n\t"+ a.toString());
			e.printStackTrace();
		}
		return null;
	}
	
	/** 
	 * 
	 * @param a
	 * @param fromClient
	 * @return
	 */
	public static byte[] createPacket(int... values) {
		if (values.length < Packet.HEAD_LENGTH) { return null; }
		int type = values[Packet.IDX_TYPE];
		try {
		switch (type) {
			case Packet.MOVE:
				return MOVE_PKT.write(values);
			case Packet.ITEM_GAIN:
				return ITEM_GAIN_PKT.write(values);
			case Packet.ITEM_LOSE:
				return ITEM_LOSE_PKT.write(values);
			case Packet.ITEM_USE:
				return ITEM_USE_PKT.write(values);
			case Packet.GET_INVENTORY:
				return GET_INV_PKT.write(values);
			case Packet.READ_INVENTORY:
				return READ_INV_PKT.write(values);
			case Packet.LOSE:
				return LOSE_PKT.write(values);
			case Packet.ESCAPE:
				return ESCAPE_PKT.write(values);
			case Packet.GAME_START:
				return GAME_START_PKT.write(values);
			case Packet.GAME_END:
				return GAME_END_PKT.write(values);
		}
		} catch (IncompatiblePacketException e) {
			System.err.println("error! could not read values.");
			e.printStackTrace();
		}
		return null;
	}
}