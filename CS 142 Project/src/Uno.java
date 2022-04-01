import java.util.Scanner;

public class Uno 
{
	public static void main(String[] args)
	{
		Scanner s = new Scanner(System.in);

		//Introduction 
		String startPrompt = "Type \"start\" to play the game, type \"rules\" to learn how to play: ";

		String rules = "\n"
				+ "Rules:\n"
				+ "------------------------------------------------------------------------------------------------\n"
				+ "Each turn you will either play or draw a card.\n"
				+ "The card played must either match the color, number, or type of the card on top of the discard pile.\n"
				+ "If you have no available moves you must draw a card and end your turn.\n" 
				+ "Some cards have special abilities that help give you an edge in the game.\n"
				+ "The skip card skips the other player's turn and you take another turn.\n"
				+ "The draw 2 card forces the other player to draw 2 cards and lose their turn.\n"
				+ "The wild card allows you to choose any color for the discard pile.\n"
				+ "The wild draw 4 allows you to choose any color for the discard pile and force the other player to draw 4 cards.\n"
				+ "------------------------------------------------------------------------------------------------\n";


		boolean startCheck = false;
		System.out.println("Welcome to the game of UNO, the first player to play all of their cards wins!\n");

		while(startCheck == false)
		{
			System.out.print(startPrompt);

			if(s.hasNext("Rules") || s.hasNext("rules"))
			{
				System.out.println(rules);
				s.nextLine();
			}
			else if(s.hasNext("Start") || s.hasNext("start"))
			{
				startCheck = true;
				s.nextLine();
			}
			else
			{
				System.out.println("Please type either start or rules!");
				s.nextLine();
			}
		}


		// initializing arrays for player and computer hands ac
		String handSizePrompt = "\nPlease enter how many cards you would like to start with (3-10): ";
		int handSize = promptPlayer(s, handSizePrompt, 3, 10);
		int handSizeMax = handSize + 30;
		int[] playerHandColor = new int[handSizeMax];
		int[] playerHandValue = new int[handSizeMax];
		int[] cpuHandColor = new int[handSizeMax];
		int[] cpuHandValue = new int[handSizeMax];

		// drawing the starting hands for both players ac
		for(int i = 0; i < handSize; i++) 
		{
			playerHandColor[i] = drawCard(9);
			playerHandValue[i] = drawCard(12); 
			cpuHandColor[i] = drawCard(9); 
			cpuHandValue[i] = drawCard(12);
		}

		// creating and displaying the starting card on the discard pile ac
		int discardColor = drawCard(8);
		int discardValue = drawCard(12);
		String discardPileText = displayTopCard(discardColor, discardValue);

		String colorPrompt = "Please enter a color (Red, Blue, Green, or Yellow): ";
		String cardSelect = "Please play a card: ";
		boolean validPlay = false;
		boolean gameOver = false;
		int playerTurn = 1;
		int cardChoice = 0;
		int cardsInHand = 0;

		System.out.println("------------------------------------------------------------");
		System.out.println("Enter the number to the left of your desired card to play it.\n");

		while(gameOver == false)
		{
			// Player's Turn ac
			if(playerTurn % 2 != 0) // if playerTurn is odd, P1's turn
			{
				// converting and displaying player's hand ac
				System.out.print("It's your turn!\n");
				cardsInHand = cardCounter(playerHandColor);
				String handText = displayHand(playerHandColor, playerHandValue);
				System.out.println("Your Cards:\n" + handText);

				// displaying top card on discard pile ac
				discardPileText = displayTopCard(discardColor, discardValue);
				System.out.println("Discard Pile: " + discardPileText + "\n");

				// Check if player needs to draw 
				if(doesPlayerDraw(playerHandColor, playerHandValue, discardColor, discardValue) == false)
				{
					System.out.println("You have no available moves.");
					System.out.print("Press enter to draw a card. ");
					s.nextLine();
					drawOneCard(playerHandColor, playerHandValue);
				}
				else
				{
					// Checking if player's input is valid 
					while(validPlay == false)
					{
						cardChoice = promptPlayer(s, cardSelect, 1, cardsInHand) - 1;
						validPlay = canPlayForPlayer(cardChoice, playerHandColor, playerHandValue, discardColor, discardValue);
					}

					if(cardsInHand == 2)
					{
						System.out.println("Uno! You have one card remaining.");
					}

					// Updating the discard pile after player plays a valid card 
					discardColor = playerHandColor[cardChoice];
					discardValue = playerHandValue[cardChoice];

					// Updating the player's hand ac
					handModifier(playerHandColor, cardChoice);
					handModifier(playerHandValue, cardChoice);

					if(discardColor == 9 && discardValue <= 8) //  Check if a wild card was played 
					{
						discardColor = wildCardPlayer(s, colorPrompt);
						discardValue = 13;
					}
					else if(discardColor == 9 && discardValue > 8) // Check if wild draw 4 is played 
					{
						System.out.println("Your opponent draws four cards!");
						drawFourCards(cpuHandColor, cpuHandValue);
						discardColor = wildCardPlayer(s, colorPrompt);
						discardValue = 13;
						playerTurn++;
					}
					else if(discardColor != 9 && discardValue == 11) // Check if a skip card was played 
					{
						System.out.println("You skipped your opponent!");
						playerTurn++;
					}
					else if(discardColor != 9 && discardValue == 12) // Check if draw two card was played 
					{
						System.out.println("Your opponent draws two cards!");
						drawTwoCards(cpuHandColor, cpuHandValue);
						playerTurn++;
					}

				}

				// checking win condition ac
				cardsInHand = cardCounter(playerHandColor);
				if(cardsInHand == 0)
				{
					System.out.println("Congratulations! You Win!");
					break;
				}

				playerTurn++;
				System.out.println("---------------------------------------------------------");
				validPlay = false;
			}

			// CPUs Turn ac
			if(playerTurn % 2 == 0)
			{
				cardsInHand = cardCounter(cpuHandColor);

				System.out.println("It's the Computer's Turn.");

				//displaying the top card on the discard pile ac
				discardPileText = displayTopCard(discardColor, discardValue);
				System.out.println("\nDiscard Pile: " + discardPileText + "\n");

				if(doesPlayerDraw(cpuHandColor, cpuHandValue, discardColor, discardValue) == false)
				{
					System.out.println("The computer has no cards that can be played, it draws a card.");
					drawOneCard(cpuHandColor, cpuHandValue);
					System.out.println("The computer currently has " + (cardsInHand + 1) + " cards in hand.");
				}
				else
				{
					cardChoice = cpuPlay(cpuHandColor, cpuHandValue, discardColor, discardValue, cardsInHand);
					cardsInHand--;
					
					discardColor = cpuHandColor[cardChoice];
					discardValue = cpuHandValue[cardChoice];
					handModifier(cpuHandColor, cardChoice);
					handModifier(cpuHandValue, cardChoice);

					String computerPlay = displayTopCard(discardColor, discardValue);
					
					//checking for win condition ac
					if(cardsInHand == 0)
					{
						System.out.println("The computer played a " + computerPlay + "!");
						System.out.println("The computer is out of cards! You Lost!");
						break;
					}

					if (discardColor != 9 && discardValue == 12) 
					{
						System.out.println("The computer played a " + computerPlay + " making you draw two cards!");
						drawTwoCards(playerHandColor, playerHandValue);
						playerTurn++;
					}
					else if (discardColor != 9 && discardValue == 11) 
					{
						System.out.println("The computer played a " + computerPlay + "!");
						playerTurn++;
					}
					else if(discardColor == 9 && discardValue <= 8)
					{
						System.out.println("The computer played a wild card!");
						discardColor = (cpuWildCard(cpuHandColor, cardsInHand) * 2);
						discardValue = 13;
					}
					else if(discardColor == 9 && discardValue >= 9)
					{
						System.out.println("The computer played a wild draw four making you draw four cards and skips your next turn!");
						drawFourCards(playerHandColor, playerHandValue);
						discardColor = (cpuWildCard(cpuHandColor, cardsInHand) * 2); // multiplying by 2 makes 1-4 correspond to numbers in code
						discardValue = 13;
						playerTurn++; 
					}
					else
					{
						System.out.println("The computer played a " + computerPlay + "!");
					}
					
					if(cardsInHand == 1)
					{
						System.out.println("Uno! The computer has one card left.");
					}
					else
					{
						System.out.println("The computer currently has " + cardsInHand + " cards in hand.");
					}
				}
			
				playerTurn++;
				validPlay = false;
				
				// giving the player a chance to see what the CPU did before moving on ac
				System.out.println("---------------------------------------------------------\n");
				System.out.print("Press enter to continue on to the next turn.");
				s.nextLine();
				System.out.println("\n---------------------------------------------------------");
			}
		}
	}

