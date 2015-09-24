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
	public static final int ITEM_TAKE = 2;
	public static final int ITEM_GIVE = 3;
	public static final int ESCAPE = 4;
	public static final int LOSE = 5;
	public static final int GAME_START = 10;
	public static final int GAME_END = 11;
	public static final int DISCONNECT = 12;
	public static final int OBJECT_CHANGED = 20;
	
	Action read(byte[] packet) throws IncompatiblePacketException;
	
	int packetLength();
}