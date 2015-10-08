package artGame.control.cmds;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.Arrays;

import org.junit.Test;

import artGame.control.IncompatiblePacketException;

public class PacketTest {
	@Test
	public void movePlayerPacket_read() {
		try {
			int recipientId = 5;
			int movingPlayerId = recipientId;
			Point current = new Point(5,4);
			Point dest = new Point(3,2);
			byte[] pkt = { 
					0, (byte)recipientId, Packet.MOVE, 							// header
						(byte)movingPlayerId, 									// packet contents
						(byte)current.getX(), 	(byte)current.getY(),(byte)0,   // packet contents
						(byte)dest.getX(), 		(byte)dest.getY(),	 (byte)0,	// packet contents
					(byte)Packet.TERMINAL 										// terminal
			};
			MovePlayerAction mpa = new MovePlayerAction(false,recipientId,movingPlayerId,current,0,dest,0);
			MovePlayerAction readPacket = new MovePlayerPacket().read(pkt);
			System.out.println(mpa.toString());
			System.out.println(readPacket.toString());
			assertTrue("Error in either MovePlayerAction or MovePlayerPacket's read method!",
					mpa.equals(readPacket));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}
	
	@Test
	public void movePlayerPacket_read1() {
		try {
			int recipientId = 5;
			int movingPlayerId = 20;
			Point current = new Point(30,4);
			Point dest = new Point(31,2);
			byte[] pkt = { 
					0, (byte)recipientId, Packet.MOVE, 						// header
						(byte)movingPlayerId, 								// packet contents
						(byte)current.getX(), (byte)current.getY(), (byte)0,// packet contents
						(byte)dest.getX(), 		(byte)dest.getY(),	(byte)0,// packet contents
					(byte)Packet.TERMINAL 									// terminal
			};
			MovePlayerAction mpa = new MovePlayerAction(false,recipientId,movingPlayerId,current,0,dest,0);
			MovePlayerAction readPacket = new MovePlayerPacket().read(pkt);
			System.out.println("MPA: "+mpa.toString());
			System.out.println("RED: "+readPacket.toString());
			assertTrue("Error in either MovePlayerAction or MovePlayerPacket's read method!",
					mpa.equals(readPacket));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}
	
	@Test
	public void readInventoryPacket_read() {
		try {
			int recipientId = 5;
			int inventoryOwnerId = recipientId;	
			int[] inv =  {4, 3, 2, 10, 50, 100};
			byte[] pkt = new byte[Packet.HEAD_LENGTH + inv.length + 2];
			pkt[0] = 0;
			pkt[1] = (byte)recipientId;
			pkt[2] = (byte)Packet.READ_INVENTORY;
			pkt[3] = (byte)inventoryOwnerId;
			for (int i = 4; i < inv.length + 4; i++) {
				pkt[i] = (byte)inv[i-4];
			}
			ReadInventoryAction ria = new ReadInventoryAction(recipientId, inventoryOwnerId, inv);
			ReadInventoryAction readPacket = new ReadInventoryPacket().read(pkt);
			System.out.println(ria.toString());
			System.out.println(readPacket.toString());
		
			assertTrue(ria.getInventory().length == inv.length);
			assertTrue(Arrays.equals(ria.getInventory(),inv));
		} catch (IncompatiblePacketException e) {
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}

	@Test
	public void getInventoryPacket_read() {
		try {
			int recipientId = 5;
			int inventoryOwnerId = recipientId;	
			byte[] pkt = {
				0, (byte)recipientId, (byte)Packet.GET_INVENTORY,	// header
				(byte)inventoryOwnerId,						// contents 
				(byte)Packet.TERMINAL						// terminal
			};
			GetInventoryAction gia = new GetInventoryAction(recipientId, inventoryOwnerId);
			GetInventoryAction readPacket = new GetInventoryPacket().read(pkt);
			System.out.println(gia.toString());
			System.out.println(readPacket.toString());
			assertTrue("Error in either GetInventoryAction or GetInventoryPacket's read method!",
					gia.equals(new GetInventoryPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			e.printStackTrace();
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
		try {
			int recipientId = 5;
			int originId = 10;	
			int item = 15;
			byte[] pkt = {
				0, (byte)recipientId, Packet.ITEM_LOSE, // header
				(byte)originId, (byte)item,				// contents
				(byte)Packet.TERMINAL					// terminal
			};
			LoseItemAction lia = new LoseItemAction(false,recipientId,originId,item);
			LoseItemAction readPacket = new LoseItemPacket().read(pkt);
			System.out.println(lia.toString());
			System.out.println(readPacket.toString());
			assertTrue("Error in either LoseItemAction or LoseItemPacket's read method!",
					lia.equals(new LoseItemPacket().read(pkt)));
		} catch (IncompatiblePacketException e) {
			e.printStackTrace();
			fail("Cannot throw an exception for equivalent packet types!");
		}
	}	
}
