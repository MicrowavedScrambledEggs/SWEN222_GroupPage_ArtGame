package artGame.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import artGame.game.Art;
import artGame.game.Character.Direction;
import artGame.game.Coordinate;
import artGame.game.Guard;
import artGame.game.Key;
import artGame.game.Player;
import artGame.game.Character;
import artGame.main.Game;
import artGame.xml.XMLHandler;
import artGame.xml.load.LoadError;

public class LoadSaveTests {

	private XMLHandler loadSaver = new XMLHandler();
	private File testGameWorldFile = new File("src/artGame/tests/TestGameWorld.xml");
	private File badFile = new File("src/artGame/tests/BadLoadFile.xml");
	private String testSaveFile = "src/artGame/tests/TestSaveFile.xml";
	private Game testGame = loadTestGame();

	@Test
	public void testLoadSaveMatch() {
		loadSaver.saveGame(testGame, testSaveFile);
		Game testLoad = loadSaver.loadGame(new File(testSaveFile));
		assertEquals(testGame, testLoad);
	}

	@Test
	public void testLoadPlayer() {
		Player expectedPlayer = new Player(Direction.EAST, 0);
		expectedPlayer.addItem(new Key(200, 200));
		expectedPlayer.addItem(new Art("Banana", 2, 2));
		expectedPlayer.setCol(0);
		expectedPlayer.setRow(0);
		Player actualPlayer = testGame.getPlayer(0);
		testCharactersAreEqual(expectedPlayer, actualPlayer);

	}

	@Test
	public void testLoadGuard1() {
		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
		path.add(new Coordinate(3,0));
		path.add(new Coordinate(4,0));
		path.add(new Coordinate(4,1));
		path.add(new Coordinate(4,2));
		path.add(new Coordinate(3,2));
		path.add(new Coordinate(2,2));
		path.add(new Coordinate(2,1));
		path.add(new Coordinate(2,0));
		Guard expectedGuard = new Guard(Direction.EAST, 1, path);
		expectedGuard.setCol(2);
		expectedGuard.setRow(0);
		Guard actualGuard = testGame.getGuard(1);
		testCharactersAreEqual(expectedGuard, actualGuard);
		assertEquals(path, actualGuard.getPath());
	}

	@Test
	public void testLoadGuard2() {
		Guard expectedGuard = new Guard(Direction.NORTH, 2);
		expectedGuard.setCol(11);
		expectedGuard.setRow(2);
		Guard actualGuard = testGame.getGuard(2);
		expectedGuard.addItem(new Art("Divorce Notice", -30000, 3));
		testCharactersAreEqual(expectedGuard, actualGuard);
		assertEquals(expectedGuard.getPath(), actualGuard.getPath());
	}
	
	@Test
	public void testBadFile() {
		try{
			loadSaver.loadGame(badFile);
			fail();
		} catch (LoadError e){
			
		}
	}

	private void testCharactersAreEqual(Character expectedCharacter,
			Character actualCharacter) {
		assertEquals(expectedCharacter.getInventory(), actualCharacter.getInventory());
		assertEquals(expectedCharacter.getDir(), actualCharacter.getDir());
		assertEquals(expectedCharacter.getRow(), actualCharacter.getRow());
		assertEquals(expectedCharacter.getCol(), actualCharacter.getCol());
		assertEquals(expectedCharacter.getId(), actualCharacter.getId());
	}

	private Game loadTestGame() {
		return loadSaver.loadGame(testGameWorldFile);
	}


}
