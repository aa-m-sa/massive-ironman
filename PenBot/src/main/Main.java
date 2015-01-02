package main;

import java.io.*;

import comms.Command;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.comm.*;

import penbot.Penbot;

/**
 * Main function
 *
 */
public class Main {

    public static void main(String[] args) {

        Penbot ironman = new Penbot();

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
                    int x = (int)buffer[1];
                    int y = (int)buffer[2];
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
