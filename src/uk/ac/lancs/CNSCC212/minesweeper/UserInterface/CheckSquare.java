package uk.ac.lancs.CNSCC212.minesweeper.UserInterface;
/**
 * @author Miao Cai
 * @Description This class provides a way to count the total number of bombs which surrounds the given square.
 * @date 3/21/2019 8:41 PM
 */
public class CheckSquare
{
    /** The GameBoard instance**/
    private GameBoard board;

    /** The height of this GameBoard instance**/
    private int boardHeight;

    /** The width of this GameBoard instance**/
    private int boardWidth;

    private static final int[] distantX = {-1, 0, 1};
    private static final int[] distantY = {-1, 0, 1};

    /**
     * Create a CheckSquare instance contained with the given board.
     * @param board the GameBoard upon which user clicks on.
     */
    public CheckSquare(GameBoard board)
    {
        this.board = board;
        // Both height and width of the board should remove its padding values.
        boardHeight = (board.getHeight() - 20) / 20;
        boardWidth = (board.getWidth() - 20) / 20;
    }

    /**
     * Returns the check result of the given position.
     * @param x the x co-ordinate of the given square.
     * @param y the y co-ordinate of the given square.
     * @return a boolean value of the check result.
     */
    private boolean hasKickedBoundary(int x, int y)
    {
        return x < 0 || x >= boardWidth || y < 0 || y >= boardHeight;
    }

    /**
     * Returns the check result whether user has found out all bombs.
     * @return a boolean value of the check result.
     */
    protected boolean isSuccess()
    {
        // Ensure count start at 0 once this method is invoked.
        int count = 0;

        for (int y = 0; y < boardHeight; y++)
        {
            for (int x = 0; x < boardWidth; x++)
            {
                if (((SmartSquare) board.getSquareAt(x, y)).getTraverse())
                    count++;
            }
        }

        return count == boardHeight * boardWidth;
    }

    /**
     * This method reveals all bombs on the board, examine the square where user guesses it has a bomb
     * @param currentX the x co-ordinate of the given square.
     * @param currentY the y co-ordinate of the given square.
     */
    protected void showBomb(int currentX, int currentY)
    {
        for (int y = 0; y < boardHeight; y++)
        {
            for (int x = 0; x < boardWidth; x++)
            {
                if (currentX == x && currentY == y){}
                else if (((SmartSquare) board.getSquareAt(x, y)).getBombExist())
                    board.getSquareAt(x, y).setImage("images/bomb.png");
                else if(((SmartSquare) board.getSquareAt(x, y)).getGuessThisSquareIsBomb())
                    board.getSquareAt(x, y).setImage("images/flagWrong.png"); // Wrong guess!
            }
        }
    }

    /**
     * This method counts the total number of bombs which surrounds the given square.
     * If there is no bombs surrounds the square, paint this square as blank then expand its surrounding squares
     * util find bombs of the surrounding squares are not empty. This method is implemented by recursion algorithm.
     * @param currentX the x co-ordinate of the given square.
     * @param currentY the y co-ordinate of the given square.
     */
    protected void countBomb(int currentX, int currentY)
    {
        // Ensure count start at 0 once this method is invoked.
        int count = 0;
        SmartSquare currentObject;

        if (hasKickedBoundary(currentX, currentY))
            return; //Skip iteration.
        else if(((SmartSquare)board.getSquareAt(currentX, currentY)).getTraverse())
            return; //Skip iteration.
        else {
            // Declare a SmartSquare instance.
            SmartSquare squareObject;

            // Get the current square.
            currentObject = (SmartSquare)board.getSquareAt(currentX, currentY);
            currentObject.setTraverse(true);

            /*
             * Check surrounding 8 squares:
             * If the square has touch the boundary, skip to next iteration of the loop.
             * Else if the square is itself, then it's unnecessary to count. Just skip to next iteration of the loop.
             * Else check whether this surrounding square contains a bomb. If it does, count accumulation.
             */
            for (int x : distantX)
            {
                for (int y: distantY)
                {
                    if (hasKickedBoundary(currentX + x, currentY + y)){}
                    else if (x == 0 && y == 0){}
                    else{
                        squareObject = (SmartSquare)board.getSquareAt(currentX + x, currentY + y);
                        count = squareObject.getBombExist() ? count + 1 : count;
                    }
                }
            }
        }

        /*
         * If count result is zero, then replace this square with its surrounding
         * squares and invoke itself to find the bombs again.
         */
        if (count != 0)
            currentObject.setImage("images/" + count + ".png");
        else {
            // Paint current square as blank.
            currentObject.setImage("images/0.png");
            countBomb(currentX - 1, currentY -1); // Upper left
            countBomb(currentX, currentY -1); // Above
            countBomb(currentX + 1, currentY -1); // Upper right
            countBomb(currentX - 1, currentY); // Left side
            countBomb(currentX + 1, currentY); // Right side
            countBomb(currentX - 1, currentY + 1); // Lower left
            countBomb(currentX, currentY + 1); // Below
            countBomb(currentX + 1, currentY + 1); // Lower right
        }
    }
}
