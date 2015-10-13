package artGame.control;

import static org.junit.Assert.*;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.junit.Test;

import artGame.control.cmds.MoveCommand;
import artGame.control.cmds.MoveCommand.Entity;
import artGame.control.cmds.TileStateCommand;
import artGame.control.cmds.TileStateCommand.Occupant;
import artGame.game.Art;
import artGame.game.EmptyTile;
import artGame.game.Player;
import artGame.game.Tile;
import artGame.game.Character.Direction;
import artGame.game.Wall;
import artGame.main.Game;
import artGame.xml.XMLHandler;

public class TestCommand {

	/** Tests the MoveCommand, given that we already know how long it is. */
	@Test
	public void testMoveCommand() {
		MoveCommand m = new MoveCommand(Entity.PLAYER, 2, 'w', 2, 2);
		try {
			ServerSocket openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			Socket client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			Socket server = openSocket.accept();
			byte[] mArr = m.bytes();
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.write(mArr);
			out.flush();
			DataInputStream in = new DataInputStream(client.getInputStream());
			byte[] cArr = new byte[m.byteSize()];
			in.readFully(cArr, 0, m.byteSize());
			if (mArr.length != cArr.length)
				fail("not equal");
			for (int i = 0; i < mArr.length; i++) {
				if (mArr[i] != cArr[i]) {
					fail(mArr[i]+" ("+i+") != "+cArr[i]);
				}
			}
			client.close();
			openSocket.close();
			server.close();
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		}
	}
	
	/** Tests reading a move command after we have learned how long it is. */
	@Test
	public void testMoveCommand1() {
		MoveCommand m = new MoveCommand(Entity.GUARD, 6, 's', 2, 2);
		try {
			ServerSocket openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			Socket client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			Socket server = openSocket.accept();
			byte[] mArr = m.bytes();
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeInt(m.byteSize());
			out.write(mArr);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] cArr = new byte[size];
			in.read(cArr, 0, size);
			if (mArr.length != mArr.length)
				fail("not equal");
			for (int i = 0; i < mArr.length; i++) {
				if (mArr[i] != cArr[i]) {
					fail(mArr[i]+" ("+i+") != "+cArr[i]);
				}
			}
			client.close();
			openSocket.close();
			server.close();
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		}
	}
	
	/** Tests reconstituting a move command from bytes */
	@Test
	public void testMoveCommand2() {
		MoveCommand m = new MoveCommand(Entity.GUARD, 6, 's', 2, 2);
		try {
			ServerSocket openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			Socket client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			Socket server = openSocket.accept();
			byte[] mArr = m.bytes();
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeInt(m.byteSize());
			out.write(mArr);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] cArr = new byte[size];
			in.read(cArr, 0, size);
			MoveCommand c = new MoveCommand(cArr);
			assertTrue(m.equals(c));
			client.close();
			openSocket.close();
			server.close();
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		}
	}
	
	/** Tests moving a player FIXME BROKEN
	 * @throws IOException */
//	@Test
//	public void testMoveCommand_movePlayer() throws IOException {
//		int id = 6;
//		ServerSocket openSocket = null;
//		Socket client = null;
//		Socket server = null;
//		int oldFloor = 1;
//		XMLHandler gameLoader = new XMLHandler();
//		// load a game to test on
//		Game g = gameLoader.loadGame(new File("Save Files/GameWorld.xml"));
//		// Initialize the player
//		Player p = g.addPlayer(id);
//		assertTrue("player cannot be null!",p != null);
//		p.setDir(Direction.SOUTH);
//		Point pt = getMoveableTile(g);
//		g.getFloor().setCharacter(p, (int)pt.getX(), (int)pt.getY());
//		System.out.println("Square "+(int)pt.getX()+", "+(int)pt.getY()+" becomes "+(int)pt.getX()+1+", "+(int)pt.getY());
//		assertNotNull(g.getFloor().getTile((int)pt.getX(), (int)pt.getY()).getOccupant());
//		// create cmd
//		MoveCommand m = new MoveCommand(Entity.PLAYER, id, 's', (int)pt.getX(), (int)pt.getY());
//		try {
//			openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
//			client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
//			server = openSocket.accept();
//			byte[] mArr = m.bytes();
//			DataOutputStream out = new DataOutputStream(server.getOutputStream());
//			out.writeInt(m.byteSize());
//			out.write(mArr);
//			DataInputStream in = new DataInputStream(client.getInputStream());
//			int size = in.readInt();
//			byte[] cArr = new byte[size];
//			in.read(cArr, 0, size);
//			MoveCommand c = new MoveCommand(cArr);
//			assertTrue(m.equals(c));
//			boolean success = c.execute(g);
//			Tile oldTile = g.getFloor().getTile((int)pt.getX(),(int)pt.getY(),oldFloor);
//			Tile newTile = g.getFloor().getTile((int)pt.getX()+1,(int)pt.getY(),oldFloor);
//			System.out.println((oldTile.getOccupant() == null)+" -> "+ (newTile.getOccupant() == null));
//			assertTrue(oldTile.getOccupant() == null);
//			assertTrue(g.getFloor().getTile((int)pt.getX()+1,(int)pt.getY(),oldFloor).getOccupant() != null);
//			assertTrue(newTile.getOccupant().equals(p));
//		} catch (IOException e) {
//			fail("IOException!");
//			e.printStackTrace();
//		} finally {
//			if (client != null) client.close();
//			if (openSocket != null) openSocket.close();
//			if (server != null) server.close();
//		}
//	}
	
	/** Tests moving a player 
	 * @throws IOException */
