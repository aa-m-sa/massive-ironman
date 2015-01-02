package main;

import java.io.*;

import comms.Command;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.comm.*;

import penbot.Penbot;
import penbot.Board;

/**
 * Main function
 *
 */
public class Main {

    public static void main(String[] args) {
        // physical bot dimensions, in mm
        double axis = 120.0;		// wheel dist for DifferentialPilot
        double wheelSize = 56.0;	// wheel size for DifferentialPilot
        double penDist = 70.0;		// dist of pen tip to axis

        // physical game board dimensions, also mm
        double outerCellSize = 14.0;
        double markSize = 10.0;

        // bot is located on the main tac tac toe board diagonal
        // distance from pen tip point to the nearest board corner in mm
        double botDistToBoard = 50;

        Board ticpaper = new Board(outerCellSize, penDist);
        Penbot ironman = new Penbot(ticpaper, axis, wheelSize, penDist,
                outerCellSize, markSize, botDistToBoard);

        System.out.println("I AM IRON MAN.");
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


        Button.waitForAnyPress();

        System.out.println("Waiting for connection...");
        NXTConnection conn = Bluetooth.waitForConnection();
        InputStream istream = conn.openInputStream();

        System.out.println("Bluetooth comms ok!");
        boolean quit = false;
        while (!quit) {
            byte[] buffer = new byte[3];
            byte commandByte = 0x00;
            int bytesRead = 0;
            if (Button.ENTER.isDown()) {
                System.out.println("ENTER PRESSED, HALT");
                quit = true;
            }
            try {
                System.out.println("Starting reading!");
                bytesRead = istream.read(buffer);
                commandByte = buffer[0];
                System.out.println("Byte " + commandByte +" read!");
            } catch (IOException e) {
                System.out.println("IO error reading msg.");
                System.out.println(e);
                Button.waitForAnyPress();
                System.exit(1);
            }

            if (bytesRead == 3) {
                if (commandByte == Command.OK) {
                    System.out.println("OK byte read!");
                } else if (commandByte == Command.DRAW) {
                    System.out.println("DRAW byte read!");
                    int x = buffer[1];
                    int y = buffer[2];
                    System.out.println("Drawing at " + x + "," + y );
                    ironman.drawCross(x,y);
                } else if (commandByte == Command.QUIT) {
                    System.out.println("QUIT byte read!");
                    quit = true;
                } else {
                    System.out.println("Unknown byte " + commandByte);
                }
            } else if (bytesRead > 0) {
                System.out.println("ARGH fail reading");
            } else {
                System.out.println("Stream closed!");
                quit = true;
            }
        }

        try {
            istream.close();
        } catch (IOException ioe) {
            System.out.println("Close failed");
        }
        System.out.println("FIN.");
        Button.waitForAnyPress();
    }
}
