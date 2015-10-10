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

import artGame.main.Game;
import artGame.main.Main;
import artGame.xml.XMLHandler;

/** Note: all tests pass if you run them individually, but due to the joys
 * of threading sometimes some will start before the last one has been closed
 * and will throw exceptions. 
 * @author Vicki
 *
 */
public class ServerThreadTest {
	private static final String BASIC = "Save Files/BasicBoard.xml";
	private static final String WORLD = "Save Files/GameWorld.xml";
	private static final int D_PORT = 8080;
//	Game CLIENT_GAME;
//	Game SERVER_GAME;
//	ServerThread SERVER;
//	ClientThread CLIENT;
	
//	@After
//	public void close() {
//		if (CLIENT != null) {
//			CLIENT.close();
//		}
//		if (SERVER != null) {
//			SERVER.close();
//		}
//		resetFieldsDefault();
//	}
	
//	private void resetGamesDefault() {
//		CLIENT_GAME = new XMLHandler().loadGame(new File(WORLD));
//		SERVER_GAME = new XMLHandler().loadGame(new File(WORLD));
//	}
	
	private Game getGame() {
//		CLIENT_GAME = new XMLHandler().loadGame(new File(game));
		return new XMLHandler().loadGame(new File(WORLD));
	}
	
//	private void resetFieldsDefault() {
//		resetGamesDefault();
//		ServerSocket openSocket;
//		ServerThread SERVER = null;
//		ClientThread CLIENT = null;
//		try {
//			openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
//			CLIENT = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), CLIENT_GAME, Main.WAIT_PERIOD);
//			Socket privateSocket = openSocket.accept();
//			SERVER = new ServerThread(SERVER_GAME, privateSocket, Main.WAIT_PERIOD);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if (SERVER != null)
//			SERVER.close();
//		if (CLIENT != null)
//			CLIENT.close();
//	}	
	
	private void resetFields(String gamePath, int port, int wait, int clients) {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(port, clients, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), port), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(g_s, privateSocket, wait);
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
