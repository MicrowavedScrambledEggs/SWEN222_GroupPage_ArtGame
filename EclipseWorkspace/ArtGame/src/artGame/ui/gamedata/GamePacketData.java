package artGame.ui.gamedata;

import java.util.ArrayList;
import java.util.List;

import artGame.game.Art;
import artGame.game.Guard;
import artGame.game.Player;
import artGame.game.Tile;
import artGame.game.Character.Direction;
import artGame.main.Game;
import artGame.game.Wall;

/**
 * Class for transferring game information across the network
 * @author Tim King 300282037
 *
 */
public class GamePacketData {

	public List<Player> players;
	public List<Guard> guards;
	public List<TileData> occupied;
	public int pid;
	
	public GamePacketData(int pid, Game game){
		this.pid = pid;
		players = new ArrayList<>();
		guards = new ArrayList<>();
		
		players = game.getPlayers();
		guards = game.getFloor().getGuards();

		occupied = getOccupiedTiles(game);
	}
	
	public GamePacketData(){
		
	}

	public List<TileData> getOccupiedTiles(Game game){
		
		List<TileData> data = new ArrayList<>();

		float width = game.getFloor().getWidth();
		float height = game.getFloor().getHeight();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				Tile t = game.getFloor().getTile(y, x);

				if (t == null) {
					continue;
				}
				
				int occu = -1;
				int dirOrd = -1;
				
				boolean contains = false;
				TileData d = null;
				
				if(t.getOccupant() != null){
					occu = t.getOccupant().getId();
					dirOrd = t.getOccupant().getDir().ordinal();
					contains = true;
				}
				
				for (Direction dir : Direction.values()) {
					if (t.getWall(dir) != null) {
						if (t.getWall(dir).getArt() != null) {
							contains = true;
							break;
						}
					}
				}
				
				d = new TileData(y, x, occu, dirOrd, new Wall[]{t.getWall(Direction.NORTH), t.getWall(Direction.WEST), 
						t.getWall(Direction.SOUTH), t.getWall(Direction.EAST)});
				
				if(contains){
					data.add(d);
				}

			}
		}
		return data;
	}

	
}
