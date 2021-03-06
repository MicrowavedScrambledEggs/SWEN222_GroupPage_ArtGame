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
/**
 * The game object that contains a game floor as well as all players
 * associated with it
 *
 */
public class Game {


	private Floor floor;
	private static Player p;
	private List<Player> players;
	private String name = null;
	
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
	}
	
	
	public void setName(String s) {
		if (s!=null) name = s;
	}
	
	public String getName() {
		return (name==null) ? "unnamed game" : name;
	}

	/**
	 * Initialises the game to use the default floor
	 */
	public void initialise(){
		floor = new Floor();
		//p = new Player(Direction.EAST,1);
		//floor.setCharacter(p, 1, 1);
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
		System.out.println("K: save game");
	}
	
	/**
	 * executes a action for the player
	 */
	public synchronized void doAction(Player p, char id){
		System.out.println(getName()+" doing action "+id+" on player "+p.getId());
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
			//do nothing
		}
	}
	
	/**
	 * Returns the game floor
	 * @return
	 */
	public Floor getFloor() {
		return floor;
	}

	/**
	 * Returns the current player(player viewing game)
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * TESTING console based game
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
	
	/**
	 * Testing console based game
	 */
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
			if(s.charAt(0) == 'k'){
				saveGame(game);
			} else {
				game.doAction(game.getPlayer(),s.charAt(0)); //replace with keylistener
				game.getFloor().moveGuards();
				if(game.getFloor().checkGuards().contains(game.getPlayer())){
					break;
				}
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

	private static void saveGame(Game game) {
		XMLHandler gameSaver = new XMLHandler();
		gameSaver.saveGame(game, "Save Files/testSaveGame.xml");
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
	public Player addPlayer(int pid) throws IllegalArgumentException {
		if (players == null) {
			players = new ArrayList<Player>();
		}
		if (!isAvailablePlayerId(pid)) {
			throw new IllegalArgumentException();
		}
		Player newPlayer = new Player(Direction.SOUTH, pid);
		players.add(newPlayer);
		return newPlayer;
	}
	
	/** Removes a given player ID from the game.
	 * 
	 * @param pid
	 * @return True if the player was removed, false otherwise.
	 */
	public synchronized boolean removePlayer(int pid) {
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

	synchronized public boolean isAvailablePlayerId(int pid) {
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
	
	public boolean equals(Object o) {
		if (o instanceof Game) {
			Game g = (Game)o;
			if (getPlayers().size() != g.getPlayers().size()) {
				return false;
			}
			for (int i = 0; i < getPlayers().size(); i++) {
				boolean equals = false;
				for (int j = 0; j < g.getPlayers().size(); j++) {
					int myId = getPlayers().get(i).getId();
					int yourId = g.getPlayers().get(j).getId();
					if (myId == myId) {
						equals = true;
						continue;
					} else if (!equals && i == g.getPlayers().size()-1) {
						return false;
					}
				}
			}
			if (g.getFloor().equals(getFloor())) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		String s = ((name != null) ? name : "GAME") +": PLAYERS ";
		for (Player p : players) {
			s += p.getId() +" ";
		}
		s += "\n\t";
		s += "FLOOR ("+ floor.getWidth() + ",";
		s += floor.getHeight()+")";
		return s;
	}
	
	public boolean hasPlayers() {
		return players.size() > 0;
	}
}