	//takes and validates user input for hand size and card selection ac
	public static int promptPlayer(Scanner s, String prompt, int min, int max) 
	{
		boolean inputChecker = false;
		int userValue = 0;

		while(inputChecker == false) 
		{
			System.out.print(prompt);

			if(s.hasNextInt()) 
			{
				userValue = s.nextInt();
				s.nextLine();
			} 
			else 
			{
				s.nextLine();
				System.out.println("Please enter a number between " + min + " and " + max + "!");
				continue;
			}

			if(userValue <= max && userValue >= min)
			{
				inputChecker = true;
			} 
			else 
			{
				System.out.println("Please enter a number between " + min + " and " + max + "!");
			}
		}
		return userValue;
	}

	//returns a random number 1-range which is used to determine the card color or value ac
	public static int drawCard(int range)
	{
		int card = (int) ((Math.random() * range) + 1);		
		return card;
	}

	public static String displayTopCard(int color, int value)
	{
		String topCardText = "";

		// displays what color the player who played the wild card chose AC
		if(color == 2 && value == 13)
		{
			topCardText += "Wild Card (Red)";
			return topCardText;
		}
		else if(color == 4 && value == 13)
		{
			topCardText += "Wild Card (Blue)";
			return topCardText;
		}
		else if(color == 6 && value == 13)
		{
			topCardText += "Wild Card (Green)";
			return topCardText;
		}
		else if(color == 8 && value == 13)
		{
			topCardText += "Wild Card (Yellow)";
			return topCardText;
		}

		if(color == 1 || color == 2)
		{
			topCardText += "Red ";
		}
		else if(color == 3 || color == 4)
		{
			topCardText += "Blue ";
		}
		else if(color == 5 || color == 6)
		{
			topCardText += "Green ";
		}
		else if(color == 7 || color == 8)
		{
			topCardText += "Yellow ";
		}

		if(value == 1) 
		{
			topCardText += "1";
		}
		else if(value == 2) 
		{
			topCardText += "2";
		}
		else if(value == 3) 
		{
			topCardText += "3";
		}
		else if(value == 4) 
		{
			topCardText += "4";
		}
		else if(value == 5) 
		{
			topCardText += "5";
		}
		else if(value == 6) 
		{
			topCardText += "6";
		}
		else if(value == 7) 
		{
			topCardText += "7";
		}
		else if(value == 8) 
		{
			topCardText += "8";
		}
		else if(value == 9) 
		{
			topCardText += "9";
		}
		else if(value == 10) 
		{
			topCardText += "0";
		}
		else if(value == 11) 
		{
			topCardText += "Skip";
		}
		else if(value == 12) 
		{
			topCardText += "Draw 2";
		}
		return topCardText;
	}

