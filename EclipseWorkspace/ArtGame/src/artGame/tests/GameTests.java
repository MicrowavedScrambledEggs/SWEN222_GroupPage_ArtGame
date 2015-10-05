package artGame.tests;
import static org.junit.Assert.*;

import org.junit.Test;

import artGame.game.Character;
import artGame.game.Character.Direction;
import artGame.game.Item;


public class GameTests {
	/* ====================================================
	 * CHARACTER TESTS
	 */
	@Test
	public void testCharharacterValidAddItem(){
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		testChar.addItem(i);
		assertTrue(testChar.getInventory().contains(i));
	}
	
	@Test
	public void testCharharacterValidTakeItem(){
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		testChar.addItem(i);
		testChar.takeItem(i);
		assertFalse(testChar.getInventory().contains(i));
	}
	
	@Test
	public void testCharharacterInvalidTakeItem(){
		Character testChar = new Character(Direction.NORTH, 0);
		Item i = new Item(1);
		Item i2 = new Item(2);
		testChar.addItem(i);
		testChar.takeItem(i2);
		assertTrue(testChar.getInventory().contains(i));
	}
}
