package uk.ac.lancs.CNSCC212.minesweeper.UserInterface;

import uk.ac.lancs.CNSCC212.minesweeper.Library.TimeChecker;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class inherits from the GameSquare class.
 * This class implements the methods from ActionListener and MouseListener to respond different click events.
 * Each square has its own unique representation of coordinates and attributes.
 * This class counts the number of its surrounding bombs once user clicks the square.
 * This class shows a pop-up window when the game failed or user succeed.
 * @author Miao Cai
 * @date 3/21/2019 8:41 PM
 */
public class SmartSquare extends GameSquare implements MouseListener, TimeChecker
{
	/** The bomb existence of this square. **/
	private boolean thisSquareHasBomb;

	/** User sets a red flag on this square. **/
	private boolean guessThisSquareIsBomb;

	/** The traversal state of this square. **/
	private boolean thisSquareHasTraversed;

	/** The x co-ordinate of this square. **/
	private int xLocation;

	/** The y co-ordinate of this square. **/
	private int yLocation;

	/** The y co-ordinate of this square. **/
	private long startTime;

	/**
	 * Create a new SmartSquare instance, which can be placed on a GameBoard.
	 * @param x the x co-ordinate of this square on the game board.
	 * @param y the y co-ordinate of this square on the game board.
	 * @param board the GameBoard upon which this square resides.
	 */
	public SmartSquare(int x, int y, GameBoard board)
	{
		// Paint this square as gray block when initialization.
		super(x, y, "images/block.png", board);

		// Assign coordinates to this square.
		xLocation = x;
		yLocation = y;

		// Initialize attributes
		thisSquareHasBomb = false;
		thisSquareHasTraversed = false;
		guessThisSquareIsBomb = false;
		startTime = 0;

		// add right mouse listener
		addMouseListener(this);
	}

	/**
	 * Set bomb existence of the SmartSquare instance as a given result.
	 * @param result the bomb existence of this SmartSquare instance.
	 */
	protected void setBombExist(boolean result)
	{
		thisSquareHasBomb = result;
	}

	/**
	 * Return bomb existence of the SmartSquare instance.
	 * @return the bomb existence of this SmartSquare instance.
	 */
	protected boolean getBombExist()
	{
		return thisSquareHasBomb;
	}

	/**
	 * Return traversal state of the SmartSquare instance.
	 * @return the traversal state of this SmartSquare instance.
	 */
	protected boolean getTraverse()
	{
		return thisSquareHasTraversed;
	}

	/**
	 * Set traversal state of the SmartSquare instance as a given result.
	 * @param result the traversal state of this SmartSquare instance.
	 */
	protected void setTraverse(boolean result)
	{
		thisSquareHasTraversed = result;
	}

	/**
	 * Return a boolean value whether user sets a red flag in this square or not.
	 * @return the state whether this square has been marked as a bomb or not.
	 */
	protected boolean getGuessThisSquareIsBomb()
	{
		return guessThisSquareIsBomb;
	}

	/**
	 * Set the start time of the game.
	 * @param time the time presented as milliseconds.
	 */
	protected void setStartTime(long time)
	{
		startTime = time;
	}

	/**
	 * Return the game start time.
	 * @return the time presented as milliseconds.
	 */
	protected long getStartTime()
	{
		return startTime;
	}

	/**
	 * An implementation method of abstract method (from GameSquare).
	 * Once get click event, detect bombs and expand into empty space.
	 */
	public void clicked()
	{

		CheckSquare cq = new CheckSquare(board);

		guessThisSquareIsBomb = false;

		if(thisSquareHasBomb)
		{
			/*
			 * If this square contains a bomb, show the bomb image.
			 * Then display the selection window
			 */
			setImage("images/bombReveal.png");
			long costTime = System.currentTimeMillis() - ((SmartSquare) board.getSquareAt(0, 0)).getStartTime();
			cq.showBomb(xLocation, yLocation);
			window("You used " + TimeChecker.calculateTime(costTime) +". Do you want to try again?", "Game Over",
					new ImageIcon("images/failFace.png"));
		} else{
			thisSquareHasTraversed = false;
			/*
			 * If this square doesn't contain a bomb, calculate its surrounding bombs.
			 * If this square has zero bombs in its surrounding squares,
			 * expanding into empty space until the surrounding of the space has at least one bomb
			 * or the space touches the window's boundary.
 			 */
			cq.countBomb(xLocation, yLocation);

			if (cq.isSuccess()) {
				long costTime = System.currentTimeMillis() - ((SmartSquare) board.getSquareAt(0, 0)).getStartTime();
				cq.showBomb(xLocation, yLocation);
				window("You win this game in " + TimeChecker.calculateTime(costTime) +
                        "! Do you want to try again?","Congratulations",
						new ImageIcon("images/passFace.jpg"));
			}
		}
	}

	/**
	 * A method to achieve pop-up window.
	 * @param msg the message to display on the window.
	 * @param title the title string for the window.
	 */
	public void window(String msg, String title, Icon img)
	{

		int choose = JOptionPane.showConfirmDialog(board, msg, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,img);

		if (choose == JOptionPane.YES_OPTION)
		{
			Menu menu = new Menu("Mine sweeper");
		}

		// Close this window after user making a choice
		board.dispose();
	}

	/**
	 * An implementation method to respond right-click events.
	 * @param e the event when user clicks on this square.
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// If user right-click on this square.
		if (e.getButton() == MouseEvent.BUTTON3)
		{
			int clickCount = e.getClickCount();

			// Show red flag.
			if (clickCount == 1)
			{
				setImage("images/redFlag.png");
				guessThisSquareIsBomb = true;
			}

			// Show question mark.
			if (clickCount == 2)
			{
				setImage("images/questionMark.png");
				guessThisSquareIsBomb = false;
			}
		}
	}

	// The following mouse events are not going to be handled in this class.
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}