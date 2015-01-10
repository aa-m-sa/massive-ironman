package game.ai;

import game.Player;
import game.domain.Board;
import game.domain.GameMove;
import game.domain.Mark;


/**
 * The simplest possible AI; makes useless but valid moves
 */
public class SimpleBotAI implements Player{
    private Board board;
    private Mark mark;

    /**
     * Create a SimpleBotAI that can see the board.
     */
    public SimpleBotAI(Board board, Mark mark) {
        this.board = board;
        this.mark = mark;
    }

    public GameMove getMove() {
        /* find just a 'random' board cell that's empty */
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isCellEmpty(i,j))
                    return new GameMove(this.mark, i, j);
            }
        }
        // can't make more moves! shouldn't end up here
        throw new IllegalStateException("AI can't make a move! Game should have already ended!");
    }

}
