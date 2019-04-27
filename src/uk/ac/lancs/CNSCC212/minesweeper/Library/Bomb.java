package uk.ac.lancs.CNSCC212.minesweeper.Library;

import uk.ac.lancs.CNSCC212.minesweeper.UserInterface.GameBoard;
/**
 * @author Miao Cai
 * @Description This class is abstract and should be extended to provide the domain specific functionality.
 * @date 3/21/2019 8:41 PM
 */
public abstract class Bomb
{
    /** The GameBoard instance**/
    protected GameBoard board;

    /** The height of this GameBoard instance**/
    protected int boardHeight;

    /** The width of this GameBoard instance**/
    protected int boardWidth;

    /**
     * Create bombs, which can be placed on a GameBoard.
     * @param board the GameBoard upon which user clicks on.
     */
    public Bomb(GameBoard board)
    {
        this.board = board;
        // Both height and width of the board should remove its padding values.
        boardHeight = (board.getHeight() - 20) / 20;
        boardWidth = (board.getWidth() - 20) / 20;
    }

    /**
     * A method that is invoked when producing bombs on the game board.
     */
    protected abstract void reproduceBomb();

}
