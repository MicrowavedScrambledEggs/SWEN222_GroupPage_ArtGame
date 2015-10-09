package artGame.control.cmds;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import artGame.control.IncompatiblePacketException;
import artGame.game.Character.Direction;

/**
 * @author Vicki
 *
 * Note to self: there could just be an array of Packets and you can loop around those
 * to look for the one that doesn't throw an exception... Massive if-blocks are non-essential. 
 */
public class BasicPacketParser {
	private static final int N = 1;
	private static final int S = 3;
	private static final int E = 2;
	private static final int W = 4;
//	private static final MovePlayerPacket MOVE_PKT = new MovePlayerPacket();
//	private static final GetItemPacket ITEM_GAIN_PKT = new GetItemPacket();
//	private static final LoseItemPacket ITEM_LOSE_PKT = new LoseItemPacket();
//	private static final UseItemPacket ITEM_USE_PKT = new UseItemPacket();
//	private static final GetInventoryPacket GET_INV_PKT = new GetInventoryPacket();
//	private static final ReadInventoryPacket READ_INV_PKT = new ReadInventoryPacket();
//	private static final CapturedPacket LOSE_PKT = new CapturedPacket();
//	private static final EscapePacket ESCAPE_PKT = new EscapePacket();
//	private static final GameStartPacket GAME_START_PKT = new GameStartPacket();
//	private static final GameEndPacket GAME_END_PKT = new GameEndPacket();
//	private static final InteractPacket INTERACT_PKT = new InteractPacket();
	
