import java.util.*;


public class prog3 {
	
	
	static int chooseFromDist(double p[]) {
		Random rand = new Random();
		
		double u = 0.0; // cum. dist
		double r = rand.nextDouble();
		
		for (int i = 1; i < p.length; i++) {
			u += p[i];
			if (r < u) {
				return i;
			}
		}
		return p.length;
	}
	
	static int rollDice(int NDice, int NSides) {
		
		int result = 0;
		
		for (int i = 0; i < NDice; i++) {
			int roll = (int) (Math.random() * NSides + 1);
			//System.out.println(roll);
			result += roll;
		}
		//System.out.println("Dice roll = " + result);
		return result;
	}
	
	static int chooseDice(int[] score, int[][][] LoseCount, int[][][] WinCount, int NDice, double M) {
		
		//two scenarios, write functions (formulas) for which dice given the two scenarios and M
		int k = NDice;
		int diceToRoll = 0;
		
		int x = score[0];
		int y = score[1];
		
		double highest_value = 0; 
		int b = 0;// B
		
		double[] probabilities = new double[k + 1];
		//array that holds f_k
		double[] F = new double[k + 1];
		
		int T = 0;
		for (int j = 0; j <= k; j++) {
			T += (WinCount[x][y][j] + LoseCount[x][y][j]);
		}
				
		for (int j = 1; j <= k; j++) {
			double f;
			if (WinCount[x][y][j] + LoseCount[x][y][j] == 0) {
				f = 0.5;
			}else {
				f = WinCount[x][y][j]/(WinCount[x][y][j] + LoseCount[x][y][j]);
			}
			
			
			F[j] = f;
			
			if (f > highest_value) {
				highest_value = f;
				b = j;
			}	
		}
		
		probabilities[b] = (T*F[b] + M)/(T*F[b] + k*M);
		
		int g = 0;
		
		for (int j = 1; j <= k; j++) {
			
			if (b == j) continue;
			
			g += F[j];
			
		}
		
		for (int i = 1; i <= k; i++) {
			if (i == b) continue; 
			
			probabilities[i] = (1 - probabilities[b]) * ( (T*F[i] + M)/(g*T + (k-1)*M) );	
		}
		
//		for (int i = 1; i < probabilities.length; i++) {
//			System.out.println("Probability: " + i + " " + probabilities[i]);
//		}
		
		diceToRoll = chooseFromDist(probabilities);
		//System.out.println("Number of Dice rolled = " + diceToRoll);
		return diceToRoll;
	}
	
