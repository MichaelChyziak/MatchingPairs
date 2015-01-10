import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class PlayerClient extends JFrame implements Runnable, GameConstants {
	
	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 1200;
	private static String IPAddress = "localhost";
	private boolean playing = false;
	private boolean turn = false;
	private boolean searching = false;
	private Socket socket = null;
	private DataInputStream in;
	private DataOutputStream out;
	private int amtOfMatches = 0;
	private JLabel turnLabel;
	private Card[][] cards;
	private JLabel scoreLabel;
	private int command;
	
	private HashMap<String, Audio> sfx;
	private Audio bgMusic;
	
	public PlayerClient() {
		bgMusic = new Audio("/NewLife.wav");
		bgMusic.loop();
		createInterface();
	}
	
	public static void main(String[] args) {
		if (args.length == 2) {
			if (args[0].equals("-server")) {
					IPAddress = args[1];
			}
		} else if (args.length == 1) {
			if (args[0].equals("-help")) {
				System.out.println("-server address --> Will pass the address of the server");
				System.out.println("-start --> Starts the program");
				System.out.println("-help --> Shows the usage of the program");
			} else if (args[0].equals("-start")) {
				new Thread(new PlayerClient()).start();
			}
		} else {
			new Thread(new PlayerClient()).start();
		}
	}
	
	public void connectToServer() {
		try {
			socket = new Socket(IPAddress, GameServer.PORT);
			InputStream inStream = socket.getInputStream();
			OutputStream outStream = socket.getOutputStream();
			in = new DataInputStream(inStream);
			out = new DataOutputStream(outStream);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error: \nNo server detected", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void leaveServer() throws IOException {
		out.writeInt(QUIT);
		out.flush();
		try {
			socket.close();
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void createInterface() {
		new JFrame();
		setTitle("Memory Game");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		
		setJMenuBar(createMenu());
		add(createGameInfoPanel(), BorderLayout.NORTH);
		add(createCardPanel());
		
		setVisible(true);
	}
	
	public JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createOptionsMenu());
		return menuBar;
	}
	
	public JMenu createOptionsMenu() {
		JMenu menu = new JMenu("Options");
	        JMenuItem findGame = new JMenuItem("Find New Game");
	        findGame.addActionListener(new NewGameListener());
	        JMenuItem quitGame = new JMenuItem("Quit Game");
	        quitGame.addActionListener(new QuitGameListener());
	        menu.add(findGame);
	        menu.add(quitGame);
	        return menu;	
	}
	
	class NewGameListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!playing && !searching) {
				connectToServer();
				if (socket != null) {
					turnLabel.setText("Searching for a game...");
					searching = true;
				}
			} else {
				JOptionPane.showMessageDialog(null, "Error: \nCannot find a new game, you are already in a game", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	}
	
	class QuitGameListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			if (playing) {
				if (turn) {
					turnLabel.setText("Currently not in a game");
					amtOfMatches = 0;
					scoreLabel.setText("Amount of Matches: " + amtOfMatches);
					playing = false;
					searching = false;
					turn = false;
					command = QUIT;
					resetAll();
					JOptionPane.showMessageDialog(null, "You Lose!", "Loser", JOptionPane.PLAIN_MESSAGE);
					try {
						leaveServer();
					} catch (IOException e) {
						//Server already ended the game
					}
				} else {
					JOptionPane.showMessageDialog(null, "Cannot leave a game unless it is your turn", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Error: \nCannot leave a game, you are currently not playing a game", "Error", JOptionPane.ERROR_MESSAGE);
			}	
		}
	}
	
	public JPanel createGameInfoPanel() {
		JPanel panel = new JPanel();
		scoreLabel = new JLabel("Amount of Matches: " + amtOfMatches);
		scoreLabel.setHorizontalAlignment(JLabel.RIGHT);
		turnLabel = new JLabel("Currently not in a game");
		turnLabel.setHorizontalAlignment(JLabel.LEFT);
		panel.add(scoreLabel);
		panel.add(turnLabel);
		return panel;
	}
	
	public JPanel createCardPanel() {
		JPanel cardPanel = new JPanel(new GridLayout(ROWS, COLUMNS));
		createCards();
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				cardPanel.add(cards[i][j].getButton());
			}
		}
		Card.resetCardNumber();
		return cardPanel;
	}
	
	public void createCards() {
		cards = new Card[ROWS][COLUMNS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				cards[i][j] = new Card();
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			while (!playing) {
				try {
					if (in.readInt() == START) {
						turnLabel.setText("Game Found, initializing...");
						playing = true;
					}
				} catch (NullPointerException | IOException e) {
				}
			}
			while (playing) {
				command = -2;
				try {
					command = in.readInt();
				} catch (NoSuchElementException | IOException e) {
				}
				if (command == TURN_START) {
					turnLabel.setText("Currently Your Turn");
					turn = true;
					command = GUESS;
				}
				
				if (command == GUESS) {
					int uniqueCard = -1;
					boolean cardChosen = false;
					for (int i = 0; i < ROWS; i++) {
						for (int j = 0; j < COLUMNS; j++) {
							cards[i][j].setVisible(true);
						}
					}
					while (!cardChosen) {
						if (command == QUIT || command == WIN || command == LOSE) {
							break;
						}
						for (int i = 0; i < ROWS; i++) {
							for (int j = 0; j < COLUMNS; j++) {
								if (cards[i][j].isSelected() && !cards[i][j].isFaceUp()) {
									try {
										cardChosen = true;
										uniqueCard = cards[i][j].getUniqueCardNumber();
										out.writeInt(CHOOSE_CARD);
										out.flush();
										in.readInt();
										out.writeInt(uniqueCard);
										out.flush();
									} catch (IOException e) {
										e.printStackTrace();
									}
									for (int k = 0; k < ROWS; k++) {
										for (int l = 0; l < COLUMNS; l++) {
											cards[k][l].setVisible(false);
										}
									}
									try {
										int cardValue = in.readInt();
										showCard(uniqueCard, cardValue);					
									} catch (IOException e) {
										e.printStackTrace();
									}
								cards[i][j].setSelectedCard(false);
								}
							}
						}
					}
				}
				
				if (command == CHOOSE_CARD) {
					try {
						int uniqueCard = in.readInt();
						int cardValue = in.readInt();
						showCard(uniqueCard, cardValue);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (command == TURN_END) {
					turnLabel.setText("Currently Opponent's Turn");
					turn = false;
				}
				if (command == WIN) {
					JOptionPane.showMessageDialog(null, "You Win!", "Winner", JOptionPane.PLAIN_MESSAGE);
					try {
						leaveServer();
					} catch (IOException e) {
						//Server already ended the game
					}
					turnLabel.setText("Currently not in a game");
					amtOfMatches = 0;
					scoreLabel.setText("Amount of Matches: " + amtOfMatches);
					playing = false;
					searching = false;
					turn = false;
					resetAll();
					break;
				}
				
				if (command == LOSE) {
					JOptionPane.showMessageDialog(null, "You Lose!", "Loser", JOptionPane.PLAIN_MESSAGE);
					try {
						leaveServer();
					} catch (IOException e) {
						e.printStackTrace();
					}
					turnLabel.setText("Currently not in a game");
					amtOfMatches = 0;
					scoreLabel.setText("Amount of Matches: " + amtOfMatches);
					playing = false;
					searching = false;
					turn = false;
					resetAll();
					break;
				}
				
				if (command == WAIT) {
					try {
						Thread.sleep(DELAY);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (command == RESET) {
					try {
						reset(in.readInt());
						reset(in.readInt());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (command == POINT) {
					amtOfMatches += 1;
					scoreLabel.setText("Amount of Matches: " + amtOfMatches);
					try {
						out.writeInt(amtOfMatches);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (command == CHECK_POINT) {
					try {
						out.writeInt(amtOfMatches);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void reset(int card) {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				if (cards[i][j].getUniqueCardNumber() == card) {
					cards[i][j].setCardFaceDown();
					
				}
			}
		}
	}
	
	public void showCard(int uniqueCard, int cardValue) {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				if (cards[i][j].getUniqueCardNumber() == uniqueCard) {
					cards[i][j].showCard(cardValue);
				}
			}
		}
	}
	
	public void resetAll() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLUMNS; j++) {
				cards[i][j].setCardFaceDown();
			}
		}
	}
}
