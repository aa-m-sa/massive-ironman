package penbot;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

import java.lang.Math;

import lejos.robotics.navigation.DifferentialPilot;

/**
 * Simple PenBot.
 *
 * PenBot is positioned on the main diagonal of tictactoe board:
 * Drawing an X in a cell can be done just with back-forth movement + rotation.
 * (So PenBot can only play as X.)
 */

public class Penbot {
    /* static physical bot dimensions, in mm */
    private static double axis = 120.0;		// wheel dist for DifferentialPilot
    private static double wheelSize = 56.0;	// wheel size for DifferentialPilot
    private static double penDist = 70.0;	// dist of pen tip to axis

    /* static physical game board dimensions, also mm */
    private static double outerCellSize = 14.0;

    // distance from pen tip point to the nearest board corner in mm
    private double botDistToBoard = 50;
    // base coordinates:
    //  the coords of the board corner nearest to the bot
    private double boardBaseY = botDistToBoard;

    /* leJOS DifferentialPilot for bot movement */
    private DifferentialPilot pilot;

    /* Board  abstraction*/
    private Board board;

    /* speeds; self explanatory */
    private double speed = 40;
    private double turnSpeed = 40.0;


    /* define size of marks PenBot draws*/
    private double markSize = 10.0;
    // distance to move backwards - forwards
    private double crossLine = this.markSize;
    // angle to to rotate
    // TODO calculate from crossLine + penDist
    private double crossRot = 10.0;

    /* motors*/
    private NXTRegulatedMotor leftMotor = Motor.A;
    private NXTRegulatedMotor rightMotor = Motor.B;
    // TODO: Separate abstraction for penMotor?
    private NXTRegulatedMotor penMotor = Motor.C;
    // angle to rotate the pen motor
    // (to lower the pen from the upmost position to touch the paper)
    private int penAngle = 15;      // original guesstimate
    private int penDown = 1;        // > 0 if positive angle lowers the pen
    private int penMaxAngle = 20;   // safety; max turn allowed for penMotor

    // print some additional dots on paper for technical debugging
    private boolean testmarkings = true;

    public Penbot() {
        pilot = new DifferentialPilot(wheelSize, wheelSize, axis, leftMotor,
                rightMotor, false);

        this.board = new Board(outerCellSize, penDist);
    }

    /**
     * Draws an X (a cross) in the specified board cell.
     */
    public void drawCross(int x, int y) {
        pilot.setRotateSpeed(turnSpeed);
        pilot.setTravelSpeed(speed);
        moveToCellCenter(x, y);
        // lower the pen
        lowerPen();
        // the upper y dir line of the X
        drawVertical();
        // the x dir 'line' of the X (rotate)
        drawHorizontal();
        // raise the pen
        raisePen();
        // return to base position
        moveToBase(x, y);
    }

    /**
     * Routine to determine the angle the pen motor should rotate so that
     * the pen is lowered from the upmost position to touch the paper.
     *
     * @return the final angle, -1 in case of fail
     */
    public int calibratePenAngle() {
        // assume the pen is raised
        int step = 2;
        int original = this.penAngle;
        int angle = 0;
        System.out.println("Enter accepts, Esc abort\nRight incr. angle, Left decrease.");
        while (true) {
            System.out.print("Cur: ");
            System.out.println(angle);
            this.penAngle = angle;
            this.drawDot();
            int button = Button.waitForAnyPress();
            if (button == Button.ID_ENTER) {
                break;
            } else if (button == Button.ID_LEFT && angle - step >= 0) {
                angle -= step;
            } else if (button == Button.ID_RIGHT && angle + step < penMaxAngle) {
                angle += step;
            } else if (button == Button.ID_ESCAPE) {
                // do not change the angle
                this.penAngle = original;
                return -1;
            }

        }

        this.penAngle = angle;
        return angle;
    }

    public void lowerPen() {
        if (penAngle < penMaxAngle) {
            this.penMotor.rotate(penDown*penAngle);
        }
    }

    public void raisePen() {
        this.penMotor.rotate(-penDown*penAngle);
    }

    public void drawDot() {
        lowerPen();
        raisePen();
    }

    private void drawVertical() {
        // vertical bar of the X mark
        pilot.travel(0.5*crossLine);
        pilot.travel(-1*crossLine);
        pilot.travel(0.5*crossLine);
    }

    private void drawHorizontal() {
        // horizontal bar of the X mark
        pilot.rotate(0.5 * crossRot);
        pilot.rotate(-crossRot);
        pilot.rotate(0.5 * crossRot);
    }

    private void moveToCellCenter(int x, int y) {
        // x, y = coordinates for cell
        // x == y for diagonal, y = x = 0 for baseY, etc

        if (this.testmarkings)
            drawDot();

        pilot.travel(boardBaseY);

        if (this.testmarkings)
            drawDot();

        // angle to rotate bot chassis to pen to point to cell x,y center
        double angle = board.getCellTargetAngle(x, y);
        // distance to travel to get pen tip there
        double dist = board.getCellTargetDist(x, y);

        System.out.println(angle + "," + dist);
        pilot.rotate(angle);
        pilot.travel(dist);
    }

    private void moveToBase(int x, int y) {
        // moveToCellCenter in reverse
        double angle = board.getCellTargetAngle(x, y);
        double dist = board.getCellTargetDist(x, y);
        pilot.travel(-dist);
        pilot.rotate(-angle);
        pilot.travel(-boardBaseY);
    }

}
