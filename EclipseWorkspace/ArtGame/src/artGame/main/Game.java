package artGame.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import artGame.game.*;
import artGame.game.Character.Direction;
import artGame.xml.XMLHandler;

public class Game {


	private Floor floor;

	private Player p;
	private List<Player> players;
	
	public Game(Floor floor,Collection<Player> players){
		this.floor = floor;
		this.players = new ArrayList<Player>();
		this.players.addAll(players);
		for(Player p:this.players){
			this.floor.setCharacter(p, p.getRow(), p.getCol());
		}
		this.p = players.iterator().next(); //TODO Badi: Just for now to get single player without nullpointerexceptions
	}
	
	public Game() {
		// TODO Auto-generated constructor stub
	}

	//placeholder for init, will need to read from xml eventually for players' positions
	public void initialise(){
		floor = new Floor();
		p = new Player(Direction.EAST,1);
		floor.setCharacter(p, 1, 1);
	}
	
	/**
	 * prints menu options
	 */
	public void printMenu(){
		Set<Item> playerInv = p.getInventory();
		System.out.println("Inventory: ");
		for(Item i:playerInv) {
			System.out.println(i);
		}
		System.out.println("what to do??:");
		System.out.println("W: move up");
		System.out.println("A: move left");
		System.out.println("S: move down");
		System.out.println("D: move right");
		System.out.println("F: interact");
		System.out.println("R: inspect");
	}
	
	/**
	 * executes a action for the player
	 */
	public void doAction(Player p, char id){
		if(id=='w'){
			p.setDir(Direction.NORTH);
			floor.moveCharacter(p);
		}
		else if(id=='a'){
			p.setDir(Direction.WEST);
			floor.moveCharacter(p);
		}
		else if(id=='s'){
			p.setDir(Direction.SOUTH);
			floor.moveCharacter(p);
		}		
		else if(id=='d'){
			p.setDir(Direction.EAST);
			floor.moveCharacter(p);
		}		
		else if(id=='f'){
			floor.interact(p);
		}
		else if(id=='r'){
			floor.inspect(p);
		}
		else{
			
		}
	}
	
	public Floor getFloor() {
		return floor;
	}

	public Player getPlayer() {
		return p;
	}
	
	public static void main(String[] args) {
		Game game = null;
		if(args.length == 0){
			game = new Game();
			game.initialise();
		} else {
			if(args[0].endsWith(".xml")){
				game = loadGame(args[0]);
			} else {
				System.out.println("Argument given not a valid save file. Loading default game");
				game = new Game();
				game.initialise();
			}
		}
		
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while(game.getFloor().isOnExit()==null){
			if(game.getFloor().checkGuards()){
				game.getPlayer().gotCaught();
				break;
			}
			game.floor.printFloor();//replace with gui display
			game.printMenu();
			String s = sc.next();
			game.doAction(game.getPlayer(),s.charAt(0)); //replace with keylistener
			game.getFloor().moveGuards();
		}
		if(game.getPlayer().isCaught()){
			System.out.println("you got arrested");
		}
		else{
			System.out.println("you ran off");
			int score = 0;
			for(Item i:game.getPlayer().getInventory()){
				if(i instanceof Art){
					score = score + ((Art)i).value;
				}
			}
			System.out.println("you made off with $"+score+" worth of art");
		}
	}

	private static Game loadGame(String fileName) {
		File loadFile = new File(fileName);
		XMLHandler gameLoader = new XMLHandler(loadFile);
		return gameLoader.getGame();
	}	
}
