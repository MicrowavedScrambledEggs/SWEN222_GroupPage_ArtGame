package artGame.control;

/* Thrown if a PacketParser object is made with a byte array which is incompatible with that type. */
@SuppressWarnings("serial")
public class IncompatiblePacketException extends Exception {

	public IncompatiblePacketException(String string) {
		super(string);
	}

	public IncompatiblePacketException() {
		super();
	}

}
