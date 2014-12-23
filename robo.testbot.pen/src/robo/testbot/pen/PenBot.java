package robo.testbot.pen;

import java.lang.Math;
import lejos.nxt.Motor;
import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.util.Delay;
import lejos.robotics.navigation.*;

/**
 * Simple PenBot.
 * 
 * Quite quick hack; testing if the physical lego structure is feasible.
 * 
 * New idea: the PenBot is positioned on the main diagonal of tictactoe board:
 * Drawing an X in a cell can be done just with back-forth movement + rotation.
 * 
 * (So PenBot can only play as X.)
 *
 */
public class PenBot {
	
	DifferentialPilot pilot;

	double speed = 40;
	double turnSpeed = 40.0;
	
	// base coord: the corner of board nearest to the bot
	// coordinates of center of the board lower edge relative to that
	double boardBaseX = 0.0; //mm
	double boardBaseY = 40.0;
	
	// size of drawn cells on the paper, mm
	double outerCellSize = 14.0;
	double outerCellDiag = Math.sqrt(2*(outerCellSize * outerCellSize));
	// length of the y line of X mark
	double crossLine = outerCellSize;
	// angle of the x arc line of X
	double crossRot = 10.0;
	
	
	// pen tip distance from the axis (of rotation)
	double penDist = 60.0;
	
	private void drawCross(int x, int y) {
		pilot.setRotateSpeed(turnSpeed);
		pilot.setTravelSpeed(speed);
		moveToCellCenter(x, y);
		//lower the pen
		// the upper y dir line of the X
		pilot.travel(0.5*crossLine);
		pilot.travel(-0.5*crossLine);
		// the x dir 'line' of the X (rotate)
		pilot.rotate(0.5*crossRot);
		pilot.rotate(-crossRot);
		pilot.rotate(0.5*crossRot);
		// the lower hald of y dir line
		pilot.travel(-0.5*crossLine);
		// raise the pen
		
		// return to base position (we are at lower diag corner)
	}
	
	private void moveToCellCenter(int x, int y) {
		// x, y = coordinates for cell
		// x == y for diagonal, y = x = 0 for baseY, etc

		pilot.travel(boardBaseY);

		// quick and dirty and outright terrible to check if this works
		// only diagonal cells implemented
		// todo: cell objs who know the rotations associated with them?
		double angle = 0;
		double dist = outerCellSize;
		if (x == 0 && x == 0) {
			dist *= 0.5*Math.sqrt(2);
		} else if (x == 1 && x == 1) {
			dist *= 1.5*Math.sqrt(2);
		} else if (x == 2 && x == 2) {
			dist *= 2.5*Math.sqrt(2);
		}

		pilot.rotate(angle);
		pilot.travel(dist);
	}
	

	public static void main(String[] args) {
		PenBot ironman = new PenBot();
		
		System.out.println("IRON MAN WAITS FOR COMMAND");
		
		ironman.pilot = new DifferentialPilot(56.0, 56.0, 120.0, Motor.A, Motor.B, false);
		
		Button.waitForPress();
		ironman.drawCross(2, 2);
		Button.waitForPress();
	}
}
