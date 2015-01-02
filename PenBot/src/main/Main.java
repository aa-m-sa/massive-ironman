package main;

import java.io.*;

import comms.Command;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.comm.*;

import penbot.Penbot;
import main.BotControl;

/**
 * Main function
 *
 */
public class Main {

    private static void setUpButtonListeners() {
        // emergency exit
        Button.ESCAPE.addButtonListener(new ButtonListener() {
            public void buttonPressed(Button b) {
                System.out.println("EMERGENCY HALT");
                System.exit(1);
            }
            public void buttonReleased(Button b) {
                // nothing;
            }
        });
    }

    public static void main(String[] args) {

        setUpButtonListeners();

        Penbot ironman = new Penbot();

        System.out.println("I AM IRON MAN.");
        Button.waitForAnyPress();

        System.out.println("Waiting for connection...");
        NXTConnection conn = Bluetooth.waitForConnection();
        System.out.println("Bluetooth comms ok!");

        BotControl control = new BotControl(ironman, conn.openInputStream());
        // we're ready to begin!
        System.out.println("READY TO READ!\n press any key");
        Button.waitForAnyPress();

        int exitStat = control.start();

        conn.close();
        System.out.println("FIN: " + exitStat);
        Button.waitForAnyPress();
    }
}
