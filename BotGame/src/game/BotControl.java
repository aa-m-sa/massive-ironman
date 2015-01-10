package game;

import comms.BTComms;
import comms.Command;
import game.domain.GameMove;

/**
 * Provides game domain object friendly 'interface' to low-level bot draw commands
 * provided by BTComms.
 *
 * TODO Not sure if this is the best way to do this; refactor?
 */
public class BotControl {
    private BTComms botComm;

    public BotControl(BTComms botComm) {
        this.botComm = botComm;
        botComm.sendCommand(Command.OK);
    }

    /**
     * Commands the bot to execute a GameMove.
     *
     * GameMove is realized as a drawing command.
     */
    public void executeMove(GameMove move) {
        // we'll assume move is a valid move
        botComm.sendCommand(Command.DRAW, move.getX(), move.getY());
    }

}
