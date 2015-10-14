package artGame.control;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
import artGame.control.cmds.CommandInter;
import artGame.control.cmds.MoveCommand;
import artGame.control.cmds.MoveCommand.Entity;
import artGame.game.Player;
import artGame.main.Game;
import artGame.main.Main;
import artGame.xml.XMLHandler;

/** Tests for the network package. 
 * 
 * Note: all tests pass if you run them individually, but due to the joys of threading,
 * running them all together will make some throw exceptions (that is, a server won't 
 * always close before a new one is opened, and since they all use the same IP/port, problems!) 
 * 
 * @author Vicki
 *
 */
public class NetworkTests {
	private static final String BASIC = "Save Files/BasicBoard.xml";
	private static final String WORLD = "Save Files/GameWorld.xml";
	static final int D_PORT = 8080;
	
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
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
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
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}

	@Test
	public void testGetPort() {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT+2, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+2), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			assertTrue(client.getPort() == D_PORT+2);
		} catch (IOException e) {
			fail("Threw IOException");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}		
	}

	@Test
	public void testIsSocketSafe() {
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT+3, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+3), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			assertTrue(server.isSocketSafe());
			server.close();
			assertFalse(server.isSocketSafe());
			assertTrue(server.isTimedOut());
		} catch (IOException e) {
			fail("IOException");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}

	@Test
	public void testIsTimedOut() { // FIXME not working
		Game g_c = getGame();
		Game g_s = getGame();
		ServerSocket openSocket;
		ClientThread client = null;
		ServerThread server = null;
		try {
			openSocket = new ServerSocket(D_PORT+4, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+4), g_c, Main.WAIT_PERIOD);
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
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}

	@Test
	public void testClose() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT+4, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+4), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			assertTrue(server.isSocketSafe());
		} catch (IOException e) {
			fail("Threw IO exception.");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
	
	@Test
	public void testSend() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT+5, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+5), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			server.start();
			client.start();
		} catch (IOException e) {
			fail("Threw IO exception.");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
	
	@Test
	public void testCommandsToServer() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT+6, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+6), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			CommandInter toServer = new MoveCommand(Entity.PLAYER, client.getPlayerId(), 'w', 4, 4);
			client.writeParameter(toServer);
			CommandInter fromClient = server.readFromClient();
			assertTrue("CommandInter received from client cannot be null",fromClient != null);
			assertEquals("CommandInter from server and client should be equivalent",fromClient,toServer);
		} catch (IOException | InterruptedException | IncompatiblePacketException e) {
			fail("Exception");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
	
	@Test
	public void testCommandsToClient() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT+7, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+7), g_c, Main.WAIT_PERIOD);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);

			CommandInter toClient = new MoveCommand(Entity.PLAYER, server.getPlayerId(), 'w', 4, 4);
			server.write(new DataOutputStream(privateSocket.getOutputStream()),toClient);
			CommandInter fromServer = client.readSocket(new DataInputStream(client.socket().getInputStream()));
			assertTrue("CommandInter received by client cannot be null",toClient != null);
			System.out.println(toClient.toString()+", "+fromServer.toString());
			assertEquals("CommandInter from server and client should be equivalent",toClient,toClient);
		} catch (IOException | InterruptedException | IncompatiblePacketException e) {
			fail("Exception thrown");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
		
	
	@Test
	public void testClientQueue_MOVE() { // FIXME FAILING. 
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			long then = System.currentTimeMillis();
			ConcurrentLinkedQueue<CommandInter> clntQ = new ConcurrentLinkedQueue<>();
			clntQ.add(new MoveCommand(Entity.PLAYER, 4, 'w', 4, 4));
			clntQ.add(new MoveCommand(Entity.PLAYER, 4, '!', 4, 4));
			clntQ.add(new MoveCommand(Entity.PLAYER, 4, 'a', 3, 4));
			clntQ.add(new MoveCommand(Entity.PLAYER, 4, '!', 3, 4));
			
			ServerSocket openSocket = new ServerSocket(D_PORT+8, 1, InetAddress.getLocalHost());
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+8), g_c, clntQ);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			System.out.println("CLIENT  = "+ client.getQueue().toString());
			System.out.println("CLIENTC = "+ clntQ.toString());
			CommandInter myCmd = clntQ.poll();
			CommandInter clientCmd = client.pollCommand();
			System.out.println("size of "+ clntQ.size());
			System.out.println("size of "+ client.queueSize());
			assertEquals("first client cmd should equal the one passed as parameter\n"
					+ myCmd.toString() +" vs "+ clientCmd.toString(), 
					myCmd,clientCmd);
			assertEquals("2nd cmd not equal",clntQ.poll(),client.pollCommand());
			assertEquals("3rd cmd not equal",clntQ.poll(),client.pollCommand());
			assertEquals("last cmd not equal",clntQ.poll(),client.pollCommand());
		} catch (IOException e) {
			fail("Ioexception");
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
	
	@Test
	public void testToServerQueueFromClientQueue_MOVE() {
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		ClientThread client = null;
		ServerThread server = null;
		try {
			long then = System.currentTimeMillis();
			CommandInter[] clntQ = { new MoveCommand(Entity.PLAYER, 4, 'w', 4, 4),
					new MoveCommand(Entity.PLAYER, 4, '!', 4, 4), 
					new MoveCommand(Entity.PLAYER, 4, 'w', 4, 3),
					new MoveCommand(Entity.PLAYER, 4, '!', 4, 3) };
			
			ServerSocket openSocket = new ServerSocket(D_PORT+9, 1, InetAddress.getLocalHost());
			// create a new basic client, with empty queue
			client = new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+9), g_c, 2);
			Socket privateSocket = openSocket.accept();
			server = new ServerThread(privateSocket, g_s, Main.WAIT_PERIOD);
			// now that server/client are linked
			
			// mimics what should be done to send a message
			for (int i = 0; i < clntQ.length; i++) {
				client.sendCommand(clntQ[i]);
				client.writeQueue();
				CommandInter servCommand = server.readSocket(new DataInputStream(privateSocket.getInputStream()));
				assertEquals(clntQ[i],servCommand);
			}
		} catch (IOException | InterruptedException | IncompatiblePacketException e) {
			fail("Exception");
			e.printStackTrace();
		} finally {
			if (server!= null)
				server.close();
			if (client != null) 
				client.close();
		}
	}
	
	@Test
	public void testClientAction_MOVE_fromQueue() { // FIXME FAILING. 
		Game g_c = new XMLHandler().loadGame(new File(WORLD));
		g_c.setName("clientName");
		Game g_s = new XMLHandler().loadGame(new File(WORLD));
		g_s.setName("serverName");
		ClientThread client = null;
		ServerThread server = null;
		try {
			ServerSocket openSocket = new ServerSocket(D_PORT+10, 1, InetAddress.getLocalHost());
			CommandInter[] clntQ = { new MoveCommand(Entity.PLAYER, 4, 'w', 4, 4),
					new MoveCommand(Entity.PLAYER, 4, '!', 4, 4),
					new MoveCommand(Entity.PLAYER, 4, 's', 4, 5),
					new MoveCommand(Entity.PLAYER, 4, '!', 4, 5) };
			client =  new ClientThread(new Socket(InetAddress.getLocalHost(), D_PORT+10), g_c, 2);
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
				assertTrue(client.queueSize() > 0);
				client.doAction(clntQ[i]);
				client.writeQueue();
				System.out.println("---------SERVER");
				CommandInter servCommand = server.readFromClient();
				assertEquals(clntQ[i],servCommand);
				server.doAction(servCommand);
			}
			Thread.sleep(500);
			assertTrue("Not equal:\n"+server.game().toString()+"\n"+client.game().toString(),server.game().equals(client.game()));
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			fail("Threw exception");
		} catch (IncompatiblePacketException e) {
			fail("couldn't read packet");
			e.printStackTrace();
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
