package game;

import game.domain.GameMove;

public interface Player {

    /**
     *  get the player's next move
     * */
    public GameMove getMove();

}
