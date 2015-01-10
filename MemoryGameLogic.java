import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.NoSuchElementException;
import java.util.Random;


public class MemoryGameLogic implements Runnable, GameConstants{
	
	private Socket[] players;
	private DataInputStream[] inPlayers;
	private DataOutputStream[] outPlayers;
	private int[][] cards;
	private boolean[] turn;
	private int playerTurn;
	private int highestScorePlayer = 0;
	private int highestScore = 0;

	public MemoryGameLogic(Socket[] players) {
		this.players = players;
		inPlayers = new DataInputStream[players.length];
		outPlayers = new DataOutputStream[players.length];
		turn = new boolean[players.length];
		randomizeCards();
	}
	
	/**
	 * Randomizes the cards location
	 */
	public void randomizeCards() {
		Random rand = new Random();
		int[] counter = new int[CARDS_TOTAL / NUM_OF_DUPLICATES];
		cards = new int[ROWS][COLUMNS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				int temp = rand.nextInt(CARDS_TOTAL / NUM_OF_DUPLICATES);
				while (counter[temp] >=  NUM_OF_DUPLICATES) {	
					temp = rand.nextInt(CARDS_TOTAL / NUM_OF_DUPLICATES);
				}
				cards[i][j] = temp;
				counter[temp] += 1;
			}
		}
	}

	@Override
	public void run() {
		try {
			try {
				for (int i = 0; i < players.length; i++) {
					inPlayers[i] = new DataInputStream(players[i].getInputStream());
					outPlayers[i] = new DataOutputStream(players[i].getOutputStream());
				}
				doGame();
			} finally {
				for (int i = 0; i < players.length; i++) {
					players[i].close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void doGame() throws IOException {
		for (int i = 0; i < players.length; i++) {
			sendTo(outPlayers[i], START);
		}
		turn[0] = true;
		for (int i = 1; i < players.length; i++) {
			turn[i] = false;
		}
		sendTo(outPlayers[0], TURN_START);
		for (int i = 1; i < players.length; i++) {
			sendTo(outPlayers[i], TURN_END);
		}
		int cardOne = NULL_CONSTANT;
		int cardTwo = NULL_CONSTANT;
		int uniqueCardOne = NULL_CONSTANT;
		int uniqueCardTwo = NULL_CONSTANT;
		while (true) {
			for (int i = 0; i < players.length; i++) {
				if (turn[i]) {
					playerTurn = i;
				}
			}
			while (turn[playerTurn]) {
				int command = getCommand(inPlayers[playerTurn]); 
				if (command == QUIT) {
					try {
						for (int i = 0; i < players.length; i++) {
							for (int j = 0; j < players.length; j++) {
								if (playerTurn != j) {
									sendTo(outPlayers[j], CHECK_POINT);
									int temp = inPlayers[j].readInt();
									if (temp >= highestScore) {
										highestScorePlayer = i;
									}
								}
							}
						}
						sendTo(outPlayers[highestScorePlayer], WIN);
						for (int i = 0; i < players.length; i++) {
							if ((highestScorePlayer != i) && (playerTurn != i)) {
								sendTo(outPlayers[i], LOSE);
							}
						}
						for (int i = 0; i < players.length; i++) {
							players[i].close();
						}		
						for (int i = 0; i < players.length; i++) {
							players[i].close();
						}
					} catch (SocketException e) {
						//A player closed the program, socket was closed	
					}
					return;
				}
				if (command == CHOOSE_CARD) {
					for (int i = 0; i < players.length; i++) {
						sendTo(outPlayers[i], CHOOSE_CARD);
					}
					int cardNumber = inPlayers[playerTurn].readInt();
					for (int i = 0; i < players.length; i++) {
						if (playerTurn != i) {
							sendTo(outPlayers[i], cardNumber);
						}
					}
					int row = (cardNumber - 1) / COLUMNS;
					int column = (cardNumber - 1) % ROWS;
					for (int i = 0; i < players.length; i++) {
						sendTo(outPlayers[i], cards[row][column]);
					}
					if (cardOne == NULL_CONSTANT) {
						uniqueCardOne = cardNumber;
						cardOne = cards[row][column];
						sendTo(outPlayers[playerTurn], GUESS);
					} else if (cardTwo == NULL_CONSTANT){
						uniqueCardTwo = cardNumber;
						cardTwo = cards[row][column];
						if (match(cardOne, cardTwo)) {
							sendTo(outPlayers[playerTurn], POINT);
							cardOne = NULL_CONSTANT;
							cardTwo = NULL_CONSTANT;
							for (int i = 0; i < players.length; i++) {
								if (playerTurn != i) {
									sendTo(outPlayers[i], CHECK_POINT);
								}
							}
							int totalMatches = 0;
							for (int i = 0; i < players.length; i++) {
								int temp = inPlayers[i].readInt();
								if (temp >= highestScore) {
									highestScorePlayer = i;
								}
								totalMatches += temp;
							}
							if (totalMatches == (CARDS_TOTAL / NUM_OF_DUPLICATES)) {
								try {
									sendTo(outPlayers[highestScorePlayer], WIN);
									for (int i = 0; i < players.length; i++) {
										if (highestScorePlayer != i) {
											sendTo(outPlayers[i], LOSE);
										}
									}
									for (int i = 0; i < players.length; i++) {
										players[i].close();
									}
								} catch (SocketException e) {
									//A player closed the program, socket was closed	
								}
								return; 	
							}
						} else {
							for (int i = 0; i < players.length; i++) {
								sendTo(outPlayers[i], WAIT);
							}
							reset(uniqueCardOne, uniqueCardTwo);
							cardOne = NULL_CONSTANT;
							cardTwo = NULL_CONSTANT;
						}
						if ((playerTurn + 1) == players.length) {
							sendTo(outPlayers[0], TURN_START);
							turn[0] = true;
							for (int i = 1; i < players.length; i++) {
								sendTo(outPlayers[i], TURN_END);
								turn[i] = false;
							}
						} else {
							sendTo(outPlayers[playerTurn + 1], TURN_START);
							turn[playerTurn + 1] = true;
							for (int i = 0; i < players.length; i++) {
								if ((playerTurn + 1) != i) {
									sendTo(outPlayers[i], TURN_END);
									turn[i] = false;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void reset(int uniqueCardOne, int uniqueCardTwo) {
		try {
			for (int i = 0; i < players.length; i++) {
				sendTo(outPlayers[i], RESET);
				sendTo(outPlayers[i], uniqueCardOne);
				sendTo(outPlayers[i], uniqueCardTwo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean match(int cardOne, int cardTwo) {
		if (cardOne == cardTwo) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getCommand(DataInputStream in) throws IOException {
		int command;
		try {
			command = in.readInt();
		} catch (NoSuchElementException | SocketException e) {
			command = QUIT;
		}
		return command;
	}
	
	public void sendTo(DataOutputStream client, int message) throws IOException {
		client.writeInt(message);
		client.flush();
	}
}
