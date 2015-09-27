package artGame.control;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Arrays;

import org.junit.Test;

public class PacketTest {
	@Test
	public void movePlayerPacket_read() {
		byte[] pkt = { 0, 5, Packet.MOVE, 5, 4, 3, 2, (byte)Integer.MAX_VALUE };		
		MovePlayerAction mpa = new MovePlayerAction(false,5,Packet.MOVE,new Point(5,4),new Point(3,2));
		
		try {
			assertTrue("Error in either MovePlayerAction or MovePlayerPacket's read method!",
					mpa.equals(new MovePlayerPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}
	
	@Test
	public void readInventoryPacket_read() {
		byte[] pkt = { 0, 5, Packet.INVENTORY, 5, 4, 3, 2, 10, 50, 100, (byte)Integer.MAX_VALUE };	
		int[] inv = {4, 3, 2, 10, 50, 100};
		ReadInventoryAction ria = new ReadInventoryAction(5, 5, inv);
		
		assertTrue(ria.getItems().length == inv.length);
		assertTrue(Arrays.equals(ria.getItems(),inv));
		
		try {
			assertTrue("Error in either ReadInventoryAction or ReadInventoryPacket's read method!",
					ria.equals(new ReadInventoryPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}

	@Test
	public void sendInventoryPacket_read() {
		byte[] pkt = { 0, 5, Packet.INVENTORY, 5, (byte)Integer.MAX_VALUE };
		SendInventoryAction sia = new SendInventoryAction(5, 5);
		
		try {
			assertTrue("Error in either SendInventoryAction or SendInventoryPacket's read method!",
					sia.equals(new SendInventoryPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}

	@Test
	public void getItemPacket_read() {
		byte[] pkt = { 0, 5, Packet.ITEM_GAIN, 10, 10, (byte)Integer.MAX_VALUE };
		GetItemAction gia = new GetItemAction(false,5,10,10);
		
		try {
			assertTrue("Error in either GetItemAction or GetItemPacket's read method!",
					gia.equals(new GetItemPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}

	@Test
	public void loseItemPacket_read() {
		byte[] pkt = { 0, 5, Packet.ITEM_LOSE, 5, (byte)Integer.MAX_VALUE };
		LoseItemAction lia = new LoseItemAction(false,5,10,10);
		
		try {
			assertTrue("Error in either LoseItemAction or LoseItemPacket's read method!",
					lia.equals(new LoseItemPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}	
}