	static ArrayList<int[][][]> PlayGame(int NDice, int NSides, int LTarget, int UTarget,
			int[][][] LoseCount, int[][][] WinCount, double M){
		
		ArrayList<int[][][]> updatedCounts = new ArrayList<>();
		
		int [] playerAScore = new int[2]; //A Score, B Score
		int [] playerBScore = new int[2]; //B Score, A Score
		int chooseDice;
		int diceRoll;
		
		//ADD KEEPING TRACK OF THE TRACE OF MOVES (Player score, Opponent Score, # Dice rolled)
		//A MOVES, if A wins increment moves in WinCount, else Lose Count and do opposite for B
		//B MOVES
		ArrayList<int[]> AMoves = new ArrayList<>();
		ArrayList<int[]> BMoves = new ArrayList<>();
		
		
		boolean a_wins = false;
		boolean b_wins = false;
		
		//Keep track of the trace of the game as it proceeds (sequence of score, number of dice rolled and outcome of roll)
		//Make sure that changes to Lose and Win count correspond to the trace
		//if A wins, do this (keep track of A or B wins), else do this
		//Keep looping until someone wins or loses
		while (true) {
			
			//Player A rolls Dice
			chooseDice = chooseDice(playerAScore, LoseCount, WinCount, NDice, M);
			diceRoll = rollDice(chooseDice, NSides);
			
			//add to PLayer A moves
			int[] move = {playerAScore[0], playerAScore[1], chooseDice};
			AMoves.add(move);
		
			//update scores
			playerAScore[0] += diceRoll;
			playerBScore[1] += diceRoll;
			
			if (playerAScore[0] > UTarget) {
				b_wins = true;
//				System.out.println("A is over the score: " + playerAScore[0]);
				break;
			}else if (playerAScore[0] >= LTarget) {
				a_wins = true;
//				System.out.println("A reached the target: " + playerAScore[0]);
				break;
			}
			
			//Player B rolls Dice
			chooseDice = chooseDice(playerBScore, LoseCount, WinCount, NDice, M);
			diceRoll = rollDice(chooseDice, NSides);
			
			//add to playerB moves
			int[] move_1 = {playerBScore[0], playerBScore[1], chooseDice};
			BMoves.add(move_1);
			
			//update scores
			playerBScore[0] += diceRoll;
			playerAScore[1] += diceRoll;
			
			if (playerBScore[0] > UTarget) {
				a_wins = true;
				//System.out.println("B is over the score: " + playerBScore[0]);
				break;
			}else if (playerBScore[0] >= LTarget) {
				b_wins = true;
//				System.out.println("B reached the target: " + playerBScore[0]);
				break;
			}
			
		}
		
		if (a_wins) {
			//increment win count for A moves
			for (int i = 0; i < AMoves.size(); i++) {
				int[] move = AMoves.get(i);
				int x = move[0];
				int y  = move[1];
				int numDice = move[2];
				WinCount[x][y][numDice]++;
			}			
			//increment lose count for B moves
			for (int i = 0; i < BMoves.size(); i++) {
				int[] move = BMoves.get(i);
				int x = move[0];
				int y  = move[1];
				int numDice = move[2];
				LoseCount[x][y][numDice]++;
			}	
		}else if (b_wins) {
			//increment win count for B moves
			for (int i = 0; i < BMoves.size(); i++) {
				int[] move = BMoves.get(i);
				int x = move[0];
				int y  = move[1];
				int numDice = move[2];
				WinCount[x][y][numDice]++;
			}	
			//increment lose count for A moves
			for (int i = 0; i < AMoves.size(); i++) {
				int[] move = AMoves.get(i);
				int x = move[0];
				int y  = move[1];
				int numDice = move[2];
				LoseCount[x][y][numDice]++;
			}	
		}
		
		updatedCounts.add(0, WinCount);
		updatedCounts.add(1, LoseCount);
	
		return updatedCounts;
	}
	
	
	static void extractAnswer(int[][][] WinCount, int[][][] LoseCount, int LTarget) {
		
		//[X,Y,J], State <X,Y>
		//For all <X,Y>, find the J for which the WinCount is highest
		//The probability of winning for that rolling J dice at that state is WinCount/ (WinCount + LoseCount) for J
		//Store these best J values in Array
		//Store the probabilities in Array
		//Print Plays and Probs in a UTarget x UTarget grid
		
		int bestMoves[][] = new int[LTarget][LTarget];
		double probAtState[][] = new double[LTarget][LTarget];
		
		
		for (int x = 0; x < LTarget; x++) {
			
			for (int y = 0; y < LTarget; y++) {
				
				int bestMove = 0; //MAYBE CHANGE TO HIGHEST WIN RATE
				double highestRate = 0;
				double total = 0;
				
				for (int j = 0; j < WinCount[0][0].length; j++) {	
					
					if (WinCount[x][y][j] + LoseCount[x][y][j] == 0) {
						continue;
					}
					double winRate = ( (double)WinCount[x][y][j]/((double)WinCount[x][y][j] + (double)LoseCount[x][y][j]) );
					if (winRate > highestRate) {
						//System.out.println("Here");
						highestRate = winRate;
						bestMove = j;
						
					}
				}
			
				bestMoves[x][y] = bestMove;
				total += (double) WinCount[x][y][bestMove];
				total += (double) LoseCount[x][y][bestMove];
				//System.out.println("Total = " + total);
				if (total == 0) {
					probAtState[x][y] = 0;
				}else {
					//System.out.println("Here");
					probAtState[x][y] = WinCount[x][y][bestMove]/total;
					//System.out.println("Prob: " + probAtState[x][y]);
				}
				
			}
			
		}
		
		
		System.out.println("Play: ");
		//print solutions in one line for one iteration then next line
		for (int x = 0; x < LTarget; x++) {
			
			for (int y = 0; y < LTarget; y++) {
				System.out.print(bestMoves[x][y] + "  ");
			}
			System.out.println();
			
		}
		System.out.println();
		System.out.println("Prob: ");
		for (int x = 0; x < LTarget; x++) {
			
			for (int y = 0; y < LTarget; y++) {
				System.out.print(String.format("%.4f", probAtState[x][y]) + "  ");
			}
			System.out.println();
			
		}
		
	}
	
		

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		
		int NDice;
		int NSides;
		int LTarget;
		int UTarget;
		double M;
		int NGames;
		
		System.out.println("Enter NDice, NSides, LTarget, UTarget, M, NGames (separated by a space):\n");
		String inputs[] = in.nextLine().split(" ");
		NDice = Integer.parseInt(inputs[0]);
		NSides = Integer.parseInt(inputs[1]);
		LTarget = Integer.parseInt(inputs[2]);
		UTarget = Integer.parseInt(inputs[3]);
		M = Double.parseDouble(inputs[4]);
		NGames = Integer.parseInt(inputs[5]);
		
//		System.out.println("NDice = " + NDice);
//		System.out.println("NSides = " + NSides);
//		System.out.println("LTarget = " + LTarget);
//		System.out.println("Utarget = " + UTarget);
//		System.out.println("M = " + M);
//		System.out.println("NGames = " + NGames);
		
		
		//WinCount[X,Y,J], LoseCount[X,Y,J]
		//X = current point for player about to player
		//Y = Point count for the opponent
		//J = number of dice that current player rolls
		
		int[][][] WinCount = new int[LTarget][LTarget][NDice + 1];
		int[][][] LoseCount = new int[LTarget][LTarget][NDice + 1];		
		
		ArrayList<int[][][]> newCounts = new ArrayList<>();
		//play NGames games
		
		for (int i = 1; i <= NGames; i++) {
			//play games
			//System.out.println("Game " + i);
			newCounts = PlayGame(NDice, NSides, LTarget, UTarget, LoseCount, WinCount, M);
			WinCount = newCounts.get(0);
			LoseCount = newCounts.get(1);
			
		}
		//Output result for extractAnswer
		extractAnswer(WinCount, LoseCount, LTarget);
		
		
		
	}


}
