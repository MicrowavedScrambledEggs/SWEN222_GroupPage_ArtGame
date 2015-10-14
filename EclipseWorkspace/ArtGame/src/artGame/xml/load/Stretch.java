package artGame.xml.load;

import java.util.ArrayList;

import artGame.game.Coordinate;

/**
 * Interface for build strategies for parts of guard's patrol paths
 *
 * @author Badi James 300156502
 *
 */
public interface Stretch extends BuildStrategy {

	public ArrayList<Coordinate> getSteps();

}
