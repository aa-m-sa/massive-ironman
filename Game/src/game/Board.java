package game;

import game.GameMove;
import game.Mark;
/**
 * Game board representation.
 * Board x,y coordinate legend:
 * 2,2|2,1|2,0
 * -----------
 * 1,2|1,1|1,0
 * -----------
 * 0,2|0,1|0,0
 *            (bot)
 */
public class Board {
    private Mark[][] board = new Mark[3][3];

    public Board() {
        initBoard();

    }

    public boolean hasGameEnded() {
        if (checkRows())
            return true;
        if (checkColumns())
            return true;
        if (checkUpDiag())
            return true;
        if (checkDownDiag())
            return true;
        return false;
    }

    public void update(GameMove move) {
        int x = move.getX();
        int y = move.getY();
        board[x][y] = move.getMark();

    }

    private void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Mark.EMPTY;
            }
        }
    }


    private boolean checkRows() {
        // true if m has won some row
        for (int i = 0; i < 3; i++) {
            if (checkRow(i))
                return true;
        }
        return false;
    }

    private boolean checkColumns() {
        // true if m has won some column
        for (int i = 0; i < 3; i++) {
            if (checkColumn(i))
                return true;
        }
        return false;
    }

    /* terribly crude way to do this, but suffices for a board this small*/

    private boolean checkRow(int i) {
        if (board[i][0] == Mark.EMPTY)
            return false;
        if (board[i][0] != board[i][1] || board[i][1] != board[i][2])
            return false;
        return true;
    }
    private boolean checkColumn(int i) {
        if (board[0][i] == Mark.EMPTY)
            return false;
        if (board[0][i] != board[1][i] || board[1][i] != board[2][i])
            return false;
        return true;
    }

    private boolean checkUpDiag(){
        if (board[1][1] == Mark.EMPTY)
            return false;
        if (board[0][2] != board[1][1] || board[2][0] != board[1][1])
            return false;
        return true;
    }

    private boolean checkDownDiag(){
        if (board[1][1] == Mark.EMPTY)
            return false;
        if (board[0][0] != board[1][1] || board[2][2] != board[1][1])
            return false;
        return true;
    }
}
