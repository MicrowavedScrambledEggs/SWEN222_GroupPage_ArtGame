package artGame.xml.load;

import artGame.game.Coordinate;

/**
 * Convenience class that adds a length of identical empty tiles to be put along a
 * row in the floor of the game to be built
 *
 * @author Badi James 300156502
 *
 */
public class TileStretchBuilder extends TileBuilder {

	private int length;

	public TileStretchBuilder(int level, GameMaker gameMaker, int length) {
		super(level, gameMaker);
		this.length = length;
	}

	@Override
	public void addToGame() {
		Coordinate currentCoord = getCoord();
		for(int i = 0; i < length; i++){
			TileBuilder tileBuilder = super.clone();
			currentCoord = new Coordinate(getCoord().getCol() + i, getCoord().getRow());
			tileBuilder.setCoord(currentCoord);
			tileBuilder.addToGame();
		}
	}

}
