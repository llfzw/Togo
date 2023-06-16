package gobang.pojo.game;

/**
 *  棋盘
 */

public class CheckBoard {

    private final int[][] checkBoard;

    public CheckBoard(int checkBoardLength){
        this.checkBoard = new int[checkBoardLength][checkBoardLength];
    }

    public int[][] getCheckBoard() {
        return checkBoard;
    }

    /**
     * 验证这个位置是否有棋子
     * @return
     */
    public boolean contains(int x, int y){
        return checkBoard[x][y] != 0;
    }

    /**
     * 验证这个位置是否有棋子
     * @return
     */
    public boolean contains(Step step){
        return checkBoard[step.getX()][step.getY()] != 0;
    }

    /**
     * 修改棋子
     * @param x
     * @param y
     * @param color
     */
    public void set(int x, int y, int color){
        checkBoard[x][y] = color;
    }

    /**
     * 修改棋子
     * @param step
     * @param color
     */
    public void set(Step step, int color){
        checkBoard[step.getX()][step.getY()] = color;
    }

}
