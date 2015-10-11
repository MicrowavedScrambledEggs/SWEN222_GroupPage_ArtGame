package artGame.xml.load;

import artGame.game.Coordinate;

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
