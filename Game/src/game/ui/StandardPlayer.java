package game.ui;

import java.util.Scanner;
import game.Player;
import game.Mark;
import game.GameMove;

/**
 * a player that reads movements from the provided Scanner
 */
public class StandardPlayer implements Player {
    private Scanner reader;
    private Mark mark;

    public StandardPlayer(Scanner reader) {
        this.reader = reader;
        this.mark = Mark.O_MARK;
    }

    // there's bound to be a better way to implement a text ui,
    // but for now, this suffices
    public GameMove getMove() {
        System.out.println("Your move: x, y");
        System.out.print(">");

        String input = reader.nextLine();
        String[] subs = input.split(",");

        int x = Integer.parseInt(subs[0]);
        int y = Integer.parseInt(subs[1]);

        // assume input is valid
        return new GameMove(this.mark, x, y);

    }
}
