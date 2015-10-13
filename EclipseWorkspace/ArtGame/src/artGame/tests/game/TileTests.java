package artGame.tests.game;

import static org.junit.Assert.*;

import org.junit.Test;

import artGame.game.Chest;
import artGame.game.EmptyTile;
import artGame.game.Key;
import artGame.game.Character.Direction;
import artGame.game.Player;
import artGame.game.StairTile;

public class TileTests {
	/**
	 * TILE TESTS
	 */
	// BASE CLASS cant be tested
	
	// CHEST CLASS
	@Test
	public void testTakeItem(){
		Chest c = new Chest(0,false,false,false,false);
		Key k = new Key(1,1);
		c.setContent(k);
		Player p = new Player(Direction.SOUTH,2);
		c.takeItem(p);
		assertTrue(c.getContent()==null && p.getInventory().contains(k));
	}
	
	// EMPTYTILE CLASS
	@Test
	public void testWalkable(){
		EmptyTile et = new EmptyTile(false,false,false,false);
		assertTrue(et.walkable());
	}
	
	@Test
	public void testNonWalkable(){
		EmptyTile et = new EmptyTile(false,false,false,false);
		et.setOccupant(new Player(Direction.NORTH,1));
		assertFalse(et.walkable());
	}
	
	//EXITTILE CLASS - cant be tested
	
	//STAIRTILE CLASS
	@Test
	public void testSetOccupant(){
		StairTile st1 = new StairTile(false,false,false,false, Direction.NORTH, true);
		StairTile st2 = new StairTile(false,false,false,false,Direction.SOUTH,false);
		st1.setLinkedTile(st2);
		st2.setLinkedTile(st1);
		Player p = new Player(Direction.NORTH, 0);
		st1.setOccupant(p);
		assertTrue(st2.getOccupant()==p);
	}
	
	@Test
	public void testSetOccupantHelper(){
		StairTile st1 = new StairTile(false,false,false,false, Direction.NORTH, true);
		st1.setLoc(1, 1);
		Player p = new Player(Direction.NORTH, 0);
		st1.setOccupantHelper(p);
		assertTrue(st1.getOccupant()==p);
	}
}
