package artGame.game;

import java.util.HashSet;
import java.util.Set;

public class Player extends Character{
	private boolean caught;
	public Player(Direction dir, int ID) {
		super(dir,ID);
		caught = false;
	}

	

	
	public String toString(){
		return "P";
	}

	

	public boolean isCaught() {
		return caught;
	}


	public void gotCaught() {
		this.caught = true;
	}

}