//	@Test
//	public void testMoveCommand_movePlayerUpStairs() throws IOException {
//		int id = 6;
//		int oldRow = 5;
//		int oldCol = 9;
//		int oldFloor = 1;
//		int newRow = 6;
//		int newCol = 9;
//		int newFloor = 1;
//		ServerSocket openSocket = null;
//		Socket client = null;
//		Socket server = null;
//		XMLHandler gameLoader = new XMLHandler();
//		// load a game to test on
//		Game g = gameLoader.loadGame(new File("Save Files/GameWorld.xml"));
//		Player p = g.addPlayer(6);
//		g.getFloor().getTile(oldRow, oldCol, oldFloor).setOccupant(p);
//		MoveCommand m = new MoveCommand(Entity.PLAYER, 6, 's', newRow, newCol);
//		try {
//			openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
//			client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
//			server = openSocket.accept();
//			byte[] mArr = m.bytes();
//			DataOutputStream out = new DataOutputStream(server.getOutputStream());
//			out.writeInt(m.byteSize());
//			out.write(mArr);
//			DataInputStream in = new DataInputStream(client.getInputStream());
//			int size = in.readInt();
//			byte[] cArr = new byte[size];
//			in.read(cArr, 0, size);
//			MoveCommand c = new MoveCommand(cArr);
//			assertTrue(m.equals(c));
//			
//			c.execute(g);
//			assertTrue(g.getFloor().getTile(oldRow,oldCol,oldFloor).getOccupant() == null);
//			assertTrue(g.getFloor().getTile(newRow,newCol,newFloor).getOccupant() != null);
//			assertTrue(g.getFloor().getTile(newRow,newCol,newFloor).getOccupant().equals(p));
//		} catch (IOException e) {
//			fail("IOException!");
//			e.printStackTrace();
//		} finally {
//			if (client != null) client.close();
//			if (openSocket != null) openSocket.close();
//			if (server != null) server.close();
//		}
//	}
	
	/** Tests reading a tile state command made using the explicit constructor. */
	@Test
	public void testTileCommand1() {
		short[] wallIds = { 0, 0, 3, 0 };
		boolean[] wallBools = { false, false, true, false };
		TileStateCommand cmd = new TileStateCommand(Occupant.GUARD, 3, wallIds, wallBools, 2, 2, 2);
		try {
			ServerSocket openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			Socket client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			Socket server = openSocket.accept();
			byte[] cmdArr = cmd.bytes();
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeInt(cmd.byteSize());
			out.write(cmdArr);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] readArr = new byte[size];
			in.read(readArr, 0, size);
			if (cmdArr.length != cmdArr.length)
				fail("not equal");
			for (int i = 0; i < cmdArr.length; i++) {
				if (cmdArr[i] != readArr[i]) {
					fail(cmdArr[i]+" ("+i+") != "+readArr[i]);
				}
			}
		client.close();
		openSocket.close();
		server.close();
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		}
	}
	
	/** Tests reading a tile state command created using a Tile. */
	@Test
	public void testTileCommand2() {
		Tile t = new EmptyTile(true,false,false,false);
		TileStateCommand cmd = new TileStateCommand(t, 3, 3, 1);
		try {
			ServerSocket openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			Socket client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			Socket server = openSocket.accept();
			byte[] cmdArr = cmd.bytes();
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeInt(cmd.byteSize());
			out.write(cmdArr);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] readArr = new byte[size];
			in.read(readArr, 0, size);
			if (cmdArr.length != cmdArr.length)
				fail("not equal");
			for (int i = 0; i < cmdArr.length; i++) {
				if (cmdArr[i] != readArr[i]) {
					fail(cmdArr[i]+" ("+i+") != "+readArr[i]);
				}
			}
		client.close();
		openSocket.close();
		server.close();
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		}
	}
	
	/** Tests reconstituting a tile state command. */
	@Test
	public void testTileCommand3() {
		Tile t = new EmptyTile(true,false,false,false);
		t.setOccupant(new artGame.game.Character(Direction.NORTH, 34));
		TileStateCommand cmd = new TileStateCommand(t, 2, 1, 1);
		try {
			ServerSocket openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			Socket client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			Socket server = openSocket.accept();
			byte[] cmdArr = cmd.bytes();
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeInt(cmd.byteSize());
			out.write(cmdArr);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] readArr = new byte[size];
			in.read(readArr, 0, size);
			TileStateCommand c = new TileStateCommand(readArr);
			assertTrue(cmd.equals(c));
		client.close();
		openSocket.close();
		server.close();
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		}
	}
	

	/** Tests updating the game floor from network. 
	 * @throws IOException */
	@Test
	public void testTileCommand_floorUpdate() throws IOException {
		ServerSocket openSocket = null;
		Socket client = null;
		Socket server = null;
		XMLHandler gameLoader = new XMLHandler();
		// load a game to test on
		Game g = gameLoader.loadGame(new File("Save Files/GameWorld.xml"));
		// get the game's tile we'll test
		int maxRow = g.getFloor().getHeight();
		int maxCol = g.getFloor().getWidth();
		int row = 1; // (potentially gets incremented in the loop)
		int col = 7;
		int floor = 1;
		EmptyTile oldTile = null;
		Direction testWall = Direction.NORTH;
		for (int r = 0; r < maxRow && oldTile == null; r++) {
			for (int c = 0; c < maxCol; c++) {
				Tile x = g.getFloor().getTile(r,c,floor);
				if ( x!= null && x.getWall(testWall) != null && x instanceof EmptyTile) {
					row = r;
					col = c;
					oldTile = (EmptyTile)x;
					System.out.println(r +", "+c+" @ "+floor);
					break;
				}
			}
		}
		if (oldTile == null || oldTile.getWall(testWall) == null) {
			fail("Couldn't find a tile");
		}
		// old tile contains an artwork
			Art a = new Art("Da Vinci's Anaconda", 40000, 40);
			oldTile.getWall(testWall).setArt(a);
		// create a tile to send
		Tile newTile = new EmptyTile(oldTile.getWall(Direction.NORTH) != null, oldTile.getWall(Direction.WEST) != null,
				oldTile.getWall(Direction.SOUTH) != null, oldTile.getWall(Direction.EAST) != null);
		// just in case we've picked a wall with other art, make sure the other art == old
		if (newTile.getWall(Direction.NORTH) != null) {
			newTile.getWall(Direction.NORTH).setArt(oldTile.getWall(Direction.NORTH).getArt());
		}
		if (newTile.getWall(Direction.SOUTH) != null) {
			newTile.getWall(Direction.SOUTH).setArt(oldTile.getWall(Direction.SOUTH).getArt());
		}
		if (newTile.getWall(Direction.EAST) != null) {
			newTile.getWall(Direction.EAST).setArt(oldTile.getWall(Direction.EAST).getArt());
		}
		if (newTile.getWall(Direction.WEST) != null) {
			newTile.getWall(Direction.WEST).setArt(oldTile.getWall(Direction.WEST).getArt());
		}
		// new tile needs the same testing art as old, though not for long!
		newTile.getWall(testWall).setArt(a);
		// compare them
		assertTrue(oldTile.getWall(testWall).equals(newTile.getWall(testWall)));
		assertTrue(oldTile.getWall(testWall) != (newTile.getWall(testWall)));
		// and since they have walls/art in the same place, the tiles should be equal!
		assertTrue(oldTile.equals(newTile));
		assertTrue(oldTile != newTile);
		
		// now let's change the new tile and send our message
		newTile.getWall(testWall).setArt(null);
		System.out.println("new tile: "+newTile.toPrintString());
		TileStateCommand cmd = new TileStateCommand(newTile, row, col, floor);
		try {
			openSocket = new ServerSocket(TestControl.D_PORT, 1, InetAddress.getLocalHost());
			client = new Socket(InetAddress.getLocalHost(), TestControl.D_PORT);
			server = openSocket.accept();
			byte[] cmdArr = cmd.bytes();
			System.out.println("old before write "+oldTile.toPrintString());
			DataOutputStream out = new DataOutputStream(server.getOutputStream());
			out.writeInt(cmd.byteSize());
			out.write(cmdArr);
			DataInputStream in = new DataInputStream(client.getInputStream());
			int size = in.readInt();
			byte[] readArr = new byte[size];
			in.read(readArr, 0, size);
			TileStateCommand c = new TileStateCommand(readArr);
			assertTrue(cmd.equals(c));
			// great, we read our command! now let's change the game
			Tile afterTile = g.getFloor().getTile(row, col, floor);
			assertTrue(afterTile.equals(oldTile));
			assertTrue(afterTile == oldTile);
			c.execute(g);
			assertTrue(oldTile.equals(newTile));
		} catch (IOException e) {
			fail("IOException!");
			e.printStackTrace();
		} finally {
			if (client != null) client.close();
			if (openSocket != null) openSocket.close();
			if (server != null) server.close();
		}
	}
	
	/** Only checks 1st floor */
	private Point getMoveableTile(Game g) {
		int width = g.getFloor().getWidth();
		int height = g.getFloor().getHeight();
		Tile oldTile = null;
		for (int col = 0; col < height && oldTile == null; col++) {
			for (int row = 0; row < width; row++) {
				Tile x = g.getFloor().getTile(row,col,1);
				if (x != null && x instanceof EmptyTile) {
					System.out.println(row +", "+col+" @ "+1);
					return new Point(row,col);
				}
			}
		}
		throw new IllegalArgumentException("Empty tile not found");
	}
}
