package artGame.tests.game;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import artGame.game.Art;
import artGame.game.Character;
import artGame.game.Coordinate;
import artGame.game.Floor;
import artGame.game.Guard;
import artGame.game.Item;
import artGame.game.Character.Direction;
import artGame.game.Sculpture;

public class CharacterTests {
	/*
	 * CHARACTER TESTS====================================================
	 */
	// BASE CLASS
	@Test
	public void testCharacterValidAddItem() {
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		testChar.addItem(i);
		assertTrue(testChar.getInventory().contains(i));
	}

	@Test
	public void testCharacterValidTakeItem() {
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		testChar.addItem(i);
		testChar.takeItem(i);
		assertFalse(testChar.getInventory().contains(i));
	}

	@Test
	public void testCharacterInvalidTakeItem() {
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		Item i2 = new Item(2);
		testChar.addItem(i);
		testChar.takeItem(i2);
		assertTrue(testChar.getInventory().contains(i));
	}

	@Test
	public void testClearInventory() {
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		Item i2 = new Item(2);
		testChar.addItem(i);
		testChar.addItem(i2);
		testChar.clearInventory();
		assertTrue(testChar.getInventory().isEmpty());
	}

	// GUARD CLASS
	// helper method
	public List<Coordinate> createPath() {
		List<Coordinate> path = new ArrayList<Coordinate>();
		path.add(new Coordinate(0, 0));
		path.add(new Coordinate(0, 1));
		path.add(new Coordinate(0, 2));
		path.add(new Coordinate(1, 2));
		path.add(new Coordinate(2, 2));
		path.add(new Coordinate(2, 1));
		path.add(new Coordinate(2, 0));
		path.add(new Coordinate(1, 0));
		return path;
	}
	public List<Coordinate> createOffsetPath() {
		List<Coordinate> path = new ArrayList<Coordinate>();
		path.add(new Coordinate(12, 0));
		path.add(new Coordinate(12, 1));
		path.add(new Coordinate(12, 2));
		path.add(new Coordinate(13, 2));
		path.add(new Coordinate(14, 2));
		path.add(new Coordinate(14, 1));
		path.add(new Coordinate(14, 0));
		path.add(new Coordinate(13, 0));
		return path;
	}

	@Test
	public void testNextCoord1() {
		// init guard to 0,0
		Guard g = new Guard(Direction.NORTH, 0, createPath());
		g.setRow(0);
		g.setCol(0);
		Coordinate next = g.nextCoord();
		assertTrue(next.getCol() == 0 && next.getRow() == 1);
	}

	@Test
	public void testNextCoord2() { // testing looping around
		// init guard to 1,0
		Guard g = new Guard(Direction.NORTH, 0, createPath());
		g.setRow(1);
		g.setCol(0);
		Coordinate next = g.nextCoord();
		assertTrue(next.getCol() == 0 && next.getRow() == 0);
	}

	@Test
	public void testOffsetPath() {
		Guard g = new Guard(Direction.NORTH, 0, createPath());
		g.offsetPath(12);
		for(int i=0;i<g.getPath().size();i++){
			assertTrue(g.getPath().get(i).equals(createOffsetPath().get(i)));
		}
	}
	
	//PLAYER CLASS - Testing not needed, only basic getter/setters
	
	//SCULPTURE CLASS
	@Test
	public void testToItem(){
		Floor f = new Floor();
		Sculpture sc = new Sculpture(Direction.NORTH,0,3000,"Test");
		Art art = sc.toItem(f);
		assertTrue(art.value == 3000 && art.name.equals("Test") && art.ID == 0);
	}
}
