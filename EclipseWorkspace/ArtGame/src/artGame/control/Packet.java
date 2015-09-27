package artGame.control;

interface Packet {
	public static enum WORLD { OBJECT_CHANGED }

	public static enum CLIENT {PKT_MOVE, PKT_INVENTORY, PKT_ITEM_TAKE, 
	PKT_ITEM_GIVE, PKT_ESCAPED, PKT_CAUGHT }

	public static enum NETWORK { GAME_START, GAME_END, DISCONNECT }

	public static enum EVENT_TYPE { CLIENT, NETWORK, WORLD }

	public static final int HEAD_LENGTH = 3;
	public static final int TERMINAL = Integer.MAX_VALUE;
	static final int MOVE = 0;
	static final int INVENTORY = 1;
	public static final int ITEM_LOSE = 2;
	public static final int ITEM_GAIN = 3;
	public static final int ESCAPE = 4;
	public static final int LOSE = 5;
	public static final int GAME_START = 10;
	public static final int GAME_END = 11;
	public static final int DISCONNECT = 12;
	public static final int OBJECT_CHANGED = 20;
	
	/** Attempts to read a packet of data as if it were produced by the
	 * 'write' method of the implementing class. 
	 * 
	 * @param Packet to read
	 * @return Corresponding action to take
	 * @throws IncompatiblePacketException If the implementing class cannot process this packet of data. 
	 */
	Action read(byte[] packet) throws IncompatiblePacketException;
	
	/** Attempts to read an action into an array of bytes, as if it were
	 * produced by the 'read' method of the implementing class. 
	 * @param 
	 * @return
	 * @throws IncompatiblePacketException If this is not possible. 
	 */
	byte[] write(Action a) throws IncompatiblePacketException;
	
	/** Following the same contract as write(Action), attempts to write
	 * the parameters into a byte packet. 
	 * @param values
	 * @return
	 * @throws IncompatiblePacketException
	 */
	byte[] write(int... values) throws IncompatiblePacketException;
	
	/** The length of a packet that can be accepted by the implementing class.
	 * Not a guarantee that read or write will be able to parse any packet of that length. 
	 * @return
	 */
	int packetLength();
}