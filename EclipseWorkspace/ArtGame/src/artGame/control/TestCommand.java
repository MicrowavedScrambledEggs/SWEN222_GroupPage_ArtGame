package artGame.control;

import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
import artGame.game.EmptyTile;
import artGame.game.Tile;

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
	
	/** Tests reading a tile state command made using the explicit constructor. */
	@Test
	public void testTileCommand1() {
		short[] wallIds = { 0, 0, 3, 0 };
		boolean[] wallBools = { false, false, true, false };
		TileStateCommand cmd = new TileStateCommand(Occupant.GUARD, 3, wallIds, wallBools);
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
		TileStateCommand cmd = new TileStateCommand(t);
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
}
