package penbot;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.Motor;

import penbot.Board;

import comms.BTCommunicator;
import comms.Message;
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
        Button.waitForAnyPress();

        BTCommunicator com = new BTCommunicator();
        com.open();
        boolean quit = false;
        while (!quit) {
            if (Button.ENTER.isDown()) {
                break;
            }
            com.stateOk();
            Message msg;
            try {
                msg = com.readMessage();
            } catch (IOException e) {
                com.stateError();
                System.out.println("IO error reading msg.");
                System.out.println(e);
                continue;
            }
            com.stateBusy();
            // is there a more sane way that isn't overtly complicated?
            if (msg == Message.DRAW_X) {
                try {
                    int x = com.readCoord();
                    int y = com.readCoord();
                    ironman.drawCross(x, y);
                } catch (IOException e) {
                    com.stateError();
                    System.out.println("Drawing X failed:\n error reading coordinates.");
                    System.out.println(e);
                }
            } else if (msg == Message.QUIT) {
                System.out.println("Quitting.");
                quit = true;
            } else {
                System.out.println("Error: Unknown command");
            }
        }

        com.close();
        System.out.println("FIN.");
        Button.waitForAnyPress();
    }
}
