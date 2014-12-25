package penbot;


import lejos.nxt.Button;


/**
 *  Main function
 *
 */
public class Main {

	public static void main(String[] args) {
		Penbot ironman = new Penbot();
		
		System.out.println("I AM IRON MAN");
		
		System.out.println("X to 0, 0")
		Button.waitForPress();
		ironman.drawCross(0, 0);
		
		System.out.println("FIN.")
		Button.waitForPress();
	}
}
