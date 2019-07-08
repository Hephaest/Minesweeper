# 效果演示图

<p align="center"><img src ="images/minesweeper.gif" width = "600px"></p>

# Java 实现经典扫雷游戏
[![LICENSE](https://img.shields.io/cocoapods/l/AFNetworking.svg)](https://github.com/Hephaest/Minesweeper/blob/master/LICENSE)
[![JDK](https://img.shields.io/badge/JDK-8u202%20-orange.svg)](https://www.oracle.com/technetwork/java/javase/8u202-relnotes-5209339.html)
[![Size](https://img.shields.io/badge/Size-54MB-critical.svg)](https://github.com/Hephaest/Minesweeper/raw/master/download/Minesweeper_64-bit_Windows_Setup.exe)
[![Dependencies](https://img.shields.io/badge/Dependencies-up%20to%20date-green.svg)](https://github.com/Hephaest/Minesweeper/tree/master/src)

[English](README.md) | 中文

本扫雷游戏以下 **功能**：
1. 如果点中炸弹会显示炸弹。
2. 玩家左键点击方块能显示该方块周围会出现几个炸弹，如果不存在炸弹的话扫描范围会被放大。
3. 满足各种行数，列数和炸弹个数要求。
4. 对不同水平的玩家提供不同的游戏难度级别。
5. 如果玩家单击鼠标右键会显示红旗。
6. 如果玩家双击鼠标右键会显示问号。
7. 如果玩家游戏挑战失败显示所有炸弹隐藏的地方以及玩家失误标记的地方。
8. 如果玩家挑战成功显示所有的炸弹(原本炸弹的位置有可能已被玩家用小红旗标识了)。

源代码包括抽象类和接口。我将程序分为三个部分来介绍：`GameDriver`，`Library`，`UserInterface`。

# 游戏驱动
这部分相当简单(因为只有一个主函数)。`Driver` 类被用于作为启动游戏的接口。

代码如下所示：
```Java
package GameDriver;

import UserInterface.Menu;
/**
 * @author Hephaest
 * @since 3/21/2019 8:41 PM
 * 此类是用来运行扫雷游戏程序的。
 */
public class Driver
{
	public static void main(String[] Args)
	{
		// 游戏启动的时候会附带一个选项菜单窗口。
		new Menu("Minesweeper");
	}
}
```
# 工具库
包 `Library` 内只有两个文件。第一个是抽象类 `Bomb`，它用于存储和有关游戏窗口的物理信息。

代码如下所示：
```Java
package Library;

import UserInterface.GameBoard;
/**
 * @author Hephaest
 * @since 3/21/2019 8:41 PM
 * 这个抽象类中的抽象方法会在被继承时实现。
 */
public abstract class Bomb
{
    /** 游戏窗口实例 **/
    protected GameBoard board;

    /** 实例的高度 **/
    protected int boardHeight;

    /** 实例的宽度 **/
    protected int boardWidth;

    /**
     * Create bombs, which can be placed on a GameBoard.
     * @param board the GameBoard upon which user clicks on.
     */
    public Bomb(GameBoard board)
    {
        this.board = board;
        // 真正加入计算的高和宽去需要减去填充边距的长度。
        boardHeight = (board.getHeight() - 20) / 20;
        boardWidth = (board.getWidth() - 20) / 20;
    }

    /**
     * 该方法将会被用于分布炸弹的位置。
     */
    protected abstract void reproduceBomb();

}
```
第二个工具就是 `TimeChecker` 接口，它使用将毫秒时间转换成相对应的时间表达，将会被用于 `SmartSquare` 类。

代码如下所示：
```Java
package Library;
/**
 * @author Hephaest
 * @since 3/21/2019 8:41 PM
 * 这个接口有个静态方法通过给定的毫秒时间换算成相对应的时间表达。
 */
public interface TimeChecker
{
    /**
     * 根据程序给定的运行时间返回程序运行时间的标准表达。
     * @param time 在游戏开始和结束之间的时间。
     * @return 总用时的文本描述。
     */
    static String calculateTime(long time)
    {
        int CONVERT_TO_SEC = 1000;
        int CONVERT_TO_OTHERS = 60;

        int ms = (int) time;
        int sec = ms / CONVERT_TO_SEC;
        int min = sec / CONVERT_TO_OTHERS; // 把秒转换成分。
        int hr = min / CONVERT_TO_OTHERS; // 把分转化成小时。

        if (hr == 0)
        {
            if(min == 0)
            {
                if (sec == 0)
                    return ms + " ms";
                else
                    return sec + " sec " + ms % 1000 + " ms";
            } else
                return min + " min " + sec % CONVERT_TO_OTHERS + " sec " + ms % CONVERT_TO_SEC + " ms";
        } else
            return hr + " hour " + min % CONVERT_TO_OTHERS + " min " + sec % CONVERT_TO_OTHERS + " sec " + ms % CONVERT_TO_SEC + " ms";
    }
}
```
# 用户界面
下方的 UML 图 可以帮助您理解以下几个类之间的关系：

<p align="center"><img src ="images/bluejScreenshot.png" width = "600px"></p>

## 游戏菜单
`Menu` 类为玩家提供了4种难度级别的选项：初级，中级，高级和自定义。尤其对于自定义来说，程序需要检验玩家的输入是否符合要求。如果玩家确定选择了以后，选择菜单消失，启动游戏窗口。

代码如下所示：
```Java
package UserInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * 该类继承 JFrame 类。
 * 该类实现了 ActionListener 里对不用点击事件的反馈。
 * 该类提供4个选项供玩家选择。
 * 这4个选项分别是 "初级"，"中级"，"高级" 和 "自定义"。
 * 在点击 "New Game" 按钮之后，菜单窗口自动关闭。
 * @author Hephaest
 * @since 3/21/2019 8:41 PM
 */
public class Menu extends JFrame implements ActionListener
{
    private JButton start;
    private JRadioButton beginner, intermediate, advanced, custom;
    private JTextField width, height, mines;

    /**
     * 创建一个给定标题的菜单。
     * @param title 菜单上的标题。
     */
    public Menu(String title)
    {
        // 设置菜单标题。
        setTitle(title);

        // 创建菜单子标题。
        JLabel subtitle = new JLabel("Difficulty");
        subtitle.setBounds(100,10,100,20);
        add(subtitle);

        // 创建 "初级" 选择按钮。
        beginner = new JRadioButton("Beginner");
        beginner.setBounds(40,40,150,20);
        add(beginner);

        // 设置 "初级" 选择的描述。
        JLabel bDescFirstLine = new JLabel("10 mines");
        bDescFirstLine.setBounds(70,60,100,20);
        JLabel bDescSecondLine = new JLabel("10 x 10 tile grid");
        bDescSecondLine.setBounds(70,80,100,20);
        add(bDescFirstLine);
        add(bDescSecondLine);

        // 创建 "中级" 选择按钮。
        intermediate=new JRadioButton("Intermediate");
        intermediate.setBounds(40,100,150,20);
        add(intermediate);

        // 设置 "中级" 选择的描述。
        JLabel iDescFirstLine = new JLabel("40 mines");
        iDescFirstLine.setBounds(70,120,100,20);
        JLabel iDescSecondLine = new JLabel("16 x 16 tile grid");
        iDescSecondLine.setBounds(70,140,100,20);
        add(iDescFirstLine);
        add(iDescSecondLine);

        // 创建 "高级" 选择按钮。
        advanced=new JRadioButton("Advanced");
        advanced.setBounds(40,160,160,20);
        add(advanced);

        // 设置 "高级" 选择的描述。
        JLabel aDescFirstLine = new JLabel("100 mines");
        aDescFirstLine.setBounds(70,180,100,20);
        JLabel aDescSecondLine = new JLabel("30 x 25 tile grid");
        aDescSecondLine.setBounds(70,200,100,20);
        add(aDescFirstLine);
        add(aDescSecondLine);

        // 创建 "自定义" 选择按钮。
        custom = new JRadioButton("Custom");
        custom.setBounds(40,220,100,20);
        add(custom);

        // 设置 "自定义" 选择的描述。
        JLabel widthLabel = new JLabel("Width (10-30):");
        widthLabel.setBounds(70,240,80,20);
        add(widthLabel);

        width = new JTextField();
        width.setBounds(170,240,40,20);
        add(width);

        JLabel heightLabel = new JLabel("height (10-25):");
        heightLabel.setBounds(70,260,90,20);
        add(heightLabel);

        height = new JTextField();
        height.setBounds(170,260,40,20);
        add(height);

        JLabel mineLabel = new JLabel("Mines (10-100):");
        mineLabel.setBounds(70,280,90,20);
        add(mineLabel);

        mines = new JTextField();
        mines.setBounds(170,280,40,20);
        add(mines);

        // 创建 "开始游戏" 选择按钮。
        start = new JButton("New Game");
        start.setBounds(80,320,100,20);
        add(start);

        // 初始化每个文本框的编辑状态。
        width.setEditable(false);
        height.setEditable(false);
        mines.setEditable(false);

        // 在每个按键上添加监听事件。
        custom.addActionListener(this);
        beginner.addActionListener(this);
        intermediate.addActionListener(this);
        advanced.addActionListener(this);
        start.addActionListener(this);

        // 确保单选。
        ButtonGroup group = new ButtonGroup();
        group.add(beginner);
        group.add(intermediate);
        group.add(advanced);
        group.add(custom);

        // 初始化菜单实例。
        beginner.setSelected(true);
        setSize(280,400);
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * 实现 ActionListener 接口。
     * @param e 点击事件。
     */
    public void actionPerformed(ActionEvent e)
    {
        // 如果用户选择 "自定义"，设置文本框为可编辑状态。
        if (e.getSource() == custom)
        {
            width.setEditable(true);
            height.setEditable(true);
            mines.setEditable(true);
        } else if (e.getSource() == start) {
            // 如果用户点击 "开始游戏" 按钮，获得相对应的炸弹总数，游戏窗口的长和宽。
            int boardWidth = 0;
            int boardHeight = 0;
            int bombs = 0;
            boolean errorFlag = false;

            if (beginner.isSelected())
            {
                boardWidth = 10;
                boardHeight = 10;
                bombs = 10;
            } else if (intermediate.isSelected()) {
                boardWidth = 16;
                boardHeight = 16;
                bombs = 40;
            } else if (advanced.isSelected()) {
                boardWidth = 30;
                boardHeight = 25;
                bombs = 100;
            } else {
                if(!checkValid(width.getText(), height.getText(), mines.getText()))
                {
                    // 设置标记并在窗口上弹出错误提示。
                    errorFlag = true;
                    JOptionPane.showMessageDialog(null, "Please enter correct numbers!");

                } else {
                    boardWidth = Integer.parseInt(width.getText());
                    boardHeight = Integer.parseInt(height.getText());
                    bombs = Integer.parseInt(mines.getText());
                }
            }

            if(!errorFlag)
            {

                // 关闭当前菜单窗口并弹出与之对应的游戏窗口。
                this.dispose();
                GameBoard b = new GameBoard("Minesweeper", boardWidth, boardHeight);
                new ProduceBombs(b, bombs);
                ((SmartSquare) b.getSquareAt(0, 0)).setStartTime(System.currentTimeMillis());
            }

        } else{
            // 如果玩家即没有选择 "Custom" 也没有点击 "New Game" 按钮，这些文本框要设置成不可编辑的状态。
            width.setEditable(false);
            height.setEditable(false);
            mines.setEditable(false);
        }
    }

    /**
     * 检查玩家的输入是否符合要求。
     * @param bWidth 游戏窗口的宽度。
     * @param bHeight 游戏窗口的高度。
     * @param bomb 炸弹的总数
     * @return 返回检查结果的布尔值。
     */
    private boolean checkValid(String bWidth, String bHeight, String bomb)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        if (bWidth == null || bHeight== null || bomb == null)
            return false;
        else if (bWidth.isEmpty() || bHeight.isEmpty() || bomb.isEmpty())
            return false;
        else if (!pattern.matcher(bWidth).matches() || !pattern.matcher(bHeight).matches() || !pattern.matcher(bomb).matches())
            return false;
        else if (Integer.parseInt(bWidth) < 10 || Integer.parseInt(bWidth) > 30 || Integer.parseInt(bHeight) < 10 || Integer.parseInt(bHeight) > 25
                || Integer.parseInt(bomb) < 10 || Integer.parseInt(bomb) > 100)
            return false;
        else
            return Integer.parseInt(bWidth) * Integer.parseInt(bHeight) >= Integer.parseInt(bomb);
    }
}
```
## 游戏窗口
`GameBoard` 类可以创建一个新的窗口，但要求这个窗口不被 In addition, the game board should contain multiple squares and each square should own its action listener.

The code is shown as follows:
```Java
package UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * This class provides a graphical model of a board game.
 * The class creates a rectangular panel of clickable squares,
 * of type SmartSquare. If a square is clicked by the user, a
 * callback method is invoked upon the corresponding SmartSquare instance.
 * The class is intended to be used as a basis for tile based games.
 * @author joe finney
 */
public class GameBoard extends JFrame implements ActionListener
{
	private JPanel boardPanel = new JPanel();

	private int boardHeight;
	private int boardWidth;
	private GameSquare[][] board;

	/**
	 * Create a new game board of the given size.
	 * As soon as an instance of this class is created, it is visualized
	 * on the screen.
	 *
	 * @param title the name printed in the window bar.
	 * @param width the width of the game area, in squares.
	 * @param height the height of the game area, in squares.
	 */
	public GameBoard(String title, int width, int height)
	{
		super();

		this.boardWidth = width;
		this.boardHeight = height;

		// Create game state
		this.board = new GameSquare[width][height];

		// Set up window
		setTitle(title);
		setSize(20 + width * 20,20 + height * 20);
		setContentPane(boardPanel);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		boardPanel.setLayout(new GridLayout(height,width));

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				board[x][y] = new SmartSquare(x, y, this);
				board[x][y].addActionListener(this);

				boardPanel.add(board[x][y]);
			}
		}

		// make our window visible
		setVisible(true);

	}

	/**
	 * Returns the GameSquare instance at a given location.
	 * @param x the x co-ordinate of the square requested.
	 * @param y the y co-ordinate of the square requested.
	 * @return the GameSquare instance at a given location
	 * if the x and y co-ordinates are valid - null otherwise.
	 */
	public GameSquare getSquareAt(int x, int y)
	{
		if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight)
			return null;

		return board[x][y];
	}

	public void actionPerformed(ActionEvent e)
	{
		// The button that has been pressed.
		GameSquare b = (GameSquare)e.getSource();
		b.clicked();
	}
}
```
## Game Sequares
The abstract class `GameSquare` only provides fundamental methods. 

The code is shown as follows:
```Java
package UserInterface;

import javax.swing.*;
import java.net.URL;

/**
 * This class provides a representation of a single game square.
 * The class is abstract, and should be extended to provide domain
 * specific functionality.
 * @author joe finney
 */
public abstract class GameSquare extends JButton
{
	/** The x co-ordinate of this square. **/
	protected int xLocation;

	/** The y co-ordinate of this square. **/
	protected int yLocation;

	/** The GameBoard upon which this GameSquare resides. **/
	protected GameBoard board;

	/**
	 * Create a new GameSquare, which can be placed on a GameBoard.
	 * @param x the x co-ordinate of this square on the game board.
	 * @param y the y co-ordinate of this square on the game board.
	 * @param filename file name.
	 * @param board the GameBoard upon which this square resides.
	 */
	public GameSquare(int x, int y, URL filename, GameBoard board)
	{
		super(new ImageIcon(filename));

		this.board = board;
		xLocation = x;
		yLocation = y;
	}

	/**
	 * Change the image displayed by this square to the given bitmap.
	 *
	 * @param filename the filename of the image to display.
	 */
	public void setImage(URL filename)
	{
		this.setIcon(new ImageIcon(filename));
	}

	/**
	 * A method that is invoked when a user clicks on this square.
	 *
	 */
	public abstract void clicked();
}
```
However, the class `SmartSquare` extends class `GameSquare` and overrides the clicked() method. As I have mention before, the click() method should achieve the following functionalities:
- Displays the number of bombs in surrounding squares if the player clicks the left mouse.
- Display a red flag if the player single clicks the right mouse.
- Display a question mark if the player double clicks the right mouse.

Besides, this object of this class has 2 special attributions: `guessThisSquareIsBomb`, `thisSquareHasTraversed`. Those attributions belong to **boolean** type. `guessThisSquareIsBomb` of an object becomes true when the player clicks this object once on the right mouse. As for `thisSquareHasTraversed`, this attribution is used to avoid infinite recursion, once player clicks an object, the program let `thisSquareHasTraversed` of this object becomes **true** .

The code is shown as follows:
```Java
package UserInterface;

import Library.TimeChecker;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class inherits from the GameSquare class.
 * This class implements the methods from ActionListener and MouseListener to respond different click events.
 * Each square has its own unique representation of coordinates and attributes.
 * This class counts the number of its surrounding bombs once user clicks the square.
 * This class shows a pop-up window when the game failed or user succeed.
 * @author Hephaest
 * @since 3/21/2019 8:41 PM
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
		super(x, y, SmartSquare.class.getResource("/block.png"), board);

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
			setImage(SmartSquare.class.getResource("/bombReveal.png"));
			long costTime = System.currentTimeMillis() - ((SmartSquare) board.getSquareAt(0, 0)).getStartTime();
			cq.showBomb(xLocation, yLocation);
			window("You used " + TimeChecker.calculateTime(costTime) +". Do you want to try again?", "Game Over",
					new ImageIcon(SmartSquare.class.getResource("/failFace.png")));
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
						new ImageIcon(SmartSquare.class.getResource("/passFace.jpg")));
			}
		}
	}

	/**
	 * A method to achieve pop-up window.
	 * @param msg the message to display on the window.
	 * @param title the title string for the window.
	 * @param img the icon.
	 */
	public void window(String msg, String title, Icon img)
	{

		int choose = JOptionPane.showConfirmDialog(board, msg, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,img);

		if (choose == JOptionPane.YES_OPTION)
		{
			new Menu("Mine sweeper");
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
				setImage(SmartSquare.class.getResource("/redFlag.png"));
				guessThisSquareIsBomb = true;
			}

			// Show question mark.
			if (clickCount == 2)
			{
				setImage(SmartSquare.class.getResource("/questionMark.png"));
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
```
The most important class is `CheckSquare`, which checks the rest of untraversed squares and displays the number of bombs in surrounding the selected one. If the number is 0, the program will check the number of bombs surrounded in the surroundings. The terminated condition is that the target has been traversed or the position of target is out of the boundary. Note that surrounding squares a classified as the eight squares that are horizontally, vertically or diagonally adjacent to the square. 

The code is shown as follows:
```Java
package UserInterface;
/**
 * @author Hephaest
 * @since 3/21/2019 8:41 PM
 * This class provides a way to count the total number of bombs which surrounds the given square.
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
                    board.getSquareAt(x, y).setImage(CheckSquare.class.getResource("/bomb.png"));
                else if(((SmartSquare) board.getSquareAt(x, y)).getGuessThisSquareIsBomb())
                    board.getSquareAt(x, y).setImage(CheckSquare.class.getResource("/flagWrong.png")); // Wrong guess!
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
            currentObject.setImage(CheckSquare.class.getResource( "/" + count + ".png"));
        else {
            // Paint current square as blank.
            currentObject.setImage(CheckSquare.class.getResource("/0.png"));
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
```
## Bombs
Randomly generating the bombs always causes collisions. The class `ProduceBombs` is used to handle this issue, each square has an attribution called `thisSquareHasBomb` to record whether the current square already contains a bomb. If the answer is true, we need to re-allocate the bomb until it is no longer conflict with others. This method mainly uses **tail recursion**.

The code is shown as follows:
```Java
package UserInterface;

import Library.Bomb;
import java.util.Random;
/**
 * This class provides a method to generate bombs on the board.
 * This class mainly uses recursion algorithm to put bombs on different squares.
 * @version V1.0
 * @author Hephaest
 * @since 2019-03-12 20:18
 */
public class ProduceBombs extends Bomb
{
    /**
     * Create a ProduceBombs instance to generate bombs at the given board.
     * Using recursion algorithm to avoid generating more than one bomb on the same square.
     * @param board the GameBoard upon which user clicks on.
     * @param number the total number of bombs.
     */
    public ProduceBombs(GameBoard board, int number)
    {

        super(board);

        int count =0;

        do {
            reproduceBomb();
            count++;
        }while (count < number);
    }

    /**
     * This method produce bombs on random square. If the assigned square has already contained a bomb, then
     * reassign a square to receive this bomb by invoking itself.
     */
    public void reproduceBomb()
    {
        Random r = new Random();

        int xLocation = r.nextInt(boardWidth);
        int yLocation = r.nextInt(boardHeight);

        SmartSquare square = (SmartSquare) board.getSquareAt(xLocation, yLocation);

        if (!square.getBombExist())
        {
            // mark this square as it has a bomb and been traversed.
            square.setBombExist(true);
            square.setTraverse(true);
        } else {
            reproduceBomb();
        }
    }
}
```
# Download
I have packed the source code as executable files for multiple platforms. Enjoy yourself! 

**For Windows 64-bit**: [Download here](https://github.com/Hephaest/Minesweeper/raw/master/download/Minesweeper_64-bit_Windows_Setup.exe)

**For MacOS**: [Download here](https://github.com/Hephaest/Minesweeper/raw/master/download/Minesweeper_MacOS_Setup.dmg)

**For Unix/Linux**: [Download here](https://github.com/Hephaest/Minesweeper/raw/master/download/Mine_Unix_SetUp.sh)
