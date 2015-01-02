package main;

import java.io.*;

import lejos.nxt.Button;
import lejos.nxt.comm.NXTConnection;
import penbot.Penbot;
import comms.Command;

/**
 *  Controling Penbot from NXTConnection.
 * */
public class BotControl {
    Penbot bot;
    NXTConnection conn;
    InputStream istream;

    public BotControl(Penbot bot, NXTConnection conn) {
        this.bot = bot;
        this.conn = conn;
        this.istream = conn.openInputStream();
        System.out.println("Stream ok!");
    }

    public int start() {
        // start the main loop
        /* main loop */
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
                System.out.println("IO error reading msg. Quitting...");
                System.out.println(e);
                quit = true;
            }

            if (bytesRead == 3) {
                if (commandByte == Command.OK) {
                    System.out.println("OK byte read!");
                } else if (commandByte == Command.DRAW) {
                    System.out.println("DRAW byte read!");
                    int x = (int)buffer[1];
                    int y = (int)buffer[2];
                    System.out.println("Drawing at " + x + "," + y );
                    bot.drawCross(x,y);
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