	//searches for every filled spot in hand array ac
	public static int cardCounter(int[] playerHand)
	{
		int cardCount = 0;

		for (int i = 0; i < playerHand.length; i++) {
			if (playerHand[i] != 0) {
				cardCount++;
			}
		}
		return cardCount;
	}

	//reads hand arrays and changes them into a string readable to the player ac
	public static String displayHand(int[] color, int[] value)
	{
		String handText = "";

		for(int i = 0; i < color.length; i++)
		{
			if(color[i] == 1 || color[i] == 2)
			{
				handText += (i + 1) + ". Red ";
			}
			else if(color[i] == 3 || color[i] == 4)
			{
				handText += (i + 1) + ". Blue ";
			}
			else if(color[i] == 5 || color[i] == 6)
			{
				handText += (i + 1) + ". Green ";
			}
			else if(color[i] == 7 || color[i] == 8)
			{
				handText += (i + 1) + ". Yellow ";
			}
			else if(color[i] == 9)
			{
				handText += (i + 1) + ". Wild ";
			}

			if(color[i] == 9 && value[i] <= 8 && value[i] > 0)
			{
				handText += "Card\n";
				continue;
			}

			if(color[i] == 9 && value[i] > 8)
			{
				handText += "Draw Four\n";
				continue;
			}

			if(value[i] == 1) 
			{
				handText += "1\n";
			}
			else if(value[i] == 2) 
			{
				handText += "2\n";
			}
			else if(value[i] == 3) 
			{
				handText += "3\n";
			}
			else if(value[i] == 4) 
			{
				handText += "4\n";
			}
			else if(value[i] == 5) 
			{
				handText += "5\n";
			}
			else if(value[i] == 6) 
			{
				handText += "6\n";
			}
			else if(value[i] == 7) 
			{
				handText += "7\n";
			}
			else if(value[i] == 8) 
			{
				handText += "8\n";
			}
			else if(value[i] == 9) 
			{
				handText += "9\n";
			}
			else if(value[i] == 10) 
			{
				handText += "0\n";
			}
			else if(value[i] == 11) 
			{
				handText += "Skip\n";
			}
			else if(value[i] == 12) 
			{
				handText += "Draw 2\n";
			}
		}
		return handText;
	}

