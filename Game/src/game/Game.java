package game;

import comms.BTComms;
import comms.Command;
import game.Player;
import game.GameMove;

/**
 * The main Tic Tac Toe Game class
 */
public class Game {

    /* game board representation */
    private Board board;

    private Player player;
    private Player botAi;
    private BotControl botControl;

    /* create a game with a Player pl*/
    public Game(Player pl, BTComms botConn) {
        this.player = pl;
        this.board = new Board();
        this.botAi = new BotAI(board);
        this.botControl = new BotControl(botConn);
    }

    /**
     * Start the main game loop
     * */
    public void start() {
        // game loop
        while (true) {
            // (bot makes the first move)
            // determine bot move
            // command penbot to move
            GameMove botMove = botAi.getMove();
            botControl.move(botMove);
            board.update(botMove);
            // check for end condition (did the bot win?)
            if (board.hasGameEnded()) {
                break;
            }
            // wait for player move
            // (a stdin player just gives a move by writing a cmd to stdin)
            // (a WebcamPlayer will use OpenCV to determine the player move
            // from a webcam feed)
            GameMove playerMove = player.getMove();
            board.update(playerMove);
            // check again (did player win?)
            if (board.hasGameEnded()) {
                break;
            }

        }
    }

}
