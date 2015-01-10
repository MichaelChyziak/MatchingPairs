import javax.swing.ImageIcon;

/**
 * Keeps all of the Game Constants of the game Pairs
 * @author Michael Chyziak
 */
public interface GameConstants {

	int SCREEN_WIDTH = 1000;
	int SCREEN_HEIGHT = 1000;
	int CARDS_TOTAL = 36;
	int NUM_OF_DUPLICATES = 2; //The amount of duplicates a single card may have
	int ROWS = 6; //rows of cards
	int COLUMNS = 6; //columns of cards
	ImageIcon FACE_DOWN_CARD = new ImageIcon("C:/Users/Michael/Desktop/images/originals/back.png"); //The back side of the card
	int DELAY = 2000; //Amount of milliseconds to delay
	String DIRECTORY_LOCATION = "C:/Users/Michael/Desktop/images/"; //CHANGE THIS DEPENDING ON WHERE YOU SAVED THE IMAGES FILE

	//The up side of the cards (images)
	ImageIcon CARD_1 = new ImageIcon(DIRECTORY_LOCATION + "/0.jpg");
	ImageIcon CARD_2 = new ImageIcon(DIRECTORY_LOCATION + "/1.jpg");
	ImageIcon CARD_3 = new ImageIcon(DIRECTORY_LOCATION + "/2.jpg");
	ImageIcon CARD_4 = new ImageIcon(DIRECTORY_LOCATION + "/3.jpg");
	ImageIcon CARD_5 = new ImageIcon(DIRECTORY_LOCATION + "/4.jpg");
	ImageIcon CARD_6 = new ImageIcon(DIRECTORY_LOCATION + "/5.jpg");
	ImageIcon CARD_7 = new ImageIcon(DIRECTORY_LOCATION + "/6.jpg");
	ImageIcon CARD_8 = new ImageIcon(DIRECTORY_LOCATION + "/7.jpg");
	ImageIcon CARD_9 = new ImageIcon(DIRECTORY_LOCATION + "/8.jpg");
	ImageIcon CARD_10 = new ImageIcon(DIRECTORY_LOCATION + "/9.jpg");
	ImageIcon CARD_11 = new ImageIcon(DIRECTORY_LOCATION + "/10.jpg");
	ImageIcon CARD_12 = new ImageIcon(DIRECTORY_LOCATION + "/11.jpg");
	ImageIcon CARD_13 = new ImageIcon(DIRECTORY_LOCATION + "/12.jpg");
	ImageIcon CARD_14 = new ImageIcon(DIRECTORY_LOCATION + "/13.jpg");
	ImageIcon CARD_15 = new ImageIcon(DIRECTORY_LOCATION + "/14.jpg");
	ImageIcon CARD_16 = new ImageIcon(DIRECTORY_LOCATION + "/15.jpg");
	ImageIcon CARD_17 = new ImageIcon(DIRECTORY_LOCATION + "/16.jpg");
	ImageIcon CARD_18 = new ImageIcon(DIRECTORY_LOCATION + "/17.jpg");

	//The images of the up side of the cards in an array of images
	ImageIcon[] IMAGES = {CARD_1, CARD_2, CARD_3, CARD_4, CARD_5, CARD_6, CARD_7, CARD_8, CARD_9, CARD_10, CARD_11, CARD_12, CARD_13, CARD_14, CARD_15, CARD_16, CARD_17, CARD_18};
	
	
	
	
	//NEW GAME CONSTANTS
	int QUIT = -1;
	int END = -2;
	int START = -3;
	int TURN_START = -4;
	int TURN_END = -5;
	int CHOOSE_CARD = -6;
	int WIN = -7;
	int LOSE = -8;
	int GUESS = -9;
	int WAIT = -10;
	int POINT = -11;
	int RESET = -12;
	int CHECK_POINT = -13;
	int NULL_CONSTANT = 1000;
	
	int BGM_LEVEL = -20;
	int SOUND_LEVEL = 0;
}
