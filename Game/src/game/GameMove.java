package game;

import game.Mark;;

public class GameMove {
    private Mark mark;
    private int x;
    private int y;

    /**
     * Create a GameMove (a mark drawn in the coordinates).
     *
     */
    public GameMove(Mark mark, int x, int y) {
        this.mark = mark;
        if (x < 0 || x > 2 || y < 0 || y > 2) {
            throw new IllegalArgumentException("Move coordinate out of game board!");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * @return the mark
     */
    public Mark getMark() {
        return mark;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }
}
