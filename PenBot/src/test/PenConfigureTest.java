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

        Penbot ironman = new Penbot();

        int cAngle = ironman.calibratePenAngle();
        System.out.println("Calibrated:");
        System.out.println(cAngle);

        if (cAngle != -1) {
            // test by drawing a X
            ironman.drawCross(0,0);
        }

    }

}