	//checks if player needs to draw a card 
	public static boolean doesPlayerDraw(int[] playerHandColor, int[] playerHandValue, int discardPileColor, int discardPileValue)
	{
		for(int i = 0; i < playerHandColor.length; i++)
		{
			//check values 
			if(playerHandValue[i] == discardPileValue)
			{
				return true;
			}
			//Check Colors 
			else if(playerHandColor[i] == 9)
			{
				return true;
			}
			else if((playerHandColor[i] == 1 || playerHandColor[i] == 2) && (discardPileColor == 1 || discardPileColor == 2))
			{
				return true;
			}
			else if((playerHandColor[i] == 3 || playerHandColor[i] == 4) && (discardPileColor == 3 || discardPileColor == 4))
			{
				return true;
			}
			else if((playerHandColor[i] == 5 || playerHandColor[i] == 6) && (discardPileColor == 5 || discardPileColor == 6))
			{
				return true;
			}
			else if((playerHandColor[i] == 7 || playerHandColor[i] == 8) && (discardPileColor == 7 || discardPileColor == 8))
			{
				return true;
			}
		}
		return false;
	}

	//check numbers/skip/draw 
	public static boolean canPlayForPlayer(int cardChoice, int[] playerHandColor, int[] playerHandValue, int discardPileColor, int discardPileValue){
		
		if(playerHandValue[cardChoice] == discardPileValue){
			return true;
		}
		//Check Colors 
		else if((playerHandColor[cardChoice] == 1 || playerHandColor[cardChoice] == 2) && (discardPileColor == 1 || discardPileColor == 2)){
			return true;
		}
		else if((playerHandColor[cardChoice] == 3 || playerHandColor[cardChoice] == 4) && (discardPileColor == 3 || discardPileColor == 4)){
			return true;
		}
		else if((playerHandColor[cardChoice] == 5 || playerHandColor[cardChoice] == 6) && (discardPileColor == 5 || discardPileColor == 6)){
			return true;
		}
		else if((playerHandColor[cardChoice] == 7 || playerHandColor[cardChoice] == 8) && (discardPileColor == 7 || discardPileColor == 8)){
			return true;
		}
		else if(playerHandColor[cardChoice] == 9){
			return true;
		}
		else
		{
			System.out.println("That is not a valid card to play. Please pick another Card.");
			return false;
		}
	}

	//moves everything to the starting indexes of the array ac
	public static int[] handModifier(int[] playerHand, int cardPlayed)
	{
		playerHand[cardPlayed] = 0;

		for(int i = 0; i < playerHand.length; i++)
		{
			if(i == playerHand.length - 1)
			{
				break;
			}

			if(playerHand[i] == 0)
			{
				playerHand[i] = playerHand[i + 1];
				playerHand[i + 1] = 0;
			}
		}
		return playerHand;
	}
	
	//looks for first empty space in player's hand and fills with a card ac
	public static void drawOneCard(int[] playerColor, int[] playerValue)
	{
		int drawOne = 0;

		for(int i = 0; i < playerColor.length; i++)
		{
			if(playerColor[i] == 0 && playerValue[i] == 0)
			{
				playerColor[i] = drawCard(9);
				playerValue[i] = drawCard(12);
				drawOne++;
			}

			if(drawOne == 1)
			{
				break;
			}
		}
	}

