package robo.testbot.motortest;

import lejos.nxt.Motor;
import lejos.nxt.LCD;
import lejos.nxt.Button;
import lejos.util.Delay;
import lejos.robotics.navigation.*;

/**
 * Simple Hello World program
 *
 */
public class MotorTest {

	public static void main(String[] args) {
		System.out.println("IRON MAN WAITS FOR COMMAND");
		/* Kokeillaans liikkuvaa bottia*/
		DifferentialPilot pilot = new DifferentialPilot(56, 56, 17.6, Motor.A, Motor.B, true);
		
		Button.waitForPress();
	}
}
