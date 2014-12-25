package penbot;

import lejos.nxt.Button;

/**
 * Main function
 * 
 */
public class Main {

    public static void main(String[] args) {
		// physical bot dimensions, in mm
		double axis = 120.0;		// wheel dist for DifferentialPilot
		double wheelSize = 56.0;	// wheel size for DifferentialPilot
		double penDist = 64.0;		// dist of pen tip to axis
		
		// physical game board dimensions, also mm
		double outerCellSize = 14.0;
		double markSize = 10.0;
		
		double botDistToBoard = 50;
		
		Board ticpaper = new Board(outerCellSize, penDist);
		Penbot ironman = new Penbot(ticpaper, axis, wheelSize, penDist, 
				outerCellSize, markSize, botDistToBoard);
		
		System.out.println("I AM IRON MAN");
		Button.waitForPress();
		System.out.println("I PRINT");
		//ironman.drawCross(0, 0);
		//ironman.drawCross(2,2);
		//ironman.drawCross(2,1);
		ironman.drawCross(0,2);
		
		System.out.println("FIN.");
		Button.waitForPress();
	}
}
