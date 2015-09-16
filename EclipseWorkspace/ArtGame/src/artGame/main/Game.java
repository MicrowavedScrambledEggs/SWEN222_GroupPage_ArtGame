package artGame.main;

import java.util.Scanner;
import java.util.Set;

import artGame.game.*;
import artGame.game.Character.Direction;

public class Game {


	private static Floor floor;
	private static Player p;
	
	public void initialise(){
		floor = new Floor();
		p = new Player(Direction.EAST,1);
		floor.addCharacter(p, 2, 2);
	}
	
	/**
	 * prints menu options
	 */
	public static void printMenu(){
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
	}
	
	/**
	 * executes a action for the player
	 */
	public static void doAction(Player p, char id){
		int currentRow = p.getRow();
		int currentCol = p.getCol();
		if(id=='w'){
			p.setDir(Direction.NORTH);
			floor.moveCharacter(p, currentRow, currentCol, currentRow-1, currentCol);
		}
		else if(id=='a'){
			p.setDir(Direction.WEST);
			floor.moveCharacter(p, currentRow, currentCol, currentRow, currentCol-1);
		}
		else if(id=='s'){
			p.setDir(Direction.SOUTH);
			floor.moveCharacter(p, currentRow, currentCol, currentRow+1, currentCol);
		}		
		else if(id=='d'){
			p.setDir(Direction.EAST);
			floor.moveCharacter(p, currentRow, currentCol, currentRow, currentCol+1);
		}		
		else if(id=='f'){
			floor.interact(p);
		}		
		else{
			
		}
	}
	public static void main(String[] args) {
		Game game = new Game();
		game.initialise();
		Scanner sc = new Scanner(System.in);
		while(floor.isOnExit()==null){
			floor.printFloor();
			printMenu();
			String s = sc.next();
			doAction(p,s.charAt(0));
		}
	}

	
}
