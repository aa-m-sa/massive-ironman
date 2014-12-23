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
	
	DifferentialPilot pilot;
	
	private void go(double speed, double dist) {
		pilot.setTravelSpeed(speed);
		pilot.travel(dist);
	}
	
	private void turn(double speed, double angle) {
		pilot.setRotateSpeed(speed);
		pilot.rotate(angle);
	}
	

	public static void main(String[] args) {
		MotorTest ironman = new MotorTest();
		
		System.out.println("IRON MAN WAITS FOR COMMAND");
		/* Kokeillaans liikkuvaa bottia*/
		ironman.pilot = new DifferentialPilot(56.0, 56.0, 120.0, Motor.A, Motor.B, false);
		
		Button.waitForPress();
		System.out.println("BOLDLY FORWARDS!");
		ironman.go(40, 56);

		System.out.println("RETREAT!");
		ironman.go(40, -7);
		
		Button.waitForPress();
		System.out.println("TURNING TO FACE THE ENEMY!");
		ironman.turn(20.0, 10.0);
		Button.waitForPress();
		ironman.turn(20.0, 10.0);
		Button.waitForPress();
	}
}
