package artGame.control;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import artGame.control.cmds.Command;
import artGame.main.Game;
import artGame.main.Main;
import artGame.xml.XMLHandler;

/** Note: all tests pass if you run them individually, but due to the joys
 * of threading, sometimes some will start before the last one has been closed
 * and will throw exceptions. 
 * @author Vicki
 *
 */
public class ServerThreadTest {
	private static final String BASIC = "Save Files/BasicBoard.xml";
	private static final String WORLD = "Save Files/GameWorld.xml";
	private static final int D_PORT = 8080;
	
	private Game getGame() {
//		CLIENT_GAME = new XMLHandler().loadGame(new File(game));
		return new XMLHandler().loadGame(new File(WORLD));
	}
	
	@Test
	public void testGetInetAddress() {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			System.out.println("hi");
			openSocket = new ServerSocket(D_PORT+1, 1, InetAddress.getLocalHost());
			System.out.println("hi");
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+1), g_c, Main.WAIT_PERIOD);
			System.out.println("hi");
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);
			System.out.println("hi");
			assertEquals("The ServerThread must return the address its socket is connected to.",privateSocket.getInetAddress(), server.getInetAddress());
		} catch (IOException e) {
			fail("Threw IO exception.");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}

	@Test
	public void testGetPort() {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);
			assertTrue(client.getPort() == D_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
		
	}

	@Test
	public void testIsSocketSafe() {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			assertTrue(server.isSocketSafe());
			server.close();
			assertFalse(server.isSocketSafe());
			assertTrue(server.isTimedOut());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}

	@Test
	public void testIsTimedOut() {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			long then = System.currentTimeMillis();
			assertFalse(server.isTimedOut());
			server.start();
			server.start();
			while (then + Main.BROADCAST_PERIOD > System.currentTimeMillis()) {}
			assertTrue(server.isTimedOut());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}

	@Test
	public void testClose() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);
			assertTrue(server.isSocketSafe());
			openSocket.close();
		} catch (IOException e) {
			fail("Threw IO exception.");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}

	@Test
	public void testIsAlive() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			assertFalse(server.isAlive());
			server.start();
			assertTrue(server.isAlive());
		} catch (IOException e) {
			fail("Threw IO exception.");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}
	
	@Test
	public void testSend() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			assertFalse(server.isAlive());
			server.start();
			assertTrue(server.isAlive());
		} catch (IOException e) {
			fail("Threw IO exception.");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}
	
	@Test
	public void testCommandsToServer() {
		boolean success = true;
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			Command toServer = new Command('w',server.getPlayerId());
			client.writeParameterReadQueue(toServer);
			Command fromClient = server.readQueueWriteParameter(null);
			assertTrue("Command to server cannot be null",toServer!= null);
			assertTrue("Command received from client cannot be null",fromClient != null);
			System.out.println(toServer.toString()+", "+fromClient.toString());
			assertEquals("Command from server and client should be equivalent",fromClient,toServer);
		} catch (IOException e) {
			success = false;
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
		assert(success);
	}
	
	@Test
	public void testCommandsToClient() {
		boolean success = true;
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			Command toServer = new Command('w',server.getPlayerId());
			client.writeAndReadParameters(toServer, null);
			Command fromClient = server.readQueueWriteParameter(null);
			server.readAndWriteParameters(toServer, null);
			assertTrue("Command to server cannot be null",toServer!= null);
			assertTrue("Command received from client cannot be null",fromClient != null);
			System.out.println(toServer.toString()+", "+fromClient.toString());
			assertEquals("Command from server and client should be equivalent",fromClient,toServer);
		} catch (IOException e) {
			success = false;
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
		assert(success);
	}
	
	@Test
	public void testLoop() {
		boolean success = true;
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);

			Command toServer = new Command('w',server.getPlayerId());
			client.writeAndReadParameters(toServer, null);
			Command fromClient = server.readQueueWriteParameter(null);
			server.readAndWriteParameters(toServer, null);
			assertTrue("Command to server cannot be null",toServer!= null);
			assertTrue("Command received from client cannot be null",fromClient != null);
			System.out.println(toServer.toString()+", "+fromClient.toString());
			assertEquals("Command from server and client should be equivalent",fromClient,toServer);
		} catch (IOException e) {
			success = false;
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
		assert(success);		
	}
	
	@Test
	public void testIds() {
		boolean success = true;
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, Main.WAIT_PERIOD);
			
			assertTrue(g_c.getPlayer().getId() == g_s.getPlayer().getId());
			assertTrue(g_c.getPlayers().size() == g_s.getPlayers().size());
		} catch (IOException e) {
			success = false;
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
		assert(success);
	}
	
//	@Test
//	public void testSuspend() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testResume() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetPriority() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testJoinLongInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testJoin() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testDumpStack() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSetDaemon() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsDaemon() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCheckAccess() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testProcessQueue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNotifyAll() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testWaitLong() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testWaitLongInt() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testWait() {
//		fail("Not yet implemented");
//	}

}