	/** This method manages the reading of actions from the input stream and returns the appropriate
	 * action for the game or server to take. 
	 * 
	 * @param in Input stream being read.
	 * @return The next action to be executed.
	 * 
	 * REQUIRES: in.readInt() == 0 || 1 
	 */
	public static Action getActionFromStream(DataInputStream in) throws IncompatiblePacketException {
		try {
			boolean isWorld = in.readBoolean();
			int pid = in.readInt();
			int type = in.readInt();

			try {
				switch (type) {
					case Packet.MOVE:
						return readAsMove(in, isWorld, pid);
					case Packet.ITEM_GAIN:
						return readAsItemGain(in, isWorld, pid);
					case Packet.ITEM_LOSE:
						return readAsItemLose(in, isWorld, pid);
					case Packet.ITEM_USE:
						return readAsItemUse(in, isWorld, pid);
					case Packet.GET_INVENTORY:
						return readAsSendInventory(in, pid);
					case Packet.READ_INVENTORY:
						return readAsInventory(in, pid);
					case Packet.LOSE:
						return readAsCaught(in, pid);
					case Packet.ESCAPE:
						return readAsEscape(in, pid);
					case Packet.GAME_START:
						return readAsStart(in, pid);
					case Packet.GAME_END:
						return readAsEnd(in, pid);
				}
			} catch (IncompatiblePacketException e) {
				System.err.println("error! could not read values.");
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Could not recognise the type from data stream.");
	}
	
	
	/** This method manages the writing of actions to the output stream.
	 * 
	 * @param out Output stream being written to.
	 * @return The next action to be executed.
	 * 
	 * REQUIRES: in.readInt() == 0 || 1 
	 */
	public static void writeActionToStream(DataOutputStream out, Action a) throws IncompatiblePacketException, IOException {
		BasicPacketParser.writeHeader(out,a.isWorldUpdate(),a.getClient());
		int type = a.type();
		switch (type) {
			case Packet.MOVE:
				writeAsMove(out, (MovePlayerAction)a);
				return;
			case Packet.ITEM_GAIN:
				writeAsGainItem(out, (GetItemAction)a);
				return;
			case Packet.ITEM_LOSE:
				writeAsLoseItem(out, (LoseItemAction)a);
				return;
			case Packet.ITEM_USE:
				writeAsUseItem(out, (UseItemAction)a);
				return;
			case Packet.GET_INVENTORY:
				writeAsGetInventory(out, (GetInventoryAction)a);
				return;
			case Packet.READ_INVENTORY:
				writeAsReadInventory(out, (ReadInventoryAction)a);
				return;
			case Packet.LOSE:
				writeAsCaughtAction(out, (CapturedAction)a);
				return;
			case Packet.ESCAPE:
				writeAsEscapeAction(out, (EscapeAction)a);
				return;
			case Packet.GAME_START:
				writeAsGameStart(out, (GameStartAction)a);
				return;
			case Packet.GAME_END:
				writeAsGameEnd(out, (GameEndAction)a);
				return;
		}
		throw new IncompatiblePacketException("BasicPacketParser is unable to parse this type ("+type+"), Action "+a.toString());
	}
	
	private static void writeHeader(DataOutputStream o, boolean isWorld, int recipient) {
		try {
			o.writeBoolean(isWorld);
			o.writeInt(recipient);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* =============================================================
	 * =============================================================
	 * 							READ METHODS
	 * =============================================================
	 * =============================================================
	 */
	
	/** Attempts to read a GameEndAction from the stream. */
	private static GameEndAction readAsEnd(DataInputStream in, int pid) throws IncompatiblePacketException {
		try {
			GameEndAction a = new GameEndAction(pid);
			if (in.readInt() == Packet.TERMINAL) {
				return a;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for this Game End action.");
	}

	/** Attempts to read a GameStartAction from the stream. */
	private static GameStartAction readAsStart(DataInputStream in, int pid) throws IncompatiblePacketException  {
		try {
			GameStartAction a = new GameStartAction(pid);
			if (in.readInt() == Packet.TERMINAL) {
				return a;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for this Game Start action.");
	}

	/** Attempts to read an EscapeAction from the stream. */
	private static EscapeAction readAsEscape(DataInputStream in, int pid) throws IncompatiblePacketException  {
		try {
			EscapeAction a = new EscapeAction(pid);
			if (in.readInt() == Packet.TERMINAL) {
				return a;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for this Escape action");
	}

	/** Attempts to read a Capture action from the stream. */
	private static CapturedAction readAsCaught(DataInputStream in, int pid) throws IncompatiblePacketException  {
		try {
			CapturedAction a = new CapturedAction(pid);
			if (in.readInt() == Packet.TERMINAL) {
				return a;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a Caught action.");
	}

	/** Attempts to read an inventory from the stream. */
	private static ReadInventoryAction readAsInventory(DataInputStream in, int pid) throws IncompatiblePacketException {
		try {
			List<Integer> inv = new ArrayList<Integer>();
			int next = Packet.READ_INVENTORY;
			int ownerId = in.readInt();
			
			while (next != Packet.TERMINAL) {
				inv.add(in.readInt());
			}
			
			return new ReadInventoryAction(pid, ownerId, inv);
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a read-inventory action.");
	}

	/** Attempts to read an inventory request from the stream. */
	private static GetInventoryAction readAsSendInventory(DataInputStream in, int pid) throws IncompatiblePacketException {
		try {
			GetInventoryAction a = new GetInventoryAction(pid,in.readInt());
			if (in.readInt() == Packet.TERMINAL) {
				return a;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a send-inventory action.");
	}

	/** Attempts to read a use-item action from the stream. */
	private static UseItemAction readAsItemUse(DataInputStream in, boolean isWorld, int pid) throws IncompatiblePacketException {
		try {
			int userId = in.readInt();
			int entityId = in.readInt();
			int itemId = in.readInt();
			int term = in.readInt();
			if (term == Packet.TERMINAL) {
				return new UseItemAction (isWorld, pid, userId, entityId, itemId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a use-item action.");
	}

	/** Attempts to read a lose-item action from the stream. */
	private static LoseItemAction readAsItemLose(DataInputStream in, boolean isWorld, int pid) throws IncompatiblePacketException {
		try {
			int loserId = in.readInt();
			int itemId = in.readInt();
			int terminal = in.readInt();
			if (terminal == Packet.TERMINAL) {
				return new LoseItemAction (isWorld, pid, loserId, itemId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a lose-item action.");
	}

	/** Attempts to read a gain-item action from the stream. */
	private static GetItemAction readAsItemGain(DataInputStream in, boolean isWorld, int pid) throws IncompatiblePacketException {
		try {
			int userId = in.readInt();
			int itemId = in.readInt();
			int terminal = in.readInt();
			if (terminal == Packet.TERMINAL) {
				return new GetItemAction (isWorld, pid, userId, itemId);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a gain-item action.");
	}

	/** Attempts to read a player move from the stream. */
	private static MovePlayerAction readAsMove(DataInputStream in, boolean isWorld, int pid) throws IncompatiblePacketException {
		try {
			int userId = in.readInt();
			int x = in.readInt();
			int y = in.readInt();
			byte dir = in.readByte();
				Direction d = MovePlayerPacket.byteToDirection(dir);
			long time = in.readLong();
			int terminal = in.readInt();
			if (terminal == Packet.TERMINAL) {
				return new MovePlayerAction (pid, userId, new Point(x,y), d, time);
			}
			System.out.println("failed with user "+userId+", x="+x+", y="+y+", d="+dir+", time="+time+"s");
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IncompatiblePacketException("Couldn't read terminal for a move action.");
	}
	
	/* =============================================================
	 * =============================================================
	 * 							WRITE METHODS
	 * =============================================================
	 * =============================================================
	 */
	
	/** Writes the packet type followed by the move action contents to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsMove(DataOutputStream o, MovePlayerAction m) {
		try {
			o.writeInt(Packet.MOVE);
			o.writeInt(m.getPlayerId());
			o.writeInt((int)m.getCurrent().getX());
			o.writeInt((int)m.getCurrent().getY());
			o.writeByte(MovePlayerPacket.directionToByte(m.getCurrentDirection()));
			o.writeLong(m.getTime());
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the get-item action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsGainItem(DataOutputStream o, GetItemAction m) {
		try {
			o.writeInt(Packet.ITEM_GAIN);
			o.writeInt(m.getItemDestination());
			o.writeInt(m.getItemId());
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the lose-item action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsLoseItem(DataOutputStream o, LoseItemAction m) {
		try {
			o.writeInt(Packet.ITEM_LOSE);
			o.writeInt(m.getItemSource());
			o.writeInt(m.getItemId());
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the use-item action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsUseItem(DataOutputStream o, UseItemAction m) {
		try {
			o.writeInt(Packet.ITEM_USE);
			o.writeInt(m.getEntityId());
			o.writeInt(m.getItemId());
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the inventory-request action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsGetInventory(DataOutputStream o, GetInventoryAction m) {
		try {
			o.writeInt(Packet.GET_INVENTORY);
			o.writeInt(m.getClient());
			o.writeInt(m.getInventoryOwner());
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the inventory to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsReadInventory(DataOutputStream o, ReadInventoryAction m) {
		try {
			o.writeInt(Packet.READ_INVENTORY);
			o.writeInt(m.getClient());
			for (int i = 0; i < m.getInventory().length; i++) {
				o.writeInt(m.getInventory()[i]);
			}
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the game start action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsGameStart(DataOutputStream o, GameStartAction m) {
		try {
			o.writeInt(Packet.GAME_START);
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the game end action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsGameEnd(DataOutputStream o, GameEndAction m) {
		try {
			o.writeInt(Packet.GAME_END);
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the captured action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsCaughtAction(DataOutputStream o, CapturedAction m) {
		try {
			o.writeInt(Packet.LOSE);
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Writes the packet type followed by the escape action to the output stream. 
	 * REQUIRES: writeHeader() has been called with appropriate parameters. */
	private static void writeAsEscapeAction(DataOutputStream o, EscapeAction m) {
		try {
			o.writeInt(Packet.ESCAPE);
			o.writeInt(Packet.TERMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** This method interprets a byte array packet as an Action. */
//	public static Action getActionFromPacket(byte[] packet, boolean fromClient) throws IncompletePacketException {
//		int action = (int)packet[Packet.IDX_TYPE];
//		try {
//			if (action == Packet.MOVE) {
//				System.out.println("Moving");
//				return MOVE_PKT.read(packet);
//			} 
//			// item packets!
//			else if (action == Packet.ITEM_GAIN) {
//				System.out.println("Giving item");
//				return ITEM_GAIN_PKT.read(packet);
//			} 
//			else if (action == Packet.ITEM_LOSE) {
//				System.out.println("Losing item");
//				return ITEM_LOSE_PKT.read(packet);
//			} 
//			else if (action == Packet.ITEM_USE) {
//				System.out.println("Using item");
//				return ITEM_USE_PKT.read(packet);
//			}
//			// Inventory packets!
//			else if (action == Packet.GET_INVENTORY) {
//				System.out.println("Server reads get-inventory request");
//				return GET_INV_PKT.read(packet);
//			} 
//			else if (action == Packet.READ_INVENTORY) {
//				System.out.println("Client reads inventory");
//				return READ_INV_PKT.read(packet);
//			}
//			// Losing-the-game packets!
//			else if (action == Packet.LOSE) {
//				System.out.println("Losing game");
//				return LOSE_PKT.read(packet);
//			}
//			// Escape packets!
//			else if (action == Packet.ESCAPE) {
//				System.out.println("Escaping game!");
//				return ESCAPE_PKT.read(packet);
//			}
//			// Start game
//			else if (action == Packet.GAME_START) {
//				System.out.println("Starting game! (should be received by client)");
//				return GAME_START_PKT.read(packet);
//			}
//			// end game
//			else if (action == Packet.GAME_END) {
//				System.out.println("Ending game! (should be received by client)");
//				return GAME_END_PKT.read(packet);
//			}
//			// interact
//			else if (action == Packet.INTERACT) {
//				System.out.println("Interaction!");
//				return INTERACT_PKT.read(packet);
//			}
//			// TODO object change
//		} catch (IncompatiblePacketException e) {
//			e.printStackTrace();
//		}
//		throw new IncompletePacketException();
//	}
	
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