	//same as drawone method ac
	public static void drawTwoCards(int[] oppoColor, int[] oppoValue)
	{
		int drawTwo = 0;

		for(int i = 0; i < oppoColor.length; i++)
		{
			if(oppoColor[i] == 0 && oppoValue[i] == 0)
			{
				oppoColor[i] = drawCard(9);
				oppoValue[i] = drawCard(12);
				drawTwo++;
			}

			if(drawTwo == 2)
			{
				break;
			}
		}
	}
	
	//same as drawone and drawtwo methods ac
	public static void drawFourCards(int[] oppoColor, int[] oppoValue)
	{
		int drawFour = 0;

		for(int i = 0; i < oppoColor.length; i++)
		{
			if(oppoColor[i] == 0 && oppoValue[i] == 0)
			{
				oppoColor[i] = drawCard(9);
				oppoValue[i] = drawCard(12);
				drawFour++;
			}

			if(drawFour == 4)
			{
				break;
			}
		}
	}

	//Asks the user for what color, if not entered exactly as is, will give message and keep the value the same
	public static int wildCardPlayer(Scanner s, String colorPrompt)
	{
		int discardPileColor = 9;
		boolean inputChecker = false;
		while(inputChecker == false)
		{
			System.out.print(colorPrompt);

			if(s.hasNext("Red") || s.hasNext("red"))
			{
				s.nextLine();
				inputChecker = true;
				return 2;
			}
			else if(s.hasNext("Blue") || s.hasNext("blue"))
			{
				s.nextLine();
				inputChecker = true;
				return 4;
			}
			else if(s.hasNext("Green") || s.hasNext("green"))
			{
				s.nextLine();
				inputChecker = true;
				return 6;
			}
			else if(s.hasNext("Yellow") || s.hasNext("yellow"))
			{
				s.nextLine();
				inputChecker = true;
				return 8;
			}
			else
			{
				System.out.println("Invalid entry. Please type either Red, Blue, Green, or Yellow.");
				s.nextLine();
			}
		}
		return discardPileColor;
	}

	//prioritizes color, then value, then wild ac
	public static int cpuPlay(int[] cardColor, int[] cardValue, int discardColor, int discardValue, int cardsInHand)
	{
		for(int i = 0; i < cardsInHand; i++)
		{
			if((cardColor[i] == 7 || cardColor[i] == 8) && (discardColor == 7 || discardColor == 8))
			{
				return i;
			}
			else if((cardColor[i] == 5 || cardColor[i] == 6) && (discardColor == 5 || discardColor == 6))
			{
				return i;
			}
			else if((cardColor[i] == 3 || cardColor[i] == 4) && (discardColor == 3 || discardColor == 4))
			{
				return i;
			}
			else if((cardColor[i] == 1 || cardColor[i] == 2) && (discardColor == 1 || discardColor == 2))
			{
				return i;
			}
			else if((cardValue[i] == discardValue) && cardColor[i] != 9)
			{
				return i;
			}
			else if(cardColor[i] == 9)
			{
				return i;
			}
		}
		return 0;
	}

	// looks for the most common color in the CPUs hand and selects it for a wild card ac
	public static int cpuWildCard(int[] cardColor, int cardsInHand)
	{
		int redTracker = 0;
		int blueTracker = 0;
		int greenTracker = 0;
		int yellowTracker = 0;

		for(int i = 0; i < cardsInHand; i++)
		{
			if(cardColor[i] == 1 || cardColor[i] == 2)
			{
				redTracker++;
			} 
			else if(cardColor[i] == 3 || cardColor[i] == 4)
			{
				blueTracker++;
			}
			else if(cardColor[i] == 5 || cardColor[i] == 6)
			{
				greenTracker++;
			}
			else if(cardColor[i] == 7 || cardColor[i] == 8)
			{
				yellowTracker++;
			}
		}

		int[] colorArray = {redTracker, blueTracker, greenTracker, yellowTracker};
		int defaultValue = 0;
		int chosenColor = 2; // defaults to green cause that's my favorite color

		for(int i = 0; i < 4; i++)
		{
			if(colorArray[i] > defaultValue)
			{
				defaultValue = colorArray[i];
				chosenColor = i + 1;
			}
		}
		return chosenColor;
	}
}