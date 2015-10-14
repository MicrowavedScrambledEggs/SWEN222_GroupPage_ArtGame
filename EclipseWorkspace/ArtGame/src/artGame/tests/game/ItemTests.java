package artGame.tests.game;

import static org.junit.Assert.*;

import org.junit.Test;

import artGame.game.Art;
import artGame.game.Door;
import artGame.game.Wall;
/**
 * Represents a key used to open locked doors
 * @author Kaishuo Yang 300335418
 *
 */
public class ItemTests {
	/*
	 * ITEM TESTS
	 */
	//BASE CLASS - tests not needed
	
	//ART CLASS
	@Test
	public void testAddWall(){
		Art art = new Art("Test",5000,0);
		Wall w = new Wall();
		Door d = new Door(true,1);
		art.addWall(w);
		art.addWall(d);
		assertTrue(art.getWalls().contains(w) && art.getWalls().contains(d));
	}
	
	//KEY CLASS - tests not needed
}