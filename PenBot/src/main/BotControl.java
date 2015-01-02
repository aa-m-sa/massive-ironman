package main;

import java.io.*;

import lejos.nxt.Button;
import penbot.Penbot;
import comms.Command;

/**
 *  Controlling Penbot via InputStream.
 * */
public class BotControl {
    private Penbot bot;
    private InputStream istream;
    private byte[] buffer;


    public BotControl(Penbot bot, InputStream istream) {
        this.bot = bot;
        this.istream = istream;
        System.out.println("Stream ok!");
    }

    private boolean readToBuffer() throws IOException {
        System.out.println("Starting reading!");
        buffer = new byte[3];
        int n = istream.read(buffer);
        System.out.println("Command" + buffer[0] +" read!");
        return n == 3;
    }

    private byte getCommand() {
        return buffer[0];
    }

    public int start() {
        // start the main loop
        /* main loop */
        boolean quit = false;
        while (!quit) {
            if (Button.ENTER.isDown()) {
                System.out.println("ENTER PRESSED, HALT");
                quit = true;
            }

            try {
                if (readToBuffer()) {
                    if (getCommand() == Command.OK) {
                        System.out.println("OK byte read!");
                    } else if (getCommand() == Command.DRAW) {
                        System.out.println("DRAW byte read!");
                        int x = (int)buffer[1];
                        int y = (int)buffer[2];
                        System.out.println("Drawing at " + x + "," + y );
                        bot.drawCross(x,y);
                    } else if (getCommand() == Command.QUIT) {
                        System.out.println("QUIT byte read!");
                        quit = true;
                    } else {
                        System.out.println("Unknown command byte " + getCommand());
                    }
                } else {
                    System.out.println("Couldn't read to buffer; stream closed!");
                    quit = true;
                }
            } catch (IOException e) {
                System.out.println("IO error reading msg. Quitting...");
                System.out.println(e);
                quit = true;
            }
        }

        /* quit cleanly! */
        try {
            istream.close();
        } catch (IOException ioe) {
            System.out.println("Close failed");
            return 1;
        }

        System.out.println("InputStream closed cleanly!");
        return 0;
    }
}
