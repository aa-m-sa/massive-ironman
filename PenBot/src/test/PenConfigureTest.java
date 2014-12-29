package test;
import penbot.Board;
import penbot.Penbot;

/**
 * Does configuring the pen motor rotation angle work?
 *
 * a 'test':
 * compile with testMain() as main function; upload and run
 *
 */

public class PenConfigureTest {

    public static void main(String[] args) {

        double axis = 120.0;
        double wheelSize = 56.0;
        double penDist = 70.0;
        double outerCellSize = 14.0;
        double markSize = 10.0;
        double botDistToBoard = 50;

        Board ticpaper = new Board(outerCellSize, penDist);
        Penbot ironman = new Penbot(ticpaper, axis, wheelSize, penDist,
                outerCellSize, markSize, botDistToBoard);

        int cAngle = ironman.calibratePenAngle();
        System.out.println("Calibrated:");
        System.out.println(cAngle);

        if (cAngle != -1) {
            // test by drawing a X
            ironman.drawCross(0,0);
        }

    }

}
