package penbot;


import lejos.nxt.Button;


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
public class Main {

	public static void main(String[] args) {
		Penbot ironman = new Penbot();
		
		System.out.println("IRON MAN WAITS FOR COMMAND");
		
		
		Button.waitForPress();
		ironman.drawCross(0, 0);
		Button.waitForPress();
	}
}
