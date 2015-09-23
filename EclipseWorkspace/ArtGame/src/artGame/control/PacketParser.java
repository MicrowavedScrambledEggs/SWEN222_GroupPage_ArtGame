package artGame.control;

/* Reminder to Vicki that Serializable is nice, but also probably more than 
 * we need. Better to stick with sending everything as bytes. 
 * 
 * The PacketParser determines the type of packet received and manages
 * its execution. 
 */
public interface PacketParser {
	
	/** Returns the byte array corresponding to this packet.
	 * If the PacketParser has not received enough data, will throw
	 * an
	 * @return
	 * @throws IncompletePacketException if the packet is incomplete. 
	 */
	
	public byte[] getBytes() throws IncompletePacketException;
	
	public abstract Action executePacket();
}
