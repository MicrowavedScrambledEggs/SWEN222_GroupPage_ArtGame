package artGame.control;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import artGame.control.cmds.Command;
import artGame.game.Player;
import artGame.main.Game;
import artGame.main.Main;
import artGame.xml.XMLHandler;

/** Note: all tests pass if you run them individually, but due to the joys of threading,
 * running them all together will make some throw exceptions (that is, a server won't 
 * always close before a new one is opened, and since they all use the same IP, problems!) 
 * @author Vicki
 *
 */
public class TestControl {
	private static final String BASIC = "Save Files/BasicBoard.xml";
	private static final String WORLD = "Save Files/GameWorld.xml";
	private static final int D_PORT = 8080;

	@Test
	public void testIds() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		final Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			final ClientThread clientThread = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			final Socket privateSocket = openSocket.accept();
			
			Thread ts = new Thread() {
				boolean r = true;
				public void run() {
					if (r) {
						System.out.println("server say hi");
						try {
							ServerThread server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
							server.sendGameInfo();
						} catch (IOException e) {
							fail("server failed");
						}
						r = false;
					}
				}
			};
			Thread tc = new Thread() {
				boolean r = true;
				public void run() {
					if (r) {
						System.out.println("client say hi");
						clientThread.receiveGameInfo();
						r = false;
					}
				}
			};
			tc.start();
			ts.start();
			assertTrue(clientThread.getPlayerId()+" and "+ clientThread.getPlayerId(),clientThread.getPlayerId() == clientThread.getPlayerId());
			// Note: equals() comparing games will on always be true, because of
			// players getting added in different orders.
		} catch (IOException e) {
			fail("Exception!");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}
	
	private Game getGame() {
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
			openSocket = new ServerSocket(D_PORT+1, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+1), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
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
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			assertTrue(client.getPort() == D_PORT);
		} catch (IOException e) {
			fail("Threw IOException");
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
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			assertTrue(server.isSocketSafe());
			server.close();
			assertFalse(server.isSocketSafe());
			assertTrue(server.isTimedOut());
		} catch (IOException e) {
			fail("IOException");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}

	@Test
	public void testIsTimedOut() { // FIXME not working
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			long then = System.currentTimeMillis();
			server.run();
			while (then + Main.BROADCAST_PERIOD + 10 > System.currentTimeMillis()) {
				Thread.sleep(10);
			}
			assertTrue(server.isTimedOut());
		} catch (IOException | InterruptedException e) {
			fail("threw exception");
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
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			assertTrue(server.isSocketSafe());
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
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			server.start();
			client.start();
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
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			Command toServer = new Command('w',server.getPlayerId());
			client.writeParameter(toServer);
			Command fromClient = server.readFromClient();
			assertTrue("Command received from client cannot be null",fromClient != null);
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
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			Command toClient = new Command('s',server.getPlayerId());
			server.writeParameter(toClient);
			Command fromServer = client.readSocket(new DataInputStream(privateSocket.getInputStream()));
			assertTrue("Command received by client cannot be null",toClient != null);
			System.out.println(toClient.toString()+", "+fromServer.toString());
			assertEquals("Command from server and client should be equivalent",toClient,toClient);
		} catch (IOException | InterruptedException e) {
			fail("Exception thrown");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
		
	
	@Test
	public void testClientQueue() { // FIXME FAILING. 
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			long then = System.currentTimeMillis();
			ConcurrentLinkedQueue<Command> clntQ = new ConcurrentLinkedQueue<>();
			clntQ.add(new Command('w',2));
			clntQ.add(new Command('!',2));
			clntQ.add(new Command('a',2));
			clntQ.add(new Command('!',2));
			
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, clntQ);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			System.out.println("CLIENT  = "+ client.getQueue().toString());
			System.out.println("CLIENTC = "+ clntQ.toString());
			assertEquals("first client cmd should equal the one passed as parameter", clntQ.poll(),client.pollCommand());
			assertEquals("2nd cmd not equal",clntQ.poll(),client.pollCommand());
			assertEquals("3rd cmd not equal",clntQ.poll(),client.pollCommand());
			assertEquals("last cmd not equal",clntQ.poll(),client.pollCommand());
		} catch (IOException e) {
			fail("Ioexception");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}
	
	@Test
	public void testToServerQueueFromClientQueue() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			long then = System.currentTimeMillis();
			Command[] clntQ = { new Command('w',2), new Command('!',2), new Command('a',2), new Command('!',2) };
			
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			// create a new basic client, with empty queue
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, 2);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			// now that server/client are linked
			
			// mimics what should be done to send a message
			for (int i = 0; i < clntQ.length; i++) {
				client.sendCommand(clntQ[i]);
				client.writeQueue();
				Command servCommand = server.readFromClient();
				assertEquals(clntQ[i],servCommand);
			}
		} catch (IOException e) {
			fail("Ioexception");
		}
		if (server!= null)
			server.close();
		if (client != null) 
			client.close();
	}
	
	@Test
	public void testClientAction() { // FIXME FAILING. 
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		g_c.setName("clientName");
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		g_s.setName("serverName");
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT, 1, InetAddress.getLocalHost());
			Command[] clntQ = { new Command('w',2), new Command('!',2), new Command('a',2), new Command('!',2) };
			client =  new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT), g_c, 2);
			// create a new basic client, with empty queue
			Socket privateSocket = openSocket.accept();
			server =  new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			// now that server/client are linked
			System.out.println("b4 threads");

			client.start();
			server.start();
			Thread.sleep(500);
			server.end();
			client.end();
			
			assertEquals("client and server should have the same ID",server.getPlayerId(),client.getPlayerId());

			// mimics what should be done to send a message
			for (int i = 0; i < clntQ.length; i++) {
				System.out.println("---------CLIENT");
				client.sendCommand(clntQ[i]);
				client.doAction(clntQ[i]);
				client.writeQueue();
				System.out.println("---------SERVER");
				Command servCommand = server.readFromClient();
				assertEquals(clntQ[i],servCommand);
				server.doAction(servCommand);
			}
			Thread.sleep(500);
			assertTrue("Not equal:\n"+server.getGame().toString()+"\n"+client.getGame().toString(),server.getGame().equals(client.getGame()));
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			fail("Threw exception");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
	
/**	
	@Test
	public void testSuspend() {
		fail("Not yet implemented");
	}

	@Test
	public void testResume() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPriority() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoinLongInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoin() {
		fail("Not yet implemented");
	}

	@Test
	public void testDumpStack() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDaemon() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsDaemon() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckAccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessQueue() {
		fail("Not yet implemented");
	}

	@Test
	public void testNotifyAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLong() {
		fail("Not yet implemented");
	}

	@Test
	public void testWaitLongInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testWait() {
		fail("Not yet implemented");
	} 
*/

	
	@After 
	public void pause() {
		Thread.yield();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) { }
	}
}
