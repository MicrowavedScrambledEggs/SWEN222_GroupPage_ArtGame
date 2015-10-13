package artGame.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import artGame.control.cmds.Action;
import artGame.game.*;
import artGame.game.Character.Direction;
import artGame.xml.XMLHandler;

public class Game {


	private Floor floor;
	private static Player p;
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
	 * returns the player with specified id
	 * throws a error if player not found
	 */
	public Player getPlayer(int id){
		for(Player p:players){
			if(p.getId()==id) return p;
		}
		throw new GameError("Player ID:" + id + " not found");
	}

	/**
	 * returns the Guard with specified id
	 * throws a error if guard not found
	 */
	public Guard getGuard(int id){
		for(Guard g:floor.getGuards()){
			if(g.getId()==id) return g;
		}
		throw new GameError("Guard ID:" + id + " not found");
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
			getFloor().moveCharacter(p);
		}
		else if(id=='a'){
			p.setDir(Direction.WEST);
			getFloor().moveCharacter(p);
		}
		else if(id=='s'){
			p.setDir(Direction.SOUTH);
			getFloor().moveCharacter(p);
		}
		else if(id=='d'){
			p.setDir(Direction.EAST);
			getFloor().moveCharacter(p);
		}
		else if(id=='f'){
			floor.interact(p);
		}
		else if(id=='r'){
			floor.inspect(p);
		}
		else if(id=='!'){
			p.setMoving(false);
		}
		else{
			//do nothing
		}
	}

	public Floor getFloor() {
		return floor;
	}

	public Player getPlayer() {
		return p;
	}

	/**
	 * TESTING
	 */
	public void playGame(){
		this.initialise();
		char nextCommand = 'z';
		Scanner sc = new Scanner(System.in);
		while(true){
			if(nextCommand!='z'){
				doAction(p,nextCommand);
				nextCommand = 'z';
			}

			this.floor.printFloor();//replace with gui display
			this.printMenu();
			nextCommand = sc.next().charAt(0);
			this.getFloor().moveGuards();
			if(this.getFloor().checkGuards().contains(this.getPlayer())){
				break;
			}

		}

		if(this.getPlayer().isCaught()){
			System.out.println("you got arrested");
		}
		else{
			System.out.println("you ran off");
			int score = 0;
			for(Item i:this.getPlayer().getInventory()){
				if(i instanceof Art){
					score = score + ((Art)i).value;
				}
			}
			System.out.println("you made off with $"+score+" worth of art");
		}
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			game.floor.printFloor();//replace with gui display
			game.printMenu();
			String s = sc.next();
			game.doAction(game.getPlayer(),s.charAt(0)); //replace with keylistener
			game.getFloor().moveGuards();
			if(game.getFloor().checkGuards().contains(game.getPlayer())){
				break;
			}
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
		XMLHandler gameLoader = new XMLHandler();
		return gameLoader.loadGame(loadFile);
	}


	/* Vicki messes with networking below this line. */

	/** Adds a new player to the Game. */
//	public Player addPlayer() {
//		int id = 1;
//		if (players != null && players.size() > 1) {
//			id = players.get(players.size()-1).getId() + 1;
//		}
//		Player newPlayer = new Player(Direction.SOUTH, id);
//		players.add(newPlayer);
//		return newPlayer;
//	}

	/** Adds an existing player to this Game instance. */
	public Player addPlayer(int i) throws IllegalArgumentException {
		if (players == null) {
			players = new ArrayList<Player>();
		}
		int id = 1;
		if (!isAvailablePlayerId(i)) {
			throw new IllegalArgumentException();
		}
		Player newPlayer = new Player(Direction.SOUTH, i);
		players.add(newPlayer);
		return newPlayer;
	}

	/** Removes a given player ID from the game.
	 *
	 * @param pid
	 * @return True if the player was removed, false otherwise.
	 */
	public boolean removePlayer(int pid) {
		if (players == null || players.size() == 0) { return false; } // alternatively, calls some kind of game over message
		if (pid >= 1 && pid < players.size()) {
			for(Player p : players) {
				if (p.getId() == pid) {
					players.remove(p);
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAvailablePlayerId(int pid) {
		if (players == null || players.size() == 0) return true;
		for (Player p : players) {
			if (p.getId() == pid) {
				return false;
			}
		}
		return true;
	}

	public List<Player> getPlayers(){
		List<Player> nl = new ArrayList<Player>();
		nl.addAll(players);
		return nl;
	}

	public void printGame() {
		if (getFloor().isOnExit() == null) {
			floor.printFloor(); // TODO replace with gui display
			printMenu();
		}
	}
}
