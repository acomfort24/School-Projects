//Morse Coders' group project: Chutes and Ladders
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.InputMismatchException;

public class ChutesAndLadders {
	
	public static Scanner scan = new Scanner(System.in);

	private static Map<Integer, Space> gameBoard = new HashMap<>();
		
  	// AC
	public static class Player {
		int id, position;

		public Player(int id) {
			this.id = id; 
			// position on game board, update w dice roll and compare to Key in map
			// to see if player landed on chute or ladder AC
			position = 0; 
		}

		public int getId() {
			return id;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int newPos) {
			position = newPos;
		}
	}

  	//AC
	public static class Space {
		// C == chute, L == ladder, X == blank AC
		public String spaceType;
		// The space the chute/ladder will move the player to AC
		int connect;

		public Space(String spaceType) {
			this.spaceType = spaceType;
			this.connect = -100;
		}

		public int getConnect() {
			return connect;
		}

		public void setConnect(int x) {
			connect = x;
		}
	}

	public static void main(String[] args) {
		System.out.println("        Welcome to Chutes and Ladders Board Game!"
				+ "\n=========================================================\n");

		String s = "Please enter the number of players! (1-4): ";

		int numberPlayers = promptNumberReadLine(scan, s, 4);	

		Queue <Player> players = new LinkedList<Player>();

		// to add the desired number of players into the queue
		for(int i = 1; i <= numberPlayers; i++) {
			Player player = new Player(i);
			players.add(player);
		}

		boolean end = false;

		key();
		createBoardMap();

		while(!end) {
			//peek to get the player in front of the queue
			Player current = players.peek();
			//get the id of that player
			int id = current.getId();
			
			if(id == 1) {
				printGameBoard();
			}

			//make the current player move
			System.out.println("It's Player " + id + "'s turn, you are currently on space " + current.getPosition() + ".\nPress any button to roll the die!");
			scan.nextLine();

			move(current);
			
			System.out.println("\n=========================================================\n");

			if(current.getPosition() == 100) {
				
				System.out.println("Do you want to play again? (Press R to restart): ");

				for(Player p : players) {
					p.setPosition(0);
				}

				while(players.peek().getId()!= 1) {
					Player p = players.peek();
					players.remove();
					players.add(p);
				}

				if(scan.nextLine().equals("R")) {
					continue;
				} else {
					break;
				}
			}
			//remove from the start of queue
			players.remove();
			//add back that player to the end of queue
			players.add(current);
		}	
	}

	// Creating the map of the game board AC
	public static void createBoardMap() {

		for(int i = 1; i < 101; i++) {
			if(i == 1 || i == 4 || i == 9 || i == 21 || i == 28 || i == 36 || i == 51 || i == 71 || i == 80) {
				Space space = new Space("L");
				gameBoard.put(i, space);
			} else if (i == 16 || i == 47 || i == 49 || i == 56 || i == 62 || i == 64 || i == 87 || i == 93 || i == 95 || i == 98) {
				Space space = new Space("C");
				gameBoard.put(i, space);
			} else if (i == 100) {
				Space space = new Space("!");
				gameBoard.put(i, space);
			} else {
				Space space = new Space("X");
				gameBoard.put(i, space);
			}
		}

		// Setting all ladder connections AC
		gameBoard.get(1).setConnect(38);
		gameBoard.get(4).setConnect(14);
		gameBoard.get(9).setConnect(31);
		gameBoard.get(21).setConnect(42);
		gameBoard.get(28).setConnect(84);
		gameBoard.get(36).setConnect(44);
		gameBoard.get(51).setConnect(67);
		gameBoard.get(71).setConnect(91);
		gameBoard.get(80).setConnect(100);

		// Setting all chute connections AC
		gameBoard.get(16).setConnect(6);
		gameBoard.get(47).setConnect(26);
		gameBoard.get(49).setConnect(11);
		gameBoard.get(56).setConnect(53);
		gameBoard.get(62).setConnect(19);
		gameBoard.get(64).setConnect(60);
		gameBoard.get(87).setConnect(24);
		gameBoard.get(93).setConnect(73);
		gameBoard.get(95).setConnect(75);
		gameBoard.get(98).setConnect(78);
	}

	public static int rollValue(Scanner s) { 

		int newValue = 0;
		int value = (int) (Math.random()*6) + 1;

		System.out.println("You rolled a " + value + "! ");

		while(value == 6 || newValue == 6) { // If rolled a 6, roll again, and add total value
			System.out.println("\nPress enter for a bonus roll!");
			s.nextLine();
			newValue = rollValue(s); // Now uses recursion if a 6 is rolled AC
			value += newValue;
		}
		return value;
	}

