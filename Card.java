import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * A card that can be turned face down and face up to show the image
 * @author Michael Chyziak
 */
public class Card extends JButton implements GameConstants{
	
	private static int cardNumber = 0; //The number that is common to all cards (gets incremented to give a unique card number)
	private int uniqueCardNumber; //The unique number of the given card
	private JButton card; //The cards JButton
	private boolean visible; //Being visible means that it is able to be pressed on (action listener is active)
	private boolean selected; //True if the card was pressed, false otherwise
	private boolean faceUp; //True if the card is face up, false if the card is face down
	
	/**
	 * No-arg constructor which starts the card face down and gives the card a unique card number as well as attaches a listener to whenever the card is pressed
	 */
	public Card() {
		card = new JButton(FACE_DOWN_CARD);
		cardNumber++;
		uniqueCardNumber = cardNumber;
		card.addActionListener(new CardListener());
		setCardFaceDown();
		visible = false;
		selected = false;
	}
	
	/**
	 * Returns if the card is face up or down
	 * @return true if the card is face up, false if face down
	 */
	public boolean isFaceUp() {
		return faceUp;
	}
	
	/**
	 * Resets the card number to its original state
	 */
	public static void resetCardNumber() {
		cardNumber = 0;
	}
	
	/**
	 * Returns the cards unique number
	 * @return the cards unique number
	 */
	public int getUniqueCardNumber() {
		return uniqueCardNumber;
	}
	
	/**
	 * Sets the image of the card to the face down image
	 */
	public void setCardFaceDown() {
		card.setIcon(FACE_DOWN_CARD);
		visible = false;
		selected = false;
		faceUp = false;
	}
	
	/**
	 * Returns the JButton of the card
	 * @return the JButton of the card
	 */
	public JButton getButton() {
		return card;
	}
	
	/**
	 * Shows the image of the card that corresponds to the integer value given
	 * @param cardValue the value of the card given
	 */
	public void showCard(int cardValue) {
		faceUp = true;
		card.setIcon(IMAGES[cardValue]); 
	}
	
	/**
	 * Returns true if the card is visible to the action listener and false otherwise
	 * @return the visibility of the card
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Sets the card to the given visibility
	 * @param visibility, if the card is visible or not
	 */
	public void setVisible(boolean visibility) {
		visible = visibility;
	}
	
	/**
	 * Returns true if the card was selected by the user and false otherwise
	 * @return true if the card was selected by the user and false otherwise
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Sets the card to be selected by the user or to be unselected
	 * @param selected
	 */
	public void setSelectedCard(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Checks when the JButton was pressed by the user
	 * @author Michael Chyziak
	 */
	class CardListener implements ActionListener {

		/**
		 * When the button is pressed by the user and the card is visible, sets the card as selected
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (visible) {
				setSelectedCard(true);
			}
		}
		
	}
}
