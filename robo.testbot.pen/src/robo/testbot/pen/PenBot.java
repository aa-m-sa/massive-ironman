package robo.testbot.pen;

import java.lang.Math;
import lejos.nxt.Motor;
import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.util.Delay;
import lejos.robotics.navigation.*;

/**
 * Simple Hello World program
 *
 */
public class PenBot {
	
	DifferentialPilot pilot;

	double speed = 40;
	double turnSpeed = 40.0;
	
	// start position of the pen (0,0)
	// coordinates of center of the board lower edge relative to that
	double boardBaseX = 0.0; //mm
	double boardBaseY = 0.0;
	
	// size of drawn cells on the paper, mm
	double outerCellSize = 14.0;
	
	
	// pen tip distance from the axis (of rotation)
	double penDist = 60.0;
	
	private void drawCross(int x, int y) {
		pilot.setRotateSpeed(turnSpeed);
		pilot.setTravelSpeed(speed);
		// assume pen is in the start position
		moveToCell(x, y);
		boolean left = true;	// sugar
		drawCrossLine(left);
		drawCrossLine(!left);
		// return to base position
	}
	
	private void moveToCell(int x, int y) {
		// moves the pen to the lower left corner of the cell
		// cell coordinates: the lower center is 0, 0
		
		// move to correct position in x direction
		
		// move to correct position in y direction
		pilot.travel(boardBaseY + y*outerCellSize + 0.5*outerCellSize);
		
	}
	
	private void drawCrossLine(boolean leftToRight) {
		// if leftToRight:
		// draw a line from the lower left corner to upper right corner
		
		// angles calculated by hand
		// assuming starting from forward facing position
		double firstRotate = 67.5;
		double secondRotate = -112.5;
		
		if (!leftToRight) {
			firstRotate *= -1;
			secondRotate *= -1;
		}
		
		pilot.rotate(firstRotate);
		double cDist = 2 * (penDist + 0.5*outerCellSize) * Math.cos(3*Math.PI/8);
		pilot.travel(cDist);
		pilot.rotate(secondRotate);
		
		// lower the pen
		pilot.travel(outerCellSize);
		pilot.travel(-outerCellSize);
		// raise the pen
		
		// return
		pilot.rotate(-secondRotate);
		pilot.travel(-cDist);
		pilot.rotate(-firstRotate);
	}
	

	public static void main(String[] args) {
		PenBot ironman = new PenBot();
		
		System.out.println("IRON MAN WAITS FOR COMMAND");
		
		ironman.pilot = new DifferentialPilot(56.0, 56.0, 120.0, Motor.A, Motor.B, false);
		
		Button.waitForPress();
		ironman.drawCross(0, 0);
		Button.waitForPress();
	}
}
