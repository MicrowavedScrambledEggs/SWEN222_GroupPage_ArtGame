package artGame.tests.game;

import static org.junit.Assert.*;

import org.junit.Test;

import artGame.game.Art;
import artGame.game.Character.Direction;
import artGame.game.Door;
import artGame.game.Key;
import artGame.game.Player;
import artGame.game.Wall;
/**
 * Represents a key used to open locked doors
 * @author Kaishuo Yang 300335418
 *
 */
public class WallTests {
	/**
	 * Wall Tests
	 */
	//BASE CLASS 
	@Test
	public void testSetArt(){
		Wall w = new Wall();
		Art art = new Art("Test", 0, 0);
		w.setArt(art);
		assertTrue(w.getArt() == art && art.getWalls().contains(w));
	}
	
	// DOOR CLASS
	@Test
	public void testDoorUnlockByKey(){
		Door door = new Door(true,0);
		Key k = new Key(0,0);
		assertTrue(door.unlock(k.ID));
		assertTrue(door.passable());
	}
	
	@Test
	public void testDoorUnlockByPlayer(){
		Door door = new Door(true,0);
		Key k = new Key(0,0);
		Player p = new Player(Direction.SOUTH,1);
		p.addItem(k);
		door.unlock(p);
		assertTrue(door.passable());
	}
}