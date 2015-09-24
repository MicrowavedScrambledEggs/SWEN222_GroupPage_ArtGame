package artGame.control;

/* Reminder to Vicki that Serializable is nice, but also probably more than 
 * we need. Better to stick with sending everything as bytes. 
 * 
 * The PacketParser determines the type of packet received and manages
 * its execution. 
 */
public interface PacketParser {
	
	public Action executePacket() throws IncompletePacketException;
}