	public static void move(Player id) { 

		int roll = rollValue(scan); //gives the random dice roll for the players turn. this is passed into canMove 
		int newPosition = roll + id.position;
		int haventMoved = 1;

		//update player position
		if (newPosition > 100) {
			System.out.println("\nSorry, you rolled " + roll + ". You went over 100 and will not move.");
			haventMoved = 0;
		}

		if(haventMoved == 1){
			//check position against the locations of chutes and ladders
			if(gameBoard.get(newPosition).spaceType == "C") {
				//update the position to be the position after going through the chute/ladder
				newPosition = gameBoard.get(newPosition).getConnect();
				System.out.println("\nOh no! You landed on a chute and went down to " + newPosition + ".");
			}

			if(gameBoard.get(newPosition).spaceType == "L") {
				newPosition = gameBoard.get(newPosition).getConnect();
				System.out.println("\nHooray! You landed on a ladder and went up to " + newPosition + "!!");
			}
			else{
				System.out.println("\nYou moved from " + id.getPosition() + " to " + newPosition + ".");
			}
			id.setPosition(newPosition);
		}
		//check for win condition
		if (newPosition == 100) {
			System.out.println("\nCongratulations. You are the winner!!");
		}
	}

	public static int promptNumberReadLine(Scanner s, String prompt, int max)  {
		int nums = 0;

		while(true) {	
			System.out.print(prompt);

			//makes sure that the input is integer
			//get the next integer if it is an integer
			try {
				nums = s.nextInt();
				s.nextLine();

				if(nums <= max && nums > 0) {
					break;
				}
				else {
					//if the integer is out of range, prompt that was not a valid number
					System.out.println("That was not a valid number! Please try again.");
					continue;
				}
			}

			catch(InputMismatchException ne) {
				s.nextLine();
				System.out.println("That was not a valid number! Please try again.");
				continue;
			}
		}
		return nums;
	}

	public static void printGameBoard() {
		//row and column keep track of the current section being printed AB
		int row = 1;
		int column = 0;
		String line = "----------------";
		String open = "|\t\t|";
		String chute = "|  slip to ";
		String ladder = "|  climb to ";
		
		lines(line);
		for(int i = 100; i > 0; i--) {//counts down from 100-0. Each i is a segment (|		|)
			String number = "|\t"+ i +"\t|";//top of each space. All spaces have this

			if(row % 2 == 1) {//checks if its an odd row, only odd rows print the i value (open)	

				System.out.print(number);
				column++;//each time a segment is printed, column increases

				if(column % 10 == 0) {//when the row is finished, go to next line 
					System.out.println(); 
					row++;
					//since each line segment increases i, i is altered accordingly, every time lines() is called, starting after row 1
					i = i + 9;
				}

			}
			if(row % 2 == 0) {//if its an even row, it is the bottom of a space
				//-1 and +1 because the underlying array is off by 1
				int ladderConnect = gameBoard.get(i).connect;
				int chuteConnect = gameBoard.get(i).connect;
				//if space has a ladder
				if(gameBoard.get(i).spaceType == "L") {
					System.out.print(ladder + ladderConnect + "\t|");
					column++;
				}
				//if space has a chute
				else if(gameBoard.get(i).spaceType == "C") {
					System.out.print(chute + chuteConnect + "\t|");
					column++;
				}
				//if space is end
				else if(gameBoard.get(i).spaceType == "!") {
					System.out.print("|   !!Finish!!\t|");
					column++;
				}
				//if space is open
				else {
					System.out.print(open);
					column++;
				}

				if(column % 10 == 0) {//when the row is finished, go to next line and print separation lines 
					System.out.println(); 
					lines(line);
					row++;
				}
			}
		}
		System.out.println("\n=========================================================\n");
	}

	public static void lines(String line) {//prints out 10 line segments
		for(int i = 0; i < 10; i++) {
			System.out.print(line); 
			if(i == 9) {
				System.out.println();
			}		
		}
	}

	private static void key(){//examples for each spaceType
		String line = "-----------------\n";
		
		System.out.println("\n=========================================================\n");
		System.out.println("Chutes and Ladders Key:\n");
		
		System.out.println("The goal of the game is to land exactly on the 100th space.");
		System.out.println("The three types of spaces you can land on are chutes, ladders, or open spaces.\n");
		
		//example chute space
		System.out.println("Avoid the chutes! They will move you downwards, away from the finish!");
		System.out.println("An example of chute is:");
		System.out.println(line + "|\t" + 17 + "\t|\n|   " + "slip to 6" + "\t|\n" + line);
		
		//example ladder space
		System.out.println("Ladders will help you reach the finish faster! Try and land on them!");
		System.out.println("An example of ladder is:");
		System.out.println(line + "|\t" + 1 + "\t|\n|  " + "climb to 38" + "\t|\n" + line);
		
		//example open space
		System.out.println("Landing on an empty space ends your turn.");
		System.out.println("An example of an open space is:");
		System.out.println(line + "|\t" + 2 + "\t|\n|\t\t|\n" + line);
		
		System.out.println("=========================================================\n");
	}
}