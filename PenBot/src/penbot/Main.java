package penbot;


import lejos.nxt.Button;


/**
 *  Main function
 *
 */
public class Main {

	public static void main(String[] args) {
		// physical bot dimensions, in mm
		double axis = 120.0;		// wheel dist for DifferentialPilot
		double wheelSize = 56.0;	// wheel size for DifferentialPilot
		double penDist = 60.0;		// dist of pen tip to axis
		
		// physical game board dimensions, also mm
		double outerCellSize = 14.0;
		double markSize = 7.0;
		
		double botDistToBoard = 40;
		
		Board ticpaper = new Board(outerCellSize, penDist);
		Penbot ironman = new Penbot(ticpaper, axis, wheelSize, penDist, 
				outerCellSize, markSize, botDistToBoard);
		
		System.out.println("I AM IRON MAN");
		
		System.out.println("X to 0, 0")
		Button.waitForPress();
		ironman.drawCross(0, 0);
		
		System.out.println("FIN.")
		Button.waitForPress();
	}
}
