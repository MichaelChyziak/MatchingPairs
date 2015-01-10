import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextArea;


public class GameServer extends JFrame implements GameConstants {

	public static final int PORT = 1181;
	private int currentNumOfPlayers = 0;
	public static final int NUM_OF_PLAYERS = 2;
	private static JTextArea logArea;
	private Socket[] players;
	
	public GameServer() throws IOException {
		players = new Socket[NUM_OF_PLAYERS];
		logArea = new JTextArea(10, 50);
		logArea.setEditable(false);
		createInterface();
		ServerSocket serverSocket = new ServerSocket(PORT);
		log("Waiting for clients to connect...");
		while (true) {
			currentNumOfPlayers = 0;
			while (currentNumOfPlayers < NUM_OF_PLAYERS) {
				players[currentNumOfPlayers] = serverSocket.accept();
				currentNumOfPlayers++;
			}
			Thread game = new Thread(new MemoryGameLogic(players));
			game.start();
		}
	}
	
	public static void log(String message) {
		logArea.append(message + "\n");
	}
	
	public static void main(String[] args) {
		try {
			new GameServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createInterface() {
		new JFrame();
		setTitle("Memory Game");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		
	    add(logArea);
		
		setVisible(true);
	}
	
	
}